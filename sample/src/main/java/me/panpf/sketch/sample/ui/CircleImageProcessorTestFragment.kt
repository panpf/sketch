package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import com.github.panpf.tools4a.dimen.ktx.dp2px
import me.panpf.sketch.display.TransitionImageDisplayer
import me.panpf.sketch.process.CircleImageProcessor
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.base.BaseFragment
import me.panpf.sketch.sample.databinding.FragmentCircleBinding

class CircleImageProcessorTestFragment : BaseFragment<FragmentCircleBinding>() {

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentCircleBinding.inflate(inflater, parent, false)

    override fun onInitData(
        binding: FragmentCircleBinding,
        savedInstanceState: Bundle?
    ) {
        binding.circleImage.apply {
            options.apply {
                val metrics = resources.displayMetrics
                setMaxSize(metrics.widthPixels / 2, metrics.heightPixels / 2)
                processor = CircleImageProcessor.getInstance()
                displayer = TransitionImageDisplayer()
            }

            displayImage(AssetImage.MEI_NV)
        }
    }
}
