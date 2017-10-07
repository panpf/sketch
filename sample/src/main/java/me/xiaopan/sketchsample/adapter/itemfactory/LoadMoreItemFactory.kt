package me.xiaopan.sketchsample.adapter.itemfactory

import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import me.xiaopan.assemblyadapter.AssemblyLoadMoreRecyclerItemFactory
import me.xiaopan.assemblyadapter.OnRecyclerLoadMoreListener
import me.xiaopan.sketchsample.R
import me.xiaopan.ssvt.bindView

class LoadMoreItemFactory(eventListener: OnRecyclerLoadMoreListener) : AssemblyLoadMoreRecyclerItemFactory(eventListener) {

    override fun createAssemblyItem(viewGroup: ViewGroup): AssemblyLoadMoreRecyclerItemFactory.AssemblyLoadMoreRecyclerItem<*> {
        return LoadMoreItem(R.layout.list_footer_load_more, viewGroup)
    }

    inner class LoadMoreItem(itemLayoutId: Int, parent: ViewGroup) : AssemblyLoadMoreRecyclerItemFactory.AssemblyLoadMoreRecyclerItem<LoadMoreItem>(itemLayoutId, parent) {
        val progressBar: ProgressBar by bindView(R.id.progress_loadMoreFooter)
        val tipsTextView: TextView by bindView(R.id.text_loadMoreFooter_content)

        override fun getErrorRetryView(): View? {
            return null
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
