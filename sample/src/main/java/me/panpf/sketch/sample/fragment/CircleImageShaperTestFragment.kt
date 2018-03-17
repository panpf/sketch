package me.panpf.sketch.sample.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import me.panpf.sketch.display.TransitionImageDisplayer
import me.panpf.sketch.sample.*
import me.panpf.sketch.sample.widget.SampleImageView
import me.panpf.sketch.shaper.CircleImageShaper

@BindContentView(R.layout.fragment_circle_image_shaper)
class CircleImageShaperTestFragment : BaseFragment() {
    val imageView: SampleImageView by bindView(R.id.image_circleImageShaperFragment)
    val strokeSeekBar: SeekBar by bindView(R.id.seekBar_circleImageShaperFragment_stroke)
    val strokeProgressTextView: TextView by bindView(R.id.text_circleImageShaperFragment_stroke)

    private var strokeProgress = 5

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        imageView.options.displayer = TransitionImageDisplayer()

        strokeSeekBar.max = 100
        strokeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                strokeProgressTextView.text = String.format("%d/%d", progress, 100)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                strokeProgress = strokeSeekBar.progress
                apply()
            }
        })
        strokeSeekBar.progress = strokeProgress

        apply()
    }

    private fun apply() {
        val imageShaper = CircleImageShaper().setStroke(Color.WHITE, strokeProgress)

        imageView.options.shaper = imageShaper
        imageView.displayImage(AssetImage.TYPE_TEST_JPG)
    }
}