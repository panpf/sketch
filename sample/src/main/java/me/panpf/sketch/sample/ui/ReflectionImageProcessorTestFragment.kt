package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import me.panpf.sketch.display.TransitionImageDisplayer
import me.panpf.sketch.process.ReflectionImageProcessor
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.base.BindingFragment
import me.panpf.sketch.sample.databinding.FragmentReflectionBinding

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
                setMaxSize(metrics.widthPixels / 2, metrics.heightPixels / 2)
                processor = ReflectionImageProcessor()
                displayer = TransitionImageDisplayer()
            }
            displayImage(AssetImage.MEI_NV)
        }
    }
}
