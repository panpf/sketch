package com.github.panpf.sketch.sample.ui.test.transform

import android.os.Bundle
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.sample.AssetImages
import com.github.panpf.sketch.sample.databinding.RoundedCornersTransformationTestFragmentBinding
import com.github.panpf.sketch.sample.ui.base.BindingFragment
import com.github.panpf.sketch.transform.RoundedCornersTransformation

class RoundedCornersTransformationTestFragment :
    BindingFragment<RoundedCornersTransformationTestFragmentBinding>() {

    private val viewModel by viewModels<RoundedCornersTransformationTestViewModel>()

    override fun onViewCreated(
        binding: RoundedCornersTransformationTestFragmentBinding,
        savedInstanceState: Bundle?
    ) {
        viewModel.radiusData.observe(viewLifecycleOwner) {
            binding.roundedCornersTransformationTestImage.displayImage(AssetImages.STATICS.first()) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                addTransformations(RoundedCornersTransformation(it.toFloat()))
            }

            binding.roundedCornersTransformationTestValueText.text =
                "%d/%d".format(it, binding.roundedCornersTransformationTestSeekBar.max)
        }

        binding.roundedCornersTransformationTestSeekBar.apply {
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