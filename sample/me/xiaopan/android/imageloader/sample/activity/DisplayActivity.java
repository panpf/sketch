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

package me.xiaopan.android.imageloader.sample.activity;

import me.xiaoapn.android.imageloader.R;
import me.xiaopan.android.imageloader.sample.adapter.StringAdapter;
import me.xiaopan.android.imageloader.sample.fragment.GalleryFragment;
import me.xiaopan.android.imageloader.sample.fragment.GridFragment;
import me.xiaopan.android.imageloader.sample.fragment.ListFragment;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class DisplayActivity extends FragmentActivity {
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
		setContentView(R.layout.activity_display);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_display);
		drawerLayout.setDrawerShadow(R.drawable.shape_drawer_shaow_down_left, GravityCompat.START);
		drawerLayout.setDrawerShadow(R.drawable.shape_drawer_shaow_down_right, GravityCompat.END);
		
		viewTypeListView = (ListView) findViewById(R.id.list_display_views);
		viewTypeListView.setAdapter(new StringAdapter(getBaseContext(), "GridView", "ListView", "Gallery"));
		viewTypeListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch(position - viewTypeListView.getHeaderViewsCount()){
					case 0 : viewType = ViewType.GRID_VIEW; update(); break;
					case 1 : viewType = ViewType.LIST_VIEW; update(); break;
					case 2 : viewType = ViewType.GALLERY; update(); break;
				}
				drawerLayout.closeDrawers();
			}
		});
		
		uriTypeListView = (ListView) findViewById(R.id.list_display_uri);
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
						update();
						break;
				}
				drawerLayout.closeDrawers();
			}
		});
		
		httpUris = getResources().getStringArray(R.array.urls);
		
		assetUris = new String[8];
		for(int w = 0; w < assetUris.length; w++){
			assetUris[w] = "assets://image_assets_test_"+(w+1)+".jpg";
		}
		
		drawableUris = new String[6];
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
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void update(){
		String[] uris = null;
		String uriName = null;
		switch(uriType){
			case HTTP : uris = httpUris; uriName = "Http"; break;
			case FILE : uris = fileUris; uriName = "File"; break;
			case CONTENT : uris = contentUris; uriName = "Content"; break;
			case ASSETS : uris = assetUris; uriName = "Assets"; break;
			case DRAWABLE : uris = drawableUris; uriName = "Drawable"; break;
			default : break;
		}
		
		if(uris != null && uris.length > 0){
			Fragment fragment = null;
			String viewName = null;
			switch(viewType){
				case GRID_VIEW : fragment = new GridFragment(); viewName = "GridView"; break;
				case LIST_VIEW : fragment = new ListFragment(); viewName = "ListView"; break;
				case GALLERY : fragment = new GalleryFragment(); viewName = "Gallery"; break;
			}
			if(fragment != null){
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
					getActionBar().setSubtitle(viewName + " " + uriName);
				}
				Bundle bundle = new Bundle();
				bundle.putStringArray(GridFragment.PARAM_REQUIRED_STRING_ARRAY_URLS, uris);
				fragment.setArguments(bundle);
				FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
				fragmentTransaction.setCustomAnimations(R.anim.base_slide_to_left_in, R.anim.base_slide_to_left_out, R.anim.base_slide_to_right_in, R.anim.base_slide_to_right_out);
				fragmentTransaction.replace(R.id.fragment_display, fragment);
				fragmentTransaction.commitAllowingStateLoss();
			}else{
				Toast.makeText(getBaseContext(), "还没有准备好此种模式，敬请期待！", Toast.LENGTH_SHORT).show();
			}
		}else{
			Toast.makeText(getBaseContext(), "还没有准备好此种模式，敬请期待！", Toast.LENGTH_SHORT).show();
		}
	}
	
	public enum ViewType{
		GRID_VIEW, LIST_VIEW, GALLERY,
	}
	
	public enum UriType{
		HTTP, FILE, CONTENT, ASSETS, DRAWABLE,
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
					Log.w(DisplayActivity.class.getSimpleName(), "空的");
				}
				break;
			case REQUEST_CODE_FILE : 
				if(arg2.getData() != null){
					String filePath = getPathByUri(getBaseContext(), arg2.getData());
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
                    }else{
                    	Toast.makeText(getBaseContext(), "没有取到文件地址，请使用系统自带的图库应用来选择文件", Toast.LENGTH_SHORT).show();
    					Log.w(DisplayActivity.class.getSimpleName(), "没有取到文件地址，请使用系统自带的图库应用来选择文件："+arg2.getData().toString());
                    }
				}else{
					Toast.makeText(getBaseContext(), "空的", Toast.LENGTH_SHORT).show();
					Log.w(DisplayActivity.class.getSimpleName(), "空的");
				}
				break;
			}
		}
	}
	
	/**
	 * 根据Uri获取路径
	 */
	public static String getPathByUri(Context context, Uri uri){
		String filePath = null;
		Cursor cursor = context.getContentResolver().query(uri, new String[]{ MediaStore.Images.Media.DATA }, null, null, null);
        if (cursor != null){
             cursor.moveToFirst();
             filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
             cursor.close();
        }
        return filePath;
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_github, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_github :
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_github))));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
