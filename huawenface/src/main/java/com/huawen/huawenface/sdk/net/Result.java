package com.huawen.huawenface.sdk.net;

public class Result<T> {
    private String code;
    private String message;

    public Result() {

    }

    public Result(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public boolean isSuccess() {
        return "0".equals(code);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
