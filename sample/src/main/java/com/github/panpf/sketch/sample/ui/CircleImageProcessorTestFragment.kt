package com.github.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.panpf.sketch.display.TransitionImageDisplayer
import com.github.panpf.sketch.process.CircleImageProcessor
import com.github.panpf.sketch.sample.AssetImage
import com.github.panpf.sketch.sample.base.BindingFragment
import com.github.panpf.sketch.sample.databinding.FragmentCircleBinding

class CircleImageProcessorTestFragment : BindingFragment<FragmentCircleBinding>() {

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentCircleBinding.inflate(inflater, parent, false)

    override fun onInitData(
        binding: FragmentCircleBinding,
        savedInstanceState: Bundle?
    ) {
        binding.circleImage.apply {
            options.apply {
                val metrics = resources.displayMetrics
                maxSize(metrics.widthPixels / 2, metrics.heightPixels / 2)
                processor = CircleImageProcessor.instance
                displayer = TransitionImageDisplayer()
            }

            displayImage(AssetImage.MEI_NV)
        }
    }
}
