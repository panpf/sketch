package me.panpf.sketch.sample.fragment

import android.os.Bundle
import android.view.View
import me.xiaopan.sketch.display.TransitionImageDisplayer
import me.xiaopan.sketch.process.ReflectionImageProcessor
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.BaseFragment
import me.panpf.sketch.sample.BindContentView
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.widget.SampleImageView
import me.panpf.sketch.sample.bindView

@BindContentView(R.layout.fragment_reflection)
class ReflectionImageProcessorTestFragment : BaseFragment() {
    val imageView: SampleImageView by bindView(R.id.image_reflectionFragment)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 缩小图片，处理速度更快，更少的内存消耗
        val metrics = resources.displayMetrics
        imageView.options.setMaxSize(metrics.widthPixels / 2, metrics.heightPixels / 2)

        imageView.options.processor = ReflectionImageProcessor()
        imageView.options.displayer = TransitionImageDisplayer()
        imageView.displayImage(AssetImage.MEI_NV)
    }
}
