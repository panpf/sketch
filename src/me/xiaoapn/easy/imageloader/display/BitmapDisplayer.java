package me.xiaoapn.easy.imageloader.display;

import me.xiaoapn.easy.imageloader.Configuration;
import me.xiaoapn.easy.imageloader.task.Request;
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
	 * @param configuration
	 * @param request
	 */
	public void display(ImageView imageView, BitmapDrawable bitmapDrawable, BitmapType bitmapType, Request request, Configuration configuration);
	
	/**
	 * 拷贝
	 * @return
	 */
	public BitmapDisplayer copy();
	
	public enum BitmapType {
		SUCCESS, FAILURE;
	}
}
