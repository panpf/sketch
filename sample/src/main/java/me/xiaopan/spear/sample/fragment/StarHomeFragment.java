package me.xiaopan.spear.sample.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.List;

import me.xiaopan.android.gohttp.GoHttp;
import me.xiaopan.android.gohttp.HttpRequest;
import me.xiaopan.android.gohttp.HttpRequestFuture;
import me.xiaopan.android.gohttp.JsonHttpResponseHandler;
import me.xiaopan.android.inject.InjectContentView;
import me.xiaopan.android.inject.InjectExtra;
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
import me.xiaopan.spear.sample.activity.DetailActivity;
import me.xiaopan.spear.sample.activity.WindowBackgroundManager;
import me.xiaopan.spear.sample.adapter.StarImageAdapter;
import me.xiaopan.spear.sample.net.request.StarImageRequest;
import me.xiaopan.spear.sample.util.ScrollingPauseLoadManager;
import me.xiaopan.spear.sample.widget.HintView;

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
    private WindowBackgroundManager.OnSetWindowBackgroundListener onSetWindowBackgroundListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity != null && activity instanceof WindowBackgroundManager.OnSetWindowBackgroundListener){
            onSetWindowBackgroundListener = (WindowBackgroundManager.OnSetWindowBackgroundListener) activity;
        }
    }

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

        onSetWindowBackgroundListener = null;
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

                recyclerView.setAdapter(starImageAdapter = new StarImageAdapter(getActivity(), null, responseObject.getImages(), StarHomeFragment.this));
                recyclerView.scheduleLayoutAnimation();
                pullRefreshLayout.stopRefresh();
                loadMoreListener.reset();
                starImageAdapter.setOnLoadMoreListener(loadMoreListener);
                if(responseObject.getImages() != null && responseObject.getImages().size() > 0){
                    applyWindowBackground(responseObject.getImages().get(0).getSourceUrl());
                }
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
                            if(getActivity() != null && onSetWindowBackgroundListener != null){
                                onSetWindowBackgroundListener.onSetWindowBackground(new BitmapDrawable(getResources(), bitmap));
                            }else{
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
