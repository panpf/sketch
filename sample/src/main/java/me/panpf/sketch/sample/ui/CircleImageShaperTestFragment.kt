package me.panpf.sketch.sample.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import me.panpf.sketch.display.TransitionImageDisplayer
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.base.BaseBindingFragment
import me.panpf.sketch.sample.databinding.FragmentCircleImageShaperBinding
import me.panpf.sketch.shaper.CircleImageShaper

class CircleImageShaperTestFragment : BaseBindingFragment<FragmentCircleImageShaperBinding>() {

    private var strokeProgress = 5

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentCircleImageShaperBinding.inflate(inflater, parent, false)

    override fun onInitData(
        binding: FragmentCircleImageShaperBinding,
        savedInstanceState: Bundle?
    ) {
        binding.imageCircleImageShaperFragment.options.displayer = TransitionImageDisplayer()

        binding.seekBarCircleImageShaperFragmentStroke.apply {
            max = 100
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    binding.textCircleImageShaperFragmentStroke.text =
                        String.format("%d/%d", progress, 100)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    strokeProgress = progress
                    apply(binding)
                }
            })
            progress = strokeProgress
        }

        apply(binding)
    }

    private fun apply(binding: FragmentCircleImageShaperBinding) {
        val imageShaper = CircleImageShaper().setStroke(Color.WHITE, strokeProgress)

        binding.imageCircleImageShaperFragment.options.shaper = imageShaper
        binding.imageCircleImageShaperFragment.displayImage(AssetImage.TYPE_TEST_JPG)
    }
}