package com.github.panpf.sketch.sample.ui

import android.app.AlertDialog
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.display.FadeInImageDisplayer
import com.github.panpf.sketch.drawable.SketchGifDrawable
import com.github.panpf.sketch.request.*
import com.github.panpf.sketch.sample.util.CompactDisplayListener
import com.github.panpf.sketch.sample.util.FixedThreeLevelScales
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.base.BindingFragment
import com.github.panpf.sketch.sample.base.parentViewModels
import com.github.panpf.sketch.sample.databinding.FragmentImageBinding
import com.github.panpf.sketch.sample.util.*
import com.github.panpf.sketch.sample.vm.ImageViewModel
import com.github.panpf.sketch.sample.vm.ShowImageMenuViewModel
import com.github.panpf.sketch.sample.vm.ShowingImageChangedViewModel
import com.github.panpf.sketch.state.MemoryCacheStateImage
import com.github.panpf.sketch.state.StateImage
import com.github.panpf.sketch.uri.UriModel
import com.github.panpf.sketch.util.SketchUtils
import com.github.panpf.sketch.zoom.AdaptiveTwoLevelScales
import java.util.*

class ImageFragment : BindingFragment<FragmentImageBinding>() {

    val args by navArgs<ImageFragmentArgs>()
    private val viewModel by viewModels<ImageViewModel> {
        ImageViewModel.Factory(requireActivity().application, args)
    }
    private val showImageMenuViewModel by viewModels<ShowImageMenuViewModel>()
    private val showingImageChangedViewModel by parentViewModels<ShowingImageChangedViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        childFragmentManager.beginTransaction()
            .add(ImageMenuFragment(), "ImageMenuFragment")
            .commit()
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        FragmentImageBinding.inflate(inflater, parent, false)

    override fun onInitViews(binding: FragmentImageBinding, savedInstanceState: Bundle?) {
        super.onInitViews(binding, savedInstanceState)

        binding.imageFragmentZoomImage.apply {
            options.isDecodeGifImage = true

            displayListener = CompactDisplayListener(
                onStarted = {
                    binding.imageFragmentHint.loading(null)
                },
                onCompleted = { _, _, _ ->
                    binding.imageFragmentHint.hidden()
                    notifyShowingImageChanged(isVisibleToUser)
                    startOrPauseGif(binding, true)
                },
                onError = {
                    showError(binding)
                },
                onCanceled = { cause: CancelCause ->
                    showCanceled(binding, cause)
                },
            )

            downloadProgressListener = DownloadProgressListener { totalLength, completedLength ->
                binding.imageFragmentHint.setProgress(totalLength, completedLength)
            }

            zoomer.apply {
                appSettingsService.threeLevelZoomModeEnabled.observe(viewLifecycleOwner) {
                    if (it == true) {
                        setZoomScales(FixedThreeLevelScales())
                    } else {
                        setZoomScales(AdaptiveTwoLevelScales())
                    }
                }

                appSettingsService.readModeEnabled.observe(viewLifecycleOwner) {
                    isReadMode = it == true
                }

                setOnViewTapListener { _, x, y ->
                    if (parentFragment is ImageViewerFragment) {
                        exitViewer()
                    } else {
                        showBlockImageByTouchPoint(binding, x, y)
                    }
                }

                setOnViewLongPressListener { _, _, _ ->
                    showImageMenuViewModel.showImageMenuEvent.postValue(binding)
                }

                registerUserVisibleChangedListener(viewLifecycleOwner) {
                    val pauseBlockDisplayer =
                        appSettingsService.pauseBlockDisplayWhenPageNoVisibleEnabled.value ?: false
                    if (pauseBlockDisplayer) {
                        blockDisplayer.setPause(!isVisibleToUser)
                    }
                }
            }
        }

        binding.imageFragmentMapping.apply {
            if (!args.showSmallMap) {
                isVisible = false
            } else {
                options.apply {
                    displayer = FadeInImageDisplayer()
                    setMaxSize(600, 600)
                }

                // Follow Matrix changes to refresh the display area
                val visibleRect = Rect()
                binding.imageFragmentZoomImage.zoomer.addOnMatrixChangeListener { imageZoomer ->
                    imageZoomer.getVisibleRect(visibleRect)
                    update(imageZoomer.drawableSize, visibleRect)
                }

                // Follow the fragments to refresh the block area
                binding.imageFragmentZoomImage.zoomer.blockDisplayer.setOnBlockChangedListener {
                    blockChanged(it)
                }

                // Click MappingView to locate to the specified location
                setOnSingleClickListener { x: Float, y: Float ->
                    if (width == 0 || height == 0) return@setOnSingleClickListener false
                    val drawable = binding.imageFragmentZoomImage.drawable
                        ?.takeIf { it.intrinsicWidth != 0 && it.intrinsicHeight != 0 }
                        ?: return@setOnSingleClickListener false

                    val widthScale = drawable.intrinsicWidth.toFloat() / width
                    val heightScale = drawable.intrinsicHeight.toFloat() / height
                    val realX = x * widthScale
                    val realY = y * heightScale

                    val showLocationAnimation =
                        appSettingsService.smallMapLocationAnimateEnabled.value ?: false
                    binding.imageFragmentZoomImage.zoomer.location(
                        realX, realY, showLocationAnimation
                    )
                    return@setOnSingleClickListener true
                }
            }
        }
    }

    override fun onInitData(binding: FragmentImageBinding, savedInstanceState: Bundle?) {
        registerUserVisibleChangedListener(viewLifecycleOwner) {
            startOrPauseGif(binding, false)
            notifyShowingImageChanged(it)
        }

        viewModel.imageUrl.observe(viewLifecycleOwner) { imageUri ->
            imageUri!!
            binding.imageFragmentZoomImage.apply {
                options.apply {
                    isDecodeGifImage = true
                    val loadingStateImage = getLoadingStateImage(imageUri)
                    loadingImage = loadingStateImage
                    displayer = if (loadingStateImage == null) FadeInImageDisplayer() else null
                }
                displayImage(imageUri)
            }
            binding.imageFragmentMapping.displayImage(imageUri)
        }
    }

    private fun showError(binding: FragmentImageBinding) {
        binding.imageFragmentHint.hint(R.drawable.ic_error, "Image display failed", "Again") {
            binding.imageFragmentZoomImage.displayImage(viewModel.imageUrl.value)
        }
    }

    private fun showCanceled(binding: FragmentImageBinding, cause: CancelCause) {
        var hintText: String? = null
        var buttonName: String? = null
        if (cause == CancelCause.PAUSE_DOWNLOAD) {
            hintText = "Pause to download new image for saving traffic"
            buttonName = "I do not care"
        } else if (cause == CancelCause.PAUSE_LOAD) {
            hintText = "Paused to load new image"
            buttonName = "Forced to load"
        }
        if (hintText != null && buttonName != null) {
            binding.imageFragmentHint.hint(R.drawable.ic_error, hintText, buttonName) {
                binding.imageFragmentZoomImage.apply {
                    val oldRequestLevel = options.requestLevel
                    options.requestLevel = RequestLevel.NET
                    displayImage(viewModel.imageUrl.value)
                    options.requestLevel = oldRequestLevel
                }
            }
        }
    }

    private fun notifyShowingImageChanged(isVisibleToUser: Boolean) {
        if (isVisibleToUser) {
            showingImageChangedViewModel.imageChangedData.postValue(viewModel.imageUrl.value)
        }
    }

    private fun startOrPauseGif(binding: FragmentImageBinding, fromDisplayCompleted: Boolean) {
        val drawable = binding.imageFragmentZoomImage.drawable
        val lastDrawable = SketchUtils.getLastDrawable(drawable)
        if (lastDrawable is SketchGifDrawable) {
            lastDrawable.followPageVisible(isVisibleToUser, fromDisplayCompleted)
        }
    }

    private fun getLoadingStateImage(imageUri: String): StateImage? {
        val loadingOptionsKey =
            args.loadingOptionsKey?.takeIf { it.isNotEmpty() } ?: return null
        val uriModel = UriModel.match(requireContext(), imageUri) ?: return null
        val memoryCacheKey = SketchUtils.makeRequestKey(
            args.normalQualityUrl, uriModel, loadingOptionsKey
        )
        val memoryCache = Sketch.with(requireContext()).configuration.memoryCache
        return if (memoryCache.get(memoryCacheKey) != null) {
            MemoryCacheStateImage(memoryCacheKey, null)
        } else {
            null
        }
    }

    private fun exitViewer() {
        findNavController().popBackStack()
    }

    private fun showBlockImageByTouchPoint(binding: FragmentImageBinding, x: Float, y: Float) {
        AlertDialog.Builder(activity).apply {
            val zoomer = binding.imageFragmentZoomImage.zoomer
            val drawablePoint = zoomer.touchPointToDrawablePoint(x.toInt(), y.toInt()) ?: return
            val block = zoomer.getBlockByDrawablePoint(drawablePoint.x, drawablePoint.y) ?: return
            val blockBitmap = block.bitmap ?: return
            setView(ImageView(activity).apply {
                setImageBitmap(blockBitmap)
            })
            setPositiveButton("取消", null)
        }.show()
    }
}