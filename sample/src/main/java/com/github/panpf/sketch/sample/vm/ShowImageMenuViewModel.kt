package com.github.panpf.sketch.sample.vm

import androidx.lifecycle.ViewModel
import com.github.panpf.liveevent.LiveEvent
import com.github.panpf.sketch.sample.databinding.FragmentImageBinding

class ShowImageMenuViewModel : ViewModel() {
    val showImageMenuEvent = LiveEvent<FragmentImageBinding>()
}