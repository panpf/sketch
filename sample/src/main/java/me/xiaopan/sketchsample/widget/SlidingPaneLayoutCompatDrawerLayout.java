package me.xiaopan.sketchsample.widget;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SlidingPaneLayout;

public class SlidingPaneLayoutCompatDrawerLayout extends DrawerLayout{
    private SlidingPaneLayout slidingPaneLayout;

    public SlidingPaneLayoutCompatDrawerLayout(Context context, SlidingPaneLayout slidingPaneLayout) {
        super(context);
        this.slidingPaneLayout = slidingPaneLayout;
    }

    @Override
    public boolean isDrawerOpen(int drawerGravity) {
        return slidingPaneLayout.isOpen();
    }

    @Override
    public boolean isDrawerVisible(int drawerGravity) {
        return slidingPaneLayout.isOpen();
    }

    @Override
    public void closeDrawer(int gravity) {
        slidingPaneLayout.closePane();
    }

    @Override
    public void openDrawer(int gravity) {
        slidingPaneLayout.openPane();
    }

    @Override
    public Resources getResources() {
        if(slidingPaneLayout != null){
            return slidingPaneLayout.getResources();
        }else{
            return super.getResources();
        }
    }
}
