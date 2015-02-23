package com.tactile.tact.services.network;


public class EmptyResponse extends APIResponse {

    private static final EmptyResult EMPTY = new EmptyResult();

    @Override
    public APIResult getResult() {
        return EMPTY;
    }	
	
}
