package com.github.panpf.sketch.sample.ui.gallery

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.github.panpf.sketch.sample.ui.model.Photo

class PhotoDiffCallback : DiffUtil.ItemCallback<Photo>() {

    override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
        return (oldItem!!)::class == (newItem!!)::class && oldItem.originalUrl == newItem.originalUrl
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        return oldItem == newItem
    }
}