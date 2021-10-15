package me.panpf.sketch.sample.ui

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
import me.panpf.sketch.Sketch
import me.panpf.sketch.display.FadeInImageDisplayer
import me.panpf.sketch.drawable.SketchGifDrawable
import me.panpf.sketch.request.*
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.appSettingsService
import me.panpf.sketch.sample.base.BaseFragment
import me.panpf.sketch.sample.base.parentViewModels
import me.panpf.sketch.sample.databinding.FragmentImageBinding
import me.panpf.sketch.sample.util.*
import me.panpf.sketch.sample.vm.ShowImageMenuViewModel
import me.panpf.sketch.sample.vm.ShowingImageChangedViewModel
import me.panpf.sketch.state.MemoryCacheStateImage
import me.panpf.sketch.state.StateImage
import me.panpf.sketch.uri.UriModel
import me.panpf.sketch.util.SketchUtils
import me.panpf.sketch.zoom.AdaptiveTwoLevelScales
import java.util.*

class ImageFragment : BaseFragment<FragmentImageBinding>() {

    private val args by navArgs<ImageFragmentArgs>()
    private val showingImageChangedViewModel by parentViewModels<ShowingImageChangedViewModel>()
    private val showImageMenuViewModel by viewModels<ShowImageMenuViewModel>()
    private val finalShowImageUrl: String by lazy {
        val showHighQualityImage =
            appSettingsService.showHighQualityImageEnabled.value ?: false
        val rawQualityUrl = args.rawQualityUrl
        if (showHighQualityImage && rawQualityUrl != null) rawQualityUrl else args.normalQualityUrl
    }

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
            options.apply {
                isDecodeGifImage = true
                val loadingStateImage = getLoadingStateImage()
                loadingImage = loadingStateImage
                displayer = if (loadingStateImage == null) FadeInImageDisplayer() else null
            }

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
                displayImage(finalShowImageUrl)

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

        binding.imageFragmentZoomImage.displayImage(finalShowImageUrl)
    }

    private fun showError(binding: FragmentImageBinding) {
        binding.imageFragmentHint.hint(R.drawable.ic_error, "Image display failed", "Again") {
            binding.imageFragmentZoomImage.displayImage(finalShowImageUrl)
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
                    displayImage(finalShowImageUrl)
                    options.requestLevel = oldRequestLevel
                }
            }
        }
    }

    private fun notifyShowingImageChanged(isVisibleToUser: Boolean) {
        if (isVisibleToUser) {
            showingImageChangedViewModel.imageChangedData.postValue(finalShowImageUrl)
        }
    }

    private fun startOrPauseGif(binding: FragmentImageBinding, fromDisplayCompleted: Boolean) {
        val drawable = binding.imageFragmentZoomImage.drawable
        val lastDrawable = SketchUtils.getLastDrawable(drawable)
        if (lastDrawable is SketchGifDrawable) {
            lastDrawable.followPageVisible(isVisibleToUser, fromDisplayCompleted)
        }
    }

    private fun getLoadingStateImage(): StateImage? {
        val loadingOptionsKey =
            args.loadingOptionsKey?.takeIf { it.isNotEmpty() } ?: return null
        val uriModel = UriModel.match(requireContext(), finalShowImageUrl) ?: return null
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