package me.panpf.sketch.sample.fragment

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_round_rect_image_processor.*
import me.panpf.sketch.display.TransitionImageDisplayer
import me.panpf.sketch.process.RoundRectImageProcessor
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.BaseFragment
import me.panpf.sketch.sample.BindContentView
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.widget.SampleImageView

@BindContentView(R.layout.fragment_round_rect_image_processor)
class RoundRectImageProcessorTestFragment : BaseFragment() {
    val imageView: SampleImageView by lazy {image_roundRectImageProcessor}
    val seekBar: SeekBar by lazy {seekBar_roundRectImageProcessor}
    val progressTextView: TextView by lazy {text_roundRectImageProcessor}

    private var progress = 30

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 缩小图片，处理速度更快，更少的内存消耗
        val metrics = resources.displayMetrics
        imageView.options.setMaxSize(metrics.widthPixels / 2, metrics.heightPixels / 2)

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
        imageView.options.processor = RoundRectImageProcessor(progress.toFloat())
        imageView.displayImage(AssetImage.MEI_NV)
    }
}
