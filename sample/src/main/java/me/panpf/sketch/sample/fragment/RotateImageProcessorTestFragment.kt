package me.panpf.sketch.sample.fragment

import android.os.Bundle
import android.view.View
import android.widget.Button
import me.panpf.sketch.display.TransitionImageDisplayer
import me.panpf.sketch.process.RotateImageProcessor
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.BaseFragment
import me.panpf.sketch.sample.BindContentView
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.widget.SampleImageView
import me.panpf.sketch.sample.bindView

@BindContentView(R.layout.fragment_rotate)
class RotateImageProcessorTestFragment : BaseFragment() {
    val imageView: SampleImageView by bindView(R.id.image_rotateFragment)
    val rotateButton: Button by bindView(R.id.button_rotateFragment)

    private var degrees = 45

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 缩小图片，处理速度更快，更少的内存消耗
        val metrics = resources.displayMetrics
        imageView.options.setMaxSize(metrics.widthPixels / 2, metrics.heightPixels / 2)

        imageView.options.displayer = TransitionImageDisplayer()

        rotateButton.setOnClickListener {
            degrees += 45
            apply()
        }

        apply()
    }

    private fun apply() {
        imageView.options.processor = RotateImageProcessor(degrees)
        imageView.displayImage(AssetImage.MEI_NV)
    }
}
