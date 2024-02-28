package com.github.panpf.sketch.sample.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.github.panpf.sketch.sample.databinding.FragmentViewNavHostBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingFragment

class ViewNavHostFragment : BaseBindingFragment<FragmentViewNavHostBinding>() {

    override fun onViewCreated(binding: FragmentViewNavHostBinding, savedInstanceState: Bundle?) {

    }

    @SuppressLint("RestrictedApi")
    override fun onFirstResume() {
        super.onFirstResume()
        findNavController().apply {
            setOnBackPressedDispatcher(requireActivity().onBackPressedDispatcher)
            enableOnBackPressed(true)
        }
    }
}