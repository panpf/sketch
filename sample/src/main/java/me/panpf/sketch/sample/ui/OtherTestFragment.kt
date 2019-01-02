package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_other_test.*
import me.panpf.sketch.request.ShapeSize
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.base.BaseFragment
import me.panpf.sketch.sample.base.BindContentView

@BindContentView(R.layout.fragment_other_test)
class OtherTestFragment : BaseFragment(){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        otherTestF_emptyImage.options.setErrorImage(R.drawable.image_loading)
        otherTestF_emptyImage.options.setShapeSize(ShapeSize.byViewFixedSize())
        otherTestF_emptyImage.displayImage("")
    }
}