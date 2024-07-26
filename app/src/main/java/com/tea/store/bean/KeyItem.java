package com.tea.store.bean;

public class KeyItem {
    public static final int TYPE_ENG = 0;
    public static final int TYPE_NUM = 1;
    public static final int TYPE_UPCAST = -1;
    public static final int TYPE_DEL = -2;
    public static final int TYPE_SWITCH = -3;
    public static final int TYPE_SPACE = -4;
    public static final int TYPE_SEARCH = -5;

    private int type;
    private String name;
    private int icon;
    private int spanSize;
    private boolean useIcon;

    public KeyItem(int type, String name, int icon, int spanSize, boolean useIcon) {
        this.type = type;
        this.name = name;
        this.icon = icon;
        this.spanSize = spanSize;
        this.useIcon = useIcon;
    }

    public boolean isUseIcon() {
        return useIcon;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }

    public int getSpanSize() {
        return spanSize;
    }
}
