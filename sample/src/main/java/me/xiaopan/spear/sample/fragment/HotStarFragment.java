package me.xiaopan.spear.sample.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpResponse;

import java.util.List;

import me.xiaopan.android.gohttp.GoHttp;
import me.xiaopan.android.gohttp.HttpRequest;
import me.xiaopan.android.gohttp.HttpRequestFuture;
import me.xiaopan.android.gohttp.StringHttpResponseHandler;
import me.xiaopan.android.inject.InjectContentView;
import me.xiaopan.android.inject.InjectView;
import me.xiaopan.android.inject.app.InjectFragment;
import me.xiaopan.android.widget.PullRefreshLayout;
import me.xiaopan.spear.CancelCause;
import me.xiaopan.spear.FailCause;
import me.xiaopan.spear.ImageFrom;
import me.xiaopan.spear.LoadListener;
import me.xiaopan.spear.Spear;
import me.xiaopan.spear.process.BlurImageProcessor;
import me.xiaopan.spear.sample.R;
import me.xiaopan.spear.sample.activity.StarHomeActivity;
import me.xiaopan.spear.sample.activity.WindowBackgroundManager;
import me.xiaopan.spear.sample.adapter.HotStarAdapter;
import me.xiaopan.spear.sample.net.request.HotManStarRequest;
import me.xiaopan.spear.sample.net.request.HotStarRequest;
import me.xiaopan.spear.sample.net.request.HotWomanStarRequest;
import me.xiaopan.spear.sample.util.ScrollingPauseLoadManager;
import me.xiaopan.spear.sample.widget.HintView;

/**
 * 热门明星页
 */
@InjectContentView(R.layout.fragment_hot_star)
public class HotStarFragment extends InjectFragment implements PullRefreshLayout.OnRefreshListener, HotStarAdapter.OnImageClickListener {

    @InjectView(R.id.refreshLayout_hotStar) private PullRefreshLayout refreshLayout;
    @InjectView(R.id.hint_hotStar) private HintView hintView;
    @InjectView(R.id.recyclerView_hotStar_content) private RecyclerView contentRecyclerView;

    private HttpRequestFuture httpRequestFuture;
    private HotStarAdapter adapter;
    private String firstImageUri;
    private WindowBackgroundManager.OnSetWindowBackgroundListener onSetWindowBackgroundListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity != null && activity instanceof WindowBackgroundManager.OnSetWindowBackgroundListener){
            onSetWindowBackgroundListener = (WindowBackgroundManager.OnSetWindowBackgroundListener) activity;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        contentRecyclerView.setOnScrollListener(new ScrollingPauseLoadManager(view.getContext()));
        refreshLayout.setOnRefreshListener(this);

        if(adapter == null){
            refreshLayout.startRefresh();
        }else{
            contentRecyclerView.setAdapter(adapter);
            contentRecyclerView.scheduleLayoutAnimation();
        }
    }

    @Override
    public void onRefresh() {
        if(httpRequestFuture != null && !httpRequestFuture.isFinished()){
            return;
        }

        load(false, false);
    }

    @Override
    public void onDetach() {
        if(httpRequestFuture != null && !httpRequestFuture.isFinished()){
            httpRequestFuture.cancel(true);
        }
        onSetWindowBackgroundListener = null;
        super.onDetach();
    }

    private void load(boolean isMan, final boolean last){
        httpRequestFuture = GoHttp.with(getActivity()).newRequest(isMan?new HotManStarRequest():new HotWomanStarRequest(), new StringHttpResponseHandler(), new HttpRequest.Listener<List<HotStarRequest.HotStar>>() {
            @Override
            public void onStarted(HttpRequest httpRequest) {
                hintView.hidden();
            }

            @Override
            public void onCompleted(HttpRequest httpRequest, HttpResponse httpResponse, List<HotStarRequest.HotStar> hotStarList, boolean b, boolean b2) {
                if(last){
                    adapter.append(hotStarList);
                    contentRecyclerView.setAdapter(adapter);
                    contentRecyclerView.scheduleLayoutAnimation();
                    refreshLayout.stopRefresh();
                    if(firstImageUri == null && hotStarList.size() > 0 && hotStarList.get(0).getStarList().size() > 0){
                        firstImageUri = hotStarList.get(0).getStarList().get(0).getHeightImage().getUrl();
                    }
                    if(firstImageUri != null){
                        applyWindowBackground(firstImageUri);
                    }
                }else{
                    adapter = new HotStarAdapter(getActivity(), hotStarList, HotStarFragment.this);
                    if(hotStarList.size() > 0 && hotStarList.get(0).getStarList().size() > 0){
                        firstImageUri = hotStarList.get(0).getStarList().get(0).getHeightImage().getUrl();
                    }
                    load(true, true);
                }
            }

            @Override
            public void onFailed(HttpRequest httpRequest, HttpResponse httpResponse, HttpRequest.Failure failure, boolean b, boolean b2) {
                refreshLayout.stopRefresh();
                if (adapter == null) {
                    hintView.failure(failure, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            refreshLayout.startRefresh();
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "刷新失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCanceled(HttpRequest httpRequest) {

            }
        }).responseHandleCompletedAfterListener(new HotStarRequest.ResponseHandler(isMan)).go();
    }

    @Override
    public void onClickImage(HotStarRequest.Star star) {
        StarHomeActivity.launch(getActivity(), star.getName());
    }

    private void applyWindowBackground(String imageUri){
        Log.e("test", "applyWindowBackground");
        if(imageUri == null || getActivity() == null){
            return;
        }
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        Spear.with(getActivity()).load(imageUri, new LoadListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onCompleted(final Bitmap bitmap, ImageFrom imageFrom) {
                if(onSetWindowBackgroundListener != null && getActivity() != null){
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (getActivity() != null && onSetWindowBackgroundListener != null) {
                                onSetWindowBackgroundListener.onSetWindowBackground(new BitmapDrawable(getResources(), bitmap));
                            } else {
                                bitmap.recycle();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailed(FailCause failCause) {

            }

            @Override
            public void onCanceled(CancelCause cancelCause) {

            }
        }).resize(displayMetrics.widthPixels, displayMetrics.heightPixels)
                .scaleType(ImageView.ScaleType.CENTER_CROP)
                .processor(new BlurImageProcessor(15, true))
                .fire();
    }
}