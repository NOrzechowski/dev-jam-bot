package com.rsi.devjam.utilities;

public class CompositeResponse {
	

	private String messageResponse;
	private boolean errorsOccured;
	
	public CompositeResponse(String message, boolean errors) {
		this.messageResponse = message;
		this.errorsOccured = errors;
	}
	
	public String getMessageResponse() {
		return messageResponse;
	}
	public void setMessageResponse(String messageResponse) {
		this.messageResponse = messageResponse;
	}
	public boolean isErrorsOccured() {
		return errorsOccured;
	}
	public void setErrorsOccured(boolean errorsOccured) {
		this.errorsOccured = errorsOccured;
	}
	
	
}
