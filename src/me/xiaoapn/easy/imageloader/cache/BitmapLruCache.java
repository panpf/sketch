package me.xiaoapn.easy.imageloader.cache;

import me.xiaoapn.easy.imageloader.util.GeneralUtils;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.util.LruCache;

public class BitmapLruCache extends LruCache<String, BitmapDrawable> {
	public BitmapLruCache(int maxSize) {
		super(maxSize);
	}

	@Override
	protected int sizeOf(String key, BitmapDrawable value) {
		final int bitmapSize = getBitmapSize(value); 
		return bitmapSize == 0 ? 1 : bitmapSize;
	}

    /**
     * Get the size in bytes of a bitmap in a BitmapDrawable.
     * @param value
     * @return size in bytes
     */
    @TargetApi(12)
    public int getBitmapSize(BitmapDrawable value) {
        Bitmap bitmap = value.getBitmap();
        if (GeneralUtils.hasHoneycombMR1()) {
            return bitmap.getByteCount();
        }else{
        	return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }
}
