package com.github.panpf.sketch.compose.sample.base

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.view.updateLayoutParams
import androidx.viewbinding.ViewBinding
import com.github.panpf.sketch.compose.sample.R
import com.github.panpf.tools4a.display.ktx.getStatusBarHeight
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
abstract class ToolbarBindingFragment<VIEW_BINDING : ViewBinding> : BaseFragment() {

    protected var toolbar: Toolbar? = null
    protected var binding: VIEW_BINDING? = null

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_toolbar, container, false).apply {
        val toolbar = findViewById<Toolbar>(R.id.toolbarFragmentToolbar)
        val contentContainer = findViewById<FrameLayout>(R.id.toolbarFragmentContent)

        setTransparentStatusBar(toolbar)

        val binding = createViewBinding(inflater, contentContainer)
        contentContainer.addView(binding.root)

        this@ToolbarBindingFragment.toolbar = toolbar
        this@ToolbarBindingFragment.binding = binding
    }

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = this.binding!!
        val toolbar = this.toolbar!!
        onInitViews(toolbar, binding, savedInstanceState)
        onInitData(toolbar, binding, savedInstanceState)
    }

    @SuppressLint("ObsoleteSdkInt")
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

    override fun onDestroyView() {
        this.binding = null
        this.toolbar = null
        super.onDestroyView()
    }
}