package com.tea.store.http;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by ZTMIDGO 2023/1/22
 */
public interface ServiceHttp {
    @FormUrlEncoded
    @POST(Url.CATEGORY)
    Call<ResponseBody> category(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST(Url.APP_LIST)
    Call<ResponseBody> appList(@FieldMap Map<String, String> map, @QueryMap Map<String, String> query);

    @FormUrlEncoded
    @POST(Url.APP_DETIALS)
    Call<ResponseBody> getAppDetial(@FieldMap Map<String, String> map);

    @GET(Url.CHECK_VERSION)
    Call<ResponseBody> checkVersion(@QueryMap Map<String, String> map);
}
