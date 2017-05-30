package me.xiaopan.sketchsample.net;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetServices {
    private static UnsplashService unsplashService;

    private NetServices() {
    }

    public static UnsplashService unsplash() {
        if (unsplashService == null) {
            synchronized (NetServices.class) {
                if (unsplashService == null) {
                    Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.unsplash.com")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    unsplashService = retrofit.create(UnsplashService.class);
                }
            }
        }
        return unsplashService;
    }
}
