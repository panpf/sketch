package com.github.panpf.sketch.sample.ui.viewer

import android.Manifest
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.panpf.assemblyadapter.pager.FragmentItemFactory
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.sample.prefsService
import com.github.panpf.sketch.sample.databinding.ImageViewerFragmentBinding
import com.github.panpf.sketch.sample.model.ImageDetail
import com.github.panpf.sketch.sample.ui.base.BindingFragment
import com.github.panpf.sketch.sample.ui.base.parentViewModels
import com.github.panpf.sketch.sample.ui.setting.ImageInfoDialogFragment
import com.github.panpf.sketch.sample.util.observeWithFragmentView
import com.github.panpf.sketch.viewability.showRingProgressIndicator
import kotlinx.coroutines.launch

// todo 增加上下滑动退出功能
// todo 增加进入和退出过渡动画
class ImageViewerFragment : BindingFragment<ImageViewerFragmentBinding>() {

    private val args by navArgs<ImageViewerFragmentArgs>()
    private val viewModel by viewModels<ImageViewerViewModel>()
    private val pagerViewModel by parentViewModels<ImageViewerPagerViewModel>()
    private val requestPermissionResult = registerForActivityResult(RequestPermission()) {
        lifecycleScope.launch {
            handleActionResult(viewModel.save(args.imageUri))
        }
    }

    override fun onViewCreated(binding: ImageViewerFragmentBinding, savedInstanceState: Bundle?) {
        binding.imageViewerZoomImage.apply {
            showRingProgressIndicator()
            prefsService.readModeEnabled.stateFlow.observeWithFragmentView(this@ImageViewerFragment) {
                zoomAbility.readModeEnabled = it
            }
            setOnClickListener {
                findNavController().popBackStack()
            }
            displayImage(args.imageUri) {
                lifecycle(viewLifecycleOwner.lifecycle)
            }
        }

        pagerViewModel.apply {
            shareEvent.listen(viewLifecycleOwner) {
                if (isResumed) {
                    lifecycleScope.launch {
                        handleActionResult(viewModel.share(args.imageUri))
                    }
                }
            }
            saveEvent.listen(viewLifecycleOwner) {
                if (isResumed) {
                    requestPermissionResult.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
            rotateEvent.listen(viewLifecycleOwner) {
                if (isResumed) {
                    binding.imageViewerZoomImage.zoomAbility.rotateBy(90)
                }
            }
            infoEvent.listen(viewLifecycleOwner) {
                if (isResumed) {
                    findNavController().navigate(
                        ImageInfoDialogFragment.createDirectionsFromImageView(
                            binding.imageViewerZoomImage,
                            args.imageUri,
                        )
                    )
                }
            }
        }
    }

    class ItemFactory : FragmentItemFactory<ImageDetail>(ImageDetail::class) {

        override fun createFragment(
            bindingAdapterPosition: Int,
            absoluteAdapterPosition: Int,
            data: ImageDetail
        ): Fragment = ImageViewerFragment().apply {
            arguments = ImageViewerFragmentArgs(data.url).toBundle()
        }
    }
}