package me.panpf.sketch.sample.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import me.xiaopan.sketch.display.TransitionImageDisplayer
import me.xiaopan.sketch.shaper.RoundRectImageShaper
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.BaseFragment
import me.panpf.sketch.sample.BindContentView
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.widget.SampleImageView
import me.panpf.sketch.sample.bindView

@BindContentView(R.layout.fragment_round_rect_image_shaper)
class RoundRectImageShaperTestFragment : BaseFragment() {
    val imageView: SampleImageView by bindView(R.id.image_roundRectImageShaperFragment)
    val radiusSeekBar: SeekBar by bindView(R.id.seekBar_roundRectImageShaperFragment_radius)
    val radiusProgressTextView: TextView by bindView(R.id.text_roundRectImageShaperFragment_radius)
    val strokeSeekBar: SeekBar by bindView(R.id.seekBar_roundRectImageShaperFragment_stroke)
    val strokeProgressTextView: TextView by bindView(R.id.text_roundRectImageShaperFragment_stroke)

    private var radiusProgress = 20
    private var strokeProgress = 5

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView.options.displayer = TransitionImageDisplayer()

        radiusSeekBar.max = 100
        radiusSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                radiusProgressTextView.text = String.format("%d/%d", progress, 100)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                radiusProgress = radiusSeekBar.progress
                apply()
            }
        })
        radiusSeekBar.progress = radiusProgress

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
        val imageShaper = RoundRectImageShaper(radiusProgress.toFloat()).setStroke(Color.WHITE, strokeProgress)

        imageView.options.shaper = imageShaper
        imageView.displayImage(AssetImage.TYPE_TEST_JPG)
    }
}