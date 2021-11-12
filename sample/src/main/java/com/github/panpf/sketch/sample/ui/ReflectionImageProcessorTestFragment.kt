package com.github.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.panpf.sketch.display.TransitionImageDisplayer
import com.github.panpf.sketch.process.ReflectionImageProcessor
import com.github.panpf.sketch.sample.AssetImage
import com.github.panpf.sketch.sample.base.BindingFragment
import com.github.panpf.sketch.sample.databinding.FragmentReflectionBinding

class ReflectionImageProcessorTestFragment : BindingFragment<FragmentReflectionBinding>() {

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentReflectionBinding.inflate(inflater, parent, false)

    override fun onInitData(
        binding: FragmentReflectionBinding,
        savedInstanceState: Bundle?
    ) {
        binding.reflectionImage.apply {
            options.apply {
                val metrics = resources.displayMetrics
                maxSize(metrics.widthPixels / 2, metrics.heightPixels / 2)
                processor = ReflectionImageProcessor()
                displayer = TransitionImageDisplayer()
            }
            displayImage(AssetImage.MEI_NV)
        }
    }
}
