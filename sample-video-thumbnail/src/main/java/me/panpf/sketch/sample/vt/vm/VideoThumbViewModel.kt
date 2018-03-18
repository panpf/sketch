package me.panpf.sketch.sample.vt.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import me.panpf.sketch.sample.vt.bean.VideoInfo
import me.panpf.sketch.sample.vt.repo.DataRepository

class VideoThumbViewModel(application: Application) : AndroidViewModel(application) {
    val videoList: MutableLiveData<List<VideoInfo>> = DataRepository.loadVideoList(getApplication())

    fun refreshVideoList() {
        DataRepository.refreshList(getApplication(), videoList)
    }
}
