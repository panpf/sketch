package me.panpf.sketch.sample.vt.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import me.panpf.sketch.sample.vt.bean.BoundaryStatus
import me.panpf.sketch.sample.vt.bean.BoundaryStatusCallback
import me.panpf.sketch.sample.vt.bean.Status
import me.panpf.sketch.sample.vt.bean.VideoInfo
import me.panpf.sketch.sample.vt.ds.VideoListDataSource

class VideoListViewModel(application: Application) : AndroidViewModel(application) {
    val initStatus = MutableLiveData<Status>()
    val pagingStatus = MutableLiveData<Status>()
    val boundaryStatus = MutableLiveData<BoundaryStatus>()

    private val dataSourceFactory = VideoListDataSource.Factory(getApplication(), initStatus, pagingStatus)
    private val pagedListConfig = PagedList.Config.Builder()
            .setPageSize(10)
            .setInitialLoadSizeHint(20)
            .setPrefetchDistance(20)
            .setEnablePlaceholders(false)
            .build()
    val videoListing = LivePagedListBuilder<Int, VideoInfo>(dataSourceFactory, pagedListConfig)
            .setBoundaryCallback(BoundaryStatusCallback<VideoInfo>(boundaryStatus))
            .build()

    fun refresh() {
        videoListing.value?.dataSource?.invalidate()
    }
}