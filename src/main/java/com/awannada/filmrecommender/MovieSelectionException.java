package com.awannada.filmrecommender;

public class MovieSelectionException extends RuntimeException {

	public MovieSelectionException(String message) {
		super(message);
	}

	public MovieSelectionException(String message, Throwable cause) {
		super(message, cause);
	}
}
