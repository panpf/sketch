package com.github.panpf.sketch.sample.ui.gallery

import android.os.Parcelable
import com.github.panpf.sketch.sample.ui.model.Photo
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
actual class PhotoPagerParams actual constructor(
    actual val photos: List<Photo>,
    actual val totalCount: Int,
    actual val startPosition: Int,
    actual val initialPosition: Int
) : Parcelable, java.io.Serializable
// PhotoPagerParams as constructor parameters of PhotoPagerScreen
// voyager will serialize the constructor parameters of PhotoPagerScreen
// On Android, you need to implement the java.io.Serializable interface