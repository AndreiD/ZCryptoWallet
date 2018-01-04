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

  String BASE_URL = "http://kimg.fun/api/v1/";

  @GET("getBalanceForAddress/{deviceId}") Call<JsonObject> getBalance(@Path("deviceId") String deviceId);

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

        builder.certificatePinner(new CertificatePinner.Builder().add("*.androidadvance.com", "sha256/RqzElicVPA6LkKm9HblOvNOUqWmD+4zNXcRb+WjcaAE=")
            .add("*.xxxxxx.com", "sha256/8Rw90Ej3Ttt8RRkrg+WYDS9n7IS03bk5bjP/UXPtaY8=")
            .add("*.xxxxxxx.com", "sha256/Ko8tivDrEjiY90yGasP6ZpBU4jwXvHqVvQI0GS3GNdA=")
            .add("*.xxxxxxx.com", "sha256/VjLZe/p3W/PJnd6lL8JVNBCGQBZynFLdZSTIqcO0SJ8=")
            .build());

        if (BuildConfig.DEBUG) {
          HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
          interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
          builder.addInterceptor(interceptor);
        }

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
