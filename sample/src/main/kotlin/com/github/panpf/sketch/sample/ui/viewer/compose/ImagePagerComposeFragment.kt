/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.sample.ui.viewer.compose

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.panpf.sketch.compose.AsyncImage
import com.github.panpf.sketch.compose.rememberAsyncImageState
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.resize.Precision.SMALLER_SIZE
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.eventService
import com.github.panpf.sketch.sample.image.PaletteBitmapDecoderInterceptor
import com.github.panpf.sketch.sample.image.simplePalette
import com.github.panpf.sketch.sample.model.ImageDetail
import com.github.panpf.sketch.sample.ui.MainFragmentDirections
import com.github.panpf.sketch.sample.ui.base.BaseFragment
import com.github.panpf.sketch.sample.ui.setting.ImageInfoDialogFragment
import com.github.panpf.sketch.sample.ui.setting.Page
import com.github.panpf.sketch.sample.ui.viewer.ImagePagerViewModel
import com.github.panpf.sketch.sample.util.WithDataActivityResultContracts
import com.github.panpf.sketch.sample.util.registerForActivityResult
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.github.panpf.sketch.stateimage.CurrentStateImage
import com.github.panpf.sketch.transform.BlurTransformation
import com.github.panpf.tools4a.display.ktx.getStatusBarHeight
import com.github.panpf.tools4a.toast.ktx.showLongToast
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.math.roundToInt

class ImagePagerComposeFragment : BaseFragment() {

    private val args by navArgs<ImagePagerComposeFragmentArgs>()
    private val imageList by lazy {
        Json.decodeFromString<List<ImageDetail>>(args.imageDetailJsonArray)
    }
    private val viewModel by viewModels<ImagePagerViewModel>()
    private val requestPermissionResult =
        registerForActivityResult(WithDataActivityResultContracts.RequestPermission())
    private val showInfoEvent = MutableSharedFlow<DisplayResult?>()

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setBackgroundColor(
                ResourcesCompat.getColor(
                    requireContext().resources,
                    R.color.windowBackground,
                    null
                )
            )
            setContent {
                ImagePager(
                    imageList = imageList,
                    totalCount = args.totalCount,
                    startPosition = args.startPosition,
                    initialPosition = args.initialPosition,
                    showInfoEvent = showInfoEvent,
                    onSettingsClick = {
                        findNavController().navigate(
                            MainFragmentDirections.actionGlobalSettingsDialogFragment(Page.ZOOM.name)
                        )
                    },
                    onShowOriginClick = {
                        val newValue = !appSettingsService.showOriginImage.value
                        appSettingsService.showOriginImage.value = newValue
                        if (newValue) {
                            showLongToast("Opened View original image")
                        } else {
                            showLongToast("Closed View original image")
                        }
                    },
                    onShareClick = {
                        share(it)
                    },
                    onSaveClick = {
                        save(it)
                    },
                    onRotateClick = {
                        viewLifecycleOwner.lifecycleScope.launch {
                            eventService.viewerPagerRotateEvent.emit(0)
                        }
                    },
                    onInfoClick = {
                        viewLifecycleOwner.lifecycleScope.launch {
                            eventService.viewerPagerInfoEvent.emit(it)
                        }
                    },
                    onImageClick = {
                        findNavController().popBackStack()
                    },
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showInfoEvent.repeatCollectWithLifecycle(
            viewLifecycleOwner,
            State.STARTED
        ) { displayResult ->
            findNavController()
                .navigate(ImageInfoDialogFragment.createNavDirections(displayResult))
        }
    }

    private fun share(image: ImageDetail) {
        val imageUri = if (appSettingsService.showOriginImage.value) {
            image.originUrl
        } else {
            image.mediumUrl ?: image.originUrl
        }
        lifecycleScope.launch {
            handleActionResult(viewModel.share(imageUri))
        }
    }

    private fun save(image: ImageDetail) {
        val imageUri = if (appSettingsService.showOriginImage.value) {
            image.originUrl
        } else {
            image.mediumUrl ?: image.originUrl
        }
        val input = WithDataActivityResultContracts.RequestPermission.Input(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ) {
            lifecycleScope.launch {
                handleActionResult(viewModel.save(imageUri))
            }
        }
        requestPermissionResult.launch(input)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ImagePager(
    imageList: List<ImageDetail>,
    initialPosition: Int,
    startPosition: Int,
    totalCount: Int,
    showInfoEvent: MutableSharedFlow<DisplayResult?>,
    onSettingsClick: (() -> Unit)? = null,
    onShowOriginClick: (() -> Unit)? = null,
    onShareClick: ((ImageDetail) -> Unit)? = null,
    onSaveClick: ((ImageDetail) -> Unit)? = null,
    onRotateClick: (() -> Unit)? = null,
    onInfoClick: ((Int) -> Unit)? = null,
    onImageClick: (() -> Unit)? = null,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val pagerState = rememberPagerState(initialPage = initialPosition - startPosition) {
            imageList.size
        }

        val density = LocalDensity.current
        val maxWidthPx = with(density) { maxWidth.toPx() }.roundToInt()
        val maxHeightPx = with(density) { maxHeight.toPx() }.roundToInt()

        val uriString = imageList[pagerState.currentPage].let {
            it.thumbnailUrl ?: it.mediumUrl ?: it.originUrl
        }
        val buttonBgColorState = remember { mutableStateOf<Int?>(null) }

        PagerBgImage(uriString, buttonBgColorState, IntSize(maxWidthPx, maxHeightPx))

        HorizontalPager(
            state = pagerState,
            beyondBoundsPageCount = 0,
            modifier = Modifier.fillMaxSize()
        ) { index ->
            ImageViewer(index, imageList[index], showInfoEvent, onImageClick)
        }

        val showOriginImage by LocalContext.current.appSettingsService.showOriginImage.stateFlow.collectAsState()
        ImagePagerTools(
            pageNumber = startPosition + pagerState.currentPage + 1,
            pageCount = totalCount,
            showOriginImage = showOriginImage,
            buttonBgColorState = buttonBgColorState,
            onSettingsClick = onSettingsClick,
            onShowOriginClick = onShowOriginClick,
            onShareClick = {
                onShareClick?.invoke(imageList[pagerState.currentPage])
            },
            onSaveClick = {
                onSaveClick?.invoke(imageList[pagerState.currentPage])
            },
            onRotateClick = onRotateClick,
            onInfoClick = {
                onInfoClick?.invoke(pagerState.currentPage)
            },
        )
    }
}

@Composable
private fun PagerBgImage(
    imageUri: String,
    buttonBgColorState: MutableState<Int?>,
    screenSize: IntSize,
) {
    val imageState = rememberAsyncImageState()
    LaunchedEffect(Unit) {
        snapshotFlow { imageState.result }.collect {
            if (it is DisplayResult.Success) {
                val simplePalette = it.simplePalette
                buttonBgColorState.value = simplePalette?.dominantSwatch?.rgb
                    ?: simplePalette?.lightVibrantSwatch?.rgb
                            ?: simplePalette?.vibrantSwatch?.rgb
                            ?: simplePalette?.lightMutedSwatch?.rgb
                            ?: simplePalette?.mutedSwatch?.rgb
                            ?: simplePalette?.darkVibrantSwatch?.rgb
                            ?: simplePalette?.darkMutedSwatch?.rgb
            }
        }
    }
    AsyncImage(
        request = DisplayRequest(LocalContext.current, imageUri) {
            resize(
                width = screenSize.width / 4,
                height = screenSize.height / 4,
                precision = SMALLER_SIZE
            )
            addTransformations(
                BlurTransformation(
                    radius = 20,
                    maskColor = ColorUtils.setAlphaComponent(Color.Black.value.toInt(), 100)
                )
            )
            placeholder(CurrentStateImage())
            disallowAnimatedImage()
            crossfade(alwaysUse = true, durationMillis = 400)
            components {
                addBitmapDecodeInterceptor(PaletteBitmapDecoderInterceptor())
            }
        },
        state = imageState,
        contentDescription = "Background",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun ImagePagerTools(
    pageNumber: Int,
    pageCount: Int,
    showOriginImage: Boolean,
    buttonBgColorState: MutableState<Int?>,
    onSettingsClick: (() -> Unit)? = null,
    onShowOriginClick: (() -> Unit)? = null,
    onShareClick: (() -> Unit)? = null,
    onSaveClick: (() -> Unit)? = null,
    onRotateClick: (() -> Unit)? = null,
    onInfoClick: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val toolbarTopMarginDp = remember {
        val toolbarTopMargin = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            context.getStatusBarHeight()
        } else {
            0
        }
        with(density) { toolbarTopMargin.toDp() }
    }
    val buttonBgColor = buttonBgColorState.value
        ?.let { Color(it) }
        ?: MaterialTheme.colors.secondary.copy(alpha = 0.7f)
    val buttonTextColor = buttonBgColorState.value
        ?.let { Color.White }
        ?: MaterialTheme.colors.onSecondary
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = toolbarTopMarginDp)
            .padding(20.dp), // margin,
        horizontalArrangement = Arrangement.End
    ) {
        val buttonModifier = Modifier
            .size(34.dp)
            .background(
                color = buttonBgColor,
                shape = RoundedCornerShape(50)
            )
            .padding(8.dp)

        IconButton(
            modifier = buttonModifier,
            onClick = { onSettingsClick?.invoke() },
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_settings),
                contentDescription = "settings",
                tint = buttonTextColor
            )
        }

        Spacer(modifier = Modifier.size(10.dp))

        IconButton(
            modifier = buttonModifier,
            onClick = { onShowOriginClick?.invoke() },
        ) {
            val iconId = if (showOriginImage) R.drawable.ic_image_2 else R.drawable.ic_image_2_fill
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = "show origin image",
                tint = buttonTextColor
            )
        }

        Spacer(modifier = Modifier.size(10.dp))

        IconButton(
            modifier = buttonModifier,
            onClick = { onShareClick?.invoke() },
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_share),
                contentDescription = "share",
                tint = buttonTextColor
            )
        }

        Spacer(modifier = Modifier.size(10.dp))

        IconButton(
            modifier = buttonModifier,
            onClick = { onSaveClick?.invoke() },
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_save),
                contentDescription = "save",
                tint = buttonTextColor
            )
        }

        Spacer(modifier = Modifier.size(10.dp))

        IconButton(
            modifier = buttonModifier,
            onClick = { onRotateClick?.invoke() },
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_rotate_right),
                contentDescription = "right rotate",
                tint = buttonTextColor
            )
        }

        Spacer(modifier = Modifier.size(10.dp))

        IconButton(
            modifier = buttonModifier,
            onClick = { onInfoClick?.invoke() },
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_info),
                contentDescription = "info",
                tint = buttonTextColor
            )
        }

        Spacer(modifier = Modifier.size(10.dp))

        Box(
            Modifier
                .height(34.dp)
                .background(
                    color = buttonBgColor,
                    shape = RoundedCornerShape(50)
                )
                .padding(horizontal = 8.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${pageNumber}/$pageCount",
                textAlign = TextAlign.Center,
                color = buttonTextColor,
                style = TextStyle(lineHeight = 12.sp),
                modifier = Modifier
            )
        }
    }
}

@Preview
@Composable
fun ImagePagerToolsPreview() {
    val buttonBgColorState = remember {
        mutableStateOf<Int?>(null)
    }
    ImagePagerTools(
        pageNumber = 9,
        pageCount = 99,
        showOriginImage = false,
        buttonBgColorState = buttonBgColorState,
    )
}