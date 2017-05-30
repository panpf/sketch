package me.xiaopan.sketchsample.net;

import java.util.List;

import me.xiaopan.sketchsample.bean.UnsplashImage;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UnsplashService {
    @GET("photos/?client_id=72acaf7c7bd1230a9828557bdf68d6f598a85c90df5ecd43bb0cc07336ea4f93&&per_page=20")
    Call<List<UnsplashImage>> listPhotos(@Query("page") int pageIndex);
}
