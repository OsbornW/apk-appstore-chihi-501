package com.tea.store.utils;

public class CategoryNames {
    public static final String getNname(int type) {
        String name = "Unknow";
        switch (type) {
            case 8:
                name = "直播";
                break;
            case 9:
                name = "点播";
                break;
            case 5:
                name = "教育";
                break;
            case 4:
                name = "生活";
                break;
            case 3:
                name = "娱乐";
                break;
            case 6:
                name = "工具";
                break;
            case 14:
                name = "休闲益智";
                break;
            case 15:
                name = "棋牌桌游";
                break;
            case 16:
                name = "动作冒险";
                break;
            case 17:
                name = "体育竞技";
                break;
            case 18:
                name = "遥控器游戏";
                break;
            case 19:
                name = "鼠标游戏";
                break;
            case 20:
                name = "手柄游戏";
                break;
        }
        return name;
    }
}
