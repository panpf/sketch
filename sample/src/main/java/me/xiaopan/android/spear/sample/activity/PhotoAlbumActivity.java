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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import me.xiaoapn.android.spear.sample.R;
import me.xiaopan.android.spear.sample.fragment.PhotoAlbumFragment;

/**
 * 相册
 */
public class PhotoAlbumActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photoa_album);

        Fragment fragment = new PhotoAlbumFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArray(PhotoAlbumFragment.PARAM_REQUIRED_STRING_ARRAY_URLS, getResources().getStringArray(R.array.urls));
        fragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.base_slide_to_left_in, R.anim.base_slide_to_left_out, R.anim.base_slide_to_right_in, R.anim.base_slide_to_right_out);
        fragmentTransaction.replace(R.id.fragment_photoAlbum_content, fragment);
        fragmentTransaction.commitAllowingStateLoss();
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
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
        }
        return super.onOptionsItemSelected(item);
    }
}
