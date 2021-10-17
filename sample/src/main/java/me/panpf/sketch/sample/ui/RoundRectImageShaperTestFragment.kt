package me.panpf.sketch.sample.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import me.panpf.sketch.display.TransitionImageDisplayer
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.base.BindingFragment
import me.panpf.sketch.sample.databinding.FragmentRoundRectImageShaperBinding
import me.panpf.sketch.sample.vm.RoundedShaperTestViewModel
import me.panpf.sketch.shaper.RoundRectImageShaper

class RoundRectImageShaperTestFragment :
    BindingFragment<FragmentRoundRectImageShaperBinding>() {

    private val viewModel by viewModels<RoundedShaperTestViewModel>()

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentRoundRectImageShaperBinding.inflate(inflater, parent, false)

    override fun onInitData(
        binding: FragmentRoundRectImageShaperBinding,
        savedInstanceState: Bundle?
    ) {
        binding.roundedShaperImage.options.displayer = TransitionImageDisplayer()

        binding.roundedShaperRadiusSeekBar.apply {
            max = 100
            progress = viewModel.testData.value!!.roundedRadius
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }

                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int, fromUser: Boolean
                ) {
                    viewModel.changeRoundedRadius(progress)
                }
            })
        }

        binding.roundedShaperStrokeWidthSeekBar.apply {
            max = 100
            progress = viewModel.testData.value!!.strokeWidth
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }

                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int, fromUser: Boolean
                ) {
                    viewModel.changeStrokeWidth(progress)
                }
            })
        }

        viewModel.testData.observe(viewLifecycleOwner) { test ->
            test ?: return@observe
            val roundedRadius = test.roundedRadius
            val strokeWidth = test.strokeWidth

            binding.roundedShaperImage.apply {
                options.shaper = RoundRectImageShaper(roundedRadius.toFloat())
                    .setStroke(Color.WHITE, strokeWidth)
                displayImage(AssetImage.MEI_NV)
            }

            binding.roundedShaperRadiusValueText.text = "%d/%d".format(roundedRadius, 100)
            binding.roundedShaperStrokeWidthValueText.text = "%d/%d".format(strokeWidth, 100)
        }
    }
}