package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_rotate.*
import me.panpf.sketch.display.TransitionImageDisplayer
import me.panpf.sketch.process.RotateImageProcessor
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.BaseFragment
import me.panpf.sketch.sample.BindContentView
import me.panpf.sketch.sample.R

@BindContentView(R.layout.fragment_rotate)
class RotateImageProcessorTestFragment : BaseFragment() {

    private var degrees = 45

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 缩小图片，处理速度更快，更少的内存消耗
        val metrics = resources.displayMetrics
        image_rotateFragment.options.setMaxSize(metrics.widthPixels / 2, metrics.heightPixels / 2)

        image_rotateFragment.options.displayer = TransitionImageDisplayer()

        button_rotateFragment.setOnClickListener {
            degrees += 45
            apply()
        }

        apply()
    }

    private fun apply() {
        image_rotateFragment.options.processor = RotateImageProcessor(degrees)
        image_rotateFragment.displayImage(AssetImage.MEI_NV)
    }
}
