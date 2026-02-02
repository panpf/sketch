package com.github.panpf.sketch.sample.ui.gallery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.image.palette.PhotoPalette
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PhotoPaletteViewModel(application: Application) : AndroidViewModel(application) {

    private val _photoPaletteState = MutableStateFlow(
        PhotoPalette(
            palette = null,
            primaryColor = getPrimaryColor(),
            primaryContainerColor = getPrimaryContainerColor()
        )
    )
    val photoPaletteState: StateFlow<PhotoPalette> = _photoPaletteState

    fun setPhotoPalette(photoPalette: PhotoPalette) {
        _photoPaletteState.value = photoPalette
    }

    private fun getPrimaryColor(): Int {
        return (getApplication() as Application).resources.getColor(R.color.md_theme_primary)
    }

    private fun getPrimaryContainerColor(): Int {
        return (getApplication() as Application).resources.getColor(R.color.md_theme_primaryContainer)
    }
}