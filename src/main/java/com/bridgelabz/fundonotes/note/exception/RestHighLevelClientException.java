package com.bridgelabz.fundonotes.note.exception;

public class RestHighLevelClientException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    public RestHighLevelClientException(final String message) {
    	super(message);
    }
}
