/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.xiaopan.android.imageloader.cache;

import me.xiaopan.android.imageloader.util.ImageLoaderUtils;
import me.xiaopan.android.imageloader.util.RecyclingBitmapDrawable;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.util.LruCache;

public class BitmapLruCache extends LruCache<String, BitmapDrawable> {
//	private Set<SoftReference<Bitmap>> mReusableBitmaps;

	public BitmapLruCache(int maxSize) {
		super(maxSize);
//		if (ImageLoaderUtils.hasHoneycomb()) {
//		    mReusableBitmaps = Collections.synchronizedSet(new HashSet<SoftReference<Bitmap>>());
//		}
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
//		}else if(ImageLoaderUtils.hasHoneycomb()){
//			mReusableBitmaps.add(new SoftReference<Bitmap>(oldValue.getBitmap()));
		}
	}
    
    /**
     * 获取可再度使用的Bitmap
     * @param options 解码选项，用于判定Bitmap是否可用
     * @return 匹配的可用的Bitmap
     */
    public Bitmap getBitmapFromReusableSet(BitmapFactory.Options options){
    	Bitmap bitmap = null;
//    	if(mReusableBitmaps != null && !mReusableBitmaps.isEmpty()){
//    		Iterator<SoftReference<Bitmap>> bitmapIterator = mReusableBitmaps.iterator();
//    		Bitmap item;
//    		while(bitmapIterator.hasNext()){
//    			item = bitmapIterator.next().get();
//    			if(item != null && item.isMutable() && !item.isRecycled()){
//    				if(canUseForInBitmap(item, options)){
//    					bitmap = item;
//    					break;
//    				}
//    			}else{
//    				bitmapIterator.remove();
//    			}
//    		}
//    	}
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
     * @param bitmapDrawable 待计算大小的图片
     * @return size in bytes
     */
    @TargetApi(12)
    public static int getBitmapSize(BitmapDrawable bitmapDrawable) {
        Bitmap bitmap = bitmapDrawable.getBitmap();
        if (ImageLoaderUtils.hasHoneycombMR1()) {
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
