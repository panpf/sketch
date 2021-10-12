package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import me.panpf.sketch.sample.base.BaseFragment
import me.panpf.sketch.sample.databinding.FragmentImageOrientationTestBinding

class ImageOrientationTestFragment : BaseFragment<FragmentImageOrientationTestBinding>() {

    private val args by navArgs<ImageOrientationTestFragmentArgs>()

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentImageOrientationTestBinding.inflate(inflater, parent, false)

    override fun onInitData(
        binding: FragmentImageOrientationTestBinding,
        savedInstanceState: Bundle?
    ) {
        binding.imageImageOrientationTestFragmentBefore.apply {
            options.isCorrectImageOrientationDisabled = true
            displayImage(args.imagePath)
        }

        binding.imageImageOrientationTestFragmentAfter.displayImage(args.imagePath)
    }
}
