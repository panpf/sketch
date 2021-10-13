package me.panpf.sketch.sample.base

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.github.panpf.tools4a.display.ktx.getStatusBarHeight
import me.panpf.sketch.sample.R
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseToolbarFragment<VIEW_BINDING : ViewBinding> : Fragment() {

    protected var toolbar: Toolbar? = null
    protected var binding: VIEW_BINDING? = null
    private val userVisibleChangedListenerList = LinkedList<UserVisibleChangedListener>()

    val isViewCreated: Boolean
        get() = view != null

    val isVisibleToUser: Boolean
        get() = isResumed && userVisibleHint

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_toolbar, container, false).apply {
            val toolbar = findViewById<Toolbar>(R.id.toolbarFragmentToolbar)
            val contentContainer = findViewById<FrameLayout>(R.id.toolbarFragmentContent)

            setTransparentStatusBar(toolbar)

            val binding = createViewBinding(inflater, contentContainer)
            contentContainer.addView(binding.root)

            this@BaseToolbarFragment.toolbar = toolbar
            this@BaseToolbarFragment.binding = binding
        }
    }

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = this.binding!!
        val toolbar = this.toolbar!!
        onInitViews(toolbar, binding, savedInstanceState)
        onInitData(toolbar, binding, savedInstanceState)
    }

    private fun setTransparentStatusBar(toolbar: Toolbar) {
        val window = requireActivity().window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
            && window.decorView.systemUiVisibility == View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        ) {
            toolbar.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin += requireContext().getStatusBarHeight()
            }
        }
    }

    override fun onDestroyView() {
        this.binding = null
        super.onDestroyView()
    }

    protected abstract fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): VIEW_BINDING

    protected open fun onInitViews(
        toolbar: Toolbar,
        binding: VIEW_BINDING,
        savedInstanceState: Bundle?
    ) {

    }

    protected abstract fun onInitData(
        toolbar: Toolbar,
        binding: VIEW_BINDING,
        savedInstanceState: Bundle?
    )

    override fun onPause() {
        super.onPause()
        if (userVisibleHint) {
            userVisibleChangedListenerList.forEach {
                it.onUserVisibleChanged(false)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (userVisibleHint) {
            userVisibleChangedListenerList.forEach {
                it.onUserVisibleChanged(true)
            }
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isResumed) {
            userVisibleChangedListenerList.forEach {
                it.onUserVisibleChanged(isVisibleToUser)
            }
        }
    }

    fun registerUserVisibleChangedListener(
        owner: LifecycleOwner,
        userVisibleChangedListener: UserVisibleChangedListener
    ) {
        userVisibleChangedListenerList.add(userVisibleChangedListener)
        owner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event.targetState == Lifecycle.State.DESTROYED) {
                userVisibleChangedListenerList.remove(userVisibleChangedListener)
            }
        })
    }

    fun registerUserVisibleChangedListener(
        userVisibleChangedListener: UserVisibleChangedListener
    ) {
        userVisibleChangedListenerList.add(userVisibleChangedListener)
    }

    fun unregisterUserVisibleChangedListener(
        userVisibleChangedListener: UserVisibleChangedListener
    ) {
        userVisibleChangedListenerList.remove(userVisibleChangedListener)
    }
}