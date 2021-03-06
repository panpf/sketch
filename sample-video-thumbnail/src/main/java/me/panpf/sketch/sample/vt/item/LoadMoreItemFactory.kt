package me.panpf.sketch.sample.vt.item

import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import me.panpf.adapter.ktx.bindView
import me.panpf.adapter.more.AssemblyMoreItem
import me.panpf.adapter.more.AssemblyMoreItemFactory
import me.panpf.adapter.more.OnLoadMoreListener
import me.panpf.sketch.sample.vt.R

class LoadMoreItemFactory(eventListener: OnLoadMoreListener? = null) : AssemblyMoreItemFactory<String>(eventListener) {

    override fun createAssemblyItem(viewGroup: ViewGroup): LoadMoreItem {
        return LoadMoreItem(this, R.layout.list_footer_load_more, viewGroup)
    }

    inner class LoadMoreItem(itemFactory: LoadMoreItemFactory, itemLayoutId: Int, parent: ViewGroup) :
            AssemblyMoreItem<String>(itemFactory, itemLayoutId, parent) {
        private val progressBar: ProgressBar by bindView(R.id.progress_loadMoreFooter)
        private val tipsTextView: TextView by bindView(R.id.text_loadMoreFooter_content)

        override fun getErrorRetryView(): View {
            return tipsTextView
        }

        override fun showLoading() {
            progressBar.visibility = View.VISIBLE
            tipsTextView.text = "LOADING..."
        }

        override fun showErrorRetry() {
            progressBar.visibility = View.GONE
            tipsTextView.text = "ERROR!"
        }

        override fun showEnd() {
            progressBar.visibility = View.GONE
            tipsTextView.text = "THE END"
        }
    }
}
