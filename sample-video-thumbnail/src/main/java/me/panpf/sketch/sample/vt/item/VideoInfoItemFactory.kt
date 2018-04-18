package me.panpf.sketch.sample.vt.item

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import me.panpf.adapter.AssemblyItem
import me.panpf.adapter.AssemblyItemFactory
import me.panpf.sketch.SketchImageView
import me.panpf.sketch.display.TransitionImageDisplayer
import me.panpf.sketch.sample.vt.R
import me.panpf.sketch.sample.vt.ext.bindView
import me.panpf.sketch.sample.vt.util.VideoThumbnailUriModel

class VideoInfoItemFactory(private val listener: VideoInfoItemListener?) : AssemblyItemFactory<VideoInfoItemFactory.VideoInfoItem>() {

    override fun isTarget(o: Any): Boolean {
        return o is me.panpf.sketch.sample.vt.bean.VideoInfo
    }

    override fun createAssemblyItem(viewGroup: ViewGroup): VideoInfoItem {
        return VideoInfoItem(R.layout.list_item_my_video, viewGroup, listener)
    }

    interface VideoInfoItemListener {
        fun onClickVideo(position: Int, videoInfo: me.panpf.sketch.sample.vt.bean.VideoInfo)
    }

    class VideoInfoItem(itemLayoutId: Int, parent: ViewGroup, private val listener: VideoInfoItemListener?)
        : AssemblyItem<me.panpf.sketch.sample.vt.bean.VideoInfo>(itemLayoutId, parent) {
        private val iconImageView: SketchImageView by bindView(R.id.image_myVideoItem_icon)
        private val noTextView: TextView by bindView(R.id.text_myVideoItem_no)
        private val nameTextView: TextView by bindView(R.id.text_myVideoItem_name)
        private val sizeTextView: TextView by bindView(R.id.text_myVideoItem_size)
        private val dateTextView: TextView by bindView(R.id.text_myVideoItem_date)
        private val durationTextView: TextView by bindView(R.id.text_myVideoItem_duration)

        override fun onConfigViews(context: Context) {
            itemView.setOnClickListener {
                listener?.onClickVideo(position, data)
            }

            iconImageView.onClickListener = View.OnClickListener { getItemView().performClick() }

            iconImageView.options
                    .setLoadingImage(R.drawable.image_loading).displayer = TransitionImageDisplayer()
        }

        override fun onSetData(i: Int, videoInfo: me.panpf.sketch.sample.vt.bean.VideoInfo) {
            noTextView.text = (i+1).toString()
            iconImageView.displayImage(VideoThumbnailUriModel.makeUri(videoInfo.path ?: ""))
            nameTextView.text = videoInfo.title
            sizeTextView.text = videoInfo.getTempFormattedSize(sizeTextView.context)
            dateTextView.text = videoInfo.tempFormattedDate
            durationTextView.text = videoInfo.tempFormattedDuration
        }
    }
}
