package me.xiaopan.sketchsample.net;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetServices {
    private static UnsplashService unsplashService;
    private static BaiduImageService baiduService;

    private NetServices() {
    }

    public static UnsplashService unsplash() {
        if (unsplashService == null) {
            synchronized (NetServices.class) {
                if (unsplashService == null) {
                    unsplashService = new Retrofit.Builder()
                            .baseUrl("https://api.unsplash.com")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build().create(UnsplashService.class);
                }
            }
        }
        return unsplashService;
    }

    public static BaiduImageService baiduImage() {
        if (baiduService == null) {
            synchronized (NetServices.class) {
                if (baiduService == null) {
                    baiduService = new Retrofit.Builder()
                            .baseUrl("http://image.baidu.com")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build().create(BaiduImageService.class);
                }
            }
        }
        return baiduService;
    }
}
