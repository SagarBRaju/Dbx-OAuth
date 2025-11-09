package com.dropbox.oauth.dto.response;

public class BaseResponse {

    private boolean success;
    private String message;
    private Object data;

    public BaseResponse() {
    }

    public BaseResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static  BaseResponse success(String message, Object data) {
        return new BaseResponse(true, message, data);
    }

    public static  BaseResponse error(String message) {
        return new BaseResponse(false, message, null);
    }
}


