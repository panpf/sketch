package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import me.panpf.sketch.display.TransitionImageDisplayer
import me.panpf.sketch.request.ShapeSize
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.base.BaseFragment
import me.panpf.sketch.sample.databinding.FragmentResizeBinding

class ShapeSizeImageShaperTestFragment : BaseFragment<FragmentResizeBinding>() {

    private var widthProgress = 50
    private var heightProgress = 50
    private var scaleType: ImageView.ScaleType = ImageView.ScaleType.FIT_CENTER
    private var currentCheckedButton: View? = null

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentResizeBinding.inflate(inflater, parent, false)

    override fun onInitData(
        binding: FragmentResizeBinding,
        savedInstanceState: Bundle?
    ) {
        binding.imageResizeFragment.options.displayer = TransitionImageDisplayer()

        binding.seekBarResizeFragmentWidth.max = 100
        binding.seekBarResizeFragmentWidth.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (progress < 20) {
                    binding.seekBarResizeFragmentWidth.progress = 20
                    return
                }

                val width = (binding.seekBarResizeFragmentWidth.progress / 100f * 1000).toInt()
                binding.textResizeFragmentWidth.text = String.format("%d/%d", width, 1000)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                widthProgress = binding.seekBarResizeFragmentWidth.progress
                apply(currentCheckedButton)
            }
        })
        binding.seekBarResizeFragmentWidth.progress = widthProgress

        binding.seekBarResizeFragmentHeight.max = 100
        binding.seekBarResizeFragmentHeight.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (progress < 20) {
                    binding.seekBarResizeFragmentHeight.progress = 20
                    return
                }
                val height = (binding.seekBarResizeFragmentHeight.progress / 100f * 1000).toInt()
                binding.textResizeFragmentHeight.text = String.format("%d/%d", height, 1000)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                heightProgress = binding.seekBarResizeFragmentHeight.progress
                apply(currentCheckedButton)
            }
        })
        binding.seekBarResizeFragmentHeight.progress = heightProgress

        binding.buttonResizeFragmentFixStart.tag = ImageView.ScaleType.FIT_START
        binding.buttonResizeFragmentFixCenter.tag = ImageView.ScaleType.FIT_CENTER
        binding.buttonResizeFragmentFixEnd.tag = ImageView.ScaleType.FIT_END
        binding.buttonResizeFragmentFixXY.tag = ImageView.ScaleType.FIT_XY
        binding.buttonResizeFragmentCenter.tag = ImageView.ScaleType.CENTER
        binding.buttonResizeFragmentCenterCrop.tag = ImageView.ScaleType.CENTER_CROP
        binding.buttonResizeFragmentCenterInside.tag = ImageView.ScaleType.CENTER_INSIDE
        binding.buttonResizeFragmentMatrix.tag = ImageView.ScaleType.MATRIX

        val buttonOnClickListener = View.OnClickListener { v ->
            scaleType = v.tag as ImageView.ScaleType
            apply(v)
        }
        binding.buttonResizeFragmentFixStart.setOnClickListener(buttonOnClickListener)
        binding.buttonResizeFragmentFixCenter.setOnClickListener(buttonOnClickListener)
        binding.buttonResizeFragmentFixEnd.setOnClickListener(buttonOnClickListener)
        binding.buttonResizeFragmentFixXY.setOnClickListener(buttonOnClickListener)
        binding.buttonResizeFragmentCenter.setOnClickListener(buttonOnClickListener)
        binding.buttonResizeFragmentCenterCrop.setOnClickListener(buttonOnClickListener)
        binding.buttonResizeFragmentCenterInside.setOnClickListener(buttonOnClickListener)
        binding.buttonResizeFragmentMatrix.setOnClickListener(buttonOnClickListener)

        if (currentCheckedButton == null) {
            currentCheckedButton = binding.buttonResizeFragmentFixCenter
        }
        apply(currentCheckedButton)
    }

    private fun apply(button: View?) {
        val width = (widthProgress / 100f * 1000).toInt()
        val height = (heightProgress / 100f * 1000).toInt()

        binding?.imageResizeFragment?.options?.shapeSize = ShapeSize(width, height, scaleType)
        binding?.imageResizeFragment?.displayImage(AssetImage.MEI_NV)

        currentCheckedButton?.isEnabled = true
        button?.isEnabled = false
        currentCheckedButton = button
    }
}
