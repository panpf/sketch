package com.github.panpf.sketch.sample.ui.test

import android.os.Bundle
import com.github.panpf.sketch.sample.databinding.TestFragmentBinding
import com.github.panpf.sketch.sample.ui.base.BindingFragment

class TestFragment : BindingFragment<TestFragmentBinding>() {

    override fun onViewCreated(binding: TestFragmentBinding, savedInstanceState: Bundle?) {
        binding.testImage.apply {
//            scaleType = ScaleType.CENTER
//            setImageDrawable(RingProgressDrawable(160).apply {
////                    progress = 1.4f
//                binding.testFragmentImageView.postDelayed({
//                    viewLifecycleOwner.lifecycleScope.launch {
//                        progress = 0.2f
//                        delay(1000)
//                        progress = 0.4f
//                        delay(1000)
//                        progress = 0.6f
//                        delay(1000)
//                        progress = 0.8f
//                        delay(1000)
//                        progress = 1f
//                    }
//                }, 1000)
//            })
//            displayImage(AssetImages.ANIMS[0]) {
//                placeholder(
//                    IconStateImage(R.drawable.ic_image_outline, ResColor(R.color.placeholder_bg))
//                )
//                crossfade()
//                resizeApplyToDrawable()
//                lifecycle(viewLifecycleOwner.lifecycle)
//                bitmapMemoryCachePolicy(DISABLED)
//            }
//            displayImage(AssetImages.FORMATS[0]) {
////                placeholder(
////                    IconStateImage(R.drawable.ic_image_outline, ResColor(R.color.placeholder_bg))
////                )
////                crossfade()
////                resizeApplyToDrawable()
////                lifecycle(viewLifecycleOwner.lifecycle)
////                bitmapMemoryCachePolicy(DISABLED)
//            }
        }
    }
}