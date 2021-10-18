package com.github.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import com.github.panpf.sketch.display.TransitionImageDisplayer
import com.github.panpf.sketch.process.GaussianBlurImageProcessor
import com.github.panpf.sketch.sample.AssetImage
import com.github.panpf.sketch.sample.base.BindingFragment
import com.github.panpf.sketch.sample.databinding.FragmentGaussianBlurBinding
import com.github.panpf.sketch.sample.vm.BlurProcessorTestViewModel

class GaussianBlurImageProcessorTestFragment : BindingFragment<FragmentGaussianBlurBinding>() {

    private val viewModel by viewModels<BlurProcessorTestViewModel>()

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
            progress = viewModel.blurRadiusData.value!!
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }

                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int, fromUser: Boolean
                ) {
                    viewModel.changeBlurRadius(progress)
                }
            })
        }

        viewModel.blurRadiusData.observe(viewLifecycleOwner) {
            val blurRadius = it ?: return@observe

            binding.gaussianBlurImage.apply {
                options.processor = GaussianBlurImageProcessor.makeRadius(blurRadius)
                displayImage(AssetImage.MEI_NV)
            }

            binding.gaussianBlurValueText.text =
                "%d/%d".format(blurRadius, binding.gaussianBlurSeekBar.max)
        }
    }
}
