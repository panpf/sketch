package com.github.panpf.sketch.sample.ds

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.panpf.sketch.sample.apiService
import com.github.panpf.sketch.sample.bean.Photo

class GiphyGifListPagingSource(private val context: Context) :
    PagingSource<Int, Photo>() {

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val pageStart = params.key ?: 0
        val pageSize = params.loadSize
        val response = try {
            context.apiService.giphy.search("young girl", pageStart, pageSize)
        } catch (e: Exception) {
            e.printStackTrace()
            return LoadResult.Error(e)
        }

        return if (response.isSuccessful) {
            val dataList = response.body()?.dataList?.map {
                Photo(
                    originalUrl = it.images.original.url,
                    thumbnailUrl = it.images.previewGif.url,
                    middenUrl = null,
                    width = it.images.original.width.toInt(),
                    height = it.images.original.height.toInt()
                )
            } ?: emptyList()
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