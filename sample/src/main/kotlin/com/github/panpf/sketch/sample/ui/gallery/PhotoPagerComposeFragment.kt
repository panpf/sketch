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
package com.github.panpf.sketch.sample.ui.gallery

import android.Manifest
import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.panpf.sketch.compose.AsyncImage
import com.github.panpf.sketch.compose.rememberAsyncImageState
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.resize.Precision.SMALLER_SIZE
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.image.PaletteDecodeInterceptor
import com.github.panpf.sketch.sample.image.simplePalette
import com.github.panpf.sketch.sample.model.ImageDetail
import com.github.panpf.sketch.sample.ui.MainFragmentDirections
import com.github.panpf.sketch.sample.ui.base.BaseComposeFragment
import com.github.panpf.sketch.sample.ui.base.StatusBarTextStyle
import com.github.panpf.sketch.sample.ui.base.StatusBarTextStyle.White
import com.github.panpf.sketch.sample.ui.setting.Page.ZOOM
import com.github.panpf.sketch.sample.util.WithDataActivityResultContracts
import com.github.panpf.sketch.sample.util.registerForActivityResult
import com.github.panpf.sketch.transform.BlurTransformation
import com.github.panpf.tools4a.display.ktx.getStatusBarHeight
import com.github.panpf.tools4a.toast.ktx.showLongToast
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.math.roundToInt

class PhotoPagerComposeFragment : BaseComposeFragment() {

    private val args by navArgs<PhotoPagerComposeFragmentArgs>()
    private val imageList by lazy {
        Json.decodeFromString<List<ImageDetail>>(args.imageDetailJsonArray)
    }
    private val viewModel by viewModels<PhotoPagerViewModel>()
    private val requestPermissionResult =
        registerForActivityResult(WithDataActivityResultContracts.RequestPermission())

    override var statusBarTextStyle: StatusBarTextStyle? = White

    @Composable
    override fun DrawContent() {
        ImagePager(
            imageList = imageList,
            totalCount = args.totalCount,
            startPosition = args.startPosition,
            initialPosition = args.initialPosition,
            onSettingsClick = {
                findNavController().navigate(
                    MainFragmentDirections.actionSettingsDialogFragment(ZOOM.name)
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
            onImageClick = {
                findNavController().popBackStack()
            },
        )
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
    onSettingsClick: () -> Unit,
    onShowOriginClick: () -> Unit,
    onShareClick: (ImageDetail) -> Unit,
    onSaveClick: (ImageDetail) -> Unit,
    onImageClick: () -> Unit,
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
        val buttonBgColorState =
            remember { mutableIntStateOf(android.graphics.Color.parseColor("#bf5660")) }

        PagerBgImage(uriString, buttonBgColorState, IntSize(maxWidthPx, maxHeightPx))

        HorizontalPager(
            state = pagerState,
            beyondBoundsPageCount = 0,
            modifier = Modifier.fillMaxSize()
        ) { index ->
            PhotoViewer(
                imageDetail = imageList[index],
                buttonBgColorState = buttonBgColorState,
                onClick = onImageClick,
                onShareClick = {
                    onShareClick.invoke(imageList[pagerState.currentPage])
                },
                onSaveClick = {
                    onSaveClick.invoke(imageList[pagerState.currentPage])
                },
            )
        }

        val showOriginImage by LocalContext.current.appSettingsService.showOriginImage.collectAsState()
        ImagePagerTools(
            pageNumber = startPosition + pagerState.currentPage + 1,
            pageCount = totalCount,
            showOriginImage = showOriginImage,
            buttonBgColorState = buttonBgColorState,
            onSettingsClick = onSettingsClick,
            onShowOriginClick = onShowOriginClick,
        )
    }
}

@Composable
private fun PagerBgImage(
    imageUri: String,
    buttonBgColorState: MutableState<Int>,
    screenSize: IntSize,
) {
    val imageState = rememberAsyncImageState()
    LaunchedEffect(Unit) {
        snapshotFlow { imageState.result }.collect {
            if (it is ImageResult.Success) {
                val simplePalette = it.simplePalette
                val accentColor = (simplePalette?.dominantSwatch?.rgb
                    ?: simplePalette?.lightVibrantSwatch?.rgb
                    ?: simplePalette?.vibrantSwatch?.rgb
                    ?: simplePalette?.lightMutedSwatch?.rgb
                    ?: simplePalette?.mutedSwatch?.rgb
                    ?: simplePalette?.darkVibrantSwatch?.rgb
                    ?: simplePalette?.darkMutedSwatch?.rgb)
                if (accentColor != null) {
                    buttonBgColorState.value = accentColor
                }
            }
        }
    }
    AsyncImage(
        request = ImageRequest(LocalContext.current, imageUri) {
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
            disallowAnimatedImage()
            crossfade(alwaysUse = true, durationMillis = 400)
            resizeApplyToDrawable()
            components {
                addDecodeInterceptor(PaletteDecodeInterceptor())
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
    buttonBgColorState: MutableState<Int>,
    onSettingsClick: () -> Unit,
    onShowOriginClick: () -> Unit,
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
    val buttonBgColor = Color(buttonBgColorState.value)
    val buttonTextColor = Color.White

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = toolbarTopMarginDp)
                .padding(20.dp), // margin,
        ) {
            val buttonModifier = Modifier
                .size(40.dp)
                .background(
                    color = buttonBgColor,
                    shape = RoundedCornerShape(50)
                )
                .padding(8.dp)
            IconButton(
                modifier = buttonModifier,
                onClick = { onShowOriginClick.invoke() },
            ) {
                val iconId =
                    if (showOriginImage) R.drawable.ic_image2_baseline else R.drawable.ic_image2_outline
                Icon(
                    painter = painterResource(id = iconId),
                    contentDescription = "show origin image",
                    tint = buttonTextColor
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            Box(
                Modifier
                    .width(40.dp)
                    .background(
                        color = buttonBgColor,
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${pageNumber.coerceAtMost(999)}\n·\n${pageCount.coerceAtMost(999)}",
                    textAlign = TextAlign.Center,
                    color = buttonTextColor,
                    style = TextStyle(lineHeight = 12.sp),
                    modifier = Modifier
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            IconButton(
                modifier = buttonModifier,
                onClick = { onSettingsClick.invoke() },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_settings),
                    contentDescription = "settings",
                    tint = buttonTextColor
                )
            }
        }
    }
}

@Preview
@Composable
fun ImagePagerToolsPreview() {
    val buttonBgColorState = remember {
        mutableIntStateOf(android.graphics.Color.parseColor("#bf5660"))
    }
    ImagePagerTools(
        pageNumber = 9,
        pageCount = 99,
        showOriginImage = false,
        buttonBgColorState = buttonBgColorState,
        onSettingsClick = {},
        onShowOriginClick = {},
    )
}