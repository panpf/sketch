package me.panpf.sketch.sample.vt.bean

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import android.text.format.Formatter
import me.panpf.javaxkt.util.formatYMDHM

class VideoInfo {
    var title: String? = null
    var path: String? = null
    var mimeType: String? = null
    var duration: Long = 0
    var date: Long = 0
    var size: Long = 0

    var tempFormattedSize: String? = null

    val tempFormattedDuration: String by lazy {
        val second = duration / 1000
        val secondsRemaining = second % 60
        val minute = second / 60
        val builder = StringBuilder()
        when {
            minute <= 0 -> builder.append("00")
            minute < 10 -> builder.append("0$minute")
            else -> builder.append(minute)
        }

        builder.append(":")
        when {
            secondsRemaining <= 0 -> builder.append("00")
            secondsRemaining < 10 -> builder.append("0$secondsRemaining")
            else -> builder.append(secondsRemaining)
        }
        builder.toString()
    }

    val tempFormattedDate: String by lazy { date.formatYMDHM() }

    fun getTempFormattedSize(context: Context): String {
        if (tempFormattedSize == null) {
            tempFormattedSize = Formatter.formatFileSize(context, size)
        }
        return tempFormattedSize!!
    }

    override fun toString(): String {
        return "VideoInfo(title=$title, path=$path, mimeType=$mimeType, duration=$duration, date=$date, size=$size, tempFormattedSize=$tempFormattedSize)"
    }

    class DiffCallback : DiffUtil.ItemCallback<VideoInfo>() {
        override fun areItemsTheSame(oldItem: VideoInfo, newItem: VideoInfo): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: VideoInfo, newItem: VideoInfo): Boolean {
            return oldItem.path.equals(newItem.path)
        }
    }
}
