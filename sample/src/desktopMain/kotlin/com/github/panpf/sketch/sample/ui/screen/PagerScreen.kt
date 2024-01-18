//package com.github.panpf.sketch.sample.ui.screen
//
//import androidx.compose.desktop.ui.tooling.preview.Preview
//import androidx.compose.foundation.ExperimentalFoundationApi
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.pager.HorizontalPager
//import androidx.compose.foundation.pager.rememberPagerState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.ArrowForward
//import androidx.compose.material.icons.filled.Settings
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.input.key.Key
//import androidx.compose.ui.input.key.KeyEventType
//import androidx.compose.ui.input.key.isMetaPressed
//import androidx.compose.ui.input.key.key
//import androidx.compose.ui.input.key.type
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.github.panpf.sketch.sample.compose.ui.ZoomImageOptionsDialog
//import com.github.panpf.sketch.sample.ui.model.ImageResource
//import com.github.panpf.sketch.sample.ui.navigation.Navigation
//import com.github.panpf.sketch.sample.ui.screen.base.ToolbarIcon
//import com.github.panpf.sketch.sample.ui.screen.base.ToolbarScreen
//import com.github.panpf.sketch.sample.ui.util.EventBus
//import kotlinx.coroutines.launch
//
//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//@Preview
//fun SlideshowScreen(
//    navigation: Navigation,
//    imageResources: List<ImageResource>,
//    initialIndex: Int
//) {
//    var showSettingsDialog by remember { mutableStateOf(false) }
//    ToolbarScreen(
//        navigation = navigation,
//        menus = {
//            ToolbarIcon(
//                imageVector = Icons.Default.Settings,
//                contentDescription = "Settings",
//                onClick = { showSettingsDialog = true }
//            )
//        }
//    ) {
//        val pagerState = rememberPagerState(initialPage = initialIndex) {
//            imageResources.size
//        }
//        HorizontalPager(
//            state = pagerState,
//            beyondBoundsPageCount = 0,
//            modifier = Modifier.fillMaxSize()
//        ) { index ->
//            ViewerScreen(navigation, imageResources[index])
//        }
//
//        val coroutineScope = rememberCoroutineScope()
//        LaunchedEffect(Unit) {
//            EventBus.keyEvent.collect { keyEvent ->
//                if (keyEvent.type == KeyEventType.KeyUp && !keyEvent.isMetaPressed) {
//                    when (keyEvent.key) {
//                        Key.PageUp, Key.DirectionLeft -> {
//                            val nextPageIndex =
//                                (pagerState.currentPage - 1).let { if (it < 0) pagerState.pageCount + it else it }
//                            pagerState.animateScrollToPage(nextPageIndex)
//                        }
//
//                        Key.PageDown, Key.DirectionRight -> {
//                            val nextPageIndex = (pagerState.currentPage + 1) % pagerState.pageCount
//                            pagerState.animateScrollToPage(nextPageIndex)
//                        }
//                    }
//                }
//            }
//        }
//
//        IconButton(
//            onClick = {
//                coroutineScope.launch {
//                    val nextPageIndex =
//                        (pagerState.currentPage - 1).let { if (it < 0) pagerState.pageCount + it else it }
//                    pagerState.animateScrollToPage(nextPageIndex)
//                }
//            },
//            modifier = Modifier
//                .padding(20.dp)
//                .size(50.dp)
//                .background(Color.Black.copy(0.5f), shape = CircleShape)
//                .align(Alignment.CenterStart)
//        ) {
//            Icon(Icons.Filled.ArrowBack, contentDescription = "Previous", tint = Color.White)
//        }
//        IconButton(
//            onClick = {
//                coroutineScope.launch {
//                    val nextPageIndex = (pagerState.currentPage + 1) % pagerState.pageCount
//                    pagerState.animateScrollToPage(nextPageIndex)
//                }
//            },
//            modifier = Modifier
//                .padding(20.dp)
//                .size(50.dp)
//                .background(Color.Black.copy(0.5f), shape = CircleShape)
//                .align(Alignment.CenterEnd)
//        ) {
//            Icon(Icons.Filled.ArrowForward, contentDescription = "Next", tint = Color.White)
//        }
//
//        PageNumber(
//            number = pagerState.currentPage + 1,
//            total = imageResources.size,
//            modifier = Modifier.align(Alignment.TopEnd)
//        )
//
//        if (showSettingsDialog) {
//            ZoomImageOptionsDialog(
//                my = true,
//                supportIgnoreExifOrientation = true,
//                state = rememberZoomImageOptionsState()
//            ) {
//                showSettingsDialog = false
//            }
//        }
//    }
//}
//
//@Composable
//private fun PageNumber(modifier: Modifier = Modifier, number: Int, total: Int) {
//    val colors = MaterialTheme.colorScheme
//    Text(
//        text = "${number}\n·\n${total}",
//        textAlign = TextAlign.Center,
//        color = colors.onTertiary,
//        style = TextStyle(lineHeight = 12.sp),
//        modifier = Modifier
//            .padding(20.dp) // margin
//            .background(
//                color = colors.tertiary,
//                shape = RoundedCornerShape(50)
//            )
//            .padding(horizontal = 8.dp, vertical = 12.dp)
//            .then(modifier)
//    )
//}