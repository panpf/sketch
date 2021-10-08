package me.panpf.sketch.sample.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import me.panpf.sketch.display.TransitionImageDisplayer
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.base.BaseFragment
import me.panpf.sketch.sample.databinding.FragmentRoundRectImageShaperBinding
import me.panpf.sketch.shaper.RoundRectImageShaper

class RoundRectImageShaperTestFragment :
    BaseFragment<FragmentRoundRectImageShaperBinding>() {

    private var radiusProgress = 20
    private var strokeProgress = 5

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentRoundRectImageShaperBinding.inflate(inflater, parent, false)

    override fun onInitData(
        binding: FragmentRoundRectImageShaperBinding,
        savedInstanceState: Bundle?
    ) {
        binding.imageRoundRectImageShaperFragment.options.displayer = TransitionImageDisplayer()

        binding.seekBarRoundRectImageShaperFragmentRadius.max = 100
        binding.seekBarRoundRectImageShaperFragmentRadius.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                binding.textRoundRectImageShaperFragmentRadius.text =
                    String.format("%d/%d", progress, 100)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                radiusProgress = binding.seekBarRoundRectImageShaperFragmentRadius.progress
                apply()
            }
        })
        binding.seekBarRoundRectImageShaperFragmentRadius.progress = radiusProgress

        binding.seekBarRoundRectImageShaperFragmentStroke.max = 100
        binding.seekBarRoundRectImageShaperFragmentStroke.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                binding.textRoundRectImageShaperFragmentStroke.text =
                    String.format("%d/%d", progress, 100)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                strokeProgress = binding.seekBarRoundRectImageShaperFragmentStroke.progress
                apply()
            }
        })
        binding.seekBarRoundRectImageShaperFragmentStroke.progress = strokeProgress

        apply()
    }

    private fun apply() {
        val imageShaper =
            RoundRectImageShaper(radiusProgress.toFloat()).setStroke(Color.WHITE, strokeProgress)

        binding?.imageRoundRectImageShaperFragment?.options?.shaper = imageShaper
        binding?.imageRoundRectImageShaperFragment?.displayImage(AssetImage.TYPE_TEST_JPG)
    }
}