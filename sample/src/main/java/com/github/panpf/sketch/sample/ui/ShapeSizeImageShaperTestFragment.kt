package com.github.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import com.github.panpf.sketch.display.TransitionImageDisplayer
import com.github.panpf.sketch.request.ShapeSize
import com.github.panpf.sketch.sample.AssetImage
import com.github.panpf.sketch.sample.base.BindingFragment
import com.github.panpf.sketch.sample.databinding.FragmentResizeBinding
import com.github.panpf.sketch.sample.vm.ResizeTestViewModel

class ShapeSizeImageShaperTestFragment : BindingFragment<FragmentResizeBinding>() {

    private val viewModel by viewModels<ResizeTestViewModel>()

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentResizeBinding.inflate(inflater, parent, false)

    override fun onInitData(
        binding: FragmentResizeBinding,
        savedInstanceState: Bundle?
    ) {
        binding.resizeImage.options.displayer = TransitionImageDisplayer()

        binding.resizeWidthSeekBar.apply {
            max = 100
            progress = viewModel.resizeTestData.value!!.width
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }

                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int, fromUser: Boolean
                ) {
                    if (progress < 20) {
                        this@apply.progress = 20
                    } else {
                        viewModel.changeWidth(progress)
                    }
                }
            })
        }

        binding.resizeHeightSeekBar.apply {
            max = 100
            progress = viewModel.resizeTestData.value!!.height
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }

                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int, fromUser: Boolean
                ) {
                    if (progress < 20) {
                        this@apply.progress = 20
                    } else {
                        viewModel.changeHeight(progress)
                    }
                }
            })
        }

        binding.resizeCenterButton.setOnClickListener {
            viewModel.changeScaleType(ImageView.ScaleType.CENTER)
        }
        binding.resizeCenterInsideButton.setOnClickListener {
            viewModel.changeScaleType(ImageView.ScaleType.CENTER_INSIDE)
        }
        binding.resizeCenterCropButton.setOnClickListener {
            viewModel.changeScaleType(ImageView.ScaleType.CENTER_CROP)
        }
        binding.resizeFixXYButton.setOnClickListener {
            viewModel.changeScaleType(ImageView.ScaleType.FIT_XY)
        }
        binding.resizeFixStartButton.setOnClickListener {
            viewModel.changeScaleType(ImageView.ScaleType.FIT_START)
        }
        binding.resizeFixEndButton.setOnClickListener {
            viewModel.changeScaleType(ImageView.ScaleType.FIT_END)
        }
        binding.resizeFixCenterButton.setOnClickListener {
            viewModel.changeScaleType(ImageView.ScaleType.FIT_CENTER)
        }
        binding.resizeMatrixButton.setOnClickListener {
            viewModel.changeScaleType(ImageView.ScaleType.MATRIX)
        }

        viewModel.resizeTestData.observe(viewLifecycleOwner) { test ->
            test ?: return@observe
            val width = (test.width / 100f * 1000).toInt()
            val height = (test.height / 100f * 1000).toInt()

            binding.resizeImage.apply {
                options.shapeSize = ShapeSize(width, height, test.scaleType)
                displayImage(AssetImage.MEI_NV)
            }

            binding.resizeWidthValueText.text = "%d/%d".format(width, 1000)
            binding.resizeHeightValueText.text = "%d/%d".format(height, 1000)

            binding.resizeCenterButton.isEnabled = test.scaleType != ImageView.ScaleType.CENTER
            binding.resizeCenterCropButton.isEnabled =
                test.scaleType != ImageView.ScaleType.CENTER_CROP
            binding.resizeCenterInsideButton.isEnabled =
                test.scaleType != ImageView.ScaleType.CENTER_INSIDE
            binding.resizeFixCenterButton.isEnabled =
                test.scaleType != ImageView.ScaleType.FIT_CENTER
            binding.resizeFixEndButton.isEnabled = test.scaleType != ImageView.ScaleType.FIT_END
            binding.resizeFixStartButton.isEnabled = test.scaleType != ImageView.ScaleType.FIT_START
            binding.resizeFixXYButton.isEnabled = test.scaleType != ImageView.ScaleType.FIT_XY
            binding.resizeMatrixButton.isEnabled = test.scaleType != ImageView.ScaleType.MATRIX
        }
    }
}
