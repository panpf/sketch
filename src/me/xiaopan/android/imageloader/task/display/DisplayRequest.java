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

package me.xiaopan.android.imageloader.task.display;

import me.xiaopan.android.imageloader.process.BitmapProcessor;
import me.xiaopan.android.imageloader.task.load.LoadRequest;
import me.xiaopan.android.imageloader.util.ImageSize;
import android.widget.ImageView;

/**
 * 显示请求
 */
public class DisplayRequest extends LoadRequest{
	private String id;	//ID
    private ImageSize targetSize;	//目标尺寸
	private DisplayListener displayListener;	//监听器
	private DisplayOptions displayOptions;	//显示选项
	private ImageViewHolder imageViewHolder;	//ImageView持有器
    private ImageView.ScaleType scaleType;  //缩放方式

	public DisplayRequest(String id, String uri) {
		super(uri);
		setId(id);
	}

    /**
     * 获取ID，此ID用来在内存缓存Bitmap时作为其KEY
     * @return ID
     */
	public String getId() {
		return id;
	}

    /**
     * 设置ID
     * @param id 此ID用来在内存缓存Bitmap时作为其KEY
     */
	public void setId(String id) {
		this.id = id;
	}

    /**
     * 设置目标尺寸
     * @param targetSize 目标尺寸
     */
    public void setTargetSize(ImageSize targetSize) {
        this.targetSize = targetSize;
    }

    /**
     * 获取显示监听器
     * @return 显示监听器
     */
	public DisplayListener getDisplayListener() {
		return displayListener;
	}

    /**
     * 设置显示监听器
     * @param displayListener 显示监听器
     */
	public void setDisplayListener(DisplayListener displayListener) {
		this.displayListener = displayListener;
	}

    /**
     * 获取显示选项
     * @return 显示选项
     */
	public DisplayOptions getDisplayOptions() {
		return displayOptions;
	}

    /**
     * 设置显示选项
     * @param displayOptions 显示选项
     */
	public void setDisplayOptions(DisplayOptions displayOptions) {
		this.displayOptions = displayOptions;
		setLoadOptions(displayOptions);
	}

    /**
     * 获取ImageView持有器
     * @return ImageView持有器
     */
	public ImageViewHolder getImageViewHolder() {
		return imageViewHolder;
	}

    /**
     * 设置ImageView持有器
     * @param imageViewHolder ImageView持有器
     */
	public void setImageViewHolder(ImageViewHolder imageViewHolder) {
		this.imageViewHolder = imageViewHolder;
	}

    @Override
    public ImageView.ScaleType getScaleType() {
        return scaleType;
    }

    @Override
    public ImageSize getMaxSize() {
        return targetSize;
    }

    /**
     * 设置缩放方式
     * @param scaleType 缩放方式
     */
    public void setScaleType(ImageView.ScaleType scaleType) {
        this.scaleType = scaleType;
    }

    /**
     * 生成ID
     */
    public static String createId(String uri, ImageSize targetSize, BitmapProcessor bitmapProcessor){
        StringBuffer stringBuffer = new StringBuffer(uri);
        if(targetSize != null){
            stringBuffer.append("_");
            stringBuffer.append(targetSize.getWidth());
            stringBuffer.append("x");
            stringBuffer.append(targetSize.getHeight());
        }
        if(bitmapProcessor != null){
            String tag = bitmapProcessor.getTag();
            if(tag != null){
                stringBuffer.append("_");
                stringBuffer.append(tag);
            }
        }
        return stringBuffer.toString();
    }
}
