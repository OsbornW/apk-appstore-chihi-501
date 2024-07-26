package com.tea.store.bean;

public class UDPMessage {
    public static final int TYPE_GET_PACKAGE_MESSAGE = 0;
    public static final int TYPE_DOWNLOAD_APK = 1;
    public static final int TYPE_RESPONSE_PACKAGE_MESSAGE = 2;

    public UDPMessage(int type, String data) {
        this.type = type;
        this.data = data;
    }

    public int type;
    public String data;

}
