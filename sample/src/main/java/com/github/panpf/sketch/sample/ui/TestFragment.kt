package com.github.panpf.sketch.sample.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView.ScaleType
import com.github.panpf.sketch.sample.base.BindingFragment
import com.github.panpf.sketch.sample.databinding.FragmentTestBinding

class TestFragment : BindingFragment<FragmentTestBinding>() {

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        FragmentTestBinding.inflate(inflater, parent, false)

    override fun onInitData(binding: FragmentTestBinding, savedInstanceState: Bundle?) {
        binding.testFragmentImageView.apply {
            scaleType = ScaleType.CENTER
//            setImageDrawable(
//                NewCircleProgressDrawable(
//                    200,
//                    0x44000000,
//                    Color.WHITE,
//                    Color.WHITE,
//                    5f
//                ).apply {
//                    progress = 1.4f
//                    binding.testFragmentImageView.post {
//                        start()
//                    }
//                })
        }
    }
}