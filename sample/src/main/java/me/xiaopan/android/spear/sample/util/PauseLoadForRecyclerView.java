package me.xiaopan.android.spear.sample.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;

import me.xiaopan.android.spear.Spear;

/**
 * 用来实现滚动的时候停止加载新图片
 */
public class PauseLoadForRecyclerView extends RecyclerView.OnScrollListener {
    private static final String PREFERENCE_PAUSE_LOAD_ON_SCROLLING = "PREFERENCE_PAUSE_LOAD_ON_SCROLLING";
    private Spear spear;
    private static boolean enable;

    public PauseLoadForRecyclerView(Context context) {
        this.spear = Spear.with(context);
        enable = isEnablePauseLoadOnScrolling(context);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        if(recyclerView.getAdapter() == null || !enable){
            return;
        }

        if(newState == RecyclerView.SCROLL_STATE_DRAGGING){
            if(!spear.isPauseLoadOnScrolling()){
                spear.setPauseLoadOnScrolling(true);
            }
        } else if(newState == RecyclerView.SCROLL_STATE_IDLE){
            if(spear.isPauseLoadOnScrolling()){
                spear.setPauseLoadOnScrolling(false);
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        }
    }

    public static boolean isEnablePauseLoadOnScrolling(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREFERENCE_PAUSE_LOAD_ON_SCROLLING, false);
    }

    public static void setEnablePauseLoadOnScrolling(Context context, boolean isPauseLoadOnScrolling){
        SharedPreferences.Editor preferences = PreferenceManager.getDefaultSharedPreferences(context).edit();
        preferences.putBoolean(PREFERENCE_PAUSE_LOAD_ON_SCROLLING, isPauseLoadOnScrolling);
        preferences.apply();
        enable = isPauseLoadOnScrolling;
    }
}
