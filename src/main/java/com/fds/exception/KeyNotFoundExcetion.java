package com.fds.exception;

public class KeyNotFoundExcetion extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8877896248859601874L;

	public KeyNotFoundExcetion() {
	}

	public KeyNotFoundExcetion(String message) {
		super(message);
	}

}
