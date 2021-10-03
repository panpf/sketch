package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import me.panpf.sketch.sample.BuildConfig
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.base.BaseBindingFragment
import me.panpf.sketch.sample.databinding.FragmentAboutBinding

class AboutFragment : BaseBindingFragment<FragmentAboutBinding>() {

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentAboutBinding.inflate(inflater, parent, false)

    override fun onInitData(binding: FragmentAboutBinding, savedInstanceState: Bundle?) {
        binding.textAboutVersions.text = getString(R.string.text_version, BuildConfig.VERSION_NAME)
        binding.textAboutTypes.text =
            getString(R.string.text_types, BuildConfig.BUILD_TYPE, BuildConfig.FLAVOR)
    }
}
