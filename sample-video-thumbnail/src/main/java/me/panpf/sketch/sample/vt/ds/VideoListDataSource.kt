package me.panpf.sketch.sample.vt.ds

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PositionalDataSource
import android.content.Context
import android.provider.MediaStore
import me.panpf.sketch.sample.vt.bean.Status
import me.panpf.sketch.sample.vt.bean.VideoInfo

class VideoListDataSource constructor(context: Context,
                                      private val initStatus: MutableLiveData<Status>,
                                      private val pagingStatus: MutableLiveData<Status>) : PositionalDataSource<VideoInfo>() {
    private val appContext = context.applicationContext

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<VideoInfo>) {
        initStatus.postValue(Status.loading())

        val cursor = appContext.contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null)
        val count = cursor?.count ?: 0
        cursor?.let {
            cursor.close()
        }

        val dataList = loadData(params.requestedStartPosition, params.requestedLoadSize)

        Thread.sleep(1000)

        callback.onResult(dataList, params.requestedStartPosition, count)

        initStatus.postValue(Status.success())
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<VideoInfo>) {
        pagingStatus.postValue(Status.loading())

        Thread.sleep(1000)

        callback.onResult(loadData(params.startPosition, params.loadSize))

        pagingStatus.postValue(Status.success())
    }

    private fun loadData(startPosition: Int, pageSize: Int): List<VideoInfo> {
        val cursor = appContext.contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Video.Media.TITLE, MediaStore.Video.Media.DATA, MediaStore.Video.Media.SIZE,
                        MediaStore.Video.Media.DURATION, MediaStore.Video.Media.DATE_TAKEN,
                        MediaStore.Video.Media.MIME_TYPE), null, null,
                MediaStore.Video.Media.DATE_TAKEN + " DESC" + " limit " + startPosition + "," + pageSize)
        val list = ArrayList<VideoInfo>(cursor?.count ?: 0)
        cursor?.use {
            while (cursor.moveToNext()) {
                val video = VideoInfo()
                video.title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE))
                video.path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))
                video.mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE))
                video.size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE))
                video.duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)).toLong()
                video.date = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN))
                list.add(video)
            }
        }
        return list
    }

    class Factory constructor(context: Context, private val initStatus: MutableLiveData<Status>, private val pagingStatus: MutableLiveData<Status>)
        : DataSource.Factory<Int, VideoInfo>() {
        private val appContext = context.applicationContext

        override fun create(): DataSource<Int, VideoInfo> {
            return VideoListDataSource(appContext, initStatus, pagingStatus)
        }
    }
}

