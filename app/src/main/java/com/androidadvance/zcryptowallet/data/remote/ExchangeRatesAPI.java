package com.androidadvance.zcryptowallet.data.remote;

import android.content.Context;
import com.androidadvance.zcryptowallet.BuildConfig;
import com.google.gson.JsonObject;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public interface ExchangeRatesAPI {

  String BASE_URL = "https://min-api.cryptocompare.com/data/";

  @GET("price?fsym=ZEN&tsyms=USD") Call<JsonObject> getExchangeRate();  //response: {"USD":52.07}


  class Factory {
    private static ExchangeRatesAPI service;

    public static ExchangeRatesAPI getIstance(Context context) {
      if (service == null) {

        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.readTimeout(25, TimeUnit.SECONDS);
        builder.connectTimeout(20, TimeUnit.SECONDS);
        builder.writeTimeout(20, TimeUnit.SECONDS);

        if (BuildConfig.DEBUG) {
          HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
          interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
          builder.addInterceptor(interceptor);
        }

        int cacheSize = 1024 * 1024; // 1 MiB
        Cache cache = new Cache(context.getCacheDir(), cacheSize);
        builder.cache(cache);

        Retrofit retrofit =
            new Retrofit.Builder().client(builder.build()).addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL).build();
        service = retrofit.create(ExchangeRatesAPI.class);
        return service;
      } else {
        return service;
      }
    }
  }
}
