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
import me.panpf.sketch.sample.databinding.FragmentCircleImageShaperBinding
import me.panpf.sketch.sample.vm.CircleShaperTestViewModel
import me.panpf.sketch.shaper.CircleImageShaper

class CircleImageShaperTestFragment : BindingFragment<FragmentCircleImageShaperBinding>() {

    private val viewModel by viewModels<CircleShaperTestViewModel>()

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentCircleImageShaperBinding.inflate(inflater, parent, false)

    override fun onInitData(
        binding: FragmentCircleImageShaperBinding,
        savedInstanceState: Bundle?
    ) {
        binding.circleShaperImage.options.displayer = TransitionImageDisplayer()

        binding.circleShaperStrokeWidthSeekBar.apply {
            max = 100
            progress = viewModel.strokeWidthData.value!!
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

        viewModel.strokeWidthData.observe(viewLifecycleOwner) {
            val strokeWidth = it ?: return@observe

            binding.circleShaperImage.apply {
                options.shaper = CircleImageShaper().setStroke(Color.WHITE, strokeWidth)
                displayImage(AssetImage.MEI_NV)
            }

            binding.circleShaperStrokeWidthText.text = "%d/%d".format(strokeWidth, 100)
        }
    }
}