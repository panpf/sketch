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
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import me.xiaoapn.android.spear.sample.R;
import me.xiaopan.android.inject.InjectContentView;
import me.xiaopan.android.inject.InjectView;
import me.xiaopan.android.inject.app.InjectFragment;
import me.xiaopan.android.spear.sample.activity.DetailActivity;
import me.xiaopan.android.spear.sample.adapter.PhotoAlbumImageAdapter;
import me.xiaopan.android.widget.PullRefreshLayout;

/**
 * 本地相册页面
 */
@InjectContentView(R.layout.fragment_photo_album)
public class PhotoAlbumFragment extends InjectFragment implements PhotoAlbumImageAdapter.OnImageClickListener, PullRefreshLayout.OnRefreshListener{
    @InjectView(R.id.refreshLayout_photoAlbum) private PullRefreshLayout pullRefreshLayout;
    @InjectView(R.id.recyclerView_photoAlbum_content) private RecyclerView recyclerView;
    private PhotoAlbumImageAdapter imageAdapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pullRefreshLayout.setOnRefreshListener(this);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        if(imageAdapter != null){
            recyclerView.setAdapter(imageAdapter);
            recyclerView.scheduleLayoutAnimation();
        }else{
            pullRefreshLayout.startRefresh();
        }
    }

    @Override
    public void onImageClick(int position) {
        DetailActivity.launch(getActivity(), (ArrayList<String>) imageAdapter.getImageUrlList(), position);
    }

    @Override
    public void onRefresh() {
        if(getActivity() != null){
            new ReadImagesTask(getActivity().getBaseContext()).execute();
        }
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
            if(getActivity() == null){
                return;
            }

            recyclerView.setAdapter(imageAdapter = new PhotoAlbumImageAdapter(getActivity(), strings, PhotoAlbumFragment.this, recyclerView));
            recyclerView.scheduleLayoutAnimation();
            pullRefreshLayout.stopRefresh();
        }
    }
}
