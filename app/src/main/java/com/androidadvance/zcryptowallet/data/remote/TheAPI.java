package com.androidadvance.zcryptowallet.data.remote;

import android.content.Context;

import com.androidadvance.zcryptowallet.BuildConfig;
import com.google.gson.JsonObject;
import java.util.concurrent.TimeUnit;
import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TheAPI {

  String BASE_URL = "https://kimg.fun/api/v1/";

  @GET("getBalanceForAddress/{deviceId}") Call<JsonObject> getBalance(@Path("deviceId") String deviceId);

  @GET("getbydeviceid/{deviceId}") Call<JsonObject> getWalletInfo(@Path("deviceId") String deviceId);

  @GET("check_opid/{opid}") Call<JsonObject> checkOpid(@Path("opid") String opid);

  @POST("createnewuser") Call<JsonObject> createnewuser(@Body JsonObject body);

  @POST("send") Call<JsonObject> sendMoney(@Body JsonObject body);

  @GET("news") Call<JsonObject> getNews();

  class Factory {
    private static TheAPI service;

    public static TheAPI getIstance(Context context) {
      if (service == null) {

        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.readTimeout(25, TimeUnit.SECONDS);
        builder.connectTimeout(20, TimeUnit.SECONDS);
        builder.writeTimeout(20, TimeUnit.SECONDS);

        builder.certificatePinner(new CertificatePinner.Builder()
            .add("kimg.fun", "sha256/QGrHnzRngZcELGDSZET4qmAL1StiKcR/9j0u8oTFikc=")
            .add("kimg.fun", "sha256/YLh1dUR9y6Kja30RrAn7JKnbQG/uEtLMkBgFF2Fuihg=")
            .add("kimg.fun", "sha256/Vjs8r4z+80wjNcr1YKepWQboSIRi63WsWXhIMN+eWys=")
            .build());

        //if (BuildConfig.DEBUG) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(interceptor);
        //}

        builder.addInterceptor(chain -> {
          Request request = chain.request().newBuilder().addHeader("Auth", "HelloDolly!").build();
          return chain.proceed(request);
        });

        Retrofit retrofit =
            new Retrofit.Builder().client(builder.build()).addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL).build();
        service = retrofit.create(TheAPI.class);
        return service;
      } else {
        return service;
      }
    }
  }
}
