package com.github.panpf.sketch.sample.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.databinding.FragmentImageHugeBinding
import com.github.panpf.sketch.sample.ui.base.BindingFragment
import com.github.panpf.sketch.viewability.showRingProgressIndicator

class HugeImageFragment : BindingFragment<FragmentImageHugeBinding>() {

    private val args by navArgs<HugeImageFragmentArgs>()

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        FragmentImageHugeBinding.inflate(inflater, parent, false)

    override fun onInitData(binding: FragmentImageHugeBinding, savedInstanceState: Bundle?) {
        binding.hugeImageFragmentZoomImageView.apply {
            showRingProgressIndicator()
            zoomAbility.readModeEnabled = true  // todo 阅读模式开关
            zoomAbility.showTileBounds = appSettingsService.showTileBoundsInHugeImagePage.value
            displayImage(args.imageUri) {
                lifecycle(viewLifecycleOwner.lifecycle)
            }
        }

        binding.hugeImageFragmentTileMapImageView.apply {
            setZoomImageView(binding.hugeImageFragmentZoomImageView)
            displayImage(args.imageUri)
        }
    }
}