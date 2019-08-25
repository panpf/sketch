package me.panpf.sketch.sample.item

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import me.panpf.adapter.AssemblyItem
import me.panpf.adapter.AssemblyItemFactory
import me.panpf.adapter.ktx.bindView
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.bean.InfoMenu

class InfoMenuItem(parent: ViewGroup, val factory: Factory) : AssemblyItem<InfoMenu>(R.layout.list_item_info_menu, parent) {
    private val titleTextView: TextView by bindView(R.id.text_infoMenuItem_title)
    private val infoTextView: TextView by bindView(R.id.text_infoMenuItem_info)

    override fun onConfigViews(context: Context) {
        itemView.setOnClickListener {
            data?.onClick(factory.adapter)
        }
    }

    override fun onSetData(i: Int, infoMenu: InfoMenu?) {
        titleTextView.text = infoMenu?.title
        infoTextView.text = infoMenu?.getInfo()
    }

    class Factory : AssemblyItemFactory<InfoMenu>() {

        override fun match(o: Any?): Boolean {
            return o is InfoMenu
        }

        override fun createAssemblyItem(viewGroup: ViewGroup): InfoMenuItem {
            return InfoMenuItem(viewGroup, this)
        }
    }
}
