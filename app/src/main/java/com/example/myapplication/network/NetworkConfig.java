package com.example.myapplication.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class NetworkConfig {
    // 支持多种网络配置
    private static final String BASE_URL_EMULATOR = "http://10.0.2.2:8080/api/"; // Android模拟器访问本地服务器
    private static final String BASE_URL_LOCALHOST = "http://localhost:8080/api/"; // 本地服务器
    private static final String BASE_URL_127 = "http://127.0.0.1:8080/api/"; // 127.0.0.1地址
    private static final String BASE_URL = BASE_URL_EMULATOR; // 使用10.0.2.2地址，Android模拟器访问本地服务器
    
    private static Retrofit retrofit;
    private static ApiService apiService;

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            // 创建日志拦截器
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // 创建OkHttpClient
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            // 创建Retrofit实例
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        if (apiService == null) {
            apiService = getRetrofit().create(ApiService.class);
        }
        return apiService;
    }
}
