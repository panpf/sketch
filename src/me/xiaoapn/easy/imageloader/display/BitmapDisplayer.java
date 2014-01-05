package me.xiaoapn.easy.imageloader.display;

import me.xiaoapn.easy.imageloader.ImageLoader;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

/**
 * 位图显示器
 */
public interface BitmapDisplayer {
	/**
	 * 显示
	 * @param imageView
	 * @param bitmapDrawable
	 * @param isFromMemoryCache
	 * @param imageLoader
	 */
	public void display(ImageView imageView, BitmapDrawable bitmapDrawable, boolean isFromMemoryCache, ImageLoader imageLoader);
}
