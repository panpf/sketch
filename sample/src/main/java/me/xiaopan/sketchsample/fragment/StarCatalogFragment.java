package me.xiaopan.sketchsample.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.assemblyadapter.AssemblyRecyclerAdapter;
import me.xiaopan.gohttp.GoHttp;
import me.xiaopan.gohttp.HttpRequest;
import me.xiaopan.gohttp.HttpRequestFuture;
import me.xiaopan.gohttp.StringHttpResponseHandler;
import me.xiaopan.prl.PullRefreshLayout;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.activity.StarHomeActivity;
import me.xiaopan.sketchsample.adapter.itemfactory.ItemTitleItemFactory;
import me.xiaopan.sketchsample.adapter.itemfactory.StarCatalogItemFactory;
import me.xiaopan.sketchsample.bean.Star;
import me.xiaopan.sketchsample.net.request.ManStarCatalogRequest;
import me.xiaopan.sketchsample.net.request.StarCatalogRequest;
import me.xiaopan.sketchsample.net.request.WomanStarCatalogRequest;
import me.xiaopan.sketchsample.util.ScrollingPauseLoadManager;
import me.xiaopan.sketchsample.util.Settings;
import me.xiaopan.sketchsample.widget.HintView;

/**
 * 明星目录页面
 */
@InjectContentView(R.layout.fragment_star_catalog)
public class StarCatalogFragment extends MyFragment implements PullRefreshLayout.OnRefreshListener, StarCatalogItemFactory.OnClickStarListener {

    @InjectView(R.id.refreshLayout_starCatalog)
    private PullRefreshLayout refreshLayout;
    @InjectView(R.id.hint_starCatalog)
    private HintView hintView;
    @InjectView(R.id.recyclerView_starCatalog_content)
    private RecyclerView recyclerView;

    private HttpRequestFuture httpRequestFuture;
    private AssemblyRecyclerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter != null ? adapter.getSpanSize(position) : 1;
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setOnScrollListener(new ScrollingPauseLoadManager(view.getContext()));
        int padding = (int) getResources().getDimension(R.dimen.home_category_margin_border);
        recyclerView.setPadding(padding, padding, padding, padding);
        recyclerView.setClipToPadding(false);

        refreshLayout.setOnRefreshListener(this);

        if (adapter == null) {
            refreshLayout.startRefresh();
        } else {
            recyclerView.setAdapter(adapter);
            recyclerView.scheduleLayoutAnimation();
        }
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onRefresh() {
        if (httpRequestFuture != null && !httpRequestFuture.isFinished()) {
            return;
        }

        load(false, false);
    }

    @Override
    public void onDetach() {
        if (httpRequestFuture != null && !httpRequestFuture.isFinished()) {
            httpRequestFuture.cancel(true);
        }

        super.onDetach();
    }

    private void load(boolean isMan, final boolean last) {
        httpRequestFuture = GoHttp.with(getActivity()).newRequest(isMan ? new ManStarCatalogRequest() : new WomanStarCatalogRequest(), new StringHttpResponseHandler(), new HttpRequest.Listener<StarCatalogRequest.Result>() {
            @Override
            public void onStarted(HttpRequest httpRequest) {
                hintView.hidden();
            }

            @Override
            public void onCompleted(HttpRequest httpRequest, HttpResponse httpResponse, StarCatalogRequest.Result result, boolean b, boolean b2) {
                if (last) {
                    append(adapter, result);
                    recyclerView.setAdapter(adapter);
                    recyclerView.scheduleLayoutAnimation();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            refreshLayout.stopRefresh();
                        }
                    }, 1000);
                } else {
                    adapter = new AssemblyRecyclerAdapter(new ArrayList());
                    append(adapter, result);
                    adapter.addItemFactory(new StarCatalogItemFactory(StarCatalogFragment.this));
                    adapter.addItemFactory(new ItemTitleItemFactory().fullSpan(recyclerView));
                    load(true, true);
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

            public void append(AssemblyRecyclerAdapter adapter, StarCatalogRequest.Result result) {
                List<Star> starList = result.getStarList();
                if (starList == null) {
                    return;
                }
                adapter.getDataList().add(result.getTitle());
                adapter.getDataList().addAll(starList);
            }
        }).responseHandleCompletedAfterListener(new StarCatalogRequest.ResponseHandler(isMan)).go();
    }

    @Override
    public void onClickImage(Star star) {
        StarHomeActivity.launch(getActivity(), star.name);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onGlobalAttrChanged(String key) {
        if (Settings.PREFERENCE_PLAY_GIF_ON_LIST.equals(key)
                || Settings.PREFERENCE_GLOBAL_IN_PREFER_QUALITY_OVER_SPEED.equals(key)
                || Settings.PREFERENCE_GLOBAL_LOW_QUALITY_IMAGE.equals(key)
                || Settings.PREFERENCE_THUMBNAIL_MODE.equals(key)
                || Settings.PREFERENCE_CACHE_PROCESSED_IMAGE.equals(key)
                || Settings.PREFERENCE_SCROLLING_PAUSE_LOAD.equals(key)
                || Settings.PREFERENCE_MOBILE_NETWORK_PAUSE_DOWNLOAD.equals(key)) {
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }
}