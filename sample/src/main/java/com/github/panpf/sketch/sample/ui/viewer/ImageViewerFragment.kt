package com.github.panpf.sketch.sample.ui.viewer

import android.Manifest
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.github.panpf.assemblyadapter.pager.FragmentItemFactory
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.sample.databinding.ImageViewerFragmentBinding
import com.github.panpf.sketch.sample.model.ImageDetail
import com.github.panpf.sketch.sample.prefsService
import com.github.panpf.sketch.sample.ui.base.BindingFragment
import com.github.panpf.sketch.sample.ui.base.parentViewModels
import com.github.panpf.sketch.sample.ui.setting.ImageInfoDialogFragment
import com.github.panpf.sketch.sample.util.observeWithFragmentView
import com.github.panpf.sketch.sample.widget.SwipeBackLayout
import com.github.panpf.sketch.stateimage.MemoryCacheStateImage
import com.github.panpf.sketch.viewability.showSectorProgressIndicator
import kotlinx.coroutines.launch

// todo Added entry and exit transition animations
class ImageViewerFragment : BindingFragment<ImageViewerFragmentBinding>() {

    private val args by navArgs<ImageViewerFragmentArgs>()
    private val viewModel by viewModels<ImageViewerViewModel>()
    private val pagerViewModel by parentViewModels<ImageViewerPagerViewModel>()
    private val swipeExitViewModel by parentViewModels<ImageViewerSwipeExitViewModel>()
    private val requestPermissionResult = registerForActivityResult(RequestPermission()) {
        lifecycleScope.launch {
            handleActionResult(viewModel.save(args.imageUri))
        }
    }

    override fun onViewCreated(binding: ImageViewerFragmentBinding, savedInstanceState: Bundle?) {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    binding.imageViewerSwipeBack.back()
                }
            })

        binding.root.background = null

        binding.imageViewerZoomImage.apply {
            showSectorProgressIndicator()
            prefsService.readModeEnabled.stateFlow.observeWithFragmentView(this@ImageViewerFragment) {
                readModeEnabled = it
            }
            setOnClickListener {
                binding.imageViewerSwipeBack.back()
            }
            setOnLongClickListener {
                startImageInfoDialog(this)
                true
            }
            displayImage(args.imageUri) {
                args.placeholderImageMemoryCacheKey?.let {
                    placeholder(MemoryCacheStateImage(it))
                }
                crossfade()
                lifecycle(viewLifecycleOwner.lifecycle)
            }
        }

        binding.imageViewerSwipeBack.callback =
            object : SwipeBackLayout.Callback {
                override fun onProgressChanged(progress: Float) {
                    swipeExitViewModel.progressChangedEvent.value = progress
                }

                override fun onBack() {
                    swipeExitViewModel.backEvent.value = true
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
                    binding.imageViewerZoomImage.rotateBy(90)
                }
            }
            infoEvent.listen(viewLifecycleOwner) {
                if (isResumed) {
                    startImageInfoDialog(binding.imageViewerZoomImage)
                }
            }
        }
    }

    private fun startImageInfoDialog(imageView: ImageView){
        val arguments1 =
            ImageInfoDialogFragment.createDirectionsFromImageView(imageView, null).arguments
        childFragmentManager.beginTransaction()
            .add(ImageInfoDialogFragment().apply {
                arguments = arguments1
            }, null)
            .commit()
    }

    class ItemFactory : FragmentItemFactory<ImageDetail>(ImageDetail::class) {

        override fun createFragment(
            bindingAdapterPosition: Int,
            absoluteAdapterPosition: Int,
            data: ImageDetail
        ): Fragment = ImageViewerFragment().apply {
            arguments =
                ImageViewerFragmentArgs(data.url, data.placeholderImageMemoryCacheKey).toBundle()
        }
    }
}