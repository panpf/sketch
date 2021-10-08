package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import me.panpf.sketch.sample.base.BaseFragment
import me.panpf.sketch.sample.databinding.FragmentImageOrientationTestBinding

class ImageOrientationTestFragment : BaseFragment<FragmentImageOrientationTestBinding>() {

    private val filePath: String by lazy {
        checkNotNull(
            arguments?.getString(
                PARAM_REQUIRED_STRING_FILE_PATH
            )
        )
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentImageOrientationTestBinding.inflate(inflater, parent, false)

    override fun onInitData(
        binding: FragmentImageOrientationTestBinding,
        savedInstanceState: Bundle?
    ) {
        binding.imageImageOrientationTestFragmentBefore.apply {
            options.isCorrectImageOrientationDisabled = true
            displayImage(filePath)
        }

        binding.imageImageOrientationTestFragmentAfter.displayImage(filePath)
    }

    companion object {
        private const val PARAM_REQUIRED_STRING_FILE_PATH = "PARAM_REQUIRED_STRING_FILE_PATH"

        fun build(filePath: String): ImageOrientationTestFragment =
            ImageOrientationTestFragment().apply {
                arguments = Bundle().apply { putString(PARAM_REQUIRED_STRING_FILE_PATH, filePath) }
            }
    }
}
