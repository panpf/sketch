package com.github.panpf.sketch.sample.ui.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.images.ExifOrientation
import com.github.panpf.sketch.sample.ui.model.PhotoTestItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExifOrientationTestViewModel : ViewModel() {

    private val _data = MutableStateFlow<List<PhotoTestItem>>(emptyList())
    val data: StateFlow<List<PhotoTestItem>> = _data

    init {
        viewModelScope.launch {
            _data.value = ComposeResImageFiles.clockExifs.map {
                PhotoTestItem(
                    title = ExifOrientation.name(it.exifOrientation),
                    photoUri = it.uri,
                )
            }
        }
    }
}