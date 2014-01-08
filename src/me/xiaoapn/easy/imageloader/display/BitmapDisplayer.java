package me.xiaoapn.easy.imageloader.display;

import me.xiaoapn.easy.imageloader.Configuration;
import me.xiaoapn.easy.imageloader.task.ImageViewAware;
import me.xiaoapn.easy.imageloader.task.Request;
import android.graphics.drawable.BitmapDrawable;

/**
 * 位图显示器
 */
public interface BitmapDisplayer {
	/**
	 * 显示
	 * @param imageViewAware
	 * @param bitmapDrawable
	 * @param bitmapType
	 * @param isFromMemoryCache
	 * @param configuration
	 * @param request
	 */
	public void display(ImageViewAware imageViewAware, BitmapDrawable bitmapDrawable, BitmapType bitmapType, boolean isFromMemoryCache, Configuration configuration, Request request);
}
