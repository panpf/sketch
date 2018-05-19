package me.panpf.sketch.sample.fragment

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.fragment_gaussian_blur.*
import me.panpf.sketch.display.TransitionImageDisplayer
import me.panpf.sketch.process.GaussianBlurImageProcessor
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.BaseFragment
import me.panpf.sketch.sample.BindContentView
import me.panpf.sketch.sample.R

@BindContentView(R.layout.fragment_gaussian_blur)
class GaussianBlurImageProcessorTestFragment : BaseFragment() {

    private var progress = 15

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 通过maxSize限制缩小读到内存的图片的尺寸，尺寸越小高斯模糊越快
        val metrics = resources.displayMetrics
        image_gaussianBlurFragment.options.setMaxSize(metrics.widthPixels / 4, metrics.heightPixels / 4)

        image_gaussianBlurFragment.options.displayer = TransitionImageDisplayer()

        seekBar_gaussianBlurFragment.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                text_gaussianBlurFragment.text = String.format("%d/%d", seekBar.progress, seekBar.max)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                progress = seekBar.progress
                apply()
            }
        })

        seekBar_gaussianBlurFragment.max = 100
        seekBar_gaussianBlurFragment.progress = progress

        apply()
    }

    private fun apply() {
        image_gaussianBlurFragment.options.processor = GaussianBlurImageProcessor.makeRadius(progress)
        image_gaussianBlurFragment.displayImage(AssetImage.MEI_NV)
    }
}
