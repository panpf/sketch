package com.github.panpf.sketch.sample.ui.test

import android.os.Bundle
import androidx.fragment.app.commit
import com.github.panpf.sketch.sample.databinding.TestFragmentBinding
import com.github.panpf.sketch.sample.ui.base.BindingFragment

class TestFragment : BindingFragment<TestFragmentBinding>() {

    override fun onViewCreated(binding: TestFragmentBinding, savedInstanceState: Bundle?) {
        childFragmentManager.commit {
            replace(binding.testFragmentContainer.id, TestShareElement1Fragment())
//            replace(binding.testFragmentContainer.id, TestShareElement2Fragment())
//            replace(binding.testFragmentContainer.id, TestViewSizeFragment())
        }
    }
}