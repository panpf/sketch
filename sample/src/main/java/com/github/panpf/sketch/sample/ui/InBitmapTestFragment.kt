package com.github.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import com.github.panpf.sketch.sample.base.ToolbarBindingFragment
import com.github.panpf.sketch.sample.databinding.FragmentInBitmapTestBinding
import com.github.panpf.sketch.sample.vm.InBitmapTestViewModel

class InBitmapTestFragment : ToolbarBindingFragment<FragmentInBitmapTestBinding>() {

    private val viewModel by viewModels<InBitmapTestViewModel>()

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentInBitmapTestBinding.inflate(inflater, parent, false)

    override fun onInitData(
        toolbar: Toolbar,
        binding: FragmentInBitmapTestBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "inBitmap Test"

        binding.inBitmapTestText.apply {
            viewModel.imageInfoData.observe(viewLifecycleOwner) {
                text = it
            }
        }

        binding.inBitmapTestImage.apply {
            viewModel.imageBitmapData.observe(viewLifecycleOwner) {
                setImageBitmap(it)
            }
        }

        binding.inBitmapTestSmallSizeButton.apply {
            setOnClickListener {
                viewModel.changeMode(InBitmapTestViewModel.Mode.SMALL_SIZE)
            }
            viewModel.modeData.observe(viewLifecycleOwner) {
                isEnabled = it != InBitmapTestViewModel.Mode.SMALL_SIZE
            }
        }

        binding.inBitmapTestSizeSameButton.apply {
            setOnClickListener {
                viewModel.changeMode(InBitmapTestViewModel.Mode.SAME_SIZE)
            }
            viewModel.modeData.observe(viewLifecycleOwner) {
                isEnabled = it != InBitmapTestViewModel.Mode.SAME_SIZE
            }
        }

        binding.inBitmapTestLargeSizeButton.apply {
            setOnClickListener {
                viewModel.changeMode(InBitmapTestViewModel.Mode.LARGE_SIZE)
            }
            viewModel.modeData.observe(viewLifecycleOwner) {
                isEnabled = it != InBitmapTestViewModel.Mode.LARGE_SIZE
            }
        }

        binding.inBitmapTestWidthAndHeightSwapButton.apply {
            setOnClickListener {
                viewModel.changeMode(InBitmapTestViewModel.Mode.WIDTH_HEIGHT_SWAP)
            }
            viewModel.modeData.observe(viewLifecycleOwner) {
                isEnabled = it != InBitmapTestViewModel.Mode.WIDTH_HEIGHT_SWAP
            }
        }

        binding.inBitmapTestFixedTwoButton.apply {
            setOnClickListener {
                viewModel.changeMode(InBitmapTestViewModel.Mode.FIXED_TWO)
            }
            viewModel.modeData.observe(viewLifecycleOwner) {
                isEnabled = it != InBitmapTestViewModel.Mode.FIXED_TWO
            }
        }
    }
}
