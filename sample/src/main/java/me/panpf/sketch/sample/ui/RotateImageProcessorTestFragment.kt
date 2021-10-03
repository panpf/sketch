package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import me.panpf.sketch.display.TransitionImageDisplayer
import me.panpf.sketch.process.RotateImageProcessor
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.base.BaseBindingFragment
import me.panpf.sketch.sample.databinding.FragmentRotateBinding

class RotateImageProcessorTestFragment : BaseBindingFragment<FragmentRotateBinding>() {

    private var degrees = 45

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentRotateBinding.inflate(inflater, parent, false)

    override fun onInitData(
        binding: FragmentRotateBinding,
        savedInstanceState: Bundle?
    ) {
        // 缩小图片，处理速度更快，更少的内存消耗
        val metrics = resources.displayMetrics
        binding.imageRotateFragment.options.setMaxSize(
            metrics.widthPixels / 2,
            metrics.heightPixels / 2
        )

        binding.imageRotateFragment.options.displayer = TransitionImageDisplayer()

        binding.buttonRotateFragment.setOnClickListener {
            degrees += 45
            apply()
        }

        apply()
    }

    private fun apply() {
        binding?.imageRotateFragment?.options?.processor = RotateImageProcessor(degrees)
        binding?.imageRotateFragment?.displayImage(AssetImage.MEI_NV)
    }
}
