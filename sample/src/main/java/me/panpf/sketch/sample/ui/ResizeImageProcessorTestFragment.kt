package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import kotlinx.android.synthetic.main.fragment_resize.*
import me.panpf.sketch.display.TransitionImageDisplayer
import me.panpf.sketch.request.Resize
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.BaseFragment
import me.panpf.sketch.sample.BindContentView
import me.panpf.sketch.sample.R

@BindContentView(R.layout.fragment_resize)
class ResizeImageProcessorTestFragment : BaseFragment() {

    private var widthProgress = 50
    private var heightProgress = 50
    private var scaleType: ImageView.ScaleType = ImageView.ScaleType.FIT_CENTER
    private var currentCheckedButton: View? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        image_resizeFragment.options.displayer = TransitionImageDisplayer()

        seekBar_resizeFragment_width.max = 100
        seekBar_resizeFragment_width.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (progress < 20) {
                    seekBar_resizeFragment_width.progress = 20
                    return
                }

                val width = (seekBar_resizeFragment_width.progress / 100f * 1000).toInt()
                text_resizeFragment_width.text = String.format("%d/%d", width, 1000)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                widthProgress = seekBar_resizeFragment_width.progress
                apply(currentCheckedButton)
            }
        })
        seekBar_resizeFragment_width.progress = widthProgress

        seekBar_resizeFragment_height.max = 100
        seekBar_resizeFragment_height.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (progress < 20) {
                    seekBar_resizeFragment_height.progress = 20
                    return
                }
                val height = (seekBar_resizeFragment_height.progress / 100f * 1000).toInt()
                text_resizeFragment_height.text = String.format("%d/%d", height, 1000)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                heightProgress = seekBar_resizeFragment_height.progress
                apply(currentCheckedButton)
            }
        })
        seekBar_resizeFragment_height.progress = heightProgress

        button_resizeFragment_fixStart.tag = ImageView.ScaleType.FIT_START
        button_resizeFragment_fixCenter.tag = ImageView.ScaleType.FIT_CENTER
        button_resizeFragment_fixEnd.tag = ImageView.ScaleType.FIT_END
        button_resizeFragment_fixXY.tag = ImageView.ScaleType.FIT_XY
        button_resizeFragment_center.tag = ImageView.ScaleType.CENTER
        button_resizeFragment_centerCrop.tag = ImageView.ScaleType.CENTER_CROP
        button_resizeFragment_centerInside.tag = ImageView.ScaleType.CENTER_INSIDE
        button_resizeFragment_matrix.tag = ImageView.ScaleType.MATRIX

        val buttonOnClickListener = View.OnClickListener { v ->
            scaleType = v.tag as ImageView.ScaleType
            apply(v)
        }
        button_resizeFragment_fixStart.setOnClickListener(buttonOnClickListener)
        button_resizeFragment_fixCenter.setOnClickListener(buttonOnClickListener)
        button_resizeFragment_fixEnd.setOnClickListener(buttonOnClickListener)
        button_resizeFragment_fixXY.setOnClickListener(buttonOnClickListener)
        button_resizeFragment_center.setOnClickListener(buttonOnClickListener)
        button_resizeFragment_centerCrop.setOnClickListener(buttonOnClickListener)
        button_resizeFragment_centerInside.setOnClickListener(buttonOnClickListener)
        button_resizeFragment_matrix.setOnClickListener(buttonOnClickListener)

        if (currentCheckedButton == null) {
            currentCheckedButton = button_resizeFragment_fixCenter
        }
        apply(currentCheckedButton)
    }

    private fun apply(button: View?) {
        if (button == null) {
            return
        }

        val width = (widthProgress / 100f * 1000).toInt()
        val height = (heightProgress / 100f * 1000).toInt()

        image_resizeFragment.options.resize = Resize(width, height, scaleType)
        image_resizeFragment.displayImage(AssetImage.MEI_NV)

        currentCheckedButton?.isEnabled = true
        button.isEnabled = false
        currentCheckedButton = button
    }
}
