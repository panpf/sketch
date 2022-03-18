package com.github.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.sample.base.BindingFragment
import com.github.panpf.sketch.sample.bean.ImageDetail
import com.github.panpf.sketch.sample.databinding.FragmentImageDetailBinding
import com.github.panpf.sketch.stateimage.StateImage
import com.github.panpf.sketch.viewability.showRingProgressIndicator
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ImageDetailFragment : BindingFragment<FragmentImageDetailBinding>() {

    private val args by navArgs<ImageDetailFragmentArgs>()

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        FragmentImageDetailBinding.inflate(inflater, parent, false)

    override fun onInitViews(binding: FragmentImageDetailBinding, savedInstanceState: Bundle?) {
        super.onInitViews(binding, savedInstanceState)
        binding.imageFragmentZoomImageView.apply {
            showRingProgressIndicator()
            zoomAbility.readModeEnabled = true
            zoomAbility.showBlockBounds = true
        }
    }

    override fun onInitData(binding: FragmentImageDetailBinding, savedInstanceState: Bundle?) {
        val imageDetail = Json.decodeFromString<ImageDetail>(args.imageDetailJson)
        binding.imageFragmentZoomImageView.displayImage(imageDetail.firstMiddenUrl) {
            placeholderImage(StateImage.memoryCache(imageDetail.placeholderImageMemoryKey, null))
        }
        binding.imageFragmentZoomImageView.apply {
            zoomAbility.useTiles = !imageDetail.firstMiddenUrl.contains("sample_huge_card.png")
        }
    }
}