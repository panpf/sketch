package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import me.panpf.sketch.display.TransitionImageDisplayer
import me.panpf.sketch.process.GaussianBlurImageProcessor
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.base.BaseFragment
import me.panpf.sketch.sample.databinding.FragmentGaussianBlurBinding

class GaussianBlurImageProcessorTestFragment : BaseFragment<FragmentGaussianBlurBinding>() {

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentGaussianBlurBinding.inflate(inflater, parent, false)

    override fun onInitData(
        binding: FragmentGaussianBlurBinding,
        savedInstanceState: Bundle?
    ) {
        binding.gaussianBlurImage.apply {
            options.apply {
                val metrics = resources.displayMetrics
                setMaxSize(metrics.widthPixels / 4, metrics.heightPixels / 4)
                displayer = TransitionImageDisplayer()
            }
        }

        binding.gaussianBlurSeekBar.apply {
            max = 100
            progress = 15
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }

                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    apply(binding)
                }
            })
        }

        apply(binding)
    }

    private fun apply(binding: FragmentGaussianBlurBinding) {
        val progress = binding.gaussianBlurSeekBar.progress

        binding.gaussianBlurImage.apply {
            options.processor = GaussianBlurImageProcessor.makeRadius(progress)
            displayImage(AssetImage.MEI_NV)
        }

        binding.gaussianBlurValueText.text =
            "%d/%d".format(progress, binding.gaussianBlurSeekBar.max)
    }
}
