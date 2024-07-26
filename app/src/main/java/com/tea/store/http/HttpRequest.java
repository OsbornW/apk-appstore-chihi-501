package com.tea.store.http;

import android.content.Context;

import com.tea.store.BuildConfig;
import com.tea.store.http.response.AppDetialResponse;
import com.tea.store.http.response.AppListResponse;
import com.tea.store.http.response.CategoryResponse;
import com.tea.store.http.response.VersionResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class HttpRequest {
    private static ServiceRequest request;
    private static UpgradeRequest upgradeRequest;

    public static void init(Context context) {
        request = new ServiceRequest(context);
        upgradeRequest = new UpgradeRequest(context);
    }

    public static Call getCategorys(ServiceRequest.Callback<CategoryResponse> callback, String userId) {
        return request.getCategorys(callback, userId);
    }

    public static Call getAppList(ServiceRequest.Callback<AppListResponse> callback, String userId, String appColumnId, String tag, String word, int page, int maxSize) {
        return request.getAppList(callback, userId, appColumnId, tag, word, page, maxSize);
    }

    public static Call getAddDetial(ServiceRequest.Callback<AppDetialResponse> callback, String appId) {
        return request.getAppDetial(callback, appId);
    }

    public static Call<ResponseBody> checkVersion(UpgradeRequest.Callback<VersionResponse> callback) {
        return upgradeRequest.checkVersion(callback);
    }
}
