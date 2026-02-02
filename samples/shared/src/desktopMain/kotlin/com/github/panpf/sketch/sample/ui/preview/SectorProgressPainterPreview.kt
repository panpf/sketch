package com.github.panpf.sketch.sample.ui.preview

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.painter.rememberSectorProgressPainter


@Preview
@Composable
fun SectorProgressPainterPreview() {
    Image(
        painter = rememberSectorProgressPainter(size = 200.dp).apply {
            progress = 0.9f
        },
        contentDescription = "",
        modifier = Modifier.background(Color.Red)
    )
}

//@Preview
//@Composable
//fun RingProgressPainterPreview() {
//    Image(
//        painter = rememberRingProgressPainter(size = 200.dp).apply {
//            progress = 0.5f
//        },
//        contentDescription = "",
//        contentScale = ContentScale.None,
//        modifier = Modifier
//            .background(Color.Red)
//            .size(300.dp),
//    )
//}