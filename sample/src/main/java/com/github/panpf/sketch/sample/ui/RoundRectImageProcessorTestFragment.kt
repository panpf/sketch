package com.github.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import com.github.panpf.sketch.display.TransitionImageDisplayer
import com.github.panpf.sketch.process.RoundRectImageProcessor
import com.github.panpf.sketch.sample.AssetImage
import com.github.panpf.sketch.sample.base.BindingFragment
import com.github.panpf.sketch.sample.databinding.FragmentRoundRectImageProcessorBinding
import com.github.panpf.sketch.sample.vm.RoundedProcessorTestViewModel

class RoundRectImageProcessorTestFragment :
    BindingFragment<FragmentRoundRectImageProcessorBinding>() {

    private val viewModel by viewModels<RoundedProcessorTestViewModel>()

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
                maxSize(metrics.widthPixels / 2, metrics.heightPixels / 2)
                displayer = TransitionImageDisplayer()
            }
        }

        binding.roundRectSeekBar.apply {
            max = 100
            progress = viewModel.roundedRadiusData.value!!
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar_roundRectImageProcessor: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar_roundRectImageProcessor: SeekBar) {
                }

                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int, fromUser: Boolean
                ) {
                    viewModel.changeRounded(progress)
                }
            })
        }

        viewModel.roundedRadiusData.observe(viewLifecycleOwner) {
            val maskOpacity = it ?: return@observe

            binding.roundRectImage.apply {
                options.processor = RoundRectImageProcessor(maskOpacity.toFloat())
                displayImage(AssetImage.MEI_NV)
            }

            binding.roundRectValueText.text =
                "%d/%d".format(maskOpacity, binding.roundRectSeekBar.max)
        }
    }
}
