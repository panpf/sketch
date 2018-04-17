package me.panpf.sketch.sample.adapter.itemfactory

import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import me.panpf.adapter.more.AssemblyLoadMoreItem
import me.panpf.adapter.more.AssemblyLoadMoreItemFactory
import me.panpf.adapter.more.OnLoadMoreListener
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.bindView

class LoadMoreItemFactory(eventListener: OnLoadMoreListener) : AssemblyLoadMoreItemFactory<LoadMoreItemFactory.LoadMoreItem>(eventListener) {

    override fun createAssemblyItem(viewGroup: ViewGroup): LoadMoreItem {
        return LoadMoreItem(this, R.layout.list_footer_load_more, viewGroup)
    }

    inner class LoadMoreItem(itemFactory: LoadMoreItemFactory, itemLayoutId: Int, parent: ViewGroup) :
            AssemblyLoadMoreItem<LoadMoreItem>(itemFactory, itemLayoutId, parent) {
        val progressBar: ProgressBar by bindView(R.id.progress_loadMoreFooter)
        val tipsTextView: TextView by bindView(R.id.text_loadMoreFooter_content)

        override fun getErrorRetryView(): View {
            return tipsTextView
        }

        override fun showLoading() {
            progressBar.visibility = View.VISIBLE
            tipsTextView.text = "别着急，您的包裹马上就来！"
        }

        override fun showErrorRetry() {
            progressBar.visibility = View.GONE
            tipsTextView.text = "Sorry！您的包裹运送失败！"
        }

        override fun showEnd() {
            progressBar.visibility = View.GONE
            tipsTextView.text = "没有您的包裹了！"
        }
    }
}
