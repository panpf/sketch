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

package me.xiaopan.easy.imageloader.sample.activity;

import me.xiaoapn.easy.imageloader.R;
import me.xiaopan.easy.imageloader.sample.adapter.StringAdapter;
import me.xiaopan.easy.imageloader.sample.fragment.GalleryFragment;
import me.xiaopan.easy.imageloader.sample.fragment.GridFragment;
import me.xiaopan.easy.imageloader.sample.fragment.ListFragment;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {
	private static final int REQUEST_CODE_CONTENT = 11;
	private static final int REQUEST_CODE_FILE = 12;
	private DrawerLayout drawerLayout;
	private ListView viewTypeListView;
	private ListView uriTypeListView;
	private String[] httpUris;
	private String[] fileUris;
	private String[] contentUris;
	private String[] assetUris;
	private String[] drawableUris;
	private ViewType viewType;
	private UriType uriType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_main);
		drawerLayout.setDrawerShadow(R.drawable.shape_drawer_shaow_down_left, GravityCompat.START);
		drawerLayout.setDrawerShadow(R.drawable.shape_drawer_shaow_down_right, GravityCompat.END);
		
		viewTypeListView = (ListView) findViewById(R.id.list_main_views);
		viewTypeListView.setAdapter(new StringAdapter(getBaseContext(), "GridView", "ListView", "Gallery", "ViewPager"));
		viewTypeListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch(position - viewTypeListView.getHeaderViewsCount()){
					case 0 : viewType = ViewType.GRID_VIEW; break;
					case 1 : viewType = ViewType.LIST_VIEW; break;
					case 2 : viewType = ViewType.GALLERY; break;
					case 3 : viewType = ViewType.VIEW_PAGER; break;
				}
				drawerLayout.closeDrawers();
				update();
			}
		});
		
		uriTypeListView = (ListView) findViewById(R.id.list_main_uri);
		uriTypeListView.setAdapter(new StringAdapter(getBaseContext(), "http://", "file://", "content://", "assets://", "drawable://"));
		uriTypeListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch(position - uriTypeListView.getHeaderViewsCount()){
					case 0 : 
						uriType = UriType.HTTP; 
						update();
						break;
					case 1 : 
						Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
						intent.setType("image/*");
						startActivityForResult(intent, REQUEST_CODE_FILE); 
						break;
					case 2 : 
						Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT);
						intent2.setType("image/*");
						startActivityForResult(intent2, REQUEST_CODE_CONTENT); 
						break;
					case 3 : 
						uriType = UriType.ASSETS; 
						update();
						break;
					case 4 : 
						uriType = UriType.DRAWABLE; 
						break;
				}
				drawerLayout.closeDrawers();
			}
		});
		
		httpUris = getResources().getStringArray(R.array.urls);
		
		assetUris = new String[70];
		for(int w = 0; w < assetUris.length; w++){
			assetUris[w] = "assets://image_assets_test_"+(w+1)+".jpg";
		}
		
		drawableUris = new String[41];
		for(int w = 0; w < drawableUris.length; w++){
			try {
				drawableUris[w] = "drawable://"+R.drawable.class.getField("image_drawable_test_"+(w+1)).getInt(null);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
		
		viewType = ViewType.GRID_VIEW;
		uriType = UriType.HTTP;
		
		update();
	}
	
	private void update(){
		String[] uris = null;
		switch(uriType){
			case HTTP : uris = httpUris; break;
			case FILE : uris = fileUris; break;
			case CONTENT : uris = contentUris; break;
			case ASSETS : uris = assetUris; break;
//			case DRAWABLE : uris = drawableUris; break;
			default : break;
		}
		
		if(uris != null && uris.length > 0){
			Fragment fragment = null;
			switch(viewType){
				case GRID_VIEW : fragment = new GridFragment(); break;
				case LIST_VIEW : fragment = new ListFragment(); break;
				case GALLERY : fragment = new GalleryFragment(); break;
				case VIEW_PAGER :  
				default : break;
			}
			if(fragment != null){
				Bundle bundle = new Bundle();
				bundle.putStringArray(GridFragment.PARAM_REQUIRED_STRING_ARRAY_URLS, uris);
				fragment.setArguments(bundle);
				getSupportFragmentManager().beginTransaction().replace(R.id.fragment_main, fragment).commitAllowingStateLoss();
			}else{
				Toast.makeText(getBaseContext(), "还没有准备好此种模式，敬请期待！", Toast.LENGTH_SHORT).show();
			}
		}else{
			Toast.makeText(getBaseContext(), "还没有准备好此种模式，敬请期待！", Toast.LENGTH_SHORT).show();
		}
	}
	
	public enum ViewType{
		GRID_VIEW, LIST_VIEW, GALLERY, VIEW_PAGER;
	}
	
	public enum UriType{
		HTTP, FILE, CONTENT, ASSETS, DRAWABLE;
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if(arg1 == RESULT_OK){
			switch(arg0){
			case REQUEST_CODE_CONTENT : 
				if(arg2.getData() != null){
					String uri = arg2.getData().toString();
					if(contentUris == null){
						contentUris = new String[30];
					}
					for(int w = 0; w < contentUris.length; w++){
						contentUris[w] = uri;
					}
					uriType = UriType.CONTENT; 
					update();
				}else{
					Toast.makeText(getBaseContext(), "空的", Toast.LENGTH_SHORT).show();
					Log.w(MainActivity.class.getSimpleName(), "空的");
				}
				break;
			case REQUEST_CODE_FILE : 
				if(arg2.getData() != null){
					Uri uri = arg2.getData();
					String filePath;
					Cursor cursor = getContentResolver().query(uri, new String[]{ MediaStore.Images.Media.DATA }, null, null, null);
                    if (cursor != null){
                         cursor.moveToFirst();
                         filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    }else{
                    	filePath = null;
                    }
                    
                    if(filePath != null){
                    	String fileUri = "file://"+filePath;
                    	if(fileUris == null){
                    		fileUris = new String[30];
                    	}
                    	for(int w = 0; w < fileUris.length; w++){
                    		fileUris[w] = fileUri;
                    	}
                    	uriType = UriType.FILE; 
                    	update();
                    }
				}else{
					Toast.makeText(getBaseContext(), "空的", Toast.LENGTH_SHORT).show();
					Log.w(MainActivity.class.getSimpleName(), "空的");
				}
				break;
			}
		}
	}
}
