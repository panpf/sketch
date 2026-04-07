package com.github.panpf.sketch.sample.ui.model

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

class VideoInfoDiffCallback : DiffUtil.ItemCallback<VideoInfo>() {

    override fun areItemsTheSame(oldItem: VideoInfo, newItem: VideoInfo): Boolean {
        @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
        return (oldItem!!)::class == (newItem!!)::class && oldItem.uri == newItem.uri
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: VideoInfo, newItem: VideoInfo): Boolean {
        return oldItem == newItem
    }
}