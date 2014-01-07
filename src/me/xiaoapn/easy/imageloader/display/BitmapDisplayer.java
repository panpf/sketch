package me.xiaoapn.easy.imageloader.display;

import me.xiaoapn.easy.imageloader.Configuration;
import me.xiaoapn.easy.imageloader.execute.task.Request;
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
	 * @param bitmapType
	 * @param isFromMemoryCache
	 * @param configuration
	 * @param request
	 */
	public void display(ImageView imageView, BitmapDrawable bitmapDrawable, BitmapType bitmapType, boolean isFromMemoryCache, Configuration configuration, Request request);
}
