package me.panpf.sketch.sample.fragment

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_reflection.*
import me.panpf.sketch.display.TransitionImageDisplayer
import me.panpf.sketch.process.ReflectionImageProcessor
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.BaseFragment
import me.panpf.sketch.sample.BindContentView
import me.panpf.sketch.sample.R

@BindContentView(R.layout.fragment_reflection)
class ReflectionImageProcessorTestFragment : BaseFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 缩小图片，处理速度更快，更少的内存消耗
        val metrics = resources.displayMetrics
        image_reflectionFragment.options.setMaxSize(metrics.widthPixels / 2, metrics.heightPixels / 2)

        image_reflectionFragment.options.processor = ReflectionImageProcessor()
        image_reflectionFragment.options.displayer = TransitionImageDisplayer()
        image_reflectionFragment.displayImage(AssetImage.MEI_NV)
    }
}
