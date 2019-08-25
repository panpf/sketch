package me.panpf.sketch.sample.item

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import me.panpf.pagerid.PagerIndicator
import me.panpf.sketch.sample.R
import me.panpf.sketch.util.SketchUtils

class TitleTabFactory(private val titles: Array<String>, val context: Context) : PagerIndicator.TabViewFactory {

    override fun addTabs(viewGroup: ViewGroup, i: Int) {
        titles.withIndex().forEach { (index, title) ->
            val textView = TextView(context)
            textView.text = title
            val padding = SketchUtils.dp2px(context, 12)
            when (index) {
                0 -> textView.setPadding(padding, padding, padding / 2, padding)
                (titles.size - 1) -> textView.setPadding(padding / 2, padding, padding, padding)
                else -> textView.setPadding(padding / 2, padding, padding / 2, padding)
            }
            textView.gravity = Gravity.CENTER
            textView.setTextColor(context.resources.getColorStateList(R.color.tab))
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12f)
            viewGroup.addView(textView)
        }
    }
}