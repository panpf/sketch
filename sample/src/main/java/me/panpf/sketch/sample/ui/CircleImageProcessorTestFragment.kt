package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_reflection.*
import me.panpf.javaxkt.util.requireNotNull
import me.panpf.sketch.display.TransitionImageDisplayer
import me.panpf.sketch.process.CircleImageProcessor
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.base.BaseFragment
import me.panpf.sketch.sample.base.BindContentView
import me.panpf.sketch.util.SketchUtils

@BindContentView(R.layout.fragment_reflection)
class CircleImageProcessorTestFragment : BaseFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 缩小图片，处理速度更快，更少的内存消耗
        val metrics = resources.displayMetrics
        image_reflectionFragment.options.setMaxSize(metrics.widthPixels / 2, metrics.heightPixels / 2)

        val layoutParams = image_reflectionFragment.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.bottomMargin = SketchUtils.dp2px(activity.requireNotNull(), 16)
        layoutParams.rightMargin = layoutParams.bottomMargin
        layoutParams.topMargin = layoutParams.rightMargin
        layoutParams.leftMargin = layoutParams.topMargin
        image_reflectionFragment.layoutParams = layoutParams

        image_reflectionFragment.options.processor = CircleImageProcessor.getInstance()
        image_reflectionFragment.options.displayer = TransitionImageDisplayer()
        image_reflectionFragment.displayImage(AssetImage.MEI_NV)
    }
}
