package com.github.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.github.panpf.sketch.request.ShapeSize
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.base.ToolbarBindingFragment
import com.github.panpf.sketch.sample.databinding.FragmentOtherTestBinding

class OtherTestFragment : ToolbarBindingFragment<FragmentOtherTestBinding>() {

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentOtherTestBinding.inflate(inflater, parent, false)

    override fun onInitData(
        toolbar: Toolbar,
        binding: FragmentOtherTestBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "Other Test"

        binding.otherTestFEmptyImage.apply {
            options.apply {
                errorImage(R.drawable.image_loading)
                shapeSize = ShapeSize.byViewFixedSize()
            }
            displayImage("")
        }
    }
}