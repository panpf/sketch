package me.panpf.sketch.sample.vt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

open class BaseFragment : androidx.fragment.app.Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val bindContentView = javaClass.getAnnotation(BindContentView::class.java)
        return if (bindContentView != null && bindContentView.value > 0) {
            inflater.inflate(bindContentView.value, container, false)
        } else {
            super.onCreateView(inflater, container, savedInstanceState)
        }
    }
}