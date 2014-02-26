package me.xiaopan.android.imageloader.task;

import android.graphics.drawable.BitmapDrawable;

class DrawableHolder {
	private int resId;	//当正在加载时显示的图片
	private BitmapDrawable drawable;	//当加载地址为空时显示的图片
	
	public DrawableHolder(int resId) {
		this.resId = resId;
	}
	
	public DrawableHolder() {
	}

	public int getResId() {
		return resId;
	}

	public void setResId(int resId) {
		this.resId = resId;
	}

	public BitmapDrawable getDrawable() {
		return drawable;
	}

	public void setDrawable(BitmapDrawable drawable) {
		this.drawable = drawable;
	}
}
