package me.xiaopan.android.spear.sample.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
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
import me.xiaopan.android.inject.InjectContentView;
import me.xiaopan.android.inject.InjectView;
import me.xiaopan.android.inject.app.InjectFragment;
import me.xiaopan.android.spear.sample.adapter.ImageRecyclerAdapter;
import me.xiaopan.android.spear.sample.net.request.ImageSearchRequest;
import me.xiaopan.android.spear.sample.widget.HintView;
import me.xiaopan.android.widget.PullRefreshLayout;

/**
 * 图片搜索Fragment
 */
@InjectContentView(R.layout.fragment_search)
public class NewImageSearchFragment extends InjectFragment implements ImageRecyclerAdapter.OnItemClickListener, PullRefreshLayout.OnRefreshListener{
    @InjectView(R.id.refreshLayout_search) PullRefreshLayout pullRefreshLayout;
    @InjectView(R.id.recyclerView_search) private RecyclerView recyclerView;
    @InjectView(R.id.hintView_search) private HintView hintView;

    private ImageSearchRequest imageSearchRequest;
    private HttpRequestFuture refreshRequestFuture;
    private ImageRecyclerAdapter imageRecyclerAdapter;
    private MyLoadMoreListener loadMoreListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageSearchRequest = new ImageSearchRequest("美女");
        loadMoreListener = new MyLoadMoreListener();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pullRefreshLayout.setOnRefreshListener(this);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
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
    public void onItemClick(int position, ImageSearchRequest.Image image) {

    }

    @Override
    public void onRefresh() {
        if(refreshRequestFuture != null && !refreshRequestFuture.isFinished()){
            return;
        }

        loadMoreListener.cancel();
        imageSearchRequest.setStart(0);
        refreshRequestFuture = GoHttp.with(getActivity()).newRequest(imageSearchRequest, new JsonHttpResponseHandler(ImageSearchRequest.Response.class), new HttpRequest.Listener<ImageSearchRequest.Response>() {
            @Override
            public void onStarted(HttpRequest httpRequest) {
                hintView.hidden();
            }

            @Override
            public void onCompleted(HttpRequest httpRequest, HttpResponse httpResponse, ImageSearchRequest.Response responseObject, boolean b, boolean b2) {
                recyclerView.setAdapter(imageRecyclerAdapter = new ImageRecyclerAdapter(getActivity(), responseObject.getImages(), recyclerView, NewImageSearchFragment.this));
                pullRefreshLayout.stopRefresh();
                imageRecyclerAdapter.setOnLoadMoreListener(loadMoreListener);
            }

            @Override
            public void onFailed(HttpRequest httpRequest, HttpResponse httpResponse, HttpRequest.Failure failure, boolean b, boolean b2) {
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
        }).go();
    }

    private class MyLoadMoreListener extends RecyclerView.OnScrollListener implements ImageRecyclerAdapter.OnLoadMoreListener{
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

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            enable = newState != 0 && (refreshRequestFuture == null || refreshRequestFuture.isFinished());
        }

        @Override
        public void onLoadMore() {
            if(refreshRequestFuture != null && !refreshRequestFuture.isFinished()){
                return;
            }

            imageSearchRequest.setStart(imageRecyclerAdapter.getItemCount());
            loadMoreRequestFuture = GoHttp.with(getActivity()).newRequest(imageSearchRequest, new JsonHttpResponseHandler(ImageSearchRequest.Response.class), new HttpRequest.Listener<ImageSearchRequest.Response>() {
                @Override
                public void onStarted(HttpRequest httpRequest) {

                }

                @Override
                public void onCompleted(HttpRequest httpRequest, HttpResponse httpResponse, ImageSearchRequest.Response responseObject, boolean b, boolean b2) {
                    List<ImageSearchRequest.Image> newImageList = responseObject.getImages();
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
                    Toast.makeText(getActivity(), "加载更多失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCanceled(HttpRequest httpRequest) {

                }
            }).go();
        }

        public void cancel(){
            if(loadMoreRequestFuture != null && !loadMoreRequestFuture.isFinished()){
                loadMoreRequestFuture.cancel(true);
            }
        }
    }
}
