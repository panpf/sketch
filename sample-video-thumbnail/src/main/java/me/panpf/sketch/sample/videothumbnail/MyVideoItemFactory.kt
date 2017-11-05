package me.panpf.sketch.sample.videothumbnail

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import me.xiaopan.assemblyadapter.AssemblyRecyclerItem
import me.xiaopan.assemblyadapter.AssemblyRecyclerItemFactory
import me.panpf.sketch.SketchImageView
import me.panpf.sketch.display.TransitionImageDisplayer

class MyVideoItemFactory(private val listener: MyVideoItemListener?) : AssemblyRecyclerItemFactory<MyVideoItemFactory.MyVideoItem>() {

    override fun isTarget(o: Any): Boolean {
        return o is VideoItem
    }

    override fun createAssemblyItem(viewGroup: ViewGroup): MyVideoItem {
        return MyVideoItem(R.layout.list_item_my_video, viewGroup)
    }

    interface MyVideoItemListener {
        fun onClickVideo(position: Int, videoItem: VideoItem)
    }

    inner class MyVideoItem(itemLayoutId: Int, parent: ViewGroup) : AssemblyRecyclerItem<VideoItem>(itemLayoutId, parent) {
        val iconImageView: SketchImageView by bindView(R.id.image_myVideoItem_icon)
        val nameTextView: TextView by bindView(R.id.text_myVideoItem_name)
        val sizeTextView: TextView by bindView(R.id.text_myVideoItem_size)
        val dateTextView: TextView by bindView(R.id.text_myVideoItem_date)
        val durationTextView: TextView by bindView(R.id.text_myVideoItem_duration)

        override fun onConfigViews(context: Context) {
            getItemView().setOnClickListener {
                listener?.onClickVideo(position, data)
            }

            iconImageView.onClickListener = View.OnClickListener { getItemView().performClick() }

            iconImageView.options
                    .setLoadingImage(R.drawable.image_loading).displayer = TransitionImageDisplayer()
        }

        override fun onSetData(i: Int, videoItem: VideoItem) {
            iconImageView.displayImage(VideoThumbnailUriModel.makeUri(videoItem.path ?: ""))
            nameTextView.text = videoItem.title
            sizeTextView.text = videoItem.getTempFormattedSize(sizeTextView.context)
            dateTextView.text = videoItem.tempFormattedDate
            durationTextView.text = videoItem.tempFormattedDuration
        }
    }
}
