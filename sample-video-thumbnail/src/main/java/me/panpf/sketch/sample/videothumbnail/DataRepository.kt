package me.panpf.sketch.sample.videothumbnail

import android.app.Application
import android.os.AsyncTask
import android.provider.MediaStore

object DataRepository {
    fun loadVideoList(application: Application, callback: Callback<List<VideoItem>>) {
        LoadVideoListTask(application, callback).execute()
    }
}

private class LoadVideoListTask constructor(val application: Application, val callback: Callback<List<VideoItem>>)
    : AsyncTask<Void, Int, List<VideoItem>>() {

    override fun doInBackground(params: Array<Void>): List<VideoItem>? {
        val cursor = application.contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Video.Media.TITLE, MediaStore.Video.Media.DATA, MediaStore.Video.Media.SIZE,
                        MediaStore.Video.Media.DURATION, MediaStore.Video.Media.DATE_TAKEN,
                        MediaStore.Video.Media.MIME_TYPE), null, null,
                MediaStore.Video.Media.DATE_TAKEN + " DESC") ?: return null

        val imagePathList = ArrayList<VideoItem>(cursor.count)
        while (cursor.moveToNext()) {
            val video = VideoItem()
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

    override fun onPostExecute(videoList: List<VideoItem>?) {
        callback.onCompleted(videoList)
    }
}


interface Callback<in DATA> {
    fun onCompleted(t: DATA?)
}
