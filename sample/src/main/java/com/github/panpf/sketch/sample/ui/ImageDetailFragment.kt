package com.github.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.sample.base.BindingFragment
import com.github.panpf.sketch.sample.bean.ImageDetail
import com.github.panpf.sketch.sample.databinding.FragmentImageBinding
import com.github.panpf.sketch.stateimage.StateImage
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ImageDetailFragment : BindingFragment<FragmentImageBinding>() {

    private val args by navArgs<ImageDetailFragmentArgs>()

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        FragmentImageBinding.inflate(inflater, parent, false)

    override fun onInitViews(binding: FragmentImageBinding, savedInstanceState: Bundle?) {
        super.onInitViews(binding, savedInstanceState)
        binding.imageFragmentImageView.showCircleProgressIndicator()
    }

    override fun onInitData(binding: FragmentImageBinding, savedInstanceState: Bundle?) {
        val imageDetail = Json.decodeFromString<ImageDetail>(args.imageDetailJson)
        binding.imageFragmentImageView.displayImage(imageDetail.firstMiddenUrl) {
            placeholderImage(
                StateImage.memoryCache(
                    imageDetail.placeholderImageMemoryKey,
                    null
                )
            )
        }
    }
}