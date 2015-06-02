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

import me.xiaopan.android.gohttp.GoHttp;
import me.xiaopan.android.gohttp.HttpRequest;
import me.xiaopan.android.gohttp.HttpRequestFuture;
import me.xiaopan.android.gohttp.StringHttpResponseHandler;
import me.xiaopan.android.inject.InjectContentView;
import me.xiaopan.android.inject.InjectView;
import me.xiaopan.android.widget.PullRefreshLayout;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.activity.StarHomeActivity;
import me.xiaopan.sketchsample.activity.WindowBackgroundManager;
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
    private WindowBackgroundManager.WindowBackgroundLoader windowBackgroundLoader;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity != null && activity instanceof WindowBackgroundManager.OnSetWindowBackgroundListener){
            windowBackgroundLoader = new WindowBackgroundManager.WindowBackgroundLoader(activity.getBaseContext(), (WindowBackgroundManager.OnSetWindowBackgroundListener) activity);
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
            if(windowBackgroundLoader != null){
                windowBackgroundLoader.restore();
            }
        }
    }

    @Override
    public void onRefresh() {
        if(httpRequestFuture != null && !httpRequestFuture.isFinished()){
            return;
        }

        load(false, false, false);
    }

    @Override
    public void onDetach() {
        if(httpRequestFuture != null && !httpRequestFuture.isFinished()){
            httpRequestFuture.cancel(true);
        }
        if(windowBackgroundLoader != null){
            windowBackgroundLoader.detach();
        }
        super.onDetach();
    }

    @Override
    protected void onUserVisibleChanged(boolean isVisibleToUser) {
        if(windowBackgroundLoader != null){
            windowBackgroundLoader.setUserVisible(isVisibleToUser);
        }
    }

    @Override
    public void onClickImage(HotStarRequest.Star star) {
        StarHomeActivity.launch(getActivity(), star.getName());
    }

    private void load(boolean isMan, final boolean last, final boolean haveSetWindowBackground){
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
                    if (!haveSetWindowBackground && windowBackgroundLoader != null && hotStarList.size() > 0 && hotStarList.get(0).getStarList().size() > 0) {
                        windowBackgroundLoader.load(hotStarList.get(0).getStarList().get(0).getHeightImage().getUrl());
                    }
                } else {
                    adapter = new HotStarAdapter(getActivity(), hotStarList, HotStarFragment.this);
                    boolean result = false;
                    if (windowBackgroundLoader != null && hotStarList.size() > 0 && hotStarList.get(0).getStarList().size() > 0) {
                        windowBackgroundLoader.load(hotStarList.get(0).getStarList().get(0).getHeightImage().getUrl());
                        result = true;
                    }
                    load(true, true, result);
                }
            }

            @Override
            public void onFailed(HttpRequest httpRequest, HttpResponse httpResponse, HttpRequest.Failure failure, boolean b, boolean b2) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.stopRefresh();
                    }
                }, 1000);
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
}