package me.panpf.sketch.sample.net;

import me.panpf.sketch.sample.bean.TenorSearchResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TenorService {

    @GET("/v1/search?key=7NBSB6BGSA7A&locale=zh_rCN&contentfilter=high&media_filter=minimal&ar_range=standard")
    Call<TenorSearchResponse> search(
            @Query("q") String queryWord,
            @Query("pos") int pageStart,
            @Query("limit") int pageSize);
}
