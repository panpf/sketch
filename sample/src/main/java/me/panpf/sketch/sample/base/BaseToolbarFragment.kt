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
import androidx.viewbinding.ViewBinding
import com.github.panpf.tools4a.display.ktx.getStatusBarHeight
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.util.DataTransferStation

@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseToolbarFragment<VIEW_BINDING : ViewBinding> : Fragment() {

    protected var binding: VIEW_BINDING? = null
    protected var toolbar: Toolbar? = null
    val isViewCreated: Boolean
        get() = view != null
    val dataTransferHelper = DataTransferStation.PageHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataTransferHelper.onCreate(savedInstanceState)
    }

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        dataTransferHelper.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        dataTransferHelper.onDestroy()
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
}