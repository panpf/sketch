/*
 * Copyright 2013 Peng fei Pan
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

package me.xiaoapn.easy.imageloader;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Rect;
import android.util.TypedValue;

/**
 * 位图解码器
 */
class BitmapDecoder {
	/**
	 * 单张图片最大像素数
	 */
	private int maxNumOfPixels;
	
	/**
	 * 最小边长，默认为-1
	 */
	private int minSlideLength;
	
	/**
	 * 图片加载器，用来输出LOG
	 */
	private ImageLoader imageLoader;
	
	/**
	 * 创建一个位图解码器，此解码器将根据最大像素数来缩小位图值合适的尺寸
	 * @param maxNumOfPixels
	 */
	public BitmapDecoder(int maxNumOfPixels, ImageLoader imageLoader){
		this.maxNumOfPixels = maxNumOfPixels;
		this.minSlideLength = -1;
		this.imageLoader = imageLoader;
	}
	
	/**
	 * 创建一个位图解码器，此解码器将根据最大像素数来缩小位图值合适的尺寸
	 * @param maxNumOfPixels
	 */
	public BitmapDecoder(int maxNumOfPixels){
		this.maxNumOfPixels = maxNumOfPixels;
		this.minSlideLength = -1;
	}
	
	/**
	 * 创建一个位图解码器，最大像素数默认为虚拟机可用最大内存的八分之一再除以4，这样可以保证图片不会太大导致内存溢出
	 */
	public BitmapDecoder(){
		this((int) (Runtime.getRuntime().maxMemory()/8/4));
	}
	
	/**
	 * 从字节数组中解码位图
	 * @param data
	 * @param offset
	 * @param length
	 * @param options
	 * @return
	 */
	public Bitmap decodeByteArray(byte[] data, int offset, int length, Options options){
		if(options == null){
			options = new Options();
		}
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, offset, length, options);
		String log = null;
		if(imageLoader != null){
			log = "原图尺寸："+options.outWidth+"x"+options.outHeight;
		}
		
		options.inSampleSize = computeSampleSize(options, minSlideLength, maxNumOfPixels);
		if(imageLoader != null){
			log += "；inSampleSize："+options.inSampleSize;
		}
		
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, offset, length, options);

		if(imageLoader != null){
			log += "；最终尺寸："+bitmap.getWidth()+"x"+bitmap.getHeight();
			imageLoader.getConfiguration().log(log);
		}
		
		return bitmap;
	}
	
	/**
	 * 从字节数组中解码位图
	 * @param data
	 * @param offset
	 * @param length
	 * @return
	 */
	public Bitmap decodeByteArray(byte[] data, int offset, int length){
		return decodeByteArray(data, offset, length, null);
	}
	
	/**
	 * 从字节数组中解码位图
	 * @param data
	 * @param options
	 * @return
	 */
	public Bitmap decodeByteArray(byte[] data, Options options){
		return decodeByteArray(data, 0, data.length, options);
	}
	
	/**
	 * 从字节数组中解码位图
	 * @param data
	 * @return
	 */
	public Bitmap decodeByteArray(byte[] data){
		return decodeByteArray(data, 0, data.length);
	}
	
	/**
	 * 从文件中解码位图
	 * @param filePath
	 * @param options
	 * @return
	 */
	public Bitmap decodeFile(String filePath, Options options){
		if(options == null){
			options = new Options();
		}
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		String log = null;
		if(imageLoader != null){
			log = "原图尺寸："+options.outWidth+"x"+options.outHeight;
		}
		
		options.inSampleSize = computeSampleSize(options, minSlideLength, maxNumOfPixels);
		if(imageLoader != null){
			log += "；inSampleSize："+options.inSampleSize;
		}
		
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

		if(imageLoader != null){
			log += "；最终尺寸："+bitmap.getWidth()+"x"+bitmap.getHeight();
			imageLoader.getConfiguration().log(log);
		}
		
		return bitmap;
	}
	
	/**
	 * 从文件中解码位图
	 * @param filePath
	 * @return
	 */
	public Bitmap decodeFile(String filePath){
		return decodeFile(filePath, null);
	}
	
	/**
	 * 从文件描述符中解码位图
	 * @param fd
	 * @param outPadding
	 * @param options
	 * @return
	 */
	public Bitmap decodeFileDescriptor(FileDescriptor fd, Rect outPadding, Options options){
		if(options == null){
			options = new Options();
		}
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFileDescriptor(fd, outPadding, options);

		String log = null;
		if(imageLoader != null){
			log = "原图尺寸："+options.outWidth+"x"+options.outHeight;
		}
		options.inSampleSize = computeSampleSize(options, minSlideLength, maxNumOfPixels);
		if(imageLoader != null){
			log += "；inSampleSize："+options.inSampleSize;
		}
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fd, outPadding, options);

		if(imageLoader != null){
			log += "；最终尺寸："+bitmap.getWidth()+"x"+bitmap.getHeight();
			imageLoader.getConfiguration().log(log);
		}
		
		return bitmap;
	}
	
	/**
	 * 从文件描述符中解码位图
	 * @param fd
	 * @return
	 */
	public Bitmap decodeFileDescriptor(FileDescriptor fd){
		return decodeFileDescriptor(fd, null, null);
	}
	
	/**
	 * 从资源文件中解码位图
	 * @param resource
	 * @param id
	 * @param options
	 * @return
	 */
	public Bitmap decodeResource(Resources resource, int id, Options options){
		if(options == null){
			options = new Options();
		}
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(resource, id, options);

		String log = null;
		if(imageLoader != null){
			log = "原图尺寸："+options.outWidth+"x"+options.outHeight;
		}
		options.inSampleSize = computeSampleSize(options, minSlideLength, maxNumOfPixels);
		if(imageLoader != null){
			log += "；inSampleSize："+options.inSampleSize;
		}
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeResource(resource, id, options);

		if(imageLoader != null){
			log += "；最终尺寸："+bitmap.getWidth()+"x"+bitmap.getHeight();
			imageLoader.getConfiguration().log(log);
		}
		
		return bitmap;
	}
	
	/**
	 * 从资源文件中解码位图
	 * @param resource
	 * @param id
	 * @return
	 */
	public Bitmap decodeResource(Resources resource, int id){
		return decodeResource(resource, id, null);
	}
	
	/**
	 * 从资源文件流中解码位图
	 * @param resource
	 * @param value
	 * @param is
	 * @param pad
	 * @param options
	 * @return
	 */
	public Bitmap decodeResourceStream(Resources resource, TypedValue value, InputStream is, Rect pad, Options options){
		if(options == null){
			options = new Options();
		}
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResourceStream(resource, value, is, pad, options);

		String log = null;
		if(imageLoader != null){
			log = "原图尺寸："+options.outWidth+"x"+options.outHeight;
		}
		options.inSampleSize = computeSampleSize(options, minSlideLength, maxNumOfPixels);
		if(imageLoader != null){
			log += "；inSampleSize："+options.inSampleSize;
		}
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeResourceStream(resource, value, is, pad, options);

		if(imageLoader != null){
			log += "；最终尺寸："+bitmap.getWidth()+"x"+bitmap.getHeight();
			imageLoader.getConfiguration().log(log);
		}
		
		return bitmap;
	}
	
	/**
	 * 从流中解码位图
	 * @param inputStream
	 * @param outPadding
	 * @param options
	 * @return
	 */
	public Bitmap decodeStream(InputStream inputStream, Rect outPadding, Options options){
		if(options == null){
			options = new Options();
		}
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(inputStream, outPadding, options);

		String log = null;
		if(imageLoader != null){
			log = "原图尺寸："+options.outWidth+"x"+options.outHeight;
		}
		options.inSampleSize = computeSampleSize(options, minSlideLength, maxNumOfPixels);
		if(imageLoader != null){
			log += "；inSampleSize："+options.inSampleSize;
		}
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeStream(inputStream, outPadding, options);

		if(imageLoader != null){
			log += "；最终尺寸："+bitmap.getWidth()+"x"+bitmap.getHeight();
			imageLoader.getConfiguration().log(log);
		}
		
		return bitmap;
	}
	
	/**
	 * 从流中解码位图
	 * @param inputStream
	 * @return
	 */
	public Bitmap decodeStream(InputStream inputStream){
		return decodeStream(inputStream, null, null);
	}
	
	/**
	 * 从Assets中解码位图
	 * @param context
	 * @param fileName
	 * @param outPadding
	 * @param options
	 * @return
	 */
	public Bitmap decodeFromAssets(Context context, String fileName, Rect outPadding, Options options){
		Bitmap bitmap = null;
		InputStream inputStream = null;
		try {
			inputStream = context.getAssets().open(fileName);
			bitmap = decodeStream(inputStream, outPadding, options);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(inputStream != null){
				try {
					inputStream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return bitmap;
	}
	
	/**
	 * 从Assets中解码位图
	 * @param context
	 * @param fileName
	 * @return
	 */
	public Bitmap decodeFromAssets(Context context, String fileName){
		return decodeFromAssets(context, fileName, null, null);
	}
	
	/**
	 * 获取最大像素数，一般由图片宽乘以高得出
	 * @return
	 */
	public int getMaxNumOfPixels() {
		return maxNumOfPixels;
	}

	/**
	 * 设置最大像素数，将根据此像素数来缩小图片至合适的大小
	 * @param maxNumOfPixels 最大像素数，由图片宽乘以高得出
	 */
	public void setMaxNumOfPixels(int maxNumOfPixels) {
		this.maxNumOfPixels = maxNumOfPixels;
	}

	/**
	 * 获取图片最小边长
	 * @return
	 */
	public int getMinSlideLength() {
		return minSlideLength;
	}

	/**
	 * 设置图片最小边长，默认为-1
	 * @param minSlideLength
	 */
	public void setMinSlideLength(int minSlideLength) {
		this.minSlideLength = minSlideLength;
	}

	/**
	 * 从字节数组中解码位图的尺寸
	 * @param data
	 * @param offset
	 * @param length
	 * @param options
	 * @return
	 */
	public static Options decodeSizeFromByteArray(byte[] data, int offset, int length, Options options){
		if(options == null){
			options = new Options();
		}
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, offset, length, options);
		options.inJustDecodeBounds = false;
		return options;
	}
	
	/**
	 * 从字节数组中解码位图的尺寸
	 * @param data
	 * @param offset
	 * @param length
	 * @return
	 */
	public static Options decodeSizeFromByteArray(byte[] data, int offset, int length){
		return decodeSizeFromByteArray(data, offset, length, null);
	}
	
	/**
	 * 从文件中解码位图的尺寸
	 * @param filePath
	 * @param options
	 * @return
	 */
	public static Options decodeSizeFromFile(String filePath, Options options){
		if(options == null){
			options = new Options();
		}
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		options.inJustDecodeBounds = false;
		return options;
	}
	
	/**
	 * 从文件中解码位图的尺寸
	 * @param filePath
	 * @return
	 */
	public static Options decodeSizeFromFile(String filePath){
		return decodeSizeFromFile(filePath, null);
	}
	
	/**
	 * 从文件描述符中解码位图的尺寸
	 * @param fd
	 * @param outPadding
	 * @param options
	 * @return
	 */
	public static Options decodeSizeFromFileDescriptor(FileDescriptor fd, Rect outPadding, Options options){
		if(options == null){
			options = new Options();
		}
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFileDescriptor(fd, outPadding, options);
		options.inJustDecodeBounds = false;
		return options;
	}
	
	/**
	 * 从文件描述符中解码位图的尺寸
	 * @param fd
	 * @return
	 */
	public static Options decodeSizeFromFileDescriptor(FileDescriptor fd){
		return decodeSizeFromFileDescriptor(fd, null, null);
	}
	
	/**
	 * 从资源文件中解码位图的尺寸
	 * @param resource
	 * @param id
	 * @param options
	 * @return
	 */
	public static Options decodeSizeFromResource(Resources resource, int id, Options options){
		if(options == null){
			options = new Options();
		}
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(resource, id, options);
		options.inJustDecodeBounds = false;
		return options;
	}
	
	/**
	 * 从资源文件中解码位图的尺寸
	 * @param resource
	 * @param id
	 * @return
	 */
	public static Options decodeSizeFromResource(Resources resource, int id){
		return decodeSizeFromResource(resource, id, null);
	}
	
	/**
	 * 从资源流中解码位图的尺寸
	 * @param resource
	 * @param value
	 * @param is
	 * @param pad
	 * @param options
	 * @return
	 */
	public static Options decodeSizeFromResourceStream(Resources resource, TypedValue value, InputStream is, Rect pad, Options options){
		if(options == null){
			options = new Options();
		}
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResourceStream(resource, value, is, pad, options);
		options.inJustDecodeBounds = false;
		return options;
	}
	
	/**
	 * 从流中解码位图的尺寸
	 * @param inputStream
	 * @param outPadding
	 * @param options
	 * @return
	 */
	public static Options decodeSizeFromStream(InputStream inputStream, Rect outPadding, Options options){
		if(options == null){
			options = new Options();
		}
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(inputStream, outPadding, options);
		options.inJustDecodeBounds = false;
		return options;
	}
	
	/**
	 * 从流中解码位图的尺寸
	 * @param inputStream
	 * @return
	 */
	public static Options decodeSizeFromStream(InputStream inputStream){
		return decodeSizeFromStream(inputStream, null, null);
	}
	
	/**
	 * 从Assets中解码位图的尺寸
	 * @param context
	 * @param fileName
	 * @param outPadding
	 * @param options
	 * @return
	 */
	public static Options decodeSizeFromAssets(Context context, String fileName, Rect outPadding, Options options){
		InputStream inputStream = null;
		try {
			if(options == null){
				options = new Options();
			}
			options.inJustDecodeBounds = true;
			inputStream = context.getAssets().open(fileName);
			BitmapFactory.decodeStream(inputStream, outPadding, options);
			options.inJustDecodeBounds = true;
		} catch (IOException e) {
			e.printStackTrace();
			options = null;
		}finally{
			if(inputStream != null){
				try {
					inputStream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return options;
	}
	
	/**
	 * 从Assets中解码位图的尺寸
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static Options decodeSizeFromAssets(Context context, String fileName){
		return decodeSizeFromAssets(context, fileName, null, null);
	}
	
	/**
	 * 计算合适的缩小倍数，注意在调用此方法之前一定要先通过Options.inJustDecodeBounds属性来获取图片的宽高
	 * @param options
	 * @param minSideLength 用于指定最小宽度或最小高度
	 * @param maxNumOfPixels 最大尺寸，由最大宽高相乘得出
	 * @return
	 */
	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
	    int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
	    int roundedSize;
	    if (initialSize <= 8) {
	        roundedSize = 1;
	        while (roundedSize < initialSize) {
	            roundedSize <<= 1;
	        }
	    } else {
	        roundedSize = (initialSize + 7) / 8 * 8;
	    }
	    return roundedSize;
	}
	
	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
	    double w = options.outWidth;
	    double h = options.outHeight;
	    int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
	    int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
	    if (upperBound < lowerBound) {
	        return lowerBound;
	    }

	    if ((maxNumOfPixels == -1) &&
	            (minSideLength == -1)) {
	        return 1;
	    } else if (minSideLength == -1) {
	        return lowerBound;
	    } else {
	        return upperBound;
	    }
	}
	
	/**
	 * 私有默认BitmapDecoder实例持有器
	 */
	private static class BitmapDecoderHolder{
		private static final BitmapDecoder INSTANCE = new BitmapDecoder();
	}
	
	/**
	 * 获取默认的实例，默认实例的最大像素数限制就是虚拟机最大内存的八分之一再除以4
	 * @return
	 */
	public static final BitmapDecoder getInstance(){
		return BitmapDecoderHolder.INSTANCE;
	}
}