package me.xiaopan.sketchsample.adapter.itemfactory;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.xiaopan.assemblyadapter.AssemblyLoadMoreRecyclerItemFactory;
import me.xiaopan.assemblyadapter.OnRecyclerLoadMoreListener;
import me.xiaopan.sketchsample.R;

public class LoadMoreItemFactory extends AssemblyLoadMoreRecyclerItemFactory {
    public LoadMoreItemFactory(OnRecyclerLoadMoreListener eventListener) {
        super(eventListener);
    }

    @Override
    public AssemblyLoadMoreRecyclerItem createAssemblyItem(ViewGroup viewGroup) {
        return new LoadMoreItem(R.layout.list_footer_load_more, viewGroup);
    }

    public class LoadMoreItem extends AssemblyLoadMoreRecyclerItem {
        @BindView(R.id.progress_loadMoreFooter)
        ProgressBar progressBar;

        @BindView(R.id.text_loadMoreFooter_content)
        TextView tipsTextView;

        public LoadMoreItem(int itemLayoutId, ViewGroup parent) {
            super(itemLayoutId, parent);
        }

        @Override
        public View getErrorRetryView() {
            return null;
        }

        @Override
        public void showLoading() {
            progressBar.setVisibility(View.VISIBLE);
            tipsTextView.setText("别着急，您的包裹马上就来！");
        }

        @Override
        public void showErrorRetry() {
            progressBar.setVisibility(View.GONE);
            tipsTextView.setText("Sorry！您的包裹运送失败！");
        }

        @Override
        public void showEnd() {
            progressBar.setVisibility(View.GONE);
            tipsTextView.setText("没有您的包裹了！");
        }

        @Override
        protected void onFindViews() {
            ButterKnife.bind(this, getItemView());
        }
    }
}
