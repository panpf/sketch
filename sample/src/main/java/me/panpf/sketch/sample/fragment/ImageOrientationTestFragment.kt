package me.panpf.sketch.sample.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import kotlinx.android.synthetic.main.fragment_image_orientation_test.*
import me.panpf.sketch.SketchImageView
import me.panpf.sketch.sample.BaseFragment
import me.panpf.sketch.sample.BindContentView
import me.panpf.sketch.sample.R

@BindContentView(R.layout.fragment_image_orientation_test)
class ImageOrientationTestFragment : BaseFragment() {

    val beforeImageView: SketchImageView by lazy {image_imageOrientationTestFragment_before}
    val afterImageView: SketchImageView by lazy {image_imageOrientationTestFragment_after}

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        beforeImageView.options.isCorrectImageOrientationDisabled = true
        beforeImageView.displayImage(filePath!!)

        TODO("不起作用了")
        TODO("往回滑动内容消失")

        afterImageView.displayImage(filePath!!)
    }

    companion object {
        private const val PARAM_REQUIRED_STRING_FILE_PATH = "PARAM_REQUIRED_STRING_FILE_PATH"

        fun build(filePath: String): ImageOrientationTestFragment {
            val bundle = Bundle()
            bundle.putString(PARAM_REQUIRED_STRING_FILE_PATH, filePath)

            val fragment = ImageOrientationTestFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
