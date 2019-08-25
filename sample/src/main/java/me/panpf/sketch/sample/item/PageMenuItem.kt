package me.panpf.sketch.sample.item

import android.view.ViewGroup
import android.widget.TextView
import me.panpf.adapter.AssemblyItem
import me.panpf.adapter.AssemblyItemFactory
import me.panpf.adapter.ktx.bindView
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.ui.Page

class PageMenuItem(parent: ViewGroup) : AssemblyItem<Page>(R.layout.list_item_page_menu, parent) {

    private val textView: TextView by bindView(R.id.text_pageMenuItem)

    override fun onSetData(i: Int, pageMenu: Page?) {
        textView.text = pageMenu?.showName
    }

    class Factory : AssemblyItemFactory<Page>() {

        override fun match(o: Any?): Boolean = o is Page

        override fun createAssemblyItem(viewGroup: ViewGroup) = PageMenuItem(viewGroup)
    }
}
