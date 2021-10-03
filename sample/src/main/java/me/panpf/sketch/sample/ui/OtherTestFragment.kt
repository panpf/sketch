package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import me.panpf.sketch.request.ShapeSize
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.base.BaseBindingFragment
import me.panpf.sketch.sample.databinding.FragmentOtherTestBinding

class OtherTestFragment : BaseBindingFragment<FragmentOtherTestBinding>() {

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentOtherTestBinding.inflate(inflater, parent, false)

    override fun onInitData(
        binding: FragmentOtherTestBinding,
        savedInstanceState: Bundle?
    ) {
        binding.otherTestFEmptyImage.options.setErrorImage(R.drawable.image_loading)
        binding.otherTestFEmptyImage.options.shapeSize = ShapeSize.byViewFixedSize()
        binding.otherTestFEmptyImage.displayImage("")
    }
}