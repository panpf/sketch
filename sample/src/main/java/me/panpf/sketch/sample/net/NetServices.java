package me.panpf.sketch.sample.net;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetServices {
    private static UnsplashService unsplashService;
    private static GiphyService giphyService;

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

    public static GiphyService giphy() {
        if (giphyService == null) {
            synchronized (NetServices.class) {
                if (giphyService == null) {
                    giphyService = new Retrofit.Builder()
                            .baseUrl("https://api.giphy.com")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build().create(GiphyService.class);
                }
            }
        }
        return giphyService;
    }
}
