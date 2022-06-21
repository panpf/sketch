package com.github.panpf.sketch.sample.ui.test.transform

import android.os.Bundle
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.sample.AssetImages
import com.github.panpf.sketch.sample.databinding.MultiTransformationTestFragmentBinding
import com.github.panpf.sketch.sample.ui.base.BindingFragment
import com.github.panpf.sketch.transform.RotateTransformation
import com.github.panpf.sketch.transform.RoundedCornersTransformation

class MultiTransformationTestFragment :
    BindingFragment<MultiTransformationTestFragmentBinding>() {

    private val rotateViewModel by viewModels<RotateTransformationTestViewModel>()
    private val roundedCornersViewModel by viewModels<RoundedCornersTransformationTestViewModel>()

    override fun onViewCreated(
        binding: MultiTransformationTestFragmentBinding,
        savedInstanceState: Bundle?
    ) {
        binding.multiTransformationTestSeekBar.apply {
            max = 100
            progress = roundedCornersViewModel.radiusData.value!!
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar_roundRectImageProcessor: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar_roundRectImageProcessor: SeekBar) {
                }

                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int, fromUser: Boolean
                ) {
                    roundedCornersViewModel.changeRadius(progress)
                }
            })
        }

        binding.multiTransformationTestButton.setOnClickListener {
            rotateViewModel.changeRotate(rotateViewModel.rotateData.value!! + 45)
        }

        rotateViewModel.rotateData.observe(viewLifecycleOwner) {
            binding.multiTransformationTestImage.displayImage(AssetImages.STATICS.first()) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                addTransformations(
                    RoundedCornersTransformation(roundedCornersViewModel.radiusData.value!!.toFloat()),
                    RotateTransformation(rotateViewModel.rotateData.value!!)
                )
            }
        }

        roundedCornersViewModel.radiusData.observe(viewLifecycleOwner) {
            binding.multiTransformationTestImage.displayImage(AssetImages.STATICS.first()) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                addTransformations(
                    RoundedCornersTransformation(roundedCornersViewModel.radiusData.value!!.toFloat()),
                    RotateTransformation(rotateViewModel.rotateData.value!!)
                )
            }

            binding.multiTransformationTestValueText.text =
                "%d/%d".format(it, binding.multiTransformationTestSeekBar.max)
        }
    }
}