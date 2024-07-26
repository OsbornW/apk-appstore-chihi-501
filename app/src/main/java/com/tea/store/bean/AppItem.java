package com.tea.store.bean;

import java.io.Serializable;

public class AppItem implements Serializable {
    public static final int STATU_IDLE = 0;
    public static final int STATU_DOWNLOADING = 1;
    public static final int STATU_DOWNLOAD_FAIL = 2;
    public static final int STATU_INSTALLING = 3;
    public static final int STATU_INSTALL_SUCCESS = 4;
    public static final int STATU_INSTALL_FAIL = 5;
    public static final int STATU_DOWNLOAD_SUCCESS = 6;

    private String name;
    private int icon;
    private String mes;
    private int status = STATU_IDLE;
    private float progress;

    private int id = -1;
    private String appName;
    private String appIcon;
    private String appState;
    private String appSize;
    private String appDownLink;
    private String appImg1;
    private double score;
    private String packageName;

    public String getPackageName() {
        return packageName;
    }

    public double getScore() {
        return score;
    }

    public String getAppImg1() {
        return appImg1;
    }

    public int getId() {
        return id;
    }

    public String getAppDownLink() {
        return appDownLink;
    }

    public String getAppIcon() {
        return appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public String getAppSize() {
        return appSize;
    }

    public String getAppState() {
        return appState;
    }

    public int getStatus() {
        return status;
    }

    public float getProgress() {
        return progress;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public String getName() {
        return name;
    }
}
