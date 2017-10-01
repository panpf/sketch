package me.xiaopan.sketchsample.fragment

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import me.xiaopan.sketch.display.TransitionImageDisplayer
import me.xiaopan.sketch.process.RoundRectImageProcessor
import me.xiaopan.sketchsample.AssetImage
import me.xiaopan.sketchsample.BaseFragment
import me.xiaopan.sketchsample.BindContentView
import me.xiaopan.sketchsample.R
import me.xiaopan.sketchsample.widget.SampleImageView
import me.xiaopan.ssvt.bindView

@BindContentView(R.layout.fragment_round_rect_image_processor)
class RoundRectImageProcessorTestFragment : BaseFragment() {
    val imageView: SampleImageView by bindView(R.id.image_roundRectImageProcessor)
    val seekBar: SeekBar by bindView(R.id.seekBar_roundRectImageProcessor)
    val progressTextView: TextView by bindView(R.id.text_roundRectImageProcessor)

    private var progress = 30

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
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
