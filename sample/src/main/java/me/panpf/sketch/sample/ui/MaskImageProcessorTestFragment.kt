package me.panpf.sketch.sample.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.fragment_mask.*
import me.panpf.sketch.display.TransitionImageDisplayer
import me.panpf.sketch.process.MaskImageProcessor
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.BaseFragment
import me.panpf.sketch.sample.BindContentView
import me.panpf.sketch.sample.R

@BindContentView(R.layout.fragment_mask)
class MaskImageProcessorTestFragment : BaseFragment() {

    private var progress = 15

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 缩小图片，处理速度更快，更少的内存消耗
        val metrics = resources.displayMetrics
        image_maskFragment.options.setMaxSize(metrics.widthPixels / 2, metrics.heightPixels / 2)

        image_maskFragment.options.displayer = TransitionImageDisplayer()

        seekBar_maskFragment.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar_maskFragment: SeekBar, progress: Int, fromUser: Boolean) {
                text_maskFragment.text = String.format("%d/%d", seekBar_maskFragment.progress, seekBar_maskFragment.max)
            }

            override fun onStartTrackingTouch(seekBar_maskFragment: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar_maskFragment: SeekBar) {
                progress = seekBar_maskFragment.progress
                apply()
            }
        })

        seekBar_maskFragment.max = 100
        seekBar_maskFragment.progress = progress

        apply()
    }

    private fun apply() {
        val alpha = (progress.toFloat() / 100 * 255).toInt()
        val maskColor = Color.argb(alpha, 0, 0, 0)
        image_maskFragment.options.processor = MaskImageProcessor(maskColor)
        image_maskFragment.displayImage(AssetImage.MASK)
    }
}
