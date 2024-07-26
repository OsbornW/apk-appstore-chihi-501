package com.tea.store.http.response;

import com.tea.store.bean.AppDetial;

public class AppDetialResponse {
    private Integer code;
    private Boolean success;
    private AppDetial result;
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

    public AppDetial getResult() {
        return result;
    }

    public void setResult(AppDetial result) {
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
