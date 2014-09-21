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

package me.xiaopan.android.spear.sample.fragment;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import me.xiaoapn.android.spear.sample.R;
import me.xiaopan.android.spear.request.DisplayListener;
import me.xiaopan.android.spear.request.ProgressCallback;
import me.xiaopan.android.spear.sample.DisplayOptionsType;
import me.xiaopan.android.spear.sample.widget.ProgressPieView;
import me.xiaopan.android.spear.util.FailureCause;
import me.xiaopan.android.spear.widget.SpearImageView;

public class ImageFragment extends Fragment {
	public static final String PARAM_REQUIRED_IMAGE_URI = "PARAM_REQUIRED_IMAGE_URI";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		String uri = null;
		Bundle bundle = getArguments();
		if(bundle != null){
			uri = bundle.getString(PARAM_REQUIRED_IMAGE_URI);
		}
		if(uri != null){
			View rootView = inflater.inflate(R.layout.fragment_image, container, false);
			final ProgressPieView progressBar = (ProgressPieView) rootView.findViewById(R.id.progress_imageFragment_progress);
            progressBar.setMax(100);

            SpearImageView imageView = (SpearImageView) rootView.findViewById(R.id.image_imageFragment_image);
			imageView.setDisplayOptions(DisplayOptionsType.VIEW_PAGER);
            imageView.setDisplayListener(new DisplayListener() {
                @Override
                public void onStarted() {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(0);
                }

                @Override
                public void onFailed(FailureCause failureCause) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCompleted(String imageUri, ImageView imageView, BitmapDrawable drawable, From from) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCanceled() {
                }
            });
            imageView.setProgressCallback(new ProgressCallback() {
                @Override
                public void onUpdateProgress(long totalLength, long completedLength) {
                    progressBar.setProgress((int) (((float) completedLength / totalLength) * 100));
                }
            });
            imageView.setImageByUri(uri);
			return rootView;
		}else{
			return null;
		}
	}
}
