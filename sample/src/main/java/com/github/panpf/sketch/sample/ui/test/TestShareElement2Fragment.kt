package com.github.panpf.sketch.sample.ui.test

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import androidx.core.view.ViewCompat
import androidx.transition.TransitionInflater
import com.github.panpf.sketch.displayAssetImage
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.databinding.TestShareElement2FragmentBinding
import com.github.panpf.sketch.sample.ui.base.BindingFragment
import java.util.concurrent.TimeUnit.MILLISECONDS

class TestShareElement2Fragment : BindingFragment<TestShareElement2FragmentBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition = TransitionInflater.from(requireContext())
                .inflateTransition(R.transition.my_move)
        }
    }

    override fun onViewCreated(
        binding: TestShareElement2FragmentBinding,
        savedInstanceState: Bundle?
    ) {
        binding.testShareElement2Image.apply {
            ViewCompat.setTransitionName(this, "transition_app_icon")
            Log.i("ShareElementTest", "$id. displayImage")
            postponeEnterTransition(100, MILLISECONDS)
            displayAssetImage("sample_huge_world.jpg") {
                listener(
                    onSuccess = { _, _ ->
                        startPostponedEnterTransition()
                    },
                    onError = { _, _ ->
                        startPostponedEnterTransition()
                    }
                )
            }

            setOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }
    }
}