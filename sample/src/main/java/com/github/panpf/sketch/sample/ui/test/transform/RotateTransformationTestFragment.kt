package com.github.panpf.sketch.sample.ui.test.transform

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.sample.AssetImages
import com.github.panpf.sketch.sample.databinding.RotateTransformationTestFragmentBinding
import com.github.panpf.sketch.sample.ui.base.BindingFragment
import com.github.panpf.sketch.transform.RotateTransformation

class RotateTransformationTestFragment :
    BindingFragment<RotateTransformationTestFragmentBinding>() {

    private val viewModel by viewModels<RotateTransformationTestViewModel>()

    override fun onViewCreated(
        binding: RotateTransformationTestFragmentBinding,
        savedInstanceState: Bundle?
    ) {
        binding.rotateTransformationTestButton.setOnClickListener {
            viewModel.changeRotate(viewModel.rotateData.value!! + 45)
        }

        viewModel.rotateData.observe(viewLifecycleOwner) {
            binding.rotateTransformationTestImage.displayImage(AssetImages.STATICS.first()) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                addTransformations(RotateTransformation(it))
            }
        }
    }
}