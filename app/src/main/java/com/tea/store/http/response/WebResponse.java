package com.tea.store.http.response;

public class WebResponse<T> {
    private Integer code;
    private Boolean success;
    private T result;
    private String msg;

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Integer getCode() {
        if (code == null) {
            return -1;
        }
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Boolean isSuccess() {
        if (success == null) {
            return false;
        }
        return success;
    }
}
