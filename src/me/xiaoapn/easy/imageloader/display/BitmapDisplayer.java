package me.xiaoapn.easy.imageloader.display;

import me.xiaoapn.easy.imageloader.Options;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

/**
 * 位图显示器
 */
public interface BitmapDisplayer {
	/**
	 * 显示
	 * @param resources
	 * @param imageView
	 * @param bitmapDrawable
	 * @param options
	 * @param isFromMemoryCache
	 */
	public void display(Resources resources, ImageView imageView, BitmapDrawable bitmapDrawable, Options options, boolean isFromMemoryCache);
}
