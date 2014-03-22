package me.xiaopan.android.imageloader.sample.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import me.xiaoapn.android.imageloader.R;
import me.xiaopan.android.imageloader.ImageLoader;
import me.xiaopan.android.imageloader.task.load.LoadRequest;

public class DownloadActivity extends Activity {
	private ImageView imageView;
	private ProgressBar progressBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_download);
		
		imageView = (ImageView) findViewById(R.id.image_download);
		progressBar = (ProgressBar) findViewById(R.id.progressBar_download);

//        String uri = "http://tupian.enterdesk.com/2013/xll/0112/taiqiumeinv/taiqiumeinv%20(3).jpg.680.510.jpg";
        String uri = "http://a.hiphotos.baidu.com/image/w%3D1366%3Bcrop%3D0%2C0%2C1366%2C768/sign=3bb4f63f58afa40f3cc6cade9d52382c/c8177f3e6709c93d388b4ffa9d3df8dcd1005445.jpg";
//        ImageLoader.getInstance(getBaseContext()).download(uri, new DownloadListener() {
//				@Override
//			public void onStart() {
//				progressBar.setVisibility(View.VISIBLE);
//			}
//
//			@Override
//			public void onUpdateProgress(final long totalLength, final long completedLength) {
//				runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						progressBar.setProgress((int) (((float)completedLength/totalLength) * 100));
//						progressBar.setVisibility(View.VISIBLE);
//					}
//				});
//			}
//
//			@Override
//			public void onComplete(final byte[] data) {
//				runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						imageView.setImageBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));
//						progressBar.setVisibility(View.GONE);
//					}
//				});
//			}
//
//			@Override
//			public void onComplete(final File cacheFile) {
//				runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						imageView.setImageURI(Uri.fromFile(cacheFile));
//						progressBar.setVisibility(View.GONE);
//					}
//				});
//			}
//
//			@Override
//			public void onFailure() {
//				runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						Toast.makeText(getBaseContext(), "下载失败", Toast.LENGTH_SHORT).show();
//						progressBar.setVisibility(View.GONE);
//					}
//				});
//			}
//
//            @Override
//            public void onCancel() {
//
//            }
//        });

        ImageLoader.getInstance(getBaseContext()).load(uri, new LoadRequest.LoadListener() {
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
            public void onComplete(final Bitmap bitmap) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
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
