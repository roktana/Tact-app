package com.tactile.tact.utils.webservices;

/**
 * Exception class to handle errors in web service classes 
 * @author Leyan Abreu Sacre
 *
 */
public class WebServiceException extends Exception {
	
	/************WEB SERVICE EXCEPTION CONSTANTS*******************/
	public static int ENCODING_EXCEPTION_STATUS = 1;
	public static String ENCODING_EXCEPTION_MESSAGE = "java.io.UnsupportedEncodingException. Thrown when a program asks for a particular character converter that is unavailable";
	public static String ENCODING_EXCEPTION_USER_MESSAGE = "";
	
	public static int HTTP_CLIENT_EXECUTION_EXCEPTION_STATUS = 2;
	public static String HTTP_CLIENT_EXECUTION_EXCEPTION_MESSAGE = "";
	public static String HTTP_CLIENT_EXECUTION_EXCEPTION_USER_MESSAGE = "";
	
	public static int NO_HTTP_CONNECTION_EXCEPTION_STATUS = 3;
	public static String NO_HTTP_CONNECTION_EXCEPTION_MESSAGE = "Not a HTTP connection.";
	public static String NO_HTTP_CONNECTION_EXCEPTION_USER_MESSAGE = "";
	
	public static int HTTP_CONNECTION_EXCEPTION_STATUS = 4;
	public static String HTTP_CONNECTION_EXCEPTION_MESSAGE = "IOException. Error trying to stablish a HTTP connection";
	public static String HTTP_CONNECTION_EXCEPTION_USER_MESSAGE = "";
	
	public static int ABORT_HTTP_CONNECTION_EXCEPTION_STATUS = 5;
	public static String ABORT_HTTP_CONNECTION_EXCEPTION_MESSAGE = "IOException. Error trying to abort a HTTP connection";
	public static String ABORT_HTTP_CONNECTION_EXCEPTION_USER_MESSAGE = "";
	
	public static int EMPTY_RESPONSE_EXCEPTION_STATUS = 6;
	public static String EMPTY_RESPONSE_EXCEPTION_MESSAGE = "Web service return an empty response";
	public static String EMPTY_RESPONSE_EXCEPTION_EXCEPTION_USER_MESSAGE = "";
	
	public static int DECODING_RESPONSE_ENTITY_EXCEPTION_STATUS = 7;
	public static String DECODING_RESPONSE_ENTITY_EXCEPTION_MESSAGE = "Error trying to decode the response entity";
	public static String DECODING_RESPONSE_ENTITY_EXCEPTION_USER_MESSAGE = "";
	
	public static int DECODING_RESPONSE_ENTITY_CONTENT_EXCEPTION_STATUS = 8;
	public static String DECODING_RESPONSE_ENTITY_CONTENT_EXCEPTION_MESSAGE = "Trying to create a stream from Entity content. The stream could not be created";
	public static String DECODING_RESPONSE_ENTITY_CONTENT_EXCEPTION_USER_MESSAGE = "";
	
	public static int DECODING_RESPONSE_ENTITY_CONTENT_ACCESS_EXCEPTION_STATUS = 9;
	public static String DECODING_RESPONSE_ENTITY_CONTENT_ACCESS_EXCEPTION_MESSAGE = "Trying to create a stream from Entity content. This entity is not repeatable and the stream has already been obtained previously";
	public static String DECODING_RESPONSE_ENTITY_CONTENT_ACCESS_EXCEPTION_USER_MESSAGE = "";
	
	public static int BAD_RESPONSE_CODE_EXCEPTION_STATUS = 10;
	public static String BAD_RESPONSE_CODE_EXCEPTION_MESSAGE = "Response code not 200";
	public static String BAD_RESPONSE_CODE_EXCEPTION_USER_MESSAGE = "";
	/**************************************************************/

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -1860160758348186549L;
	
	private int status;
	private String message;
	private String originalExceptionMessage;
	private String userMessage;
	
	public WebServiceException(int pStatus, String pMessage, String pOriginalMessage, String pUserMessage) {
		super();
		this.status = pStatus;
		this.message = pMessage;
		this.originalExceptionMessage = pOriginalMessage;
		this.userMessage = pUserMessage;
	}
		
	public String getOriginalExceptionMessage() {
		return originalExceptionMessage;
	}

	public void setOriginalExceptionMessage(String originalExceptionMessage) {
		this.originalExceptionMessage = originalExceptionMessage;
	}

	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getUserMessage() {
		return userMessage;
	}
	public void setUserMessage(String userMessage) {
		this.userMessage = userMessage;
	}
}

