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
import com.github.panpf.sketch.compose.sample.R
import com.github.panpf.tools4a.display.ktx.getStatusBarHeight

@Suppress("MemberVisibilityCanBePrivate")
abstract class ToolbarFragment : BaseFragment() {

    protected var toolbar: Toolbar? = null

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_toolbar, container, false).apply {
        val toolbar = findViewById<Toolbar>(R.id.toolbarFragmentToolbar)
        val contentContainer = findViewById<FrameLayout>(R.id.toolbarFragmentContent)

        setTransparentStatusBar(toolbar)

        val view = createView(toolbar, inflater, contentContainer)
        contentContainer.addView(view)

        this@ToolbarFragment.toolbar = toolbar
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

    protected abstract fun createView(
        toolbar: Toolbar,
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): View

    override fun onDestroyView() {
        this.toolbar = null
        super.onDestroyView()
    }
}