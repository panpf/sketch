package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import me.panpf.sketch.display.TransitionImageDisplayer
import me.panpf.sketch.process.RoundRectImageProcessor
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.base.BaseBindingFragment
import me.panpf.sketch.sample.databinding.FragmentRoundRectImageProcessorBinding

class RoundRectImageProcessorTestFragment :
    BaseBindingFragment<FragmentRoundRectImageProcessorBinding>() {

    private var progress = 30

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentRoundRectImageProcessorBinding.inflate(inflater, parent, false)

    override fun onInitData(
        binding: FragmentRoundRectImageProcessorBinding,
        savedInstanceState: Bundle?
    ) {
        // 缩小图片，处理速度更快，更少的内存消耗
        val metrics = resources.displayMetrics
        binding.imageRoundRectImageProcessor.options.setMaxSize(
            metrics.widthPixels / 2,
            metrics.heightPixels / 2
        )

        binding.imageRoundRectImageProcessor.options.displayer = TransitionImageDisplayer()

        binding.seekBarRoundRectImageProcessor.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar_roundRectImageProcessor: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                binding.textRoundRectImageProcessor.text = String.format(
                    "%d/%d",
                    seekBar_roundRectImageProcessor.progress,
                    seekBar_roundRectImageProcessor.max
                )
            }

            override fun onStartTrackingTouch(seekBar_roundRectImageProcessor: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar_roundRectImageProcessor: SeekBar) {
                progress = seekBar_roundRectImageProcessor.progress
                apply()
            }
        })

        binding.seekBarRoundRectImageProcessor.max = 100
        binding.seekBarRoundRectImageProcessor.progress = progress

        apply()
    }

    private fun apply() {
        binding?.imageRoundRectImageProcessor?.options?.processor =
            RoundRectImageProcessor(progress.toFloat())
        binding?.imageRoundRectImageProcessor?.displayImage(AssetImage.MEI_NV)
    }
}
