package com.github.panpf.sketch.sample.ui.test.transform

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.sample.AssetImages
import com.github.panpf.sketch.sample.databinding.CircleCropTransformationTestFragmentBinding
import com.github.panpf.sketch.sample.ui.base.BindingFragment
import com.github.panpf.sketch.transform.CircleCropTransformation

class CircleCropTransformationTestFragment :
    BindingFragment<CircleCropTransformationTestFragmentBinding>() {

    val viewModel by viewModels<CircleCropTransformationTestViewModel>()

    override fun onViewCreated(
        binding: CircleCropTransformationTestFragmentBinding,
        savedInstanceState: Bundle?
    ) {
        viewModel.scaleData.observe(viewLifecycleOwner) {
            binding.circleCropTransformationTestImage.displayImage(AssetImages.FORMATS.first()) {
                addTransformations(CircleCropTransformation(it))
            }
        }

        binding.circleCropTransformationTestStartButton.setOnClickListener {
            viewModel.scaleData.postValue(START_CROP)
        }

        binding.circleCropTransformationTestCenterButton.setOnClickListener {
            viewModel.scaleData.postValue(CENTER_CROP)
        }

        binding.circleCropTransformationTestEndButton.setOnClickListener {
            viewModel.scaleData.postValue(END_CROP)
        }

        binding.circleCropTransformationTestFillButton.setOnClickListener {
            viewModel.scaleData.postValue(FILL)
        }
    }
}