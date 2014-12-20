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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import me.xiaopan.android.spear.sample.DisplayOptionsType;
import me.xiaopan.android.spear.sample.activity.ImageDetailActivity;
import me.xiaopan.android.spear.widget.SpearImageView;

/**
 * 本地相册页面
 */
public class PhotoAlbumFragment extends Fragment {
    public static final String PARAM_REQUIRED_STRING_ARRAY_URLS = "PARAM_REQUIRED_STRING_ARRAY_URLS";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        GridView gridView = new GridView(getActivity());
        gridView.setBackgroundColor(Color.BLACK);
        gridView.setPadding(0, 0, 0, 0);
        gridView.setNumColumns(2);
        gridView.setVerticalSpacing(2);
        gridView.setHorizontalSpacing(2);
        gridView.setAdapter(new ImageGridAdapter(getActivity(), getArguments().getStringArray(PARAM_REQUIRED_STRING_ARRAY_URLS), 2, 2));
        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putStringArray(PhotoAlbumFragment.PARAM_REQUIRED_STRING_ARRAY_URLS, getArguments().getStringArray(PhotoAlbumFragment.PARAM_REQUIRED_STRING_ARRAY_URLS));
                bundle.putInt(ImageDetailFragment.PARAM_OPTIONAL_INT_CURRENT_POSITION, position);
                Intent intent = new Intent(getActivity(), ImageDetailActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        return gridView;
    }

    private static class ImageGridAdapter extends BaseAdapter {
        private Context context;
        private String[] imageUris;
        private int imageWidth = -1;

        @SuppressWarnings("deprecation")
        @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
        public ImageGridAdapter(Context context, String[] imageUris, int column, int horizontalSpacing){
            this.context = context;
            this.imageUris = imageUris;
            if(column > 1){
                WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                Display display = windowManager.getDefaultDisplay();
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2){
                    imageWidth = (display.getWidth()-((column-1)*horizontalSpacing))/column;
                }else{
                    Point point = new Point();
                    display.getSize(point);
                    imageWidth = (point.x-((column-1)*horizontalSpacing))/column;
                }
            }
        }

        @Override
        public Object getItem(int position) {
            return imageUris[position];
        }

        @Override
        public int getCount() {
            return imageUris!=null?imageUris.length:0;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SpearImageView spearImageView;
            if(convertView == null){
                spearImageView = new SpearImageView(context);
                spearImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                if(imageWidth != -1){
                    spearImageView.setLayoutParams(new AbsListView.LayoutParams(imageWidth, imageWidth));
                }
                spearImageView.setClickable(true);
                spearImageView.setDisplayOptions(DisplayOptionsType.LOCAL_PHOTO_ALBUM_ITEM);
                convertView = spearImageView;
            }else{
                spearImageView = (SpearImageView) convertView;
            }
            spearImageView.setImageByUri(imageUris[position]);
            return convertView;
        }
    }
}
