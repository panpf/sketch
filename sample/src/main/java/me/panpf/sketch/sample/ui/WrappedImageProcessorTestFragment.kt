package me.panpf.sketch.sample.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import me.panpf.sketch.display.TransitionImageDisplayer
import me.panpf.sketch.process.MaskImageProcessor
import me.panpf.sketch.process.RotateImageProcessor
import me.panpf.sketch.process.RoundRectImageProcessor
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.base.BaseBindingFragment
import me.panpf.sketch.sample.databinding.FragmentWrappedBinding

class WrappedImageProcessorTestFragment : BaseBindingFragment<FragmentWrappedBinding>() {

    private var roundRectRadiusProgress = 30
    private var maskAlphaProgress = 45
    private var rotateProgress = 45

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentWrappedBinding.inflate(inflater, parent, false)

    override fun onInitData(
        binding: FragmentWrappedBinding,
        savedInstanceState: Bundle?
    ) {
        // 缩小图片，处理速度更快，更少的内存消耗
        val metrics = resources.displayMetrics
        binding.imageWrappedFragment.options.setMaxSize(
            metrics.widthPixels / 2,
            metrics.heightPixels / 2
        )

        binding.imageWrappedFragment.options.displayer = TransitionImageDisplayer()

        binding.seekBarWrappedFragmentWidth.max = 100
        binding.seekBarWrappedFragmentWidth.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (progress < 20) {
                    binding.seekBarWrappedFragmentWidth.progress = 20
                    return
                }

                val width = (binding.seekBarWrappedFragmentWidth.progress / 100f * 1000).toInt()
                binding.textWrappedFragmentWidth.text = String.format("%d/%d", width, 1000)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                roundRectRadiusProgress = binding.seekBarWrappedFragmentWidth.progress
                apply()
            }
        })
        binding.seekBarWrappedFragmentWidth.progress = roundRectRadiusProgress

        binding.seekBarWrappedFragmentHeight.max = 100
        binding.seekBarWrappedFragmentHeight.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (progress < 20) {
                    binding.seekBarWrappedFragmentHeight.progress = 20
                    return
                }
                val height = (binding.seekBarWrappedFragmentHeight.progress / 100f * 1000).toInt()
                binding.textWrappedFragmentHeight.text = String.format("%d/%d", height, 1000)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                maskAlphaProgress = binding.seekBarWrappedFragmentHeight.progress
                apply()
            }
        })
        binding.seekBarWrappedFragmentHeight.progress = maskAlphaProgress

        binding.buttonWrappedFragment.setOnClickListener {
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

        binding?.imageWrappedFragment?.options?.processor =
            MaskImageProcessor(maskColor, rotateImageProcessor)
        binding?.imageWrappedFragment?.displayImage(AssetImage.MEI_NV)
    }
}
