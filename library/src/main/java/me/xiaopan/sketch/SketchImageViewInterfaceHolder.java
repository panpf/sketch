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

package me.xiaopan.sketch;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * ImageView持有器，以弱引用的方式持有关联的ImageView
 */
public class SketchImageViewInterfaceHolder {
	private DisplayRequest displayRequest;
	private Reference<SketchImageViewInterface> sketchImageViewInterfaceReference;

	public SketchImageViewInterfaceHolder(SketchImageViewInterface imageView, DisplayRequest displayRequest) {
		this.sketchImageViewInterfaceReference = new WeakReference<SketchImageViewInterface>(imageView);
        this.displayRequest = displayRequest;
	}

	public SketchImageViewInterface getSketchImageViewInterface() {
		final SketchImageViewInterface sketchImageViewInterface = sketchImageViewInterfaceReference.get();
		if (displayRequest != null) {
			DisplayRequest holderDisplayRequest = BindFixedRecycleBitmapDrawable.getDisplayRequestBySketchImageInterface(sketchImageViewInterface);
            if(holderDisplayRequest != null && holderDisplayRequest == displayRequest){
            	return sketchImageViewInterface;
            }else{
            	return null;
            }
        }else{
        	return sketchImageViewInterface;
        }
	}

	public boolean isCollected() {
		return getSketchImageViewInterface() == null;
	}
}
