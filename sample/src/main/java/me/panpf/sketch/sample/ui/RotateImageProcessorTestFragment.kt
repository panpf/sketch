package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import me.panpf.sketch.display.TransitionImageDisplayer
import me.panpf.sketch.process.RotateImageProcessor
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.base.BaseFragment
import me.panpf.sketch.sample.databinding.FragmentRotateBinding
import me.panpf.sketch.sample.vm.RotateProcessorTestViewModel

class RotateImageProcessorTestFragment : BaseFragment<FragmentRotateBinding>() {

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
