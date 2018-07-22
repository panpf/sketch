package me.panpf.sketch.sample.base

import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.panpf.sketch.sample.util.DataTransferStation

abstract class BaseFragment : Fragment() {
    val dataTransferHelper = DataTransferStation.PageHelper(this)
    var isViewCreated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataTransferHelper.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val bindContentView = javaClass.getAnnotation(BindContentView::class.java)
        return if (bindContentView != null && bindContentView.value > 0) {
            inflater.inflate(bindContentView.value, container, false)
        } else {
            super.onCreateView(inflater, container, savedInstanceState)
        }
    }

    override fun onViewCreated(@NonNull view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isViewCreated = true
    }

    override fun onPause() {
        super.onPause()
        if (userVisibleHint) {
            onUserVisibleChanged(false)
        }
    }

    override fun onResume() {
        super.onResume()
        if (userVisibleHint) {
            onUserVisibleChanged(true)
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isResumed) {
            onUserVisibleChanged(isVisibleToUser)
        }
    }

    protected open fun onUserVisibleChanged(isVisibleToUser: Boolean) {

    }

    val isVisibleToUser: Boolean
        get() = isResumed && userVisibleHint

    override fun onDestroyView() {
        super.onDestroyView()
        isViewCreated = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        dataTransferHelper.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        dataTransferHelper.onDestroy()
    }
}