package com.tea.store.bean;

import java.io.Serializable;

public class Version implements Serializable {
    private String downLink;
    private double version;
    private String desc;
    private String activity;
    private String packageName;
    private String appName;
    private String channel;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public double getVersion() {
        return version;
    }

    public String getDesc() {
        return desc;
    }

    public String getDownLink() {
        return downLink;
    }
}
