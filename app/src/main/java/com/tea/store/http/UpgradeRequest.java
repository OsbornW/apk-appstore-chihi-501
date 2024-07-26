package com.tea.store.http;

import android.content.Context;
import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tea.store.App;
import com.tea.store.BuildConfig;
import com.tea.store.bean.JsonParse;
import com.tea.store.config.Config;
import com.tea.store.enums.ServiceStatus;
import com.tea.store.http.response.VersionResponse;
import com.tea.store.http.ssl.CustomTrustManager;
import com.tea.store.http.ssl.SSLSocketClient;
import com.tea.store.utils.DeviceUuidFactory;
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

public class UpgradeRequest {
    private static final int DEFAULT_TIMEOUT = 30;
    private static final Gson GSON = new Gson();
    private static final Gson MAP_GSON = new GsonBuilder().enableComplexMapKeySerialization().create();
    private static String TAG = UpgradeRequest.class.getSimpleName();
    private final Retrofit retrofit;
    private final ServiceHttp request;

    public UpgradeRequest(Context context) {
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
                .baseUrl(Url.SOYA_BASE_URL)
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

    public Call<ResponseBody> checkVersion(Callback<VersionResponse> callback) {
        Map<String, String> map = new HashMap<>();
        map.put("appId", Config.APPID);
        map.put("channel", Config.CHANNEL);
        map.put("chihi_type", Config.CHIHI_TYPE);
        map.put("version", String.valueOf(BuildConfig.VERSION_CODE));
        map.put("sdk", String.valueOf(Build.VERSION.SDK_INT));
        map.put("uuid", String.valueOf(DeviceUuidFactory.getUUID(App.getContext())));
        map.put("model", Build.MODEL);
        map.put("brand", Build.BRAND);
        map.put("product", Build.PRODUCT);
        Call<ResponseBody> call = request.checkVersion(map);
        asyncRequest(call, VersionResponse.class, callback, "checkVersion");
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
