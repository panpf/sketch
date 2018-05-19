package me.panpf.sketch.sample.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.fragment_circle_image_shaper.*
import me.panpf.sketch.display.TransitionImageDisplayer
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.BaseFragment
import me.panpf.sketch.sample.BindContentView
import me.panpf.sketch.sample.R
import me.panpf.sketch.shaper.CircleImageShaper

@BindContentView(R.layout.fragment_circle_image_shaper)
class CircleImageShaperTestFragment : BaseFragment() {

    private var strokeProgress = 5

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        image_circleImageShaperFragment.options.displayer = TransitionImageDisplayer()

        seekBar_circleImageShaperFragment_stroke.max = 100
        seekBar_circleImageShaperFragment_stroke.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                text_circleImageShaperFragment_stroke.text = String.format("%d/%d", progress, 100)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                strokeProgress = seekBar_circleImageShaperFragment_stroke.progress
                apply()
            }
        })
        seekBar_circleImageShaperFragment_stroke.progress = strokeProgress

        apply()
    }

    private fun apply() {
        val imageShaper = CircleImageShaper().setStroke(Color.WHITE, strokeProgress)

        image_circleImageShaperFragment.options.shaper = imageShaper
        image_circleImageShaperFragment.displayImage(AssetImage.TYPE_TEST_JPG)
    }
}