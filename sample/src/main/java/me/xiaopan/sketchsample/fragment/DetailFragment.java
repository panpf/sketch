/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketchsample.fragment;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.format.Formatter;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectExtra;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.drawable.BindDrawable;
import me.xiaopan.sketch.drawable.SketchDrawable;
import me.xiaopan.sketch.feature.large.LargeImageViewer;
import me.xiaopan.sketch.feature.zoom.ImageZoomer;
import me.xiaopan.sketch.request.UriScheme;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.adapter.ImageFragmentAdapter;
import me.xiaopan.sketchsample.util.AnimationBatchExecutor;
import me.xiaopan.sketchsample.util.AnimationUtils;
import me.xiaopan.sketchsample.util.ApplyWallpaperAsyncTask;
import me.xiaopan.sketchsample.util.PageNumberSetter;
import me.xiaopan.sketchsample.util.SaveImageAsyncTask;
import me.xiaopan.sketchsample.util.ViewPagerPlayer;
import me.xiaopan.sketchsample.widget.DepthPageTransformer;
import me.xiaopan.sketchsample.widget.MyImageView;

/**
 * 图片详情页面
 */
@InjectContentView(R.layout.fragment_detail)
public class DetailFragment extends MyFragment implements View.OnClickListener, ImageZoomer.OnViewTapListener {
    public static final String PARAM_REQUIRED_STRING_ARRAY_LIST_URLS = "PARAM_REQUIRED_STRING_ARRAY_LIST_URLS";
    public static final String PARAM_OPTIONAL_INT_DEFAULT_POSITION = "PARAM_OPTIONAL_INT_DEFAULT_POSITION";

    @InjectView(R.id.pager_detail_content)
    private ViewPager viewPager;
    @InjectView(R.id.button_detail_share)
    private View shareButton;
    @InjectView(R.id.button_detail_play)
    private View playButton;
    @InjectView(R.id.button_detail_applyWallpaper)
    private View applyWallpaperButton;
    @InjectView(R.id.button_detail_save)
    private View saveButton;
    @InjectView(R.id.button_detail_info)
    private View infoButton;
    @InjectView(R.id.text_detail_currentItem)
    private TextView currentItemTextView;
    @InjectView(R.id.text_detail_countItem)
    private TextView countTextView;
    @InjectView(R.id.layout_detail_toolbar)
    private View toolbarLayout;
    @InjectView(R.id.layout_detail_number)
    private View numberLayout;

    @InjectExtra(PARAM_REQUIRED_STRING_ARRAY_LIST_URLS)
    private List<String> uris;
    @InjectExtra(PARAM_OPTIONAL_INT_DEFAULT_POSITION)
    private int position;
    private boolean show = false;

    private Handler handler;
    private AnimationBatchExecutor animationBatchExecutor;
    private ViewPagerPlayer viewPagerPlayer;
    private boolean recoverPlay;
    private StartPlay startPlay;

    public static String parseFileType(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf < 0) {
            return null;
        }
        String fileType = fileName.substring(lastIndexOf + 1);
        if ("".equals(fileType.trim())) {
            return null;
        }
        return fileType;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        startPlay = new StartPlay();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        numberLayout.setVisibility(View.GONE);
        shareButton.setVisibility(View.INVISIBLE);
        applyWallpaperButton.setVisibility(View.INVISIBLE);
        playButton.setVisibility(View.INVISIBLE);
        saveButton.setVisibility(View.INVISIBLE);
        infoButton.setVisibility(View.INVISIBLE);
        toolbarLayout.setVisibility(View.GONE);

        animationBatchExecutor = new AnimationBatchExecutor(getActivity(),
                R.anim.action_show, R.anim.action_hidden, 70,
                shareButton, applyWallpaperButton, playButton, saveButton, infoButton);
        viewPagerPlayer = new ViewPagerPlayer(viewPager);
        new PageNumberSetter(currentItemTextView, viewPager);
        viewPager.setPageTransformer(true, new DepthPageTransformer());

        shareButton.setOnClickListener(this);
        applyWallpaperButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        infoButton.setOnClickListener(this);

        if (uris != null) {
            viewPager.setAdapter(new ImageFragmentAdapter(getChildFragmentManager(), uris));
            viewPager.setCurrentItem(position);
            currentItemTextView.setText(position + 1 + "");
            countTextView.setText(uris.size() + "");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (recoverPlay && !viewPagerPlayer.isPlaying()) {
            handler.postDelayed(startPlay, 1000);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (viewPagerPlayer.isPlaying()) {
            viewPagerPlayer.stop();
            recoverPlay = true;
        }
        handler.removeCallbacks(startPlay);
    }

    /**
     * 切换工具栏和页码的显示状态
     */
    private void toggleToolbarVisibleState() {
        show = !show;
        animationBatchExecutor.start(show);
        if (show) {
            AnimationUtils.visibleViewByAlpha(toolbarLayout);
            AnimationUtils.visibleViewByAlpha(numberLayout);
        } else {
            AnimationUtils.goneViewByAlpha(numberLayout);
            AnimationUtils.goneViewByAlpha(toolbarLayout);
        }
    }

    private File getImageFile(String imageUrl, String type) {
        String currentUrl = uris.get(viewPager.getCurrentItem());
        if (currentUrl == null || "".equals(currentUrl.trim())) {
            Toast.makeText(getActivity(), type + "，当前图片的URL是空的，没法拿到图片", Toast.LENGTH_LONG).show();
            return null;
        } else if (currentUrl.startsWith("http://") || currentUrl.startsWith("https://")) {
            DiskCache.Entry diskCacheEntry = Sketch.with(getActivity()).getConfiguration().getDiskCache().get(currentUrl);
            if (diskCacheEntry != null) {
                return diskCacheEntry.getFile();
            } else {
                Toast.makeText(getActivity(), "图片还没有下载好哦，再等一会儿吧！", Toast.LENGTH_LONG).show();
                return null;
            }
        } else if (currentUrl.startsWith("/")) {
            return new File(currentUrl);
        } else {
            Toast.makeText(getActivity(), "我去，怎么会有这样的URL " + imageUrl, Toast.LENGTH_LONG).show();
            return null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_detail_share:
                File imageFile = getImageFile(uris.get(viewPager.getCurrentItem()), "分享");
                if (imageFile != null) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imageFile));
                    intent.setType("image/" + parseFileType(imageFile.getName()));
                    List<ResolveInfo> infoList = getActivity().getPackageManager().queryIntentActivities(intent, 0);
                    if (infoList != null && !infoList.isEmpty()) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), "您的设备上没有能够分享的APP", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.button_detail_applyWallpaper:
                File imageFile2 = getImageFile(uris.get(viewPager.getCurrentItem()), "设置壁纸");
                if (imageFile2 != null) {
                    new ApplyWallpaperAsyncTask(getActivity(), imageFile2) {
                        @Override
                        protected void onPostExecute(Boolean aBoolean) {
                            if (getActivity() != null) {
                                Toast.makeText(getActivity(), aBoolean ? "设置壁纸成功" : "设置壁纸失败", Toast.LENGTH_LONG).show();
                            }
                        }
                    }.execute(0);
                }
                break;
            case R.id.button_detail_play:
                viewPagerPlayer.start();
                toggleToolbarVisibleState();
                break;
            case R.id.button_detail_save:
                String currentUrl = uris.get(viewPager.getCurrentItem());
                if (currentUrl == null || "".equals(currentUrl.trim())) {
                    Toast.makeText(getActivity(), "保存图片失败，因为当前图片的URL是空的，没法拿到图片", Toast.LENGTH_LONG).show();
                } else if (currentUrl.startsWith("http://") || currentUrl.startsWith("https://")) {
                    DiskCache.Entry imageFile3DiskCacheEntry = Sketch.with(getActivity()).getConfiguration().getDiskCache().get(currentUrl);
                    if (imageFile3DiskCacheEntry != null) {
                        new SaveImageAsyncTask(getActivity(), imageFile3DiskCacheEntry.getFile()).execute("");
                    } else {
                        Toast.makeText(getActivity(), "图片还没有下载好哦，再等一会儿吧！", Toast.LENGTH_LONG).show();
                    }
                } else if (currentUrl.startsWith("/")) {
                    Toast.makeText(getActivity(), "当前图片本就是本地的无需保存", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "我去，怎么会有这样的URL " + currentUrl, Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.button_detail_info:
                List<Fragment> childFragmentList = getChildFragmentManager().getFragments();
                for (Fragment childFragment : childFragmentList) {
                    if (childFragment != null && childFragment.isResumed() && childFragment.getUserVisibleHint() && childFragment instanceof ImageFragment) {
                        ImageFragment imageFragment = (ImageFragment) childFragment;
                        MyImageView imageView = imageFragment.getImageView();
                        Drawable drawable = SketchUtils.getLastDrawable(imageView != null ? imageView.getDrawable() : null);

                        if (drawable instanceof BindDrawable) {
                            Toast.makeText(getActivity(), "正在读取图片，请稍后", Toast.LENGTH_LONG).show();
                        } else if (drawable instanceof SketchDrawable) {
                            SketchDrawable sketchDrawable = (SketchDrawable) drawable;

                            StringBuilder messageBuilder = new StringBuilder();
                            messageBuilder.append("URI：").append(sketchDrawable.getImageUri());
                            messageBuilder.append("\n");
                            messageBuilder.append("类型：").append(sketchDrawable.getMimeType());
                            messageBuilder.append("\n");
                            messageBuilder.append("尺寸：").append(sketchDrawable.getOriginWidth()).append("x").append(sketchDrawable.getOriginHeight());

                            File image = null;
                            UriScheme uriScheme = UriScheme.valueOfUri(sketchDrawable.getImageUri());
                            if (uriScheme == UriScheme.FILE) {
                                image = new File(UriScheme.FILE.crop(sketchDrawable.getImageUri()));
                            } else if (uriScheme == UriScheme.NET) {
                                DiskCache.Entry diskCacheEntry = Sketch.with(getActivity()).getConfiguration().getDiskCache().get(sketchDrawable.getImageUri());
                                if (diskCacheEntry != null) {
                                    image = diskCacheEntry.getFile();
                                }
                            }
                            messageBuilder.append("\n");
                            if (image != null) {
                                messageBuilder.append("占用空间：").append(Formatter.formatFileSize(getActivity(), image.length()));
                            } else {
                                messageBuilder.append("占用空间：").append("未知");
                            }

                            int previewDrawableByteCount = sketchDrawable.getByteCount();
                            int pixelByteCount = previewDrawableByteCount / drawable.getIntrinsicWidth() / drawable.getIntrinsicHeight();
                            int originImageByteCount = sketchDrawable.getOriginWidth() * sketchDrawable.getOriginHeight() * pixelByteCount;
                            messageBuilder.append("\n");
                            messageBuilder.append("占用内存：").append(Formatter.formatFileSize(getActivity(), originImageByteCount));

                            messageBuilder.append("\n");
                            messageBuilder.append("\n");
                            messageBuilder.append("预览图尺寸：").append(drawable.getIntrinsicWidth()).append("x").append(drawable.getIntrinsicHeight());
                            messageBuilder.append("\n");
                            messageBuilder.append("预览图Config：").append(sketchDrawable.getBitmapConfig());
                            messageBuilder.append("\n");
                            messageBuilder.append("预览图占用内存：").append(Formatter.formatFileSize(getActivity(), previewDrawableByteCount));

                            messageBuilder.append("\n");
                            messageBuilder.append("\n");
                            messageBuilder.append("缩放倍数：").append(SketchUtils.formatFloat(imageView.getImageZoomFunction().getImageZoomer().getZoomScale(), 2));

                            messageBuilder.append("\n");
                            Rect visibleRect = new Rect();
                            imageView.getImageZoomFunction().getImageZoomer().getVisibleRect(visibleRect);
                            messageBuilder.append("可见区域：").append(visibleRect.toShortString());

                            if (imageView.isSupportLargeImage()) {
                                messageBuilder.append("\n");
                                LargeImageViewer largeImageViewer = imageView.getLargeImageViewer();
                                if (largeImageViewer.isReady()) {
                                    messageBuilder.append("\n");
                                    messageBuilder.append("大图功能占用内存：").append(Formatter.formatFileSize(getActivity(), largeImageViewer.getTilesAllocationByteCount()));
                                    messageBuilder.append("\n");
                                    messageBuilder.append("碎片基数：").append(largeImageViewer.getTiles());
                                    messageBuilder.append("\n");
                                    messageBuilder.append("碎片数量：").append(largeImageViewer.getTileList().size());
                                    messageBuilder.append("\n");
                                    messageBuilder.append("解码区域：").append(largeImageViewer.getDecodeRect().toShortString());
                                    messageBuilder.append("\n");
                                    messageBuilder.append("解码SRC区域：").append(largeImageViewer.getDecodeSrcRect().toShortString());
                                } else if (largeImageViewer.isInitializing()) {
                                    messageBuilder.append("\n");
                                    messageBuilder.append("大图功能正在初始化...");
                                } else {
                                    messageBuilder.append("\n");
                                    messageBuilder.append("无需使用大图功能");
                                }
                            }

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage(messageBuilder.toString());
                            builder.setNegativeButton("取消", null);
                            builder.show();
                        } else {
                            Toast.makeText(getActivity(), "未知来源的图片，无法获取其详细信息", Toast.LENGTH_LONG).show();
                        }
                        break;
                    }
                }
                break;
        }
    }

    @Override
    public void onViewTap(View view, float x, float y) {
        // 如果正在播放就关闭自动播放
        if (viewPagerPlayer.isPlaying()) {
            viewPagerPlayer.stop();
        }

        toggleToolbarVisibleState();
    }

    private class StartPlay implements Runnable {
        @Override
        public void run() {
            viewPagerPlayer.start();
            recoverPlay = false;
        }
    }
}
