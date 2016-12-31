package me.xiaopan.sketchsample.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Toast;

import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.List;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectExtra;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.assemblyadapter.AssemblyRecyclerAdapter;
import me.xiaopan.assemblyadapter.OnRecyclerLoadMoreListener;
import me.xiaopan.gohttp.GoHttp;
import me.xiaopan.gohttp.HttpRequest;
import me.xiaopan.gohttp.HttpRequestFuture;
import me.xiaopan.gohttp.JsonHttpResponseHandler;
import me.xiaopan.gohttp.StringHttpResponseHandler;
import me.xiaopan.prl.PullRefreshLayout;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.activity.ApplyBackgroundCallback;
import me.xiaopan.sketchsample.activity.DetailActivity;
import me.xiaopan.sketchsample.adapter.itemfactory.LoadMoreItemFactory;
import me.xiaopan.sketchsample.adapter.itemfactory.StaggeredImageItemFactory;
import me.xiaopan.sketchsample.adapter.itemfactory.StarHeaderItemFactory;
import me.xiaopan.sketchsample.net.request.StarHomeBackgroundRequest;
import me.xiaopan.sketchsample.net.request.StarImageRequest;
import me.xiaopan.sketchsample.util.ScrollingPauseLoadManager;
import me.xiaopan.sketchsample.widget.HintView;

/**
 * 明星个人页面
 */
@InjectContentView(R.layout.fragment_star_home)
public class StarHomeFragment extends MyFragment implements StaggeredImageItemFactory.OnItemClickListener, PullRefreshLayout.OnRefreshListener, OnRecyclerLoadMoreListener {
    public static final String PARAM_REQUIRED_STRING_STAR_TITLE = "PARAM_REQUIRED_STRING_STAR_TITLE";
    public static final String PARAM_REQUIRED_STRING_STAR_URL = "PARAM_REQUIRED_STRING_STAR_URL";

    @InjectView(R.id.refreshLayout_starHome)
    private PullRefreshLayout pullRefreshLayout;
    @InjectView(R.id.recycler_starHome)
    private RecyclerView recyclerView;
    @InjectView(R.id.hintView_starHome)
    private HintView hintView;

    @InjectExtra(PARAM_REQUIRED_STRING_STAR_TITLE)
    private String starTitle;
    @InjectExtra(PARAM_REQUIRED_STRING_STAR_URL)
    private String starHomeUrl;

    private StarImageRequest starImageRequest;
    private HttpRequestFuture refreshRequest;
    private AssemblyRecyclerAdapter adapter;
    private HttpRequestFuture loadMoreRequest;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        starImageRequest = new StarImageRequest(Uri.parse(starHomeUrl).getLastPathSegment());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pullRefreshLayout.setOnRefreshListener(this);

        recyclerView.setOnScrollListener(new ScrollingPauseLoadManager(getActivity()));
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        int padding  = SketchUtils.dp2px(getActivity(), 2);
        recyclerView.setPadding(padding, padding, padding, padding);
        recyclerView.setClipToPadding(false);

        if (adapter == null) {
            pullRefreshLayout.startRefresh();
        } else {
            setAdapter(adapter);
        }
    }

    @Override
    public void onDetach() {
        if (refreshRequest != null && !refreshRequest.isFinished()) {
            refreshRequest.cancel(true);
        }
        super.onDetach();
    }

    @Override
    protected void onUserVisibleChanged(boolean isVisibleToUser) {
        if (applyBackgroundCallback != null && isVisibleToUser) {
            changeBackground(backgroundImageUri);
        }
    }

    private void changeBackground(String imageUri) {
        this.backgroundImageUri = imageUri;
        if (applyBackgroundCallback != null) {
            applyBackgroundCallback.onApplyBackground(backgroundImageUri);
        }
    }

    @Override
    public void onItemClick(int position, StarImageRequest.Image image, String loadingImageOptionsInfo) {
        List<StarImageRequest.Image> imageList = adapter.getDataList();
        ArrayList urlList = new ArrayList<String>();
        for (StarImageRequest.Image imageItem : imageList) {
            urlList.add(imageItem.getSourceUrl());
        }
        DetailActivity.launch(getActivity(), urlList, loadingImageOptionsInfo, position - adapter.getHeaderItemCount());
    }

    private void setAdapter(AssemblyRecyclerAdapter adapter) {
        recyclerView.setAdapter(adapter);
        recyclerView.scheduleLayoutAnimation();
        this.adapter = adapter;
    }

    @Override
    public void onRefresh() {
        if (refreshRequest != null && !refreshRequest.isFinished()) {
            return;
        }

        if (loadMoreRequest != null && !loadMoreRequest.isFinished()) {
            loadMoreRequest.cancel(true);
        }

        if (adapter != null) {
            adapter.setLoadMoreEnd(false);
        }

        refreshRequest = GoHttp.with(getActivity()).newRequest(starHomeUrl, new StringHttpResponseHandler(), new HttpRequest.Listener<StarHomeBackgroundRequest.Background>() {
            @Override
            public void onStarted(HttpRequest httpRequest) {
                hintView.hidden();
            }

            @Override
            public void onCompleted(HttpRequest httpRequest, HttpResponse httpResponse, StarHomeBackgroundRequest.Background backgroundObject, boolean b, boolean b2) {
                if (getActivity() == null) {
                    return;
                }

                loadItems(backgroundObject.getBackgroundImageUrl());
            }

            @Override
            public void onFailed(HttpRequest httpRequest, HttpResponse httpResponse, HttpRequest.Failure failure, boolean b, boolean b2) {
                if (getActivity() == null) {
                    return;
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullRefreshLayout.stopRefresh();
                    }
                }, 500);
                if (adapter == null) {
                    hintView.failed(failure, new View.OnClickListener() {
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
        refreshRequest = GoHttp.with(getActivity()).newRequest(starImageRequest, new JsonHttpResponseHandler(StarImageRequest.Response.class), new HttpRequest.Listener<StarImageRequest.Response>() {
            @Override
            public void onStarted(HttpRequest httpRequest) {
                hintView.hidden();
            }

            @Override
            public void onCompleted(HttpRequest httpRequest, HttpResponse httpResponse, StarImageRequest.Response responseObject, boolean b, boolean b2) {
                if (getActivity() == null) {
                    return;
                }

                AssemblyRecyclerAdapter adapter = new AssemblyRecyclerAdapter(responseObject.getImages());
                if (backgroundImageUrl != null) {
                    adapter.addHeaderItem(new StarHeaderItemFactory().fullSpan(recyclerView), backgroundImageUrl);
                }
                adapter.addItemFactory(new StaggeredImageItemFactory(StarHomeFragment.this));
                adapter.setLoadMoreItem(new LoadMoreItemFactory(StarHomeFragment.this).fullSpan(recyclerView));

                setAdapter(adapter);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullRefreshLayout.stopRefresh();
                    }
                }, 500);

                if (responseObject.getImages() != null && responseObject.getImages().size() > 0) {
                    changeBackground(responseObject.getImages().get(0).getSourceUrl());
                }
            }

            @Override
            public void onFailed(HttpRequest httpRequest, HttpResponse httpResponse, HttpRequest.Failure failure, boolean b, boolean b2) {
                if (getActivity() == null) {
                    return;
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullRefreshLayout.stopRefresh();
                    }
                }, 500);
                if (adapter == null) {
                    hintView.failed(failure, new View.OnClickListener() {
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
        }).responseHandleCompletedAfterListener(new StarImageRequest.ResponseHandler(getActivity().getBaseContext())).go();
    }

    @Override
    public void onLoadMore(AssemblyRecyclerAdapter assemblyRecyclerAdapter) {
        starImageRequest.setStart(adapter.getDataCount());
        loadMoreRequest = GoHttp.with(getActivity()).newRequest(starImageRequest, new JsonHttpResponseHandler(StarImageRequest.Response.class), new HttpRequest.Listener<StarImageRequest.Response>() {
            @Override
            public void onStarted(HttpRequest httpRequest) {

            }

            @Override
            public void onCompleted(HttpRequest httpRequest, HttpResponse httpResponse, StarImageRequest.Response responseObject, boolean b, boolean b2) {
                if (getActivity() == null) {
                    return;
                }

                List<StarImageRequest.Image> newImageList = responseObject.getImages();
                if (newImageList != null && newImageList.size() > 0) {
                    adapter.addAll(newImageList);
                    if (newImageList.size() < starImageRequest.getSize()) {
                        adapter.setLoadMoreEnd(true);
                        Toast.makeText(getActivity(), "新送达" + newImageList.size() + "个包裹，已全部送完！", Toast.LENGTH_SHORT).show();
                    } else {
                        adapter.setLoadMoreEnd(false);
                        Toast.makeText(getActivity(), "新送达" + newImageList.size() + "个包裹", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    adapter.setLoadMoreEnd(true);
                    Toast.makeText(getActivity(), "没有您的包裹了", Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(HttpRequest httpRequest, HttpResponse httpResponse, HttpRequest.Failure failure, boolean b, boolean b2) {
                if (getActivity() == null) {
                    return;
                }
                adapter.loadMoreFailed();
                Toast.makeText(getActivity(), "快递投递失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCanceled(HttpRequest httpRequest) {
                adapter.loadMoreFailed();
            }
        }).responseHandleCompletedAfterListener(new StarImageRequest.ResponseHandler(getActivity().getBaseContext())).go();
    }
}
