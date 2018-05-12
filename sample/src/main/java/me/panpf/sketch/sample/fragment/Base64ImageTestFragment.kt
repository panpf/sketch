package me.panpf.sketch.sample.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import kotlinx.android.synthetic.main.fragment_base64_test.*
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.BaseFragment
import me.panpf.sketch.sample.BindContentView
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.widget.SampleImageView

@BindContentView(R.layout.fragment_base64_test)
class Base64ImageTestFragment : BaseFragment() {

    val editText: EditText by lazy {edit_base64TestFragment}
    val imageView: SampleImageView by lazy {image_base64TestFragment}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                showImage(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })

        editText.setText(AssetImage.URI_TEST_BASE64)
    }

    private fun showImage(imageText: String) {
        imageView.displayImage(imageText)
    }
}
