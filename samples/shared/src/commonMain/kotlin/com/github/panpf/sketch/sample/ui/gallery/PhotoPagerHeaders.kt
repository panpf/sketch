package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.panpf.sketch.sample.AppEvents
import com.github.panpf.sketch.sample.AppSettings
import com.github.panpf.sketch.sample.Res
import com.github.panpf.sketch.sample.ic_image2_baseline
import com.github.panpf.sketch.sample.ic_image2_outline
import com.github.panpf.sketch.sample.ic_settings
import com.github.panpf.sketch.sample.image.palette.PhotoPalette
import com.github.panpf.sketch.sample.ui.LocalNavBackStack
import com.github.panpf.sketch.sample.ui.components.MyDialog
import com.github.panpf.sketch.sample.ui.components.rememberMyDialogState
import com.github.panpf.sketch.sample.ui.setting.AppSettingsList
import com.github.panpf.sketch.sample.ui.setting.Page
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@Composable
fun PhotoPagerHeaders(
    params: PhotoPagerParams,
    pagerState: PagerState,
    photoPaletteState: MutableState<PhotoPalette>,
) {
    val photoPalette by photoPaletteState
    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val navBackStack = LocalNavBackStack.current
            IconButton(
                onClick = { navBackStack.removeLastOrNull() },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = photoPalette.containerColor,
                    contentColor = photoPalette.contentColor
                ),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.Companion
                        .size(40.dp)
                        .padding(8.dp),
                )
            }

            Spacer(Modifier.weight(1f))

            val appEvents: AppEvents = koinInject()
            val appSettings: AppSettings = koinInject()
            val showOriginImage by appSettings.showOriginImage.collectAsState()
            val image2IconPainter = if (showOriginImage)
                painterResource(Res.drawable.ic_image2_baseline) else painterResource(Res.drawable.ic_image2_outline)
            val coroutineScope = rememberCoroutineScope()
            IconButton(
                onClick = {
                    val newValue = !appSettings.showOriginImage.value
                    appSettings.showOriginImage.value = newValue
                    coroutineScope.launch {
                        if (newValue) {
                            appEvents.toastFlow.emit("Now show original image")
                        } else {
                            appEvents.toastFlow.emit("Now show thumbnails image")
                        }
                    }
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = photoPalette.containerColor,
                    contentColor = photoPalette.contentColor
                ),
            ) {
                Icon(
                    painter = image2IconPainter,
                    contentDescription = "show origin image",
                    modifier = Modifier.Companion
                        .size(40.dp)
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.size(10.dp))


            val settingsDialogState = rememberMyDialogState()
            IconButton(
                onClick = { settingsDialogState.show() },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = photoPalette.containerColor,
                    contentColor = photoPalette.contentColor
                ),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_settings),
                    contentDescription = "settings",
                    modifier = Modifier.Companion
                        .size(40.dp)
                        .padding(8.dp)
                )
            }
            MyDialog(settingsDialogState) {
                AppSettingsList(Page.VIEWER)
            }

            Spacer(modifier = Modifier.size(10.dp))

            Box(
                Modifier.Companion
                    .height(40.dp)
                    .background(
                        color = photoPalette.containerColor,
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                val numberText by remember {
                    derivedStateOf {
                        val number = params.startPosition + pagerState.currentPage + 1
                        "${number}/${params.totalCount}"
                    }
                }
                Text(
                    text = numberText,
                    textAlign = TextAlign.Center,
                    color = photoPalette.contentColor,
                    style = TextStyle(lineHeight = 12.sp),
                )
            }
        }
    }
}