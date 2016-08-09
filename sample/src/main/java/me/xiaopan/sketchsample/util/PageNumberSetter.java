package me.xiaopan.sketchsample.util;

import android.support.v4.view.ViewPager;
import android.widget.TextView;

public class PageNumberSetter implements ViewPager.OnPageChangeListener {
    private TextView textView;
    private ViewPager.OnPageChangeListener onPageChangeListener;

    public PageNumberSetter(TextView textView, ViewPager viewPager) {
        this.textView = textView;
        viewPager.setOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        textView.setText((position + 1) + "");
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
    }
}
