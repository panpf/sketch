package com.github.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.sample.base.BindingFragment
import com.github.panpf.sketch.sample.databinding.FragmentImageBinding

class ImageFragment : BindingFragment<FragmentImageBinding>() {

    private val args by navArgs<ImageFragmentArgs>()

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        FragmentImageBinding.inflate(inflater, parent, false)

    override fun onInitData(binding: FragmentImageBinding, savedInstanceState: Bundle?) {
        binding.imageFragmentImageView.apply {
            showCircleProgressIndicator()
            displayImage(args.url)
        }
    }
}