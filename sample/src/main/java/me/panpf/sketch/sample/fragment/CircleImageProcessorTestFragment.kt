package me.panpf.sketch.sample.fragment

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_reflection.*
import me.panpf.sketch.display.TransitionImageDisplayer
import me.panpf.sketch.process.CircleImageProcessor
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.BaseFragment
import me.panpf.sketch.sample.BindContentView
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.widget.SampleImageView
import me.panpf.sketch.util.SketchUtils

@BindContentView(R.layout.fragment_reflection)
class CircleImageProcessorTestFragment : BaseFragment() {
    val imageView: SampleImageView by lazy {image_reflectionFragment}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 缩小图片，处理速度更快，更少的内存消耗
        val metrics = resources.displayMetrics
        imageView.options.setMaxSize(metrics.widthPixels / 2, metrics.heightPixels / 2)

        val layoutParams = imageView.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.bottomMargin = SketchUtils.dp2px(activity, 16)
        layoutParams.rightMargin = layoutParams.bottomMargin
        layoutParams.topMargin = layoutParams.rightMargin
        layoutParams.leftMargin = layoutParams.topMargin
        imageView.layoutParams = layoutParams

        imageView.options.processor = CircleImageProcessor.getInstance()
        imageView.options.displayer = TransitionImageDisplayer()
        imageView.displayImage(AssetImage.MEI_NV)
    }
}
