package com.github.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.github.panpf.sketch.display.TransitionImageDisplayer
import com.github.panpf.sketch.process.RotateImageProcessor
import com.github.panpf.sketch.sample.AssetImage
import com.github.panpf.sketch.sample.base.BindingFragment
import com.github.panpf.sketch.sample.databinding.FragmentRotateBinding
import com.github.panpf.sketch.sample.vm.RotateProcessorTestViewModel

class RotateImageProcessorTestFragment : BindingFragment<FragmentRotateBinding>() {

    private val viewModel by viewModels<RotateProcessorTestViewModel>()

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

        binding.rotateActionButton.setOnClickListener {
            viewModel.changeRotate(viewModel.rotateData.value!! + 45)
        }

        viewModel.rotateData.observe(viewLifecycleOwner) {
            val degrees = it ?: return@observe

            binding.rotateImage.apply {
                options.processor = RotateImageProcessor(degrees)
                displayImage(AssetImage.MEI_NV)
            }
        }
    }
}
