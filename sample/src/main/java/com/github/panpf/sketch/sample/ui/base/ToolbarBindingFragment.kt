package com.github.panpf.sketch.sample.ui.base

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
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.util.createViewBinding
import com.github.panpf.tools4a.display.ktx.getStatusBarHeight

@Suppress("MemberVisibilityCanBePrivate")
abstract class ToolbarBindingFragment<VIEW_BINDING : ViewBinding> : BaseFragment() {

    protected var toolbar: Toolbar? = null
    protected var binding: VIEW_BINDING? = null

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.toolbar_fragment, container, false).apply {
        val toolbar = findViewById<Toolbar>(R.id.toolbarToolbar)
        val contentContainer = findViewById<FrameLayout>(R.id.toolbarContent)

        setTransparentStatusBar(toolbar)

        val binding = createViewBinding(inflater, contentContainer) as VIEW_BINDING
        contentContainer.addView(binding.root)

        this@ToolbarBindingFragment.toolbar = toolbar
        this@ToolbarBindingFragment.binding = binding
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

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewCreated(this.toolbar!!, this.binding!!, savedInstanceState)
    }

    protected open fun onViewCreated(
        toolbar: Toolbar,
        binding: VIEW_BINDING,
        savedInstanceState: Bundle?
    ) {

    }

    override fun onDestroyView() {
        this.binding = null
        this.toolbar = null
        super.onDestroyView()
    }
}