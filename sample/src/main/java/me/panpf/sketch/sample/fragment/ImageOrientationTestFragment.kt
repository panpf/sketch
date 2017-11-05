package me.panpf.sketch.sample.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import me.panpf.sketch.SketchImageView
import me.panpf.sketch.sample.BaseFragment
import me.panpf.sketch.sample.BindContentView
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.bindView

@BindContentView(R.layout.fragment_image_orientation_test)
class ImageOrientationTestFragment : BaseFragment() {

    val beforeImageView: SketchImageView by bindView(R.id.image_imageOrientationTestFragment_before)
    val afterImageView: SketchImageView by bindView(R.id.image_imageOrientationTestFragment_after)

    private var filePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val arguments = arguments
        if (arguments != null) {
            filePath = arguments.getString(PARAM_REQUIRED_STRING_FILE_PATH)
        }

        if (TextUtils.isEmpty(filePath)) {
            throw IllegalArgumentException("Not found filePath param")
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        beforeImageView.options.isCorrectImageOrientationDisabled = true
        beforeImageView.displayImage(filePath!!)

        afterImageView.displayImage(filePath!!)
    }

    companion object {
        private val PARAM_REQUIRED_STRING_FILE_PATH = "PARAM_REQUIRED_STRING_FILE_PATH"

        fun build(filePath: String): ImageOrientationTestFragment {
            val bundle = Bundle()
            bundle.putString(PARAM_REQUIRED_STRING_FILE_PATH, filePath)

            val fragment = ImageOrientationTestFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
