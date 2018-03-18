package me.panpf.sketch.sample.vt.repo

import android.app.Application
import android.os.AsyncTask
import android.provider.MediaStore
import me.panpf.sketch.sample.vt.bean.VideoInfo

object DataRepository {
    fun loadVideoList(application: Application, callback: Callback<List<VideoInfo>>) {
        LoadVideoListTask(application, callback).execute()
    }
}

private class LoadVideoListTask constructor(val application: Application, val callback: Callback<List<VideoInfo>>)
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
        callback.onCompleted(videoList)
    }
}


interface Callback<in DATA> {
    fun onCompleted(t: DATA?)
}
