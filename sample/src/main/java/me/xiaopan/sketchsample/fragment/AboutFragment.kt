package me.xiaopan.sketchsample.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import me.xiaopan.sketchsample.BaseFragment
import me.xiaopan.sketchsample.BindContentView
import me.xiaopan.sketchsample.BuildConfig
import me.xiaopan.sketchsample.R
import me.xiaopan.ssvt.bindView

/**
 * 关于 Fragment
 */
@BindContentView(R.layout.fragment_about)
class AboutFragment : BaseFragment() {
    val versionTextView: TextView by bindView(R.id.text_about_versions)
    val typesTextView: TextView by bindView(R.id.text_about_types)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        versionTextView.text = getString(R.string.text_version, BuildConfig.VERSION_NAME)
        typesTextView.text = getString(R.string.text_types, BuildConfig.BUILD_TYPE, BuildConfig.FLAVOR)
    }
}
