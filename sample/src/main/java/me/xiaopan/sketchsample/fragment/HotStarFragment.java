package me.xiaopan.sketchsample.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.apache.http.HttpResponse;

import java.util.List;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.gohttp.GoHttp;
import me.xiaopan.gohttp.HttpRequest;
import me.xiaopan.gohttp.HttpRequestFuture;
import me.xiaopan.gohttp.StringHttpResponseHandler;
import me.xiaopan.prl.PullRefreshLayout;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.activity.ApplyBackgroundCallback;
import me.xiaopan.sketchsample.activity.StarHomeActivity;
import me.xiaopan.sketchsample.adapter.HotStarAdapter;
import me.xiaopan.sketchsample.net.request.HotManStarRequest;
import me.xiaopan.sketchsample.net.request.HotStarRequest;
import me.xiaopan.sketchsample.net.request.HotWomanStarRequest;
import me.xiaopan.sketchsample.util.ScrollingPauseLoadManager;
import me.xiaopan.sketchsample.widget.HintView;

/**
 * 热门明星页
 */
@InjectContentView(R.layout.fragment_hot_star)
public class HotStarFragment extends MyFragment implements PullRefreshLayout.OnRefreshListener, HotStarAdapter.OnImageClickListener {

    @InjectView(R.id.refreshLayout_hotStar) private PullRefreshLayout refreshLayout;
    @InjectView(R.id.hint_hotStar) private HintView hintView;
    @InjectView(R.id.recyclerView_hotStar_content) private RecyclerView contentRecyclerView;

    private HttpRequestFuture httpRequestFuture;
    private HotStarAdapter adapter;

    private ApplyBackgroundCallback applyBackgroundCallback;
    private String backgroundImageUri;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ApplyBackgroundCallback) {
            applyBackgroundCallback = (ApplyBackgroundCallback) activity;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        contentRecyclerView.setOnScrollListener(new ScrollingPauseLoadManager(view.getContext()));
        refreshLayout.setOnRefreshListener(this);

        if (adapter == null) {
            refreshLayout.startRefresh();
        } else {
            contentRecyclerView.setAdapter(adapter);
            contentRecyclerView.scheduleLayoutAnimation();
        }
    }

    @Override
    public void onRefresh() {
        if (httpRequestFuture != null && !httpRequestFuture.isFinished()) {
            return;
        }

        load(false, false, false);
    }

    @Override
    public void onDetach() {
        if (httpRequestFuture != null && !httpRequestFuture.isFinished()) {
            httpRequestFuture.cancel(true);
        }
        super.onDetach();
    }

    @Override
    protected void onUserVisibleChanged(boolean isVisibleToUser) {
        if (applyBackgroundCallback != null && isVisibleToUser) {
            changeBackground(backgroundImageUri);
        }
    }

    @Override
    public void onClickImage(HotStarRequest.Star star) {
        StarHomeActivity.launch(getActivity(), star.getName());
    }

    private void changeBackground(String imageUri) {
        this.backgroundImageUri = imageUri;
        if (applyBackgroundCallback != null) {
            applyBackgroundCallback.onApplyBackground(backgroundImageUri);
        }
    }

    private void load(boolean isMan, final boolean last, final boolean haveSetWindowBackground) {
        httpRequestFuture = GoHttp.with(getActivity()).newRequest(isMan ? new HotManStarRequest() : new HotWomanStarRequest(), new StringHttpResponseHandler(), new HttpRequest.Listener<List<HotStarRequest.HotStar>>() {
            @Override
            public void onStarted(HttpRequest httpRequest) {
                hintView.hidden();
            }

            @Override
            public void onCompleted(HttpRequest httpRequest, HttpResponse httpResponse, List<HotStarRequest.HotStar> hotStarList, boolean b, boolean b2) {
                if (last) {
                    adapter.append(hotStarList);
                    contentRecyclerView.setAdapter(adapter);
                    contentRecyclerView.scheduleLayoutAnimation();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            refreshLayout.stopRefresh();
                        }
                    }, 1000);
                    if (!haveSetWindowBackground && hotStarList.size() > 0 && hotStarList.get(0).getStarList().size() > 0) {
                        changeBackground(hotStarList.get(0).getStarList().get(0).getHeightImage().getUrl());
                    }
                } else {
                    adapter = new HotStarAdapter(getActivity(), hotStarList, HotStarFragment.this);
                    boolean result = false;
                    if (hotStarList.size() > 0 && hotStarList.get(0).getStarList().size() > 0) {
                        changeBackground(hotStarList.get(0).getStarList().get(0).getHeightImage().getUrl());
                        result = true;
                    }
                    load(true, true, result);
                }
            }

            @Override
            public void onFailed(HttpRequest httpRequest, HttpResponse httpResponse, HttpRequest.
                    Failure failure, boolean b, boolean b2) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.stopRefresh();
                    }
                }, 1000);
                if (adapter == null) {
                    hintView.failed(failure, new View.OnClickListener() {
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
}