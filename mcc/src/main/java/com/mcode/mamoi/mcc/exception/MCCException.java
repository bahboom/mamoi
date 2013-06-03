package com.mcode.mamoi.mcc.exception;

public class MCCException extends Exception {
	public MCCException(String message, String sourceFile, int lineNumber) {
		super("\nERROR: " + message + "\nSource File: " + sourceFile + ", Line: " + lineNumber);
	}
}
