package me.panpf.sketch.sample.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.fragment_round_rect_image_shaper.*
import me.panpf.sketch.display.TransitionImageDisplayer
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.BaseFragment
import me.panpf.sketch.sample.BindContentView
import me.panpf.sketch.sample.R
import me.panpf.sketch.shaper.RoundRectImageShaper

@BindContentView(R.layout.fragment_round_rect_image_shaper)
class RoundRectImageShaperTestFragment : BaseFragment() {

    private var radiusProgress = 20
    private var strokeProgress = 5

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        image_roundRectImageShaperFragment.options.displayer = TransitionImageDisplayer()

        seekBar_roundRectImageShaperFragment_radius.max = 100
        seekBar_roundRectImageShaperFragment_radius.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                text_roundRectImageShaperFragment_radius.text = String.format("%d/%d", progress, 100)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                radiusProgress = seekBar_roundRectImageShaperFragment_radius.progress
                apply()
            }
        })
        seekBar_roundRectImageShaperFragment_radius.progress = radiusProgress

        seekBar_roundRectImageShaperFragment_stroke.max = 100
        seekBar_roundRectImageShaperFragment_stroke.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                text_roundRectImageShaperFragment_stroke.text = String.format("%d/%d", progress, 100)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                strokeProgress = seekBar_roundRectImageShaperFragment_stroke.progress
                apply()
            }
        })
        seekBar_roundRectImageShaperFragment_stroke.progress = strokeProgress

        apply()
    }

    private fun apply() {
        val imageShaper = RoundRectImageShaper(radiusProgress.toFloat()).setStroke(Color.WHITE, strokeProgress)

        image_roundRectImageShaperFragment.options.shaper = imageShaper
        image_roundRectImageShaperFragment.displayImage(AssetImage.TYPE_TEST_JPG)
    }
}