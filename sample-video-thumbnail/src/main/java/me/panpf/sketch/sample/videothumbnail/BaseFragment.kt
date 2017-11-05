package me.panpf.sketch.sample.videothumbnail

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

open class BaseFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val bindContentView = javaClass.getAnnotation(BindContentView::class.java)
        if (bindContentView != null && bindContentView.value > 0) {
            return inflater!!.inflate(bindContentView.value, container, false)
        } else {
            return super.onCreateView(inflater, container, savedInstanceState)
        }
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

    protected fun onUserVisibleChanged(isVisibleToUser: Boolean) {

    }

    val isVisibleToUser: Boolean
        get() = isResumed && userVisibleHint
}