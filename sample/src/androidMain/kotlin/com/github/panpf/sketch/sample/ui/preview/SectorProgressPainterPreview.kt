package com.github.panpf.sketch.sample.ui.preview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.compose.painter.rememberRingProgressPainter
import com.github.panpf.sketch.compose.painter.rememberSectorProgressPainter
import com.github.panpf.sketch.drawable.RingProgressDrawable
import com.github.panpf.sketch.drawable.SectorProgressDrawable
import com.github.panpf.tools4a.dimen.ktx.dp2px


@Preview
@Composable
fun SectorProgressPainterPreview() {
    Image(
        painter = rememberSectorProgressPainter(size = 200.dp).apply {
            progress = 0.5f
        },
        contentDescription = "",
        contentScale = ContentScale.None,
        modifier = Modifier
            .background(Color.Red)
            .size(300.dp)
    )
}

@Preview
@Composable
fun RingProgressPainterPreview() {
    Image(
        painter = rememberRingProgressPainter(size = 200.dp).apply {
            progress = 0.5f
        },
        contentDescription = "",
        contentScale = ContentScale.None,
        modifier = Modifier
            .background(Color.Red)
            .size(300.dp),
    )
}

@Preview
@Composable
fun SectorDrawableProgressPainterPreview() {
    val progressDrawable = remember {
        SectorProgressDrawable(size = 200.dp2px)
    }
    LaunchedEffect(Unit) {
        progressDrawable.progress = 0.5f
    }
    Image(
        painter = rememberDrawableProgressPainter(drawable = progressDrawable),
        contentDescription = "",
        contentScale = ContentScale.None,
        modifier = Modifier
            .background(Color.Red)
            .size(300.dp)
    )
}

@Preview
@Composable
fun RingDrawableProgressPainterPreview() {
    val progressDrawable = remember {
        RingProgressDrawable(size = 200.dp2px)
    }
    LaunchedEffect(Unit) {
        progressDrawable.progress = 0.5f
    }
    Image(
        painter = rememberDrawableProgressPainter(drawable = progressDrawable),
        contentDescription = "",
        contentScale = ContentScale.None,
        modifier = Modifier
            .background(Color.Red)
            .size(300.dp),
    )
}