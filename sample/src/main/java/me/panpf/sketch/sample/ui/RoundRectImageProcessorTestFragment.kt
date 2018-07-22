package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.fragment_round_rect_image_processor.*
import me.panpf.sketch.display.TransitionImageDisplayer
import me.panpf.sketch.process.RoundRectImageProcessor
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.base.BaseFragment
import me.panpf.sketch.sample.base.BindContentView
import me.panpf.sketch.sample.R

@BindContentView(R.layout.fragment_round_rect_image_processor)
class RoundRectImageProcessorTestFragment : BaseFragment() {

    private var progress = 30

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 缩小图片，处理速度更快，更少的内存消耗
        val metrics = resources.displayMetrics
        image_roundRectImageProcessor.options.setMaxSize(metrics.widthPixels / 2, metrics.heightPixels / 2)

        image_roundRectImageProcessor.options.displayer = TransitionImageDisplayer()

        seekBar_roundRectImageProcessor.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar_roundRectImageProcessor: SeekBar, progress: Int, fromUser: Boolean) {
                text_roundRectImageProcessor.text = String.format("%d/%d", seekBar_roundRectImageProcessor.progress, seekBar_roundRectImageProcessor.max)
            }

            override fun onStartTrackingTouch(seekBar_roundRectImageProcessor: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar_roundRectImageProcessor: SeekBar) {
                progress = seekBar_roundRectImageProcessor.progress
                apply()
            }
        })

        seekBar_roundRectImageProcessor.max = 100
        seekBar_roundRectImageProcessor.progress = progress

        apply()
    }

    private fun apply() {
        image_roundRectImageProcessor.options.processor = RoundRectImageProcessor(progress.toFloat())
        image_roundRectImageProcessor.displayImage(AssetImage.MEI_NV)
    }
}
