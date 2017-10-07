package me.xiaopan.sketchsample

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.xiaopan.sketchsample.util.DataTransferStation

open class BaseFragment : Fragment() {
    val dataTransferHelper = DataTransferStation.PageHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataTransferHelper.onCreate(savedInstanceState)
    }

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

    protected open fun onUserVisibleChanged(isVisibleToUser: Boolean) {

    }

    val isVisibleToUser: Boolean
        get() = isResumed && userVisibleHint

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        dataTransferHelper.onSaveInstanceState(outState!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        dataTransferHelper.onDestroy()
    }
}