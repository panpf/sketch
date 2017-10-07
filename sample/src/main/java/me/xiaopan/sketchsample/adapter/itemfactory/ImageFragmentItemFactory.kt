package me.xiaopan.sketchsample.adapter.itemfactory

import android.content.Context
import android.support.v4.app.Fragment

import me.xiaopan.assemblyadapter.AssemblyFragmentItemFactory
import me.xiaopan.sketchsample.bean.Image
import me.xiaopan.sketchsample.fragment.ImageFragment
import me.xiaopan.sketchsample.util.AppConfig

class ImageFragmentItemFactory(private val context: Context, private var loadingImageOptionsId: String?) : AssemblyFragmentItemFactory<Image>() {

    override fun isTarget(o: Any): Boolean {
        return o is Image
    }

    override fun createFragment(i: Int, image: Image): Fragment {
        val showTools = AppConfig.getBoolean(context, AppConfig.Key.SHOW_TOOLS_IN_IMAGE_DETAIL)
        return ImageFragment.build(image, loadingImageOptionsId, showTools)
    }
}
