package me.panpf.sketch.sample.vt.item

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import me.panpf.adapter.AssemblyRecyclerItem
import me.panpf.adapter.AssemblyRecyclerItemFactory
import me.panpf.sketch.SketchImageView
import me.panpf.sketch.display.TransitionImageDisplayer
import me.panpf.sketch.sample.vt.R
import me.panpf.sketch.sample.vt.ext.bindView
import me.panpf.sketch.sample.vt.util.VideoThumbnailUriModel

class VideoInfoItemFactory(private val listener: VideoInfoItemListener?) : AssemblyRecyclerItemFactory<VideoInfoItemFactory.VideoInfoItem>() {

    override fun isTarget(o: Any): Boolean {
        return o is me.panpf.sketch.sample.vt.bean.VideoInfo
    }

    override fun createAssemblyItem(viewGroup: ViewGroup): VideoInfoItem {
        return VideoInfoItem(R.layout.list_item_my_video, viewGroup)
    }

    interface VideoInfoItemListener {
        fun onClickVideo(position: Int, videoInfo: me.panpf.sketch.sample.vt.bean.VideoInfo)
    }

    inner class VideoInfoItem(itemLayoutId: Int, parent: ViewGroup) : AssemblyRecyclerItem<me.panpf.sketch.sample.vt.bean.VideoInfo>(itemLayoutId, parent) {
        private val iconImageView: SketchImageView by bindView(R.id.image_myVideoItem_icon)
        private val nameTextView: TextView by bindView(R.id.text_myVideoItem_name)
        private val sizeTextView: TextView by bindView(R.id.text_myVideoItem_size)
        private val dateTextView: TextView by bindView(R.id.text_myVideoItem_date)
        private val durationTextView: TextView by bindView(R.id.text_myVideoItem_duration)

        override fun onConfigViews(context: Context) {
            getItemView().setOnClickListener {
                listener?.onClickVideo(position, data)
            }

            iconImageView.onClickListener = View.OnClickListener { getItemView().performClick() }

            iconImageView.options
                    .setLoadingImage(R.drawable.image_loading).displayer = TransitionImageDisplayer()
        }

        override fun onSetData(i: Int, videoInfo: me.panpf.sketch.sample.vt.bean.VideoInfo) {
            iconImageView.displayImage(VideoThumbnailUriModel.makeUri(videoInfo.path ?: ""))
            nameTextView.text = videoInfo.title
            sizeTextView.text = videoInfo.getTempFormattedSize(sizeTextView.context)
            dateTextView.text = videoInfo.tempFormattedDate
            durationTextView.text = videoInfo.tempFormattedDuration
        }
    }
}
