package com.fds.core;

import java.io.Serializable;
import java.util.Optional;

public class Customer implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private int age;
	private String country;
	private Integer timeToLive = null; 
	public Customer() {}
	public Customer(String name, int age, String country, Optional<Integer> timeToLive) {
		super();
		this.name = name;
		this.age = age;
		this.country = country;
		this.timeToLive = timeToLive.isPresent() ? timeToLive.get():0;
	}
	public Customer(String name, int age, String country) {
		super();
		this.name = name;
		this.age = age;
		this.country = country;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	} 
	 
	
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public Integer getTimeToLive() {
		return timeToLive;
	}
	public void setTimeToLive(Integer timeToLive) {
		this.timeToLive = timeToLive;
	}
	@Override
	public String toString() {
		return "Customer [name=" + name + ", age=" + age + ", country=" + country + ", timeToLive=" + timeToLive + "]";
	}
	
	
	 	
}
