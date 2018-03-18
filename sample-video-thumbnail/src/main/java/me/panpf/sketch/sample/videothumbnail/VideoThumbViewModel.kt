package me.panpf.sketch.sample.videothumbnail

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData

class VideoThumbViewModel(application: Application) : AndroidViewModel(application) {
    val videoList: MutableLiveData<List<VideoItem>> = MutableLiveData()

    init {
        videoList.value = null
        refreshVideoList()
    }

    fun refreshVideoList() {
        DataRepository.loadVideoList(getApplication(), object : Callback<List<VideoItem>> {
            override fun onCompleted(t: List<VideoItem>?) {
                videoList.value = t
            }
        })
    }
}
