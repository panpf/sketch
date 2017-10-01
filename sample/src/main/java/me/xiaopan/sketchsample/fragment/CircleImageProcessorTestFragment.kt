package me.xiaopan.sketchsample.fragment

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import me.xiaopan.sketch.display.TransitionImageDisplayer
import me.xiaopan.sketch.process.CircleImageProcessor
import me.xiaopan.sketch.util.SketchUtils
import me.xiaopan.sketchsample.AssetImage
import me.xiaopan.sketchsample.BaseFragment
import me.xiaopan.sketchsample.BindContentView
import me.xiaopan.sketchsample.R
import me.xiaopan.sketchsample.widget.SampleImageView
import me.xiaopan.ssvt.bindView

@BindContentView(R.layout.fragment_reflection)
class CircleImageProcessorTestFragment : BaseFragment() {
    val imageView: SampleImageView by bindView(R.id.image_reflectionFragment)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
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
