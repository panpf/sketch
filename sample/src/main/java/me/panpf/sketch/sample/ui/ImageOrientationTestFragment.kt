package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_image_orientation_test.*
import me.panpf.sketch.sample.BaseFragment
import me.panpf.sketch.sample.BindContentView
import me.panpf.sketch.sample.R

@BindContentView(R.layout.fragment_image_orientation_test)
class ImageOrientationTestFragment : BaseFragment() {

    private val filePath: String by lazy { checkNotNull(arguments?.getString(PARAM_REQUIRED_STRING_FILE_PATH)) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        image_imageOrientationTestFragment_before.options.isCorrectImageOrientationDisabled = true
        image_imageOrientationTestFragment_before.displayImage(filePath)

        image_imageOrientationTestFragment_after.displayImage(filePath)
    }

    companion object {
        private const val PARAM_REQUIRED_STRING_FILE_PATH = "PARAM_REQUIRED_STRING_FILE_PATH"

        fun build(filePath: String): ImageOrientationTestFragment = ImageOrientationTestFragment().apply {
            arguments = Bundle().apply { putString(PARAM_REQUIRED_STRING_FILE_PATH, filePath) }
        }
    }
}
