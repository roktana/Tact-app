package com.tactile.tact.services.network.response;

import java.util.List;

import com.tactile.tact.services.network.APIResponse;

public class ArrayResponse extends ServerResponse {
	
	public ArrayResponse(List<APIResponse> responseList) {
		this.list = responseList;
	}
	
	public List<APIResponse> list;
	
	public List<APIResponse> getList() {
		return list;
	}

}
