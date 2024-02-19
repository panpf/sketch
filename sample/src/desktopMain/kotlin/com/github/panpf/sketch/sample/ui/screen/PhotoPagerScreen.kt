package com.github.panpf.sketch.sample.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import com.github.panpf.sketch.sample.ui.MyEvents
import com.github.panpf.sketch.sample.ui.model.ImageDetail
import com.github.panpf.sketch.sample.ui.screen.base.ToolbarScreen
import kotlinx.coroutines.launch

class PhotoPagerScreen(
    val imageList: List<ImageDetail>,
    val totalCount: Int,
    val startPosition: Int,
    val initialPosition: Int,
) : ToolbarScreen() {

    @Composable
    override fun Content() {
        Box(Modifier.fillMaxSize()) {
            val photoPagerEvents = rememberPhotoPagerEvents()
            PhotoPager(
                imageList = imageList,
                totalCount = totalCount,
                startPosition = startPosition,
                initialPosition = initialPosition,
                photoPagerEvents = photoPagerEvents,
                onSettingsClick = {
//                findNavController().navigate(
//                    MainFragmentDirections.actionSettingsDialogFragment(ZOOM.name)
//                )
                },
                onShowOriginClick = {
//                val newValue = !appSettingsService.showOriginImage.value
//                appSettingsService.showOriginImage.value = newValue
//                if (newValue) {
//                    showLongToast("Opened View original image")
//                } else {
//                    showLongToast("Closed View original image")
//                }
                },
                onShareClick = {
//                share(it)
                },
                onSaveClick = {
//                save(it)
                },
                onImageClick = {
//                findNavController().popBackStack()
                },
                onImageLongClick = { imageResult ->
//                findNavController()
//                    .navigate(PhotoInfoDialogFragment.createNavDirections(imageResult))
                },
                onInfoClick = { imageResult ->
//                findNavController()
//                    .navigate(PhotoInfoDialogFragment.createNavDirections(imageResult))
                },
            )

            val colorScheme = MaterialTheme.colorScheme
            val navigator = LocalNavigator.current!!
            IconButton(
                modifier = Modifier
                    .padding(20.dp) // margin,
                    .size(40.dp)
                    .background(
                        color = colorScheme.primary,
                        shape = RoundedCornerShape(50)
                    )
                    .padding(8.dp),
                onClick = { navigator.pop() },
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = colorScheme.onPrimary
                )
            }

            TurnPage(photoPagerEvents)
        }
    }

    @Composable
    private fun BoxScope.TurnPage(photoPagerEvents: PhotoPagerEvents) {
        val coroutineScope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            MyEvents.keyEvent.collect { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyUp && !keyEvent.isMetaPressed) {
                    when (keyEvent.key) {
                        Key.PageUp, Key.DirectionLeft -> {
                            photoPagerEvents.previousPageFlow.emit(Unit)
                        }

                        Key.PageDown, Key.DirectionRight -> {
                            photoPagerEvents.nextPageFlow.emit(Unit)
                        }
                    }
                }
            }
        }

        IconButton(
            onClick = {
                coroutineScope.launch {
                    photoPagerEvents.previousPageFlow.emit(Unit)
                }
            },
            modifier = Modifier
                .padding(20.dp)
                .size(50.dp)
                .background(Color.Black.copy(0.5f), shape = CircleShape)
                .align(Alignment.CenterStart)
        ) {
            Icon(Filled.ArrowBack, contentDescription = "Previous", tint = Color.White)
        }

        IconButton(
            onClick = {
                coroutineScope.launch {
                    photoPagerEvents.nextPageFlow.emit(Unit)
                }
            },
            modifier = Modifier
                .padding(20.dp)
                .size(50.dp)
                .background(Color.Black.copy(0.5f), shape = CircleShape)
                .align(Alignment.CenterEnd)
        ) {
            Icon(Filled.ArrowForward, contentDescription = "Next", tint = Color.White)
        }
    }
}