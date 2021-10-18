package com.github.panpf.sketch.sample.bean

import androidx.lifecycle.MutableLiveData

class CheckMenu(
    val title: String,
    private val data: MutableLiveData<Boolean>,
) {

    val isChecked: Boolean
        get() = data.value ?: false

    fun onClick() {
        data.postValue(!(data.value ?: false))
    }
}
