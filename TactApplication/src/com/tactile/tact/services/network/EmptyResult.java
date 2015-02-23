package com.tactile.tact.services.network;

import java.util.List;

public class EmptyResult implements APIResult {

    @Override
    public List<String> getErrorList() {
        return null;
    }

    @Override
    public String getErrorString() {
        return null;
    }
	
}
