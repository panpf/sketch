package me.xiaopan.android.spear.sample.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.apache.http.HttpResponse;

import java.util.List;

import me.xiaoapn.android.spear.sample.R;
import me.xiaopan.android.gohttp.GoHttp;
import me.xiaopan.android.gohttp.HttpRequest;
import me.xiaopan.android.gohttp.HttpRequestFuture;
import me.xiaopan.android.gohttp.StringHttpResponseHandler;
import me.xiaopan.android.inject.InjectContentView;
import me.xiaopan.android.inject.InjectView;
import me.xiaopan.android.inject.app.InjectFragment;
import me.xiaopan.android.spear.sample.activity.StarHomeActivity;
import me.xiaopan.android.spear.sample.adapter.HotStarAdapter;
import me.xiaopan.android.spear.sample.net.request.HotManStarRequest;
import me.xiaopan.android.spear.sample.net.request.HotStarRequest;
import me.xiaopan.android.spear.sample.net.request.HotWomanStarRequest;
import me.xiaopan.android.spear.sample.widget.HintView;
import me.xiaopan.android.widget.PullRefreshLayout;

/**
 * 热门明星页
 */
@InjectContentView(R.layout.fragment_hot_star)
public class HotStarFragment extends InjectFragment implements PullRefreshLayout.OnRefreshListener, HotStarAdapter.OnImageClickListener{

    @InjectView(R.id.refreshLayout_hotStar) private PullRefreshLayout refreshLayout;
    @InjectView(R.id.hint_hotStar) private HintView hintView;
    @InjectView(R.id.recyclerView_hotStar_content) private RecyclerView contentRecyclerView;

    private HttpRequestFuture httpRequestFuture;
    private HotStarAdapter adapter;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        refreshLayout.setOnRefreshListener(this);

        if(adapter == null){
            refreshLayout.startRefresh();
        }else{
            contentRecyclerView.setAdapter(adapter);
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
                    refreshLayout.stopRefresh();
                }else{
                    adapter = new HotStarAdapter(getActivity(), hotStarList, HotStarFragment.this);
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
}