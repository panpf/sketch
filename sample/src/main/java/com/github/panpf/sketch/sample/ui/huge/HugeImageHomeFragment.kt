package com.github.panpf.sketch.sample.ui.huge

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.databinding.ContainerFragmentBinding
import com.github.panpf.sketch.sample.ui.base.ToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.huge.Layout.COLUMN

class HugeImageHomeFragment : ToolbarBindingFragment<ContainerFragmentBinding>() {

    private val viewModel by viewModels<HugeImageListViewModel>()

    override fun onViewCreated(
        toolbar: Toolbar,
        binding: ContainerFragmentBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "Huge Image"

        val menu = toolbar.menu.add("Layout").apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            setOnMenuItemClickListener {
                val newLayout = if (viewModel.layoutData.value == COLUMN) {
                    Layout.ROW
                } else {
                    COLUMN
                }
                viewModel.changeTab(newLayout)
                true
            }
        }

        viewModel.layoutData.observe(viewLifecycleOwner) {
            val meuIcon = if (it == COLUMN) {
                R.drawable.ic_layout_row
            } else {
                R.drawable.ic_layout_column
            }
            menu.setIcon(meuIcon)

            val fragment = if (it == COLUMN) {
                HugeImageHorPagerFragment()
            } else {
                HugeImageVerPagerFragment()
            }
            childFragmentManager.beginTransaction()
                .replace(binding.containerFragmentContainer.id, fragment)
                .commit()
        }
    }
}