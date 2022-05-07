package com.github.panpf.sketch.sample.ui.test

import android.os.Bundle
import android.util.Log
import androidx.core.view.ViewCompat
import androidx.fragment.app.commit
import com.github.panpf.sketch.displayAssetImage
import com.github.panpf.sketch.sample.databinding.TestShareElement1FragmentBinding
import com.github.panpf.sketch.sample.ui.base.BindingFragment
import java.util.concurrent.TimeUnit.MILLISECONDS

class TestShareElement1Fragment : BindingFragment<TestShareElement1FragmentBinding>() {

    override fun onViewCreated(
        binding: TestShareElement1FragmentBinding,
        savedInstanceState: Bundle?
    ) {
        binding.testShareElement1Image.apply {
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
                requireParentFragment().childFragmentManager.commit {
                    setReorderingAllowed(true)
                    addSharedElement(binding.testShareElement1Image, "transition_app_icon")
                    replace(this@TestShareElement1Fragment.id, TestShareElement2Fragment())
                    addToBackStack(null)
                }
            }
        }
    }
}