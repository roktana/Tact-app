package com.tactile.tact.services.network;

public class ServerErrorResponse extends ErrorResponse {
	
	public int statusCode;

    public ServerErrorResponse() {
        //super("The server was unable to process your request.  Please try again later.");
        super("You appear to have lost your data connection. Please check and try again.");
    }

    public ServerErrorResponse(int statusCode) {
        //super("The server was unable to process your request.  Please try again later.");
        super("You appear to have lost your data connection. Please check and try again.");
        this.statusCode = statusCode;
    }
    
    // init with a better error message
    public ServerErrorResponse(String error, int statusCode) {
        super(error);
        this.statusCode = statusCode;
    }
}
