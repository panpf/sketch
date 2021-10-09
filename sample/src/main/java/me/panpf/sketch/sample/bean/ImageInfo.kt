package me.panpf.sketch.sample.bean

import android.content.Context
import android.text.format.Formatter
import com.github.panpf.assemblyadapter.recycler.DiffKey
import com.github.panpf.tools4j.date.Datex
import com.github.panpf.tools4j.date.ktx.format
import com.github.panpf.tools4j.date.ktx.toDate

class ImageInfo(
    val title: String?,
    val path: String,
    val mimeType: String,
    val date: Long,
    val size: Long,
) : DiffKey {

    override val diffKey = "VideoInfo-$path"

    private var tempFormattedSize: String? = null

    val tempFormattedDate: String by lazy {
        date.toDate().format(Datex.yMdHm)
    }

    fun getTempFormattedSize(context: Context): String {
        if (tempFormattedSize == null) {
            tempFormattedSize = Formatter.formatFileSize(context, size)
        }
        return tempFormattedSize!!
    }

    override fun toString(): String {
        return "ImageInfo(title=$title, path=$path, mimeType=$mimeType, date=$date, size=$size, tempFormattedSize=$tempFormattedSize)"
    }
}
