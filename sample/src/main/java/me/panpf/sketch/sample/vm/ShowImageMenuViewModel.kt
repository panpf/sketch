package me.panpf.sketch.sample.vm

import androidx.lifecycle.ViewModel
import com.github.panpf.liveevent.LiveEvent
import me.panpf.sketch.sample.databinding.FragmentImageBinding

class ShowImageMenuViewModel : ViewModel() {
    val showImageMenuEvent = LiveEvent<FragmentImageBinding>()
}