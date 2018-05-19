package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_about.*
import me.panpf.sketch.sample.BaseFragment
import me.panpf.sketch.sample.BindContentView
import me.panpf.sketch.sample.BuildConfig
import me.panpf.sketch.sample.R

/**
 * 关于 Fragment
 */
@BindContentView(R.layout.fragment_about)
class AboutFragment : BaseFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text_about_versions.text = getString(R.string.text_version, BuildConfig.VERSION_NAME)
        text_about_types.text = getString(R.string.text_types, BuildConfig.BUILD_TYPE, BuildConfig.FLAVOR)
    }
}
