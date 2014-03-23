package me.xiaopan.android.imageloader.sample.activity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import me.xiaoapn.android.imageloader.R;
import me.xiaopan.android.imageloader.sample.adapter.BlackStringAdapter;

public class MainActivity extends ListActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		
		getListView().setAdapter(new BlackStringAdapter(getBaseContext(), new String[]{"download", "load", "display", "多次加载同一张图片"}));

        getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Class<?> targetClass = null;
				switch(position - getListView().getHeaderViewsCount()){
					case 0 : 
						targetClass = DownloadActivity.class;
						break;
					case 1 : 
						targetClass = LoadActivity.class;
						break;
					case 2 : 
						targetClass = DisplayActivity.class;
						break;
				}
				if(targetClass != null){
					startActivity(new Intent(getBaseContext(), targetClass));
				}
			}
		});
	}
}
