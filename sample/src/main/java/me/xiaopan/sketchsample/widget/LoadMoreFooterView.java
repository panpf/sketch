package me.xiaopan.sketchsample.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.etsy.android.grid.HeaderViewListAdapter;

import me.xiaopan.sketchsample.R;

public class LoadMoreFooterView extends FrameLayout implements HeaderViewListAdapter.OnGetFooterViewListener{
    private boolean loading;
    private boolean end;
    private OnLoadMoreListener onLoadMoreListener;
    private ProgressBar progressBar;
    private TextView tipsTextView;
    private boolean pause;

    public LoadMoreFooterView(Context context) {
        super(context);
        init();
    }

    public LoadMoreFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadMoreFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.list_footer_load_more, this);
        progressBar = (ProgressBar) findViewById(R.id.progress_loadMoreFooter);
        tipsTextView = (TextView) findViewById(R.id.text_loadMoreFooter_content);
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public boolean isEnd() {
        return end;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setEnd(boolean end) {
        this.end = end;
        if(end){
            progressBar.setVisibility(View.GONE);
            tipsTextView.setText("没有您的包裹了！");
        }else{
            progressBar.setVisibility(View.VISIBLE);
            tipsTextView.setText("别着急，您的包裹马上就来！");
        }
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void loadFinished(boolean success){
        if(!success){
            progressBar.setVisibility(View.GONE);
            tipsTextView.setText("Sorry！您的包裹运送失败！");
        }
        loading = false;
    }

    @Override
    public void onGetFooterView(View view) {
        if(view != this){
            return;
        }

        if(end){
            progressBar.setVisibility(View.GONE);
            tipsTextView.setText("没有您的包裹了！");
        }else{
            progressBar.setVisibility(View.VISIBLE);
            tipsTextView.setText("别着急，您的包裹马上就来！");
        }

        if(onLoadMoreListener == null || end || pause || loading){
            return;
        }

        onLoadMoreListener.onLoadMore(this);
        loading = true;
    }

    public interface OnLoadMoreListener {
        void onLoadMore(LoadMoreFooterView loadMoreFooterView);
    }
}