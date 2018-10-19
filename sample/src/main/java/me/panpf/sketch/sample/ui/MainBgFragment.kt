package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.updateLayoutParams
import kotlinx.android.synthetic.main.fm_main_bg.*
import me.panpf.androidxkt.view.isOrientationPortrait
import me.panpf.sketch.sample.ImageOptions
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.base.BaseFragment
import me.panpf.sketch.sample.base.BindContentView
import me.panpf.sketch.sample.event.ChangeMainPageBgEvent
import me.panpf.sketch.sample.event.RegisterEvent
import me.panpf.sketch.sample.util.DeviceUtils
import org.greenrobot.eventbus.Subscribe

@RegisterEvent
@BindContentView(R.layout.fm_main_bg)
class MainBgFragment : BaseFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // + DeviceUtils.getNavigationBarHeightByUiVisibility(this) 是为了兼容 MIX 2
        mainBgFm_image.updateLayoutParams {
            width = resources.displayMetrics.widthPixels
            height = resources.displayMetrics.heightPixels
            if (this@MainBgFragment.isOrientationPortrait()) {
                height += DeviceUtils.getWindowHeightSupplement(activity)
            } else {
                width += DeviceUtils.getWindowHeightSupplement(activity)
            }
        }
        mainBgFm_image.setOptions(ImageOptions.WINDOW_BACKGROUND)
    }

    @Suppress("unused")
    @Subscribe
    fun onEvent(eventChange: ChangeMainPageBgEvent) {
        mainBgFm_image.displayImage(eventChange.imageUrl)
    }
}