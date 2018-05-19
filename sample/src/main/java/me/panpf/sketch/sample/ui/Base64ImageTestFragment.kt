package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import kotlinx.android.synthetic.main.fragment_base64_test.*
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.BaseFragment
import me.panpf.sketch.sample.BindContentView
import me.panpf.sketch.sample.R

@BindContentView(R.layout.fragment_base64_test)
class Base64ImageTestFragment : BaseFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edit_base64TestFragment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                showImage(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })

        edit_base64TestFragment.setText(AssetImage.URI_TEST_BASE64)
    }

    private fun showImage(imageText: String) {
        image_base64TestFragment.displayImage(imageText)
    }
}
