package com.tactile.tact.services.network;

import java.util.ArrayList;
import java.util.List;

public class ErrorResult implements APIResult {
    // primarily for internal/HTTP errors whence we get nothing back from the server
    List<String> errorList = null;

    public ErrorResult(String errorMessage) {
        this.errorList = new ArrayList<String>();
        this.errorList.add(errorMessage);
    }


    public ErrorResult(List<String> errorList) {
        this.errorList = errorList;
    }

    @Override
    public List<String> getErrorList() {
        return this.errorList;
    }

    @Override
    public String getErrorString() {
        if (this.errorList == null || this.errorList.size() == 0) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        for (int i=0; i< this.errorList.size(); i++) {
            if (i !=0 )
                sb.append(".  ");
            sb.append(this.errorList.get(i));
        }

        return sb.toString();
    }

}
