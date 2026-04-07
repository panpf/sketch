package com.github.panpf.sketch.sample.ui.test

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.paging.LoadState.Loading
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.videoFramePercent
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.ui.base.ToolbarScaffold
import com.github.panpf.sketch.sample.ui.common.PagingListAppendState
import com.github.panpf.sketch.sample.ui.common.PagingListRefreshState
import com.github.panpf.sketch.sample.ui.components.MyAsyncImage
import com.github.panpf.sketch.sample.ui.components.PermissionContainer
import com.github.panpf.sketch.sample.ui.model.VideoInfo
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.gallery.GALLERY
import org.koin.compose.viewmodel.koinViewModel

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun LocalVideosTestScreen() {
    BaseScreen {
        ToolbarScaffold(title = "Local Videos") {
            PermissionContainer(
                permission = Permission.GALLERY,
                permissionRequired = false,
            ) {
                val viewModel: LocalVideoListViewModel = koinViewModel()
                val pagingItems = viewModel.pagingFlow.collectAsLazyPagingItems()
                val pullRefreshState = rememberPullRefreshState(
                    refreshing = pagingItems.loadState.refresh is Loading,
                    onRefresh = { pagingItems.refresh() }
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pullRefresh(pullRefreshState)
                ) {
                    val listState = rememberLazyListState()
                    LazyColumn(state = listState) {
                        items(
                            count = pagingItems.itemCount,
                            contentType = { 1 }
                        ) { index ->
                            VideoItem(pagingItems[index]!!)
                        }
                        if (pagingItems.itemCount > 0) {
                            item(
                                key = "AppendState",
                                contentType = 2
                            ) {
                                PagingListAppendState(pagingItems)
                            }
                        }
                    }

                    PullRefreshIndicator(
                        refreshing = pagingItems.loadState.refresh is Loading,
                        state = pullRefreshState,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )

                    PagingListRefreshState(pagingItems)
                }
            }
        }
    }
}

@Composable
fun VideoItem(videoInfo: VideoInfo) {
    ConstraintLayout(Modifier.fillMaxWidth().padding(16.dp)) {
        val context = LocalPlatformContext.current
        val (cover, duration, title, size, date) = createRefs()

        MyAsyncImage(
            request = remember(videoInfo.uri) {
                ImageRequest(context, videoInfo.uri) {
                    videoFramePercent(0.5f)
                }
            },
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp, 70.dp)
                .constrainAs(cover) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
        )

        Text(
            text = videoInfo.tempFormattedDuration,
            color = Color.White,
            fontSize = 12.sp,
            style = TextStyle(shadow = Shadow(color = Color.Black, blurRadius = 5f)),
            modifier = Modifier.constrainAs(duration) {
                end.linkTo(cover.end, margin = 4.dp)
                bottom.linkTo(cover.bottom, margin = 4.dp)
            },
        )

        Text(
            text = videoInfo.title.orEmpty(),
            maxLines = 2,
            minLines = 2,
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow.Ellipsis,
            fontSize = 16.sp,
            modifier = Modifier.constrainAs(title) {
                start.linkTo(cover.end, margin = 16.dp)
                top.linkTo(cover.top)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
        )

        Text(
            text = videoInfo.getTempFormattedSize(context),
            fontSize = 12.sp,
            modifier = Modifier.constrainAs(size) {
                start.linkTo(title.start)
                bottom.linkTo(cover.bottom)
            },
        )

        Text(
            text = videoInfo.tempFormattedDate,
            fontSize = 12.sp,
            modifier = Modifier.constrainAs(date) {
                start.linkTo(size.end, margin = 16.dp)
                bottom.linkTo(size.bottom)
            },
        )
    }
}

@Preview
@Composable
fun VideoItemPreview() {
    val videoInfo = VideoInfo(
        title = "Video",
        uri = "uri",
        mimeType = "video/mp4",
        duration = (83.7 * 60 * 1000).toLong(),
        date = System.currentTimeMillis(),
        size = 103 * 1024 * 1024L
    )
    VideoItem(videoInfo)
}