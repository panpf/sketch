package me.xiaoapn.easy.imageloader.display;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * 位图显示器
 */
public interface BitmapDisplayer {
	/**
	 * 显示
	 * @param imageView 图片视图
	 * @param bitmap 位图
	 * @param isFromMemoryCache 当前位图是否来自内存缓存
	 */
	public void display(ImageView imageView, Bitmap bitmap, boolean isFromMemoryCache);
}
