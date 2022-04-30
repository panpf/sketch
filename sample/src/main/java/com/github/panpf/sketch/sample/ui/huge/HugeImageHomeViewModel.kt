package com.github.panpf.sketch.sample.ui.huge

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HugeImageListViewModel : ViewModel() {

    val layoutData = MutableLiveData(Layout.COLUMN)

    fun changeTab(tab: Layout) {
        if (tab != layoutData.value!!) {
            layoutData.postValue(tab)
        }
    }
}

enum class Layout {
    COLUMN, ROW
}