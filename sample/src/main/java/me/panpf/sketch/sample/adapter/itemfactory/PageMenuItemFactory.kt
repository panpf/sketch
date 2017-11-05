package me.panpf.sketch.sample.adapter.itemfactory

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import me.xiaopan.assemblyadapter.AssemblyRecyclerItem
import me.xiaopan.assemblyadapter.AssemblyRecyclerItemFactory
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.activity.MainActivity
import me.panpf.sketch.sample.bindView

class PageMenuItemFactory(private val onClickItemListener: OnClickItemListener) : AssemblyRecyclerItemFactory<PageMenuItemFactory.PageMenuItem>() {

    override fun isTarget(o: Any): Boolean {
        return o is MainActivity.Page
    }

    override fun createAssemblyItem(viewGroup: ViewGroup): PageMenuItem {
        return PageMenuItem(R.layout.list_item_page_menu, viewGroup)
    }

    interface OnClickItemListener {
        fun onClickItem(page: MainActivity.Page)
    }

    inner class PageMenuItem(itemLayoutId: Int, parent: ViewGroup) : AssemblyRecyclerItem<MainActivity.Page>(itemLayoutId, parent) {
        val textView: TextView by bindView(R.id.text_pageMenuItem)

        override fun onConfigViews(context: Context) {
            textView.setOnClickListener { onClickItemListener.onClickItem(data) }
        }

        override fun onSetData(i: Int, pageMenu: MainActivity.Page) {
            textView.text = pageMenu.showName
        }
    }
}
