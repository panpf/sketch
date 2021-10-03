package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import me.panpf.sketch.display.TransitionImageDisplayer
import me.panpf.sketch.process.ReflectionImageProcessor
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.base.BaseBindingFragment
import me.panpf.sketch.sample.databinding.FragmentReflectionBinding

class ReflectionImageProcessorTestFragment : BaseBindingFragment<FragmentReflectionBinding>() {

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentReflectionBinding.inflate(inflater, parent, false)

    override fun onInitData(
        binding: FragmentReflectionBinding,
        savedInstanceState: Bundle?
    ) {
        // 缩小图片，处理速度更快，更少的内存消耗
        val metrics = resources.displayMetrics
        binding.imageReflectionFragment.options.setMaxSize(
            metrics.widthPixels / 2,
            metrics.heightPixels / 2
        )

        binding.imageReflectionFragment.options.processor = ReflectionImageProcessor()
        binding.imageReflectionFragment.options.displayer = TransitionImageDisplayer()
        binding.imageReflectionFragment.displayImage(AssetImage.MEI_NV)
    }
}
