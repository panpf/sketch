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

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentMaskBinding.inflate(inflater, parent, false)

    override fun onInitData(
        binding: FragmentMaskBinding,
        savedInstanceState: Bundle?
    ) {
        binding.maskImage.apply {
            options.apply {
                val metrics = resources.displayMetrics
                setMaxSize(metrics.widthPixels / 2, metrics.heightPixels / 2)
                displayer = TransitionImageDisplayer()
            }
        }

        binding.maskSeekBar.apply {
            max = 100
            progress = 15
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar_maskFragment: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar_maskFragment: SeekBar) {
                }

                override fun onProgressChanged(
                    seekBar_maskFragment: SeekBar,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    apply(binding)
                }
            })
        }

        apply(binding)
    }

    private fun apply(binding: FragmentMaskBinding) {
        val progress = binding.maskSeekBar.progress

        val alpha = (progress.toFloat() / 100 * 255).toInt()
        val maskColor = Color.argb(alpha, 0, 0, 0)
        binding.maskImage.apply {
            options.processor = MaskImageProcessor(maskColor)
            displayImage(AssetImage.MASK)
        }

        binding.maskValueText.text = "%d/%d".format(progress, binding.maskSeekBar.max)
    }
}
