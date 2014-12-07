package me.xiaopan.android.spear.sample.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import me.xiaopan.android.gohttp.JsonHttpResponseHandler;
import me.xiaopan.android.gohttp.StringHttpResponseHandler;
import me.xiaopan.android.inject.InjectContentView;
import me.xiaopan.android.inject.InjectExtra;
import me.xiaopan.android.inject.InjectView;
import me.xiaopan.android.inject.app.InjectFragment;
import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.sample.adapter.StarImageAdapter;
import me.xiaopan.android.spear.sample.net.request.StarHomeRequest;
import me.xiaopan.android.spear.sample.net.request.StarImageRequest;
import me.xiaopan.android.spear.sample.widget.HintView;
import me.xiaopan.android.widget.PullRefreshLayout;

/**
 * 明星个人页面
 */
@InjectContentView(R.layout.fragment_search)
public class StarFragment extends InjectFragment implements StarImageAdapter.OnItemClickListener, PullRefreshLayout.OnRefreshListener{
    public static final String PARAM_REQUIRED_STRING_STAR_TITLE = "PARAM_REQUIRED_STRING_STAR_TITLE";
    public static final String PARAM_REQUIRED_STRING_STAR_URL = "PARAM_REQUIRED_STRING_STAR_URL";

    @InjectView(R.id.refreshLayout_search) PullRefreshLayout pullRefreshLayout;
    @InjectView(R.id.recyclerView_search) private RecyclerView recyclerView;
    @InjectView(R.id.hintView_search) private HintView hintView;

    private StarImageRequest starImageRequest;
    private HttpRequestFuture refreshRequestFuture;
    private StarImageAdapter imageRecyclerAdapter;
    private MyLoadMoreListener loadMoreListener;

    @InjectExtra(PARAM_REQUIRED_STRING_STAR_TITLE) private String starTitle;
    @InjectExtra(PARAM_REQUIRED_STRING_STAR_URL) private String starHomeUrl;

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
        recyclerView.setOnScrollListener(loadMoreListener);

        if(imageRecyclerAdapter == null){
            pullRefreshLayout.startRefresh();
        }else{
            recyclerView.setAdapter(imageRecyclerAdapter);
        }
    }

    @Override
    public void onDetach() {
        if(refreshRequestFuture != null && !refreshRequestFuture.isFinished()){
            refreshRequestFuture.cancel(true);
        }
        super.onDetach();
    }

    @Override
    public void onItemClick(int position, StarImageRequest.Image image) {

    }

    @Override
    public void onRefresh() {
        if(refreshRequestFuture != null && !refreshRequestFuture.isFinished()){
            return;
        }

        loadMoreListener.cancel();
        refreshRequestFuture = GoHttp.with(getActivity()).newRequest(starHomeUrl, new StringHttpResponseHandler(), new HttpRequest.Listener<StarHomeRequest.Response>() {
            @Override
            public void onStarted(HttpRequest httpRequest) {
                hintView.hidden();
            }

            @Override
            public void onCompleted(HttpRequest httpRequest, HttpResponse httpResponse, StarHomeRequest.Response responseObject, boolean b, boolean b2) {
                if(getActivity() == null){
                    return;
                }

                recyclerView.setBackgroundColor(responseObject.getBackgroundColor());
                loadItems(responseObject.getBackgroundImageUrl());
            }

            @Override
            public void onFailed(HttpRequest httpRequest, HttpResponse httpResponse, HttpRequest.Failure failure, boolean b, boolean b2) {
                if(getActivity() == null){
                    return;
                }

                pullRefreshLayout.stopRefresh();
                if (imageRecyclerAdapter == null) {
                    hintView.failure(failure, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pullRefreshLayout.startRefresh();
                        }
                    });
                }else{
                    Toast.makeText(getActivity(), "刷新失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCanceled(HttpRequest httpRequest) {

            }
        }).responseHandleCompletedAfterListener(new StarHomeRequest.HomeRequestResponseHandle()).go();
    }

    private void loadItems(final String backgroundImageUrl){
        starImageRequest.setStart(0);
        refreshRequestFuture = GoHttp.with(getActivity()).newRequest(starImageRequest, new JsonHttpResponseHandler(StarImageRequest.Response.class), new HttpRequest.Listener<StarImageRequest.Response>() {
            @Override
            public void onStarted(HttpRequest httpRequest) {
                hintView.hidden();
            }

            @Override
            public void onCompleted(HttpRequest httpRequest, HttpResponse httpResponse, StarImageRequest.Response responseObject, boolean b, boolean b2) {
                if(getActivity() == null){
                    return;
                }

                recyclerView.setAdapter(imageRecyclerAdapter = new StarImageAdapter(getActivity(), backgroundImageUrl, responseObject.getImages(), StarFragment.this));
                pullRefreshLayout.stopRefresh();
                loadMoreListener.reset();
                imageRecyclerAdapter.setOnLoadMoreListener(loadMoreListener);
            }

            @Override
            public void onFailed(HttpRequest httpRequest, HttpResponse httpResponse, HttpRequest.Failure failure, boolean b, boolean b2) {
                if(getActivity() == null){
                    return;
                }

                pullRefreshLayout.stopRefresh();
                if (imageRecyclerAdapter == null) {
                    hintView.failure(failure, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pullRefreshLayout.startRefresh();
                        }
                    });
                }else{
                    Toast.makeText(getActivity(), "刷新失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCanceled(HttpRequest httpRequest) {
            }
        }).responseHandleCompletedAfterListener(new StarImageRequest.ResponseHandler()).go();
    }

    private class MyLoadMoreListener extends RecyclerView.OnScrollListener implements StarImageAdapter.OnLoadMoreListener{
        private boolean enable;
        private boolean end;
        private HttpRequestFuture loadMoreRequestFuture;

        @Override
        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        @Override
        public boolean isEnable() {
            return !end && enable;
        }

        public void reset(){
            end = false;
            enable = true;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            enable = newState != 0 && (refreshRequestFuture == null || refreshRequestFuture.isFinished());
            if(newState == RecyclerView.SCROLL_STATE_DRAGGING){
                Spear.with(getActivity()).pause();
            }else if(newState ==RecyclerView.SCROLL_STATE_IDLE){
                Spear.with(getActivity()).resume();
                if(imageRecyclerAdapter != null){
                    imageRecyclerAdapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onLoadMore() {
            if(refreshRequestFuture != null && !refreshRequestFuture.isFinished()){
                return;
            }

            starImageRequest.setStart(imageRecyclerAdapter.getImageList().size());
            loadMoreRequestFuture = GoHttp.with(getActivity()).newRequest(starImageRequest, new JsonHttpResponseHandler(StarImageRequest.Response.class), new HttpRequest.Listener<StarImageRequest.Response>() {
                @Override
                public void onStarted(HttpRequest httpRequest) {

                }

                @Override
                public void onCompleted(HttpRequest httpRequest, HttpResponse httpResponse, StarImageRequest.Response responseObject, boolean b, boolean b2) {
                    if(getActivity() == null){
                        return;
                    }

                    List<StarImageRequest.Image> newImageList = responseObject.getImages();
                    if (newImageList == null || newImageList.size() == 0) {
                        end = true;
                        return;
                    }

                    imageRecyclerAdapter.getImageList().addAll(newImageList);
                    imageRecyclerAdapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(), "新加载"+newImageList.size()+"条数据", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailed(HttpRequest httpRequest, HttpResponse httpResponse, HttpRequest.Failure failure, boolean b, boolean b2) {
                    if(getActivity() == null){
                        return;
                    }

                    Toast.makeText(getActivity(), "加载更多失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCanceled(HttpRequest httpRequest) {

                }
            }).responseHandleCompletedAfterListener(new StarImageRequest.ResponseHandler()).go();
        }

        public void cancel(){
            if(loadMoreRequestFuture != null && !loadMoreRequestFuture.isFinished()){
                loadMoreRequestFuture.cancel(true);
            }
        }
    }
}
