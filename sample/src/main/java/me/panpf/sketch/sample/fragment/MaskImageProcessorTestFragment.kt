package me.panpf.sketch.sample.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import me.panpf.sketch.display.TransitionImageDisplayer
import me.panpf.sketch.process.MaskImageProcessor
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.BaseFragment
import me.panpf.sketch.sample.BindContentView
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.widget.SampleImageView
import me.panpf.sketch.sample.bindView

@BindContentView(R.layout.fragment_mask)
class MaskImageProcessorTestFragment : BaseFragment() {
    val imageView: SampleImageView by bindView(R.id.image_maskFragment)
    val seekBar: SeekBar by bindView(R.id.seekBar_maskFragment)
    val progressTextView: TextView by bindView(R.id.text_maskFragment)

    private var progress = 15

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
        val alpha = (progress.toFloat() / 100 * 255).toInt()
        val maskColor = Color.argb(alpha, 0, 0, 0)
        imageView.options.processor = MaskImageProcessor(maskColor)
        imageView.displayImage(AssetImage.MASK)
    }
}
