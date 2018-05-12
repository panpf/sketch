package me.panpf.sketch.sample.fragment

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_gaussian_blur.*
import me.panpf.sketch.display.TransitionImageDisplayer
import me.panpf.sketch.process.GaussianBlurImageProcessor
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.BaseFragment
import me.panpf.sketch.sample.BindContentView
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.widget.SampleImageView

@BindContentView(R.layout.fragment_gaussian_blur)
class GaussianBlurImageProcessorTestFragment : BaseFragment() {
    val imageView: SampleImageView by lazy {image_gaussianBlurFragment}
    val seekBar: SeekBar by lazy {seekBar_gaussianBlurFragment}
    val progressTextView: TextView by lazy {text_gaussianBlurFragment}

    private var progress = 15

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 通过maxSize限制缩小读到内存的图片的尺寸，尺寸越小高斯模糊越快
        val metrics = resources.displayMetrics
        imageView.options.setMaxSize(metrics.widthPixels / 4, metrics.heightPixels / 4)

        imageView.options.displayer = TransitionImageDisplayer()

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressTextView.text = String.format("%d/%d", seekBar.progress, seekBar.max)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                progress = seekBar.progress
                apply()
            }
        })

        seekBar.max = 100
        seekBar.progress = progress

        apply()
    }

    private fun apply() {
        imageView.options.processor = GaussianBlurImageProcessor.makeRadius(progress)
        imageView.displayImage(AssetImage.MEI_NV)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
