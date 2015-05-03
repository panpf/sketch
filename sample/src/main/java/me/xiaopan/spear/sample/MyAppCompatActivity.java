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

package me.xiaopan.spear.sample;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import me.xiaopan.android.inject.InjectView;
import me.xiaopan.android.inject.app.InjectActionBarActivity;

public abstract class MyAppCompatActivity extends InjectActionBarActivity {
    @InjectView(R.id.toolbar) protected Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        if(toolbar != null){
            onPreSetSupportActionBar();
            setSupportActionBar(toolbar);
            onPostSetSupportActionBar();
        }
	}

    protected void onPreSetSupportActionBar(){

    }

    protected void onPostSetSupportActionBar(){

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }
}
