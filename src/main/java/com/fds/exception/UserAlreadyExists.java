package com.fds.exception;

public class UserAlreadyExists extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9096425379226189973L;

	public UserAlreadyExists(){
	}
	
	public UserAlreadyExists(String message) {
		super(message);
	}

}
