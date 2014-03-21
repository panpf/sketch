package me.xiaopan.android.imageloader.sample.activity;

import java.io.File;

import me.xiaoapn.android.imageloader.R;
import me.xiaopan.android.imageloader.ImageLoader;
import me.xiaopan.android.imageloader.task.download.DownloadRequest.DownloadListener;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class DownloadActivity extends Activity {
	private ImageView imageView;
	private ProgressBar progressBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_download);
		
		imageView = (ImageView) findViewById(R.id.image_download);
		progressBar = (ProgressBar) findViewById(R.id.progressBar_download);
		
		ImageLoader.getInstance(getBaseContext()).download("http://tupian.enterdesk.com/2013/xll/0112/taiqiumeinv/taiqiumeinv%20(3).jpg.680.510.jpg", new DownloadListener() {
			@Override
			public void onStart() {
				progressBar.setVisibility(View.VISIBLE);
			}

			@Override
			public void onUpdateProgress(final long totalLength, final long completedLength) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						progressBar.setProgress((int) (((float)completedLength/totalLength) * 100));
						progressBar.setVisibility(View.VISIBLE);
					}
				});
			}
			
			@Override
			public void onComplete(final byte[] data) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						imageView.setImageBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));
						progressBar.setVisibility(View.GONE);
					}
				});
			}
			
			@Override
			public void onComplete(final File cacheFile) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						imageView.setImageURI(Uri.fromFile(cacheFile));
						progressBar.setVisibility(View.GONE);
					}
				});
			}
			
			@Override
			public void onFailure() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(getBaseContext(), "下载失败", Toast.LENGTH_SHORT).show();
						progressBar.setVisibility(View.GONE);
					}
				});
			}

            @Override
            public void onCancel() {

            }
        });
	}
}
