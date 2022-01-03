package com.github.panpf.sketch.sample.ds

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.panpf.sketch.sample.apiService
import com.github.panpf.sketch.sample.bean.PexelsPhoto

class PexelsImageListPagingSource(private val context: Context) :
    PagingSource<Int, PexelsPhoto>() {

    override fun getRefreshKey(state: PagingState<Int, PexelsPhoto>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PexelsPhoto> {
        val pageIndex = (params.key ?: 0).coerceAtLeast(1)
        val response = try {
            context.apiService.pexels.curated(pageIndex)
        } catch (e: Exception) {
            e.printStackTrace()
            return LoadResult.Error(e)
        }

        return if (response.isSuccessful) {
            val dataList = response.body()?.photos ?: emptyList()
            val nextKey = if (dataList.isNotEmpty()) {
                pageIndex + 1
            } else {
                null
            }
            LoadResult.Page(dataList, null, nextKey)
        } else {
            LoadResult.Error(Exception("Http coded error: code=${response.code()}. message=${response.message()}"))
        }
    }
}