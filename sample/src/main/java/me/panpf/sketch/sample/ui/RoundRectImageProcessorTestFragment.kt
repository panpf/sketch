package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import me.panpf.sketch.display.TransitionImageDisplayer
import me.panpf.sketch.process.RoundRectImageProcessor
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.base.BaseFragment
import me.panpf.sketch.sample.databinding.FragmentRoundRectImageProcessorBinding

class RoundRectImageProcessorTestFragment :
    BaseFragment<FragmentRoundRectImageProcessorBinding>() {

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentRoundRectImageProcessorBinding.inflate(inflater, parent, false)

    override fun onInitData(
        binding: FragmentRoundRectImageProcessorBinding,
        savedInstanceState: Bundle?
    ) {
        binding.roundRectImage.apply {
            options.apply {
                val metrics = resources.displayMetrics
                setMaxSize(metrics.widthPixels / 2, metrics.heightPixels / 2)
                displayer = TransitionImageDisplayer()
            }
        }

        binding.roundRectSeekBar.apply {
            max = 100
            progress = 30
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar_roundRectImageProcessor: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar_roundRectImageProcessor: SeekBar) {
                }

                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    apply(binding)
                }
            })
        }

        apply(binding)
    }

    private fun apply(binding: FragmentRoundRectImageProcessorBinding) {
        val progress = binding.roundRectSeekBar.progress

        binding.roundRectImage.apply {
            options.processor = RoundRectImageProcessor(progress.toFloat())
            displayImage(AssetImage.MEI_NV)
        }

        binding.roundRectValueText.text =
            "%d/%d".format(progress, binding.roundRectSeekBar.max)
    }
}
