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
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;

import me.xiaoapn.android.spear.sample.R;
import me.xiaopan.android.inject.InjectContentView;
import me.xiaopan.android.inject.InjectExtra;
import me.xiaopan.android.inject.InjectView;
import me.xiaopan.android.spear.sample.MyActionBarActivity;
import me.xiaopan.android.spear.sample.fragment.StarHomeFragment;

/**
 * 明星个人主页
 */
@InjectContentView(R.layout.activity_star_home)
public class StarHomeActivity extends MyActionBarActivity {
    @InjectView(R.id.drawer_starHome) private DrawerLayout drawerLayout;
    @InjectView(R.id.list_starHom_category) private ListView categoryListView;

    @InjectExtra(StarHomeFragment.PARAM_REQUIRED_STRING_STAR_TITLE) private String starTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        drawerLayout.setDrawerShadow(R.drawable.shape_drawer_shaow_down_left, GravityCompat.START);

        getSupportActionBar().setTitle(starTitle);

        Point screenSize = getScreenSize(getBaseContext());
        ViewGroup.LayoutParams params = categoryListView.getLayoutParams();
        params.width = screenSize.x/2;
        categoryListView.setLayoutParams(params);

        StarHomeFragment starHomeFragment = new StarHomeFragment();
        starHomeFragment.setArguments(getIntent().getExtras());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_starHom_content, starHomeFragment)
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

    /**
     * 获取屏幕尺寸
     */
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static Point getScreenSize(Context context){
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2){
            return new Point(display.getWidth(), display.getHeight());
        }else{
            Point point = new Point();
            display.getSize(point);
            return point;
        }
    }
}
