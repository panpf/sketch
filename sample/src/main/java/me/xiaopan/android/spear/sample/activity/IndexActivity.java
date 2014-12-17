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

package me.xiaopan.android.spear.sample.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ListView;

import me.xiaoapn.android.spear.sample.R;
import me.xiaopan.android.inject.InjectContentView;
import me.xiaopan.android.inject.InjectView;
import me.xiaopan.android.spear.sample.MyActionBarActivity;
import me.xiaopan.android.spear.sample.fragment.IndexFragment;

/**
 * 百度图片
 */
@InjectContentView(R.layout.activity_index)
public class IndexActivity extends MyActionBarActivity{
    @InjectView(R.id.drawer_index) private DrawerLayout drawerLayout;
    @InjectView(R.id.list_index_category) private ListView categoryListView;

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        drawerLayout.setDrawerShadow(R.drawable.shape_drawer_shaow_down_left, GravityCompat.START);

        ViewGroup.LayoutParams params = categoryListView.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels/2;
        categoryListView.setLayoutParams(params);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_star_content, new IndexFragment())
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        getMenuInflater().inflate(R.menu.menu_photo_album, menu);
        getMenuInflater().inflate(R.menu.menu_github, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_search :
                startActivity(new Intent(getBaseContext(), SearchActivity.class));
                break;
            case R.id.menu_github :
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_github))));
                break;
            case R.id.menu_photoAlbum :
                startActivity(new Intent(getBaseContext(), PhotoAlbumActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
