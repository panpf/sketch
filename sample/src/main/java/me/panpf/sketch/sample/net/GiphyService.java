package me.panpf.sketch.sample.net;

import me.panpf.sketch.sample.bean.GiphySearchResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GiphyService {

    @GET("/v1/gifs/search?type=gifs&sort=&api_key=Gc7131jiJuvI7IdN0HZ1D7nh0ow5BU6g&pingback_id=17c5f87f46b18d99")
    Call<GiphySearchResponse> search(
            @Query("q") String queryWord,
            @Query("offset") int pageStart,
            @Query("limit") int pageSize);
}
