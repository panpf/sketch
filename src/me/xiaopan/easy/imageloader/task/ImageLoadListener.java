package me.xiaopan.easy.imageloader.task;

import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

public interface ImageLoadListener {
	public void onStarted(String imageUri, ImageView imageView);
	public void onFailed(String imageUri, ImageView imageView);
	public void onComplete(String imageUri, ImageView imageView, BitmapDrawable drawable);
	public void onCancelled(String imageUri, ImageView imageView);
}
