package com.github.panpf.sketch.sample.ui.gallery

import dev.icerock.moko.permissions.Permission
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import com.github.panpf.sketch.sample.ui.components.VideoPlayer

actual fun localPhotoListPermission(): Any? = Permission.STORAGE

@Composable
actual fun LocalPhotoListPage(screen: Screen) {
    // Add logic to handle mp4 video playback
    VideoPlayer(videoUri = "sample.mp4")
}
