package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.base.BaseToolbarFragment
import me.panpf.sketch.sample.databinding.FragmentBase64TestBinding

class Base64ImageFragment : BaseToolbarFragment<FragmentBase64TestBinding>() {

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentBase64TestBinding.inflate(inflater, parent, false)

    override fun onInitData(
        toolbar: Toolbar,
        binding: FragmentBase64TestBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "Base64 Image"

        binding.editBase64TestFragment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                showImage(binding, s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding.editBase64TestFragment.setText(AssetImage.URI_TEST_BASE64)
    }

    private fun showImage(binding: FragmentBase64TestBinding, imageText: String) {
        binding.imageBase64TestFragment.displayImage(imageText)
    }
}
