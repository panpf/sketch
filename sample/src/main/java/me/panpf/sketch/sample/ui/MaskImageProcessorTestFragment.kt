package me.panpf.sketch.sample.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import me.panpf.sketch.display.TransitionImageDisplayer
import me.panpf.sketch.process.MaskImageProcessor
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.base.BaseFragment
import me.panpf.sketch.sample.databinding.FragmentMaskBinding

class MaskImageProcessorTestFragment : BaseFragment<FragmentMaskBinding>() {

    private var progress = 15

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentMaskBinding.inflate(inflater, parent, false)

    override fun onInitData(
        binding: FragmentMaskBinding,
        savedInstanceState: Bundle?
    ) {
        // 缩小图片，处理速度更快，更少的内存消耗
        val metrics = resources.displayMetrics
        binding.imageMaskFragment.options.setMaxSize(
            metrics.widthPixels / 2,
            metrics.heightPixels / 2
        )

        binding.imageMaskFragment.options.displayer = TransitionImageDisplayer()

        binding.seekBarMaskFragment.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar_maskFragment: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                binding.textMaskFragment.text =
                    String.format("%d/%d", seekBar_maskFragment.progress, seekBar_maskFragment.max)
            }

            override fun onStartTrackingTouch(seekBar_maskFragment: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar_maskFragment: SeekBar) {
                progress = seekBar_maskFragment.progress
                apply()
            }
        })

        binding.seekBarMaskFragment.max = 100
        binding.seekBarMaskFragment.progress = progress

        apply()
    }

    private fun apply() {
        val alpha = (progress.toFloat() / 100 * 255).toInt()
        val maskColor = Color.argb(alpha, 0, 0, 0)
        binding?.imageMaskFragment?.options?.processor = MaskImageProcessor(maskColor)
        binding?.imageMaskFragment?.displayImage(AssetImage.MASK)
    }
}
