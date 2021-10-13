package me.panpf.sketch.sample.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShowingImageChangedViewModel : ViewModel() {
    val imageChangedData = MutableLiveData<String>()
}