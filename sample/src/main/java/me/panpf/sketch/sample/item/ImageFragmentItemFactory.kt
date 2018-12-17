package me.panpf.sketch.sample.item

import android.content.Context
import androidx.fragment.app.Fragment
import me.panpf.adapter.pager.AssemblyFragmentItemFactory

import me.panpf.sketch.sample.bean.Image
import me.panpf.sketch.sample.ui.ImageFragment
import me.panpf.sketch.sample.AppConfig

class ImageFragmentItemFactory(private val context: Context, private var loadingImageOptionsId: String?) : AssemblyFragmentItemFactory<Image>() {

    override fun match(o: Any?): Boolean {
        return o is Image
    }

    override fun createFragment(i: Int, image: Image?): androidx.fragment.app.Fragment {
        val showTools = AppConfig.getBoolean(context, AppConfig.Key.SHOW_TOOLS_IN_IMAGE_DETAIL)
        return ImageFragment.build(checkNotNull(image), loadingImageOptionsId, showTools)
    }
}
