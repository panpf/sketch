package me.xiaopan.android.spear.sample.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import me.xiaopan.android.spear.Spear;

/**
 * 滚动中暂停暂停加载新图片管理器
 */
public class ScrollingPauseLoadManager extends RecyclerView.OnScrollListener implements AbsListView.OnScrollListener{
    private Spear spear;
    private Settings settings;

    public ScrollingPauseLoadManager(Context context) {
        this.spear = Spear.with(context);
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
            spear.getConfiguration().setPauseLoad(true);
        } else if(newState == RecyclerView.SCROLL_STATE_IDLE){
            if(spear.getConfiguration().isPauseLoad()){
                spear.getConfiguration().setPauseLoad(false);
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(!settings.isScrollingPauseLoad() || view.getAdapter() == null || !(view.getAdapter() instanceof BaseAdapter)){
            return;
        }

        if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
            if(!spear.getConfiguration().isPauseLoad()){
                spear.getConfiguration().setPauseLoad(true);
            }
        } else if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
            if(spear.getConfiguration().isPauseLoad()){
                spear.getConfiguration().setPauseLoad(false);
                ((BaseAdapter)view.getAdapter()).notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
