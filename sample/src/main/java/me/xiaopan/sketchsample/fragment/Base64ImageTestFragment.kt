package me.xiaopan.sketchsample.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import me.xiaopan.sketchsample.AssetImage
import me.xiaopan.sketchsample.BaseFragment
import me.xiaopan.sketchsample.BindContentView
import me.xiaopan.sketchsample.R
import me.xiaopan.sketchsample.widget.SampleImageView
import me.xiaopan.ssvt.bindView

@BindContentView(R.layout.fragment_base64_test)
class Base64ImageTestFragment : BaseFragment() {

    val editText: EditText by bindView(R.id.edit_base64TestFragment)
    val imageView: SampleImageView by bindView(R.id.image_base64TestFragment)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
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
