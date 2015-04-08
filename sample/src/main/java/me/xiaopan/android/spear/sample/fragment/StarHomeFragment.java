package me.xiaopan.android.spear.sample.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.List;

import me.xiaoapn.android.spear.sample.R;
import me.xiaopan.android.gohttp.GoHttp;
import me.xiaopan.android.gohttp.HttpRequest;
import me.xiaopan.android.gohttp.HttpRequestFuture;
import me.xiaopan.android.gohttp.JsonHttpResponseHandler;
import me.xiaopan.android.gohttp.StringHttpResponseHandler;
import me.xiaopan.android.inject.InjectContentView;
import me.xiaopan.android.inject.InjectExtra;
import me.xiaopan.android.inject.InjectView;
import me.xiaopan.android.inject.app.InjectFragment;
import me.xiaopan.android.spear.sample.activity.DetailActivity;
import me.xiaopan.android.spear.sample.adapter.StarImageAdapter;
import me.xiaopan.android.spear.sample.net.request.StarHomeBackgroundRequest;
import me.xiaopan.android.spear.sample.net.request.StarImageRequest;
import me.xiaopan.android.spear.sample.util.ScrollingPauseLoadManager;
import me.xiaopan.android.spear.sample.widget.HintView;
import me.xiaopan.android.widget.PullRefreshLayout;

/**
 * 明星个人页面
 */
@InjectContentView(R.layout.fragment_star_home)
public class StarHomeFragment extends InjectFragment implements StarImageAdapter.OnItemClickListener, PullRefreshLayout.OnRefreshListener {
    public static final String PARAM_REQUIRED_STRING_STAR_TITLE = "PARAM_REQUIRED_STRING_STAR_TITLE";
    public static final String PARAM_REQUIRED_STRING_STAR_URL = "PARAM_REQUIRED_STRING_STAR_URL";

    @InjectView(R.id.refreshLayout_starHome) private PullRefreshLayout pullRefreshLayout;
    @InjectView(R.id.recyclerView_starHome) private RecyclerView recyclerView;
    @InjectView(R.id.hintView_starHome) private HintView hintView;

    @InjectExtra(PARAM_REQUIRED_STRING_STAR_TITLE) private String starTitle;
    @InjectExtra(PARAM_REQUIRED_STRING_STAR_URL) private String starHomeUrl;

    private StarImageRequest starImageRequest;
    private HttpRequestFuture refreshRequestFuture;
    private StarImageAdapter starImageAdapter;
    private MyLoadMoreListener loadMoreListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        starImageRequest = new StarImageRequest(Uri.parse(starHomeUrl).getLastPathSegment());
        loadMoreListener = new MyLoadMoreListener();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pullRefreshLayout.setOnRefreshListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setOnScrollListener(new ScrollingPauseLoadManager(getActivity()));

        if (starImageAdapter == null) {
            pullRefreshLayout.startRefresh();
        } else {
            recyclerView.setAdapter(starImageAdapter);
            recyclerView.scheduleLayoutAnimation();
        }
    }

    @Override
    public void onDetach() {
        if (refreshRequestFuture != null && !refreshRequestFuture.isFinished()) {
            refreshRequestFuture.cancel(true);
        }

        super.onDetach();
    }

    @Override
    public void onItemClick(int position, StarImageRequest.Image image) {
        DetailActivity.launch(getActivity(), (ArrayList<String>) starImageAdapter.getImageUrlList(), position);
    }

    @Override
    public void onRefresh() {
        if (refreshRequestFuture != null && !refreshRequestFuture.isFinished()) {
            return;
        }

        loadMoreListener.cancel();
        refreshRequestFuture = GoHttp.with(getActivity()).newRequest(starHomeUrl, new StringHttpResponseHandler(), new HttpRequest.Listener<StarHomeBackgroundRequest.Background>() {
            @Override
            public void onStarted(HttpRequest httpRequest) {
                hintView.hidden();
            }

            @Override
            public void onCompleted(HttpRequest httpRequest, HttpResponse httpResponse, StarHomeBackgroundRequest.Background backgroundObject, boolean b, boolean b2) {
                if (getActivity() == null) {
                    return;
                }

//                recyclerView.setBackgroundColor(backgroundObject.getBackgroundColor());
                loadItems(backgroundObject.getBackgroundImageUrl());
            }

            @Override
            public void onFailed(HttpRequest httpRequest, HttpResponse httpResponse, HttpRequest.Failure failure, boolean b, boolean b2) {
                if (getActivity() == null) {
                    return;
                }

                pullRefreshLayout.stopRefresh();
                if (starImageAdapter == null) {
                    hintView.failure(failure, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pullRefreshLayout.startRefresh();
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "刷新失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCanceled(HttpRequest httpRequest) {

            }
        }).responseHandleCompletedAfterListener(new StarHomeBackgroundRequest.ResponseHandler()).go();
    }

    private void loadItems(final String backgroundImageUrl) {
        starImageRequest.setStart(0);
        refreshRequestFuture = GoHttp.with(getActivity()).newRequest(starImageRequest, new JsonHttpResponseHandler(StarImageRequest.Response.class), new HttpRequest.Listener<StarImageRequest.Response>() {
            @Override
            public void onStarted(HttpRequest httpRequest) {
                hintView.hidden();
            }

            @Override
            public void onCompleted(HttpRequest httpRequest, HttpResponse httpResponse, StarImageRequest.Response responseObject, boolean b, boolean b2) {
                if (getActivity() == null) {
                    return;
                }

                recyclerView.setAdapter(starImageAdapter = new StarImageAdapter(getActivity(), backgroundImageUrl, responseObject.getImages(), StarHomeFragment.this));
                recyclerView.scheduleLayoutAnimation();
                pullRefreshLayout.stopRefresh();
                loadMoreListener.reset();
                starImageAdapter.setOnLoadMoreListener(loadMoreListener);
            }

            @Override
            public void onFailed(HttpRequest httpRequest, HttpResponse httpResponse, HttpRequest.Failure failure, boolean b, boolean b2) {
                if (getActivity() == null) {
                    return;
                }

                pullRefreshLayout.stopRefresh();
                if (starImageAdapter == null) {
                    hintView.failure(failure, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pullRefreshLayout.startRefresh();
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "刷新失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCanceled(HttpRequest httpRequest) {
            }
        }).responseHandleCompletedAfterListener(new StarImageRequest.ResponseHandler()).go();
    }

    private class MyLoadMoreListener implements StarImageAdapter.OnLoadMoreListener {
        private boolean end;
        private HttpRequestFuture loadMoreRequestFuture;

        @Override
        public boolean isEnd() {
            return end;
        }

        public void reset() {
            end = false;
        }

        @Override
        public void onLoadMore() {
            if (refreshRequestFuture != null && !refreshRequestFuture.isFinished()) {
                return;
            }

            starImageRequest.setStart(starImageAdapter.getDataSize());
            loadMoreRequestFuture = GoHttp.with(getActivity()).newRequest(starImageRequest, new JsonHttpResponseHandler(StarImageRequest.Response.class), new HttpRequest.Listener<StarImageRequest.Response>() {
                @Override
                public void onStarted(HttpRequest httpRequest) {

                }

                @Override
                public void onCompleted(HttpRequest httpRequest, HttpResponse httpResponse, StarImageRequest.Response responseObject, boolean b, boolean b2) {
                    if (getActivity() == null) {
                        return;
                    }

                    int count = starImageAdapter.getItemCount();

                    List<StarImageRequest.Image> newImageList = responseObject.getImages();
                    if (newImageList != null && newImageList.size() > 0) {
                        starImageAdapter.append(newImageList);
                        if(newImageList.size() < starImageRequest.getSize()){
                            end = true;
                            Toast.makeText(getActivity(), "新送达" + newImageList.size() + "个包裹，已全部送完！", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getActivity(), "新送达" + newImageList.size() + "个包裹", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        end = true;
                        Toast.makeText(getActivity(), "没有您的包裹了", Toast.LENGTH_SHORT).show();
                    }
                    starImageAdapter.notifyItemInserted(count);
                }

                @Override
                public void onFailed(HttpRequest httpRequest, HttpResponse httpResponse, HttpRequest.Failure failure, boolean b, boolean b2) {
                    if (getActivity() == null) {
                        return;
                    }
                    starImageAdapter.loadMoreFail();
                    Toast.makeText(getActivity(), "快递投递失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCanceled(HttpRequest httpRequest) {

                }
            }).responseHandleCompletedAfterListener(new StarImageRequest.ResponseHandler()).go();
        }

        public void cancel() {
            if (loadMoreRequestFuture != null && !loadMoreRequestFuture.isFinished()) {
                loadMoreRequestFuture.cancel(true);
            }
        }
    }
}
