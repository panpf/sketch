package com.github.panpf.sketch.sample.ui.test.transform

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.panpf.sketch.sample.ui.base.LifecycleAndroidViewModel
import com.github.panpf.sketch.sample.util.ExifOrientationTestFileHelper
import com.github.panpf.sketch.sample.util.ExifOrientationTestFileHelper.TestFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExifOrientationTestPagerViewModel(application1: Application) :
    LifecycleAndroidViewModel(application1) {

    val data = MutableLiveData<List<TestFile>>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            data.postValue(ExifOrientationTestFileHelper(application1, "sample.jpeg").files())
        }
    }
}