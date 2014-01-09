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

package me.xiaoapn.easy.imageloader.task;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;

import me.xiaoapn.easy.imageloader.Configuration;
import me.xiaoapn.easy.imageloader.decode.FileNewBitmapInputStreamListener;
import me.xiaoapn.easy.imageloader.decode.OnNewBitmapInputStreamListener;
import me.xiaoapn.easy.imageloader.download.ImageDownloader;
import me.xiaoapn.easy.imageloader.download.ImageDownloader.OnCompleteListener;
import me.xiaoapn.easy.imageloader.util.IoUtils;
import me.xiaoapn.easy.imageloader.util.RecyclingBitmapDrawable;
import me.xiaoapn.easy.imageloader.util.Scheme;
import me.xiaoapn.easy.imageloader.util.Utils;

import org.apache.http.client.HttpClient;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;

public class BitmapLoadCallable implements Callable<BitmapDrawable> {
	private static final String ERROR_UNSUPPORTED_SCHEME = "UIL doesn't support scheme(protocol) by default [%s]. You should implement this support yourself (BaseImageDownloader.getStreamFromOtherSource(...))";
	private String logName;
	private Request request;
	private Configuration configuration;
	private ReentrantLock reentrantLock;
	private ImageViewAware imageViewAware;
	
	public BitmapLoadCallable(Request request, ImageViewAware imageViewAware, ReentrantLock reentrantLock, Configuration configuration) {
		this.logName = getClass().getSimpleName();
		this.request = request;
		this.reentrantLock = reentrantLock;
		this.configuration = configuration;
		this.imageViewAware = imageViewAware;
	}

	@Override
	public BitmapDrawable call() throws Exception {
		BitmapDrawable bitmapDrawable = null;
		reentrantLock.lock();
		try{
			bitmapDrawable = configuration.getBitmapCacher().get(request.getId());
			if(bitmapDrawable == null){
				final Scheme scheme = Scheme.ofUri(request.getImageUri());
				if(scheme != Scheme.UNKNOWN){
					/* 初始化输入流获取监听器并解码图片 */
					OnNewBitmapInputStreamListener newBitmapInputStreamListener = null;
					if(scheme == Scheme.HTTP || scheme == Scheme.HTTPS){
						if(request.getOptions().getCacheConfig().isCacheInDisk()){
							final File cacheFile = Utils.getCacheFile(configuration, request.getOptions(), Utils.encodeUrl(request.getImageUri()));
							if(Utils.isAvailableOfFile(cacheFile, request.getOptions().getCacheConfig().getDiskCachePeriodOfValidity(), configuration, request.getName())){
								newBitmapInputStreamListener = new FileNewBitmapInputStreamListener(cacheFile);
							}else{
								newBitmapInputStreamListener = getNetNewBitmapInputStreamListener(request.getName(), request.getImageUri(), cacheFile, request.getOptions().getMaxRetryCount(), configuration.getHttpClient());
							}
						}else{
							newBitmapInputStreamListener = getNetNewBitmapInputStreamListener(request.getName(), request.getImageUri(), null, request.getOptions().getMaxRetryCount(), configuration.getHttpClient());
						}
					}else{
						newBitmapInputStreamListener = new OnNewBitmapInputStreamListener() {
							@Override
							public InputStream onNewBitmapInputStream() {
								return getBitmapInputStream(configuration.getContext(), scheme, request.getImageUri());
							}
						};
					}
					
					Bitmap bitmap = configuration.getBitmapDecoder().decode(newBitmapInputStreamListener, request.getTargetSize(), configuration, request.getName());
					if(bitmap != null && !bitmap.isRecycled()){
						if(request.getOptions().getBitmapProcessor() != null){
							Bitmap newBitmap = request.getOptions().getBitmapProcessor().process(bitmap, imageViewAware);
							if(newBitmap != bitmap){
								bitmap.recycle();
								bitmap = newBitmap;
							}
						}
						if (Utils.hasHoneycomb()) {
		                    // Running on Honeycomb or newer, so wrap in a standard BitmapDrawable
		                    bitmapDrawable = new BitmapDrawable(configuration.getResources(), bitmap);
		                } else {
		                    // Running on Gingerbread or older, so wrap in a RecyclingBitmapDrawable
		                    // which will recycle automagically
		                	bitmapDrawable = new RecyclingBitmapDrawable(configuration.getResources(), bitmap);
		                }
						if(request.getOptions().getCacheConfig().isCacheInMemory()){
							configuration.getBitmapCacher().put(request.getId(), bitmapDrawable);
						}
					}
				}
			}
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}finally{
			reentrantLock.unlock();
		}
		
		return bitmapDrawable;
	}
	
	/**
     * 获取网络输入流监听器
     * @param requestName
     * @param imageUrl
     * @param cacheFile
     * @param maxRetryCount
     * @param httpClient
     * @return
     */
    private OnNewBitmapInputStreamListener getNetNewBitmapInputStreamListener(String requestName, String imageUrl, File cacheFile, int maxRetryCount, HttpClient httpClient){
    	final NewBitmapInputStreamListenerHolder holder = new NewBitmapInputStreamListenerHolder();
    	new ImageDownloader(requestName, imageUrl, cacheFile, maxRetryCount, httpClient, configuration, new OnCompleteListener() {
			@Override
			public void onFailed() {}
			
			@Override
			public void onComplete(final byte[] data) {
				holder.newBitmapInputStreamListener = new OnNewBitmapInputStreamListener() {
					@Override
					public InputStream onNewBitmapInputStream() {
						return new BufferedInputStream(new ByteArrayInputStream(data), IoUtils.BUFFER_SIZE);
					}
				};
			}
			
			@Override
			public void onComplete(final File cacheFile) {
				holder.newBitmapInputStreamListener = new OnNewBitmapInputStreamListener() {
					@Override
					public InputStream onNewBitmapInputStream() {
						try {
							return new BufferedInputStream(new FileInputStream(cacheFile), IoUtils.BUFFER_SIZE);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
							return null;
						}
					}
				};
			}
		}).execute();
    	return holder.newBitmapInputStreamListener;
    }
    
    /**
     * 获取位图输入流
     * @param context
     * @param scheme
     * @param imageUri
     * @return
     */
    private InputStream getBitmapInputStream(Context context, Scheme scheme, String imageUri){
    	switch (scheme) {
			case FILE:
				return getStreamFromFile(Scheme.FILE.crop(imageUri));
			case CONTENT:
				return getStreamFromContent(context, Scheme.CONTENT.crop(imageUri));
			case ASSETS:
				return getStreamFromAssets(context, Scheme.ASSETS.crop(imageUri));
			case DRAWABLE:
				return getStreamFromDrawable(context, Scheme.DRAWABLE.crop(imageUri));
			default:
				if(configuration.isDebugMode()){
					Log.d(configuration.getLogTag(), new StringBuffer(logName).append("：").append(String.format(ERROR_UNSUPPORTED_SCHEME, imageUri)).toString());
				}
				return null;
		}
    }
    
    /**
     * 从本地文件获取输入流
     * @param filePath
     * @return
     * @throws IOException
     */
    private InputStream getStreamFromFile(String filePath) {
		return new FileNewBitmapInputStreamListener(new File(filePath)).onNewBitmapInputStream();
	}
    
	/**
	 * 从Content获取输入流
	 * @param context
	 * @param contentUri
	 * @return
	 */
	private InputStream getStreamFromContent(Context context, String contentUri) {
		try{
			ContentResolver res = context.getContentResolver();
			Uri uri = Uri.parse(contentUri);
			return res.openInputStream(uri);
		}catch(FileNotFoundException e){
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 从Assets获取输入流
	 * @param context
	 * @param assetsFilePath
	 * @return
	 */
	private InputStream getStreamFromAssets(Context context, String assetsFilePath){
		try{
			return context.getAssets().open(assetsFilePath);
		}catch(IOException e){
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 从Drawable获取输入流
	 * @param context
	 * @param drawableId
	 * @return
	 */
	private InputStream getStreamFromDrawable(Context context, String drawableIdString) {
		BitmapDrawable drawable = (BitmapDrawable) context.getResources().getDrawable(Integer.parseInt(drawableIdString));
		Bitmap bitmap = drawable.getBitmap();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 0, os);
		return new ByteArrayInputStream(os.toByteArray());
	}
	
	private class NewBitmapInputStreamListenerHolder{
		OnNewBitmapInputStreamListener newBitmapInputStreamListener;
	}
}
