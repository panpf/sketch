package me.panpf.sketch.sample.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.fragment_wrapped.*
import me.panpf.sketch.display.TransitionImageDisplayer
import me.panpf.sketch.process.MaskImageProcessor
import me.panpf.sketch.process.RotateImageProcessor
import me.panpf.sketch.process.RoundRectImageProcessor
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.base.BaseFragment
import me.panpf.sketch.sample.base.BindContentView
import me.panpf.sketch.sample.R

@BindContentView(R.layout.fragment_wrapped)
class WrappedImageProcessorTestFragment : BaseFragment() {

    private var roundRectRadiusProgress = 30
    private var maskAlphaProgress = 45
    private var rotateProgress = 45

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 缩小图片，处理速度更快，更少的内存消耗
        val metrics = resources.displayMetrics
        image_wrappedFragment.options.setMaxSize(metrics.widthPixels / 2, metrics.heightPixels / 2)

        image_wrappedFragment.options.displayer = TransitionImageDisplayer()

        seekBar_wrappedFragment_width.max = 100
        seekBar_wrappedFragment_width.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (progress < 20) {
                    seekBar_wrappedFragment_width.progress = 20
                    return
                }

                val width = (seekBar_wrappedFragment_width.progress / 100f * 1000).toInt()
                text_wrappedFragment_width.text = String.format("%d/%d", width, 1000)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                roundRectRadiusProgress = seekBar_wrappedFragment_width.progress
                apply()
            }
        })
        seekBar_wrappedFragment_width.progress = roundRectRadiusProgress

        seekBar_wrappedFragment_height.max = 100
        seekBar_wrappedFragment_height.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (progress < 20) {
                    seekBar_wrappedFragment_height.progress = 20
                    return
                }
                val height = (seekBar_wrappedFragment_height.progress / 100f * 1000).toInt()
                text_wrappedFragment_height.text = String.format("%d/%d", height, 1000)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                maskAlphaProgress = seekBar_wrappedFragment_height.progress
                apply()
            }
        })
        seekBar_wrappedFragment_height.progress = maskAlphaProgress

        button_wrappedFragment.setOnClickListener {
            rotateProgress += 45
            apply()
        }

        apply()
    }

    private fun apply() {
        val roundRectImageProcessor = RoundRectImageProcessor(roundRectRadiusProgress.toFloat())
        val rotateImageProcessor = RotateImageProcessor(rotateProgress, roundRectImageProcessor)

        val alpha = (maskAlphaProgress.toFloat() / 100 * 255).toInt()
        val maskColor = Color.argb(alpha, 0, 0, 0)

        image_wrappedFragment.options.processor = MaskImageProcessor(maskColor, rotateImageProcessor)
        image_wrappedFragment.displayImage(AssetImage.MEI_NV)
    }
}
