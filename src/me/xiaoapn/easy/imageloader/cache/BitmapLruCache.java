package me.xiaoapn.easy.imageloader.cache;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import me.xiaoapn.easy.imageloader.util.RecyclingBitmapDrawable;
import me.xiaoapn.easy.imageloader.util.Utils;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.util.LruCache;

public class BitmapLruCache extends LruCache<String, BitmapDrawable> {
	private Set<SoftReference<Bitmap>> mReusableBitmaps;
	
	public BitmapLruCache(int maxSize) {
		super(maxSize);
		if (Utils.hasHoneycomb()) {
		    mReusableBitmaps = Collections.synchronizedSet(new HashSet<SoftReference<Bitmap>>());
		}
	}

	@Override
	protected int sizeOf(String key, BitmapDrawable value) {
		final int bitmapSize = getBitmapSize(value); 
		return bitmapSize == 0 ? 1 : bitmapSize;
	}
	
    @Override
	protected void entryRemoved(boolean evicted, String key, BitmapDrawable oldValue, BitmapDrawable newValue) {
		if(RecyclingBitmapDrawable.class.isInstance(oldValue)){
			((RecyclingBitmapDrawable) oldValue).setIsCached(false);
		}else if(Utils.hasHoneycomb()){
			mReusableBitmaps.add(new SoftReference<Bitmap>(oldValue.getBitmap()));
		}
	}
    
    /**
     * 获取可再度使用的Bitmap
     * @param options
     * @return
     */
    public Bitmap getBitmapFromReusableSet(BitmapFactory.Options options){
    	Bitmap bitmap = null;
    	if(mReusableBitmaps != null && !mReusableBitmaps.isEmpty()){
    		Iterator<SoftReference<Bitmap>> bitmapIterator = mReusableBitmaps.iterator();
    		Bitmap item;
    		while(bitmapIterator.hasNext()){
    			item = bitmapIterator.next().get();
    			if(item != null && item.isMutable() && !item.isRecycled()){
    				if(canUseForInBitmap(item, options)){
    					bitmap = item;
    					break;
    				}
    			}else{
    				bitmapIterator.remove();
    			}
    		}
    	}
    	return bitmap;
    }
    
    @TargetApi(19)
	public static boolean canUseForInBitmap(Bitmap candidate, BitmapFactory.Options targetOptions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // From Android 4.4 (KitKat) onward we can re-use if the byte size of
            // the new bitmap is smaller than the reusable bitmap candidate
            // allocation byte count.
            int width = targetOptions.outWidth / targetOptions.inSampleSize;
            int height = targetOptions.outHeight / targetOptions.inSampleSize;
            int byteCount = width * height * getBytesPerPixel(candidate.getConfig());
            return byteCount <= candidate.getAllocationByteCount();
        }else{
        	// On earlier versions, the dimensions must match exactly and the inSampleSize must be 1
        	return candidate.getWidth() == targetOptions.outWidth && candidate.getHeight() == targetOptions.outHeight && targetOptions.inSampleSize == 1;
        }
    }

	/**
     * Get the size in bytes of a bitmap in a BitmapDrawable.
     * @param value
     * @return size in bytes
     */
    @TargetApi(12)
    public static int getBitmapSize(BitmapDrawable value) {
        Bitmap bitmap = value.getBitmap();
        if (Utils.hasHoneycombMR1()) {
            return bitmap.getByteCount();
        }else{
        	return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }
    
    /**
     * A helper function to return the byte usage per pixel of a bitmap based on its configuration.
     */
    public static int getBytesPerPixel(Config config) {
        if (config == Config.ARGB_8888) {
            return 4;
        } else if (config == Config.RGB_565) {
            return 2;
        } else if (config == Config.ARGB_4444) {
            return 2;
        } else if (config == Config.ALPHA_8) {
            return 1;
        }
        return 1;
    }
}
