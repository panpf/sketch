package com.github.panpf.sketch.sample.ui.viewer

import android.os.Bundle
import androidx.navigation.navArgs
import com.github.panpf.sketch.sample.databinding.FragmentContainerActivityBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingActivity

/**
 * Because the sliding back function needs to reveal the content of the previous page when sliding.
 * The Navigation component currently cannot implement add Fragment.
 * So it can only be implemented with Activity.
 */
class ImageViewerActivity : BaseBindingActivity<FragmentContainerActivityBinding>() {
    private val args by navArgs<ImageViewerActivityArgs>()

    override fun onCreate(binding: FragmentContainerActivityBinding, savedInstanceState: Bundle?) {
        val newArgs = ImageViewerPagerFragmentArgs(
            args.imageDetailJsonArray,
            args.defaultPosition
        )
        supportFragmentManager.beginTransaction()
            .replace(
                binding.fragmentContainerActivityContainerView.id,
                ImageViewerPagerFragment().apply {
                    arguments = newArgs.toBundle()
                })
            .commit()
    }
}