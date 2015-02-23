package com.tactile.tact.services.network;

import java.util.List;

public interface APIResult {
    public List<String> getErrorList();
    public String getErrorString();
}
