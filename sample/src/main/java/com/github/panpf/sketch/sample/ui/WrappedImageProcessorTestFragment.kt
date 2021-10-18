package com.github.panpf.sketch.sample.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import com.github.panpf.sketch.display.TransitionImageDisplayer
import com.github.panpf.sketch.process.MaskImageProcessor
import com.github.panpf.sketch.process.RotateImageProcessor
import com.github.panpf.sketch.process.RoundRectImageProcessor
import com.github.panpf.sketch.sample.AssetImage
import com.github.panpf.sketch.sample.base.BindingFragment
import com.github.panpf.sketch.sample.databinding.FragmentWrappedBinding
import com.github.panpf.sketch.sample.vm.WrappedProcessorTestViewModel

class WrappedImageProcessorTestFragment : BindingFragment<FragmentWrappedBinding>() {

    private val viewModel by viewModels<WrappedProcessorTestViewModel>()

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentWrappedBinding.inflate(inflater, parent, false)

    override fun onInitData(
        binding: FragmentWrappedBinding,
        savedInstanceState: Bundle?
    ) {
        binding.wrappedImage.apply {
            options.apply {
                val metrics = resources.displayMetrics
                setMaxSize(metrics.widthPixels / 2, metrics.heightPixels / 2)
                displayer = TransitionImageDisplayer()
            }
        }

        binding.wrappedRoundedRadiusSeekBar.apply {
            max = 100
            progress = viewModel.wrappedTestData.value!!.roundedRadius
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }

                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int, fromUser: Boolean
                ) {
                    if (progress < 20) {
                        this@apply.progress = 20
                    } else {
                        viewModel.changeRoundedRadius(progress)
                    }
                }
            })
        }

        binding.wrappedMaskOpacitySeekBar.apply {
            max = 100
            progress = viewModel.wrappedTestData.value!!.maskOpacity
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }


                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int, fromUser: Boolean
                ) {
                    if (progress < 20) {
                        this@apply.progress = 20
                    } else {
                        viewModel.changeMaskOpacity(progress)
                    }
                }
            })
        }

        binding.wrappedRotateButton.setOnClickListener {
            viewModel.changeRotate(viewModel.wrappedTestData.value!!.rotate + 45)
        }

        viewModel.wrappedTestData.observe(viewLifecycleOwner) { test ->
            test ?: return@observe
            val roundedRadius = test.roundedRadius
            val maskOpacity = test.maskOpacity
            val rotate = test.rotate

            binding.wrappedImage.apply {
                val roundRectImageProcessor = RoundRectImageProcessor(roundedRadius.toFloat())
                val rotateImageProcessor = RotateImageProcessor(rotate, roundRectImageProcessor)
                val alpha = (maskOpacity.toFloat() / 100 * 255).toInt()
                val maskColor = Color.argb(alpha, 0, 0, 0)
                options.processor = MaskImageProcessor(maskColor, rotateImageProcessor)
                displayImage(AssetImage.MEI_NV)
            }

            binding.wrappedRoundedRadiusValueText.text = "%d/%d".format(roundedRadius, 1000)
            binding.wrappedMaskOpacityValueText.text = "%d/%d".format(maskOpacity, 1000)
        }
    }
}
