package me.xiaopan.sketchsample.net;

import me.xiaopan.sketchsample.bean.BaiduImageSearchResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BaiduImageService {

    @GET("search/acjson?tn=resultjson_com&ipn=rj&ct=201326592&is=&fp=result&cl=2&lm=-1&ie=utf-8&oe=utf-8&adpicid=&st=-1&z=&ic=0&s=&se=&tab=&width=&height=&face=0&istype=2&qc=&nc=1&fr=&gsm=3c&1437824884073=")
    Call<BaiduImageSearchResult> searchPhoto(
            @Query("word") String word,
            @Query("queryWord") String queryWord,
            @Query("pn") int pageStart,
            @Query("rn") int pageSize);
}
