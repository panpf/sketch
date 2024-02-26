package com.github.panpf.sketch.sample.ui.screen

import android.content.Context
import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import com.github.panpf.tools4a.display.ktx.getStatusBarHeight

actual fun getTopMargin(context: Context): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        context.getStatusBarHeight()
    } else {
        0
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
actual fun BoxScope.PlatformPagerTools(
    buttonBgColorState: MutableState<Color>,
    pagerState: PagerState
) {

}