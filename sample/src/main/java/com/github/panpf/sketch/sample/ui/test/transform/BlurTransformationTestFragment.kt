package com.github.panpf.sketch.sample.ui.test.transform

import android.os.Bundle
import android.widget.SeekBar
import androidx.fragment.app.viewModels
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
            binding.blurTransformationTestImage.displayImage(AssetImages.FORMATS.first()) {
                addTransformations(BlurTransformation(it))
            }

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
                    viewModel.changeRadius(progress)
                }
            })
        }
    }
}