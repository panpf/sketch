package com.github.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.sample.base.BindingFragment
import com.github.panpf.sketch.sample.databinding.FragmentImageBinding
import com.github.panpf.sketch.internal.showCircleProgressIndicator
import com.github.panpf.sketch.internal.showDataFrom

class ImageFragment : BindingFragment<FragmentImageBinding>() {

    private val args by navArgs<ImageFragmentArgs>()

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        FragmentImageBinding.inflate(inflater, parent, false)

    override fun onInitViews(binding: FragmentImageBinding, savedInstanceState: Bundle?) {
        super.onInitViews(binding, savedInstanceState)
        binding.imageFragmentImageView.showCircleProgressIndicator()
        binding.imageFragmentImageView.showDataFrom()
    }

    override fun onInitData(binding: FragmentImageBinding, savedInstanceState: Bundle?) {
        binding.imageFragmentImageView.displayImage(args.url)
    }
}