package me.panpf.sketch.sample.ds

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import me.panpf.sketch.sample.apiService
import me.panpf.sketch.sample.bean.GiphyGif

class GiphyGifListPagingSource(private val context: Context) :
    PagingSource<Int, GiphyGif>() {

    override fun getRefreshKey(state: PagingState<Int, GiphyGif>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GiphyGif> {
        val pageStart = params.key ?: 0
        val pageSize = params.loadSize
        val response = try {
            context.apiService.giphy.search("young girl", pageStart, pageSize)
        } catch (e: Exception) {
            e.printStackTrace()
            return LoadResult.Error(e)
        }

        return if (response.isSuccessful) {
            val dataList = response.body()?.dataList ?: emptyList()
            val nextKey = if (dataList.isNotEmpty()) {
                pageStart + pageSize
            } else {
                null
            }
            LoadResult.Page(dataList, null, nextKey)
        } else {
            LoadResult.Error(Exception("Http coded error: code=${response.code()}. message=${response.message()}"))
        }
    }
}