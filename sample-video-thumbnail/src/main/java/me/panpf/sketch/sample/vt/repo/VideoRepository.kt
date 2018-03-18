package me.panpf.sketch.sample.vt.repo

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.os.AsyncTask
import android.provider.MediaStore
import me.panpf.sketch.sample.vt.bean.VideoInfo

object DataRepository {
    fun loadVideoList(application: Application): MutableLiveData<List<VideoInfo>> {
        val videoListLiveData = MutableLiveData<List<VideoInfo>>()
        LoadVideoListTask(application, videoListLiveData).execute()
        return videoListLiveData
    }

    fun refreshList(application: Application, videoListLiveData: MutableLiveData<List<VideoInfo>>) {
        LoadVideoListTask(application, videoListLiveData).execute()
    }
}

private class LoadVideoListTask constructor(
        val application: Application, val videoListLiveData: MutableLiveData<List<VideoInfo>>)
    : AsyncTask<Void, Int, List<VideoInfo>>() {

    override fun doInBackground(params: Array<Void>): List<VideoInfo>? {
        val cursor = application.contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Video.Media.TITLE, MediaStore.Video.Media.DATA, MediaStore.Video.Media.SIZE,
                        MediaStore.Video.Media.DURATION, MediaStore.Video.Media.DATE_TAKEN,
                        MediaStore.Video.Media.MIME_TYPE), null, null,
                MediaStore.Video.Media.DATE_TAKEN + " DESC") ?: return null

        val imagePathList = ArrayList<VideoInfo>(cursor.count)
        while (cursor.moveToNext()) {
            val video = VideoInfo()
            video.title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE))
            video.path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))
            video.mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE))
            video.size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE))
            video.duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)).toLong()
            video.date = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN))
            imagePathList.add(video)
        }
        cursor.close()
        return imagePathList
    }

    override fun onPostExecute(videoList: List<VideoInfo>?) {
        videoListLiveData.postValue(videoList)
    }
}
