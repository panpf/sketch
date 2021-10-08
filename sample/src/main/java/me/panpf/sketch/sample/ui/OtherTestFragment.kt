package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import me.panpf.sketch.request.ShapeSize
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.base.BaseToolbarFragment
import me.panpf.sketch.sample.databinding.FragmentOtherTestBinding

class OtherTestFragment : BaseToolbarFragment<FragmentOtherTestBinding>() {

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

        binding.otherTestFEmptyImage.options.setErrorImage(R.drawable.image_loading)
        binding.otherTestFEmptyImage.options.shapeSize = ShapeSize.byViewFixedSize()
        binding.otherTestFEmptyImage.displayImage("")
    }
}