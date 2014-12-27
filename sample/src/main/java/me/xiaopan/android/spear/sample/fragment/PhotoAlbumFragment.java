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

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import me.xiaoapn.android.spear.sample.R;
import me.xiaopan.android.spear.sample.activity.ImageDetailActivity;
import me.xiaopan.android.spear.sample.adapter.ImageGridAdapter;

/**
 * 本地相册页面
 */
public class PhotoAlbumFragment extends Fragment implements ImageGridAdapter.OnImageClickListener{
    private GridView gridView;
    private ImageGridAdapter imageGridAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        gridView = new GridView(getActivity());
        gridView.setBackgroundColor(Color.WHITE);
        gridView.setPadding(0, 0, 0, 0);
        gridView.setNumColumns(2);
        gridView.setVerticalSpacing(2);
        gridView.setHorizontalSpacing(2);

        if(imageGridAdapter != null){
            gridView.setAdapter(imageGridAdapter);
        }else{
            new ReadImagesTask(getActivity().getBaseContext()).execute();
        }

        return gridView;
    }

    @Override
    public void onImageClick(int position) {
        ImageDetailActivity.launch(getActivity(), (ArrayList<String>) imageGridAdapter.getImageUrlList(), position);
    }

    private class ReadImagesTask extends AsyncTask<Void, Integer, List<String>> {
        private Context context;

        private ReadImagesTask(Context context) {
            this.context = context;
        }

        @Override
        protected List<String> doInBackground(Void[] params) {
            Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String where = MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?";
            String[] whereParams = new String[] { "image/jpeg", "image/png"};
            String sortOrder = MediaStore.Images.Media.DATE_MODIFIED;
            ContentResolver mContentResolver = context.getContentResolver();

            //只查询jpeg和png的图片
            Cursor mCursor = mContentResolver.query(mImageUri, null, where, whereParams, sortOrder);
            if(mCursor == null){
                return null;
            }

            List<String> imagePathList = new ArrayList<>(mCursor.getCount());
            while (mCursor.moveToNext()) {
                //获取图片的路径
                imagePathList.add(mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA)));
            }
            mCursor.close();
            return imagePathList;
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            gridView.setAdapter(imageGridAdapter = new ImageGridAdapter(getActivity(), strings, 2, 2, PhotoAlbumFragment.this));
        }
    }
}
