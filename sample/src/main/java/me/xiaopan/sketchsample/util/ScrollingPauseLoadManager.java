package me.xiaopan.sketchsample.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

import me.xiaopan.sketch.Sketch;

/**
 * 滚动中暂停暂停加载新图片管理器
 */
public class ScrollingPauseLoadManager extends RecyclerView.OnScrollListener implements AbsListView.OnScrollListener{
    private Sketch sketch;
    private Settings settings;

    public ScrollingPauseLoadManager(Context context) {
        this.sketch = Sketch.with(context);
        this.settings = Settings.with(context);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        if(!settings.isScrollingPauseLoad() || recyclerView.getAdapter() == null){
            return;
        }

        if(newState == RecyclerView.SCROLL_STATE_DRAGGING){
            sketch.getConfiguration().setPauseLoad(true);
        } else if(newState == RecyclerView.SCROLL_STATE_IDLE){
            if(sketch.getConfiguration().isPauseLoad()){
                sketch.getConfiguration().setPauseLoad(false);
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        ListAdapter listAdapter = view.getAdapter();
        if(listAdapter == null){
            return;
        }
        if(listAdapter instanceof WrapperListAdapter){
            listAdapter = ((WrapperListAdapter)listAdapter).getWrappedAdapter();
        }
        if(!settings.isScrollingPauseLoad() || listAdapter == null || !(listAdapter instanceof BaseAdapter)){
            return;
        }

        if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
            if(!sketch.getConfiguration().isPauseLoad()){
                sketch.getConfiguration().setPauseLoad(true);
            }
        } else if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
            if(sketch.getConfiguration().isPauseLoad()){
                sketch.getConfiguration().setPauseLoad(false);
                ((BaseAdapter)listAdapter).notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
