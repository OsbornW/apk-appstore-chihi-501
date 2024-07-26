package com.tea.store.http.response;

import com.tea.store.bean.AppListResult;

public class AppListResponse {
    private Integer code;
    private Boolean success;
    private AppListResult result;
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

    public AppListResult getResult() {
        return result;
    }

    public void setResult(AppListResult result) {
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
