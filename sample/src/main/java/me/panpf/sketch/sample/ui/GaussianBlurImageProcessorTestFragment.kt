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

    private var progress = 15

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentGaussianBlurBinding.inflate(inflater, parent, false)

    override fun onInitData(
        binding: FragmentGaussianBlurBinding,
        savedInstanceState: Bundle?
    ) {
        // 通过maxSize限制缩小读到内存的图片的尺寸，尺寸越小高斯模糊越快
        val metrics = resources.displayMetrics
        binding.imageGaussianBlurFragment.options.setMaxSize(
            metrics.widthPixels / 4,
            metrics.heightPixels / 4
        )

        binding.imageGaussianBlurFragment.options.displayer = TransitionImageDisplayer()

        binding.seekBarGaussianBlurFragment.apply {
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    binding.textGaussianBlurFragment.text =
                        String.format("%d/%d", seekBar.progress, seekBar.max)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    progress = seekBar.progress
                    apply(binding)
                }
            })
            max = 100
            progress = progress
        }

        apply(binding)
    }

    private fun apply(binding: FragmentGaussianBlurBinding) {
        binding.imageGaussianBlurFragment.options.processor =
            GaussianBlurImageProcessor.makeRadius(progress)
        binding.imageGaussianBlurFragment.displayImage(AssetImage.MEI_NV)
    }
}
