package com.github.panpf.sketch.sample.ui.test.transform

import android.os.Bundle
import androidx.core.os.bundleOf
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.sample.databinding.ExifOrientationTestFragmentBinding
import com.github.panpf.sketch.sample.ui.base.BindingFragment
import java.io.File

class ExifOrientationTestFragment :
    BindingFragment<ExifOrientationTestFragmentBinding>() {

    companion object {
        fun create(file: File): ExifOrientationTestFragment = ExifOrientationTestFragment().apply {
            arguments = bundleOf("filePath" to file.path)
        }
    }

    override fun onViewCreated(
        binding: ExifOrientationTestFragmentBinding,
        savedInstanceState: Bundle?
    ) {
        val filePath = arguments?.getString("filePath")

        binding.exifOrientationTestImage.displayImage(filePath) {
            ignoreExifOrientation(true)
        }

        binding.exifOrientationTestImage2.displayImage(filePath) {
            ignoreExifOrientation(false)
        }
    }
}