package com.github.panpf.sketch.sample.ui.test.transform

import android.graphics.Color
import android.os.Bundle
import android.widget.SeekBar
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.viewModels
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.sample.AssetImages
import com.github.panpf.sketch.sample.databinding.BlurTransformationTestFragmentBinding
import com.github.panpf.sketch.sample.ui.base.BindingFragment
import com.github.panpf.sketch.transform.BlurTransformation

class BlurTransformationTestFragment : BindingFragment<BlurTransformationTestFragmentBinding>() {

    private val viewModel by viewModels<BlurTransformationTestViewModel>()

    override fun onViewCreated(
        binding: BlurTransformationTestFragmentBinding,
        savedInstanceState: Bundle?
    ) {
        viewModel.radiusData.observe(viewLifecycleOwner) {
            update(binding, it, viewModel.maskColorData.value)

            binding.blurTransformationTestValueText.text =
                "%d/%d".format(it, binding.blurTransformationTestSeekBar.max)
        }

        binding.blurTransformationTestSeekBar.apply {
            max = 100
            progress = viewModel.radiusData.value!!
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar_roundRectImageProcessor: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar_roundRectImageProcessor: SeekBar) {
                }

                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int, fromUser: Boolean
                ) {
                    viewModel.changeRadius(progress.coerceAtLeast(1))
                }
            })
        }
        viewModel.maskColorData.observe(viewLifecycleOwner) {
            update(binding, viewModel.radiusData.value!!, it)
        }

        binding.blurTransformationTestRedButton.setOnClickListener {
            viewModel.changeMaskColor(ColorUtils.setAlphaComponent(Color.RED, 128))
        }

        binding.blurTransformationTestGreenButton.setOnClickListener {
            viewModel.changeMaskColor(ColorUtils.setAlphaComponent(Color.GREEN, 128))
        }

        binding.blurTransformationTestBlueButton.setOnClickListener {
            viewModel.changeMaskColor(ColorUtils.setAlphaComponent(Color.BLUE, 128))
        }

        binding.blurTransformationTestNoneButton.setOnClickListener {
            viewModel.changeMaskColor(null)
        }

        binding.blurTransformationTestNoneButton.isChecked = true
    }

    private fun update(
        binding: BlurTransformationTestFragmentBinding,
        radius: Int,
        maskColor: Int?
    ) {
        binding.blurTransformationTestImage.displayImage(AssetImages.STATICS.first()) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            addTransformations(BlurTransformation(radius, maskColor))
        }
    }
}