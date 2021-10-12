package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import me.panpf.sketch.display.TransitionImageDisplayer
import me.panpf.sketch.process.RotateImageProcessor
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.base.BaseFragment
import me.panpf.sketch.sample.databinding.FragmentRotateBinding

class RotateImageProcessorTestFragment : BaseFragment<FragmentRotateBinding>() {

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentRotateBinding.inflate(inflater, parent, false)

    override fun onInitData(
        binding: FragmentRotateBinding,
        savedInstanceState: Bundle?
    ) {
        binding.rotateImage.apply {
            options.apply {
                val metrics = resources.displayMetrics
                setMaxSize(metrics.widthPixels / 2, metrics.heightPixels / 2)
                displayer = TransitionImageDisplayer()
            }
        }

        var degrees = 45

        binding.rotateActionButton.setOnClickListener {
            degrees += 45
            apply(binding, degrees)
        }

        apply(binding, degrees)
    }

    private fun apply(binding: FragmentRotateBinding, degrees: Int) {
        binding.rotateImage.apply {
            options.processor = RotateImageProcessor(degrees)
            displayImage(AssetImage.MEI_NV)
        }
    }
}
