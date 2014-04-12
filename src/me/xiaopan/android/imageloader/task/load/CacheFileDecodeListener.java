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

package me.xiaopan.android.imageloader.task.load;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import me.xiaopan.android.imageloader.decode.BitmapDecoder;
import me.xiaopan.android.imageloader.task.TaskRequest;

import java.io.File;

public class CacheFileDecodeListener implements BitmapDecoder.DecodeListener {
	private File file;

	public CacheFileDecodeListener(File file, TaskRequest taskRequest) {
		this.file = file;
	}

    @Override
    public Bitmap onDecode(BitmapFactory.Options options) {
        return BitmapFactory.decodeFile(file.getPath(), options);
    }

    @Override
    public void onDecodeSuccess() {
        file.setLastModified(System.currentTimeMillis());
    }

    @Override
    public void onDecodeFailure() {
        file.delete();
    }
}
