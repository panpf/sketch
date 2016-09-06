/*
 * Copyright (C) 2016 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketchsample.fragment;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.TextView;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectExtra;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.sketch.display.TransitionImageDisplayer;
import me.xiaopan.sketch.feature.large.LargeImageViewer;
import me.xiaopan.sketch.feature.zoom.ImageZoomer;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.activity.WindowBackgroundManager;
import me.xiaopan.sketchsample.widget.MappingView;
import me.xiaopan.sketchsample.widget.MyImageView;

@InjectContentView(R.layout.fragment_large_image)
public class LargeImageFragment extends MyFragment {

    @InjectView(R.id.largeImage)
    private MyImageView imageView;

    @InjectView(R.id.mapping)
    private MappingView mappingView;

    @InjectView(R.id.text_largeImageFragment_scale)
    private TextView scaleTextView;

    @InjectExtra("imageUri")
    private String imageUri;

    private WindowBackgroundManager.WindowBackgroundLoader windowBackgroundLoader;

    private String scale;
    private String bytes = "0.0 B";

    public static LargeImageFragment build(String imageUri) {
        Bundle bundle = new Bundle();
        bundle.putString("imageUri", imageUri);
        LargeImageFragment fragment = new LargeImageFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity != null && activity instanceof WindowBackgroundManager.OnSetWindowBackgroundListener) {
            windowBackgroundLoader = new WindowBackgroundManager.WindowBackgroundLoader(activity.getBaseContext(), (WindowBackgroundManager.OnSetWindowBackgroundListener) activity);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView.setSupportZoom(true);
        imageView.setSupportLargeImage(true);

        imageView.getOptions().setImageDisplayer(new TransitionImageDisplayer());
        imageView.displayImage(imageUri);

        final ImageZoomer imageZoomer = imageView.getImageZoomFunction().getImageZoomer();
        imageZoomer.addOnMatrixChangeListener(new ImageZoomer.OnMatrixChangedListener() {
            @Override
            public void onMatrixChanged(ImageZoomer imageZoomer) {
                Rect visibleRect = new Rect();
                imageZoomer.getVisibleRect(visibleRect);
                mappingView.update(imageZoomer.getDrawableWidth(), visibleRect);
                scale = String.valueOf(SketchUtils.formatFloat(imageZoomer.getZoomScale(), 2));
                scaleTextView.setText(String.format("%s · %s", scale, bytes));
            }
        });
        mappingView.getOptions().setImageDisplayer(new TransitionImageDisplayer());
        mappingView.getOptions().setMaxSize(600, 600);
        mappingView.displayImage(imageUri);

        imageView.getLargeImageFunction().getLargeImageViewer().getTileManager().setOnTileChangedListener(new LargeImageViewer.OnTileChangedListener() {
            @Override
            public void onTileChanged(LargeImageViewer largeImageViewer) {
                mappingView.onTileChanged(largeImageViewer);
                bytes = Formatter.formatShortFileSize(getActivity(), largeImageViewer.getTileManager().getBytes());
                scaleTextView.setText(String.format("%s · %s", scale, bytes));
            }
        });
        imageView.getLargeImageFunction().getLargeImageViewer().setShowDrawRect(true);

        if (windowBackgroundLoader != null) {
            windowBackgroundLoader.load(imageUri);
        }
    }

    @Override
    public void onDetach() {
        if (windowBackgroundLoader != null) {
            windowBackgroundLoader.detach();
        }
        super.onDetach();
    }

    @Override
    protected void onUserVisibleChanged(boolean isVisibleToUser) {
        if (windowBackgroundLoader != null) {
            windowBackgroundLoader.setUserVisible(isVisibleToUser);
        }
    }
}
