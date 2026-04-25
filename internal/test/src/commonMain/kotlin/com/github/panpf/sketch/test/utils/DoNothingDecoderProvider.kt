package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.util.DecoderProvider

class DoNothingDecoderProvider : DecoderProvider {

    override fun factory(context: PlatformContext): Decoder.Factory {
        return DoNothingDecoder.Factory()
    }
}