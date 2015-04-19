package me.xiaopan.spear.sample.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.apache.http.HttpResponse;

import me.xiaopan.android.gohttp.GoHttp;
import me.xiaopan.android.gohttp.HttpRequest;
import me.xiaopan.android.gohttp.HttpRequestFuture;
import me.xiaopan.android.gohttp.StringHttpResponseHandler;
import me.xiaopan.android.inject.InjectContentView;
import me.xiaopan.android.inject.InjectView;
import me.xiaopan.android.widget.PullRefreshLayout;
import me.xiaopan.spear.sample.MyFragment;
import me.xiaopan.spear.sample.R;
import me.xiaopan.spear.sample.activity.StarHomeActivity;
import me.xiaopan.spear.sample.adapter.StarCatalogAdapter;
import me.xiaopan.spear.sample.net.request.ManStarCatalogRequest;
import me.xiaopan.spear.sample.net.request.StarCatalogRequest;
import me.xiaopan.spear.sample.net.request.WomanStarCatalogRequest;
import me.xiaopan.spear.sample.util.ScrollingPauseLoadManager;
import me.xiaopan.spear.sample.widget.HintView;

/**
 * 明星目录页面
 */
@InjectContentView(R.layout.fragment_star_catalog)
public class StarCatalogFragment extends MyFragment implements PullRefreshLayout.OnRefreshListener, StarCatalogAdapter.OnImageClickListener {

    @InjectView(R.id.refreshLayout_starCatalog) private PullRefreshLayout refreshLayout;
    @InjectView(R.id.hint_starCatalog) private HintView hintView;
    @InjectView(R.id.recyclerView_starCatalog_content) private RecyclerView contentRecyclerView;

    private HttpRequestFuture httpRequestFuture;
    private StarCatalogAdapter adapter;

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

        super.onDetach();
    }

    private void load(boolean isMan, final boolean last){
        httpRequestFuture = GoHttp.with(getActivity()).newRequest(isMan?new ManStarCatalogRequest():new WomanStarCatalogRequest(), new StringHttpResponseHandler(), new HttpRequest.Listener<StarCatalogRequest.Result>() {
            @Override
            public void onStarted(HttpRequest httpRequest) {
                hintView.hidden();
            }

            @Override
            public void onCompleted(HttpRequest httpRequest, HttpResponse httpResponse, StarCatalogRequest.Result result, boolean b, boolean b2) {
                if(last){
                    adapter.append(result);
                    contentRecyclerView.setAdapter(adapter);
                    contentRecyclerView.scheduleLayoutAnimation();
                    refreshLayout.stopRefresh();
                }else{
                    adapter = new StarCatalogAdapter(getActivity(), result, StarCatalogFragment.this);
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
        }).responseHandleCompletedAfterListener(new StarCatalogRequest.ResponseHandler(isMan)).go();
    }

    @Override
    public void onClickImage(StarCatalogRequest.Star star) {
        StarHomeActivity.launch(getActivity(), star.getName());
    }
}