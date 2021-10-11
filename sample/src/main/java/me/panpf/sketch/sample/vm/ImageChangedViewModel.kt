package me.panpf.sketch.sample.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ImageChangedViewModel : ViewModel() {
    val imageChangedData = MutableLiveData<String>()
}