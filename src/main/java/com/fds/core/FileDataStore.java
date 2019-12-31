package com.fds.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fds.exception.KeyNotFoundExcetion;
import com.fds.exception.UserAlreadyExists;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * Hello world!
 *
 */
public class FileDataStore {
	Timer crunchifyTimer;

	public FileDataStore() {
	}

	public FileDataStore(int seconds) {
		crunchifyTimer = new Timer();
		crunchifyTimer.schedule(new CrunchifyReminder(), 0, seconds * 1000);
	}

	static Gson gson = new Gson();
	static Map<String, Customer> customerMapObj = new HashMap<>();

	private String getResourceFolderPath() {
		Path file = Paths.get("resource", "Customer.json");
		return file.toString();
	}

	public static void main(String[] args) throws Exception {
		FileDataStore fileDataStore = new FileDataStore();
		fileDataStore.readJson();
		new FileDataStore(1);
		try (Scanner scan = new Scanner(System.in)) {
			int option = 0;
			do {
				System.out.println("1. Create Customer");
				System.out.println("2. Read Customer");
				System.out.println("3. Delete Customer");
				System.out.println("0. exit");
				option = scan.nextInt();
				switch (option) {
				case 1:
					fileDataStore.createCustomer(scan);
					break;
				case 2:
					fileDataStore.readCustomer(scan);
					break;
				case 3:
					fileDataStore.deleteCustomer(scan);
					break;
				case 0:
					break;
				default:
					System.out.println("Please select valid option!!!!!!!");
					break;
				}
			} while (option != 0);
		}
	}

	private void deleteCustomer(Scanner scan) throws IOException {
		System.out.println("Enter name you want to delete :");
		String name = "";
		do {
			name = scan.next();
			name = name.trim();
			if (name.isEmpty()) {
				System.out.println("Please enter valid name");
			}
		} while (name.isEmpty());
		if (!customerMapObj.containsKey(name)) {
			throw new KeyNotFoundExcetion("key not exists in Data store");
		}
		customerMapObj.remove(name);
		convertMapToFile();

	}

	private void readCustomer(Scanner scan) {
		System.out.println("Enter name you want to details :");
		String name = "";
		do {
			name = scan.next();
			name = name.trim();
			if (name.isEmpty()) {
				System.out.println("Please enter valid name");
			}
		} while (name.isEmpty());
		if (!customerMapObj.containsKey(name)) {
			throw new KeyNotFoundExcetion("key not exists in Data store");
		}

		Customer cust = customerMapObj.get(name);
		System.out.println(cust.toString());
	}

	private void createCustomer(Scanner scan) throws IOException {
		Customer newCust = new Customer();
		System.out.println("Enter Customer Name :");
		String name = "";
		do {
			name = scan.next();
			name = name.trim();
			if (name.isEmpty()) {
				System.out.println("Please enter valid name");
			}
		} while (name.isEmpty());
		newCust.setName(name);
		System.out.println("Enter Customer Age :");
		int age = 0;
		try {
			do {
				age = scan.nextInt();
				if (!(age > 0 && 100 >= age)) {
					System.out.println("Age should be 1 to 100");
					age = 0;
				}
			} while (age == 0);
		} catch (NumberFormatException e) {
			System.out.println("Please enter valid number");
		}
		newCust.setAge(age);
		System.out.println("Enter Customer Country :");
		newCust.setCountry(scan.next());

		System.out.println("Enter Time to live customer (seconds)(To skip please enter speace and enter )  :");
		if (scan.hasNext()) {
			String timeToLime = scan.next().trim();
			if (!timeToLime.isEmpty()) {
				newCust.setTimeToLive(Integer.parseInt(timeToLime));
			}
		}
		if (customerMapObj.containsKey(newCust.getName())) {
			throw new UserAlreadyExists("Key already Exits...");
		}
		customerMapObj.put(newCust.getName(), newCust);
		convertMapToFile();

	}

	private String getDataStoreFilePath() throws IOException {
		String path = getFilePath();
		if (path.isEmpty()) {
			path = getResourceFolderPath();
		} else {
			Path sysPath = Paths.get(path);
			if (!Files.exists(sysPath)) {
				throw new FileNotFoundException("System file path not exits.." + path);
			}
			path = path + "/Customer.json";
		}
		return path;
	}

	private String getFilePath() throws IOException {
		Properties prop = new Properties();
		String propFileName = "datastore.properties";
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
		if (inputStream != null) {
			prop.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		}
		return prop.getProperty("location");

	}

	@SuppressWarnings("unchecked")
	public void createFile(List<Customer> custDetails, String locationToStore) throws IOException {
		JSONObject custDetail = new JSONObject();
		custDetails.forEach(customer -> {
			String cust = gson.toJson(customer);
			customerMapObj.put(customer.getName(), customer);
			JsonElement json = JsonParser.parseString(cust);
			custDetail.put(customer.getName(), json.getAsJsonObject());
		});
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(locationToStore))) {
			writer.write(custDetail.toString());
		}
	}

	public synchronized void convertMapToFile() throws IOException {
		String cust = gson.toJson(customerMapObj);
		JsonElement json = JsonParser.parseString(cust);
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(getDataStoreFilePath()))) {
			writer.write(json.toString());
		}
	}

	/**
	 * Read JSON from a file into a Map
	 */
	public Object readJson() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		try {
			customerMapObj = mapper.readValue(new File(getDataStoreFilePath()),
					new TypeReference<Map<String, Customer>>() {
					});
			System.out.println(customerMapObj.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapper;
	}

	// Check for element's expired time.
	private void clearExipredElementsFromMap(Map<String, Customer> map) throws IOException {
		Date currentTime = new Date();
		Date actualExpiredTime = new Date();
		Iterator<Entry<String, Customer>> crunchifyIterator = map.entrySet().iterator();
		while (crunchifyIterator.hasNext()) {
			Entry<String, Customer> entry = crunchifyIterator.next();
			Customer crunchifyElement = entry.getValue();
			if (crunchifyElement.getTimeToLive() != null) {
				actualExpiredTime.setTime(currentTime.getTime() - crunchifyElement.getTimeToLive() * 1000l);
				if (currentTime.compareTo(actualExpiredTime) > 0) {
					customerMapObj.remove(entry.getKey());
					convertMapToFile();
				}
			}
		}
	}

	class CrunchifyReminder extends TimerTask {
		public void run() {
			try {
				clearExipredElementsFromMap(customerMapObj);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
