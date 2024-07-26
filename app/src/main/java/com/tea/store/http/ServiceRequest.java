package com.tea.store.http;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tea.store.BuildConfig;
import com.tea.store.bean.JsonParse;
import com.tea.store.enums.ServiceStatus;
import com.tea.store.http.response.AppDetialResponse;
import com.tea.store.http.response.AppListResponse;
import com.tea.store.http.response.CategoryResponse;
import com.tea.store.http.ssl.CustomTrustManager;
import com.tea.store.http.ssl.SSLSocketClient;
import com.tea.store.utils.PLog;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ServiceRequest {
    private static final int DEFAULT_TIMEOUT = 30;
    private static final Gson GSON = new Gson();
    private static final Gson MAP_GSON = new GsonBuilder().enableComplexMapKeySerialization().create();
    private static String TAG = ServiceRequest.class.getSimpleName();
    private final Retrofit retrofit;
    private final ServiceHttp request;

    public ServiceRequest(Context context) {
        Map<String, String> header = Header.newMap(context);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .sslSocketFactory(SSLSocketClient.getSSLSocketFactory(), new CustomTrustManager())
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        Request.Builder requestBuilder = original.newBuilder();
                        for (Map.Entry<String, String> entry : header.entrySet()) {
                            requestBuilder.addHeader(entry.getKey(), entry.getValue());
                        }
                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                })
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Url.BASE_URL)
                .client(client)
                .build();

        request = retrofit.create(ServiceHttp.class);
    }

    private static <T> void byCallback(Call<ResponseBody> call, Callback<T> callback, T data, boolean isNetError) {
        if (callback != null) {
            if (isNetError) {
                callback.onCallback(call, ServiceStatus.STATE_NET_WORK_ERROR, null);
            } else {
                if (data == null) {
                    callback.onCallback(call, ServiceStatus.STATE_SERVICE_ERROR, null);
                } else {
                    callback.onCallback(call, ServiceStatus.STATE_SUCCESS, data);
                }
            }
        }
    }

    public Call<ResponseBody> getCategorys(Callback<CategoryResponse> callback, String userId) {
        Map<String, String> map = new HashMap<>();
        map.put("userId", userId);
        map.put("channel", BuildConfig.CHANNEL);
        Call<ResponseBody> call = request.category(map);
        asyncRequest(call, CategoryResponse.class, callback, "getCategorys");
        System.out.println(call.request().url().toString()+"--------------------------");
        return call;
    }

    public Call<ResponseBody> getAppDetial(Callback<AppDetialResponse> callback, String userId) {
        Map<String, String> map = new HashMap<>();
        map.put("appId", userId);
        map.put("channel", BuildConfig.CHANNEL);
        Call<ResponseBody> call = request.category(map);
        asyncRequest(call, AppDetialResponse.class, callback, "getAppDetial");
        return call;
    }

    public Call<ResponseBody> getAppList(Callback<AppListResponse> callback, String userId, String appColumnId, String tag, String word, int page, int maxSize) {
        Map<String, String> field = new HashMap<>();
        field.put("userId", userId);
        if (!TextUtils.isEmpty(word)) field.put("keyword", word);
        if (!TextUtils.isEmpty(appColumnId)) field.put("appColumnId", appColumnId);
        if (!TextUtils.isEmpty(tag)) field.put("tag", tag);

        Map<String, String> query = new HashMap<>();
        query.put("pageNo", String.valueOf(page));
        query.put("pageSize", String.valueOf(maxSize));
        query.put("channel", BuildConfig.CHANNEL);

        Call<ResponseBody> call = request.appList(field, query);


        asyncRequest(call, AppListResponse.class, callback, "getAppList");
        return call;
    }

    public <T> void asyncRequest(Call<ResponseBody> call, Class tClass, Callback<T> callback, String tag) {
        asyncRequest(call, new InnerParse<>(tClass), callback, tag);
    }

    public <T> void asyncRequest(Call<ResponseBody> call, JsonParse<T> parse, Callback<T> callback, String tag) {
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                T data = null;
                try {
                    String json = new String(response.body() != null ? response.body().bytes() : response.errorBody().bytes());
                    PLog.i(TAG, tag + ": " + json);
                    data = parse.parse(json);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    byCallback(call, callback, data, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                byCallback(call, callback, null, true);
                t.printStackTrace();
            }
        });
    }

    public interface Callback<T> {
        void onCallback(Call call, int status, T result);
    }

    private class InnerParse<T> implements JsonParse<T> {
        private final Class<T> tClass;

        public InnerParse(Class<T> tClass) {
            this.tClass = tClass;
        }

        @Override
        public T parse(String json) {
            return GSON.fromJson(json, tClass);
        }
    }
}
