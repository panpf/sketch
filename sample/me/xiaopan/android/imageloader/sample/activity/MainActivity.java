package me.xiaopan.android.imageloader.sample.activity;

import me.xiaopan.android.imageloader.sample.adapter.BlackStringAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainActivity extends Activity {
	private ListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		listView = new ListView(getBaseContext());
		setContentView(listView);
		
		listView.setAdapter(new BlackStringAdapter(getBaseContext(), new String[]{"download", "load", "display"}));
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Class<?> targetClass = null;
				switch(position - listView.getHeaderViewsCount()){
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
