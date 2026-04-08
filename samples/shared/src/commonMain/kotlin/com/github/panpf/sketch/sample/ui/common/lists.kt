package com.github.panpf.sketch.sample.ui.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.paging.PagingConfig
import com.github.panpf.sketch.sample.util.Platform
import com.github.panpf.sketch.sample.util.current
import com.github.panpf.sketch.sample.util.isMobile

val gridPagingConfig = if (Platform.current.isMobile()) PagingConfig(
    pageSize = 40,
    initialLoadSize = 80,
    prefetchDistance = 20,
    enablePlaceholders = false,
) else PagingConfig(
    pageSize = 120,
    initialLoadSize = 240,
    prefetchDistance = 60,
    enablePlaceholders = false,
)

val listPagingConfig = if (Platform.current.isMobile()) PagingConfig(
    pageSize = 20,
    initialLoadSize = 40,
    prefetchDistance = 10,
    enablePlaceholders = false,
) else PagingConfig(
    pageSize = 60,
    initialLoadSize = 120,
    prefetchDistance = 30,
    enablePlaceholders = false,
)

@Composable
fun listContentPaddingWithNavigationBarsWindowInset(): PaddingValues {
    val density = LocalDensity.current
    val navigationBarsBottomInsetPixels = WindowInsets.navigationBars.getBottom(density)
    val navigationBarsBottomInsetDp = with(density) {
        navigationBarsBottomInsetPixels.toDp()
    }
    return remember { PaddingValues(bottom = navigationBarsBottomInsetDp) }
}