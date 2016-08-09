package me.xiaopan.sketchsample.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;

import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.List;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectExtra;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.gohttp.GoHttp;
import me.xiaopan.gohttp.HttpRequest;
import me.xiaopan.gohttp.HttpRequestFuture;
import me.xiaopan.gohttp.JsonHttpResponseHandler;
import me.xiaopan.gohttp.StringHttpResponseHandler;
import me.xiaopan.prl.PullRefreshLayout;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.OptionsType;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.activity.DetailActivity;
import me.xiaopan.sketchsample.activity.WindowBackgroundManager;
import me.xiaopan.sketchsample.adapter.ImageStaggeredGridAdapter;
import me.xiaopan.sketchsample.net.request.StarHomeBackgroundRequest;
import me.xiaopan.sketchsample.net.request.StarImageRequest;
import me.xiaopan.sketchsample.util.ScrollingPauseLoadManager;
import me.xiaopan.sketchsample.widget.HintView;
import me.xiaopan.sketchsample.widget.LoadMoreFooterView;
import me.xiaopan.sketchsample.widget.MyImageView;

/**
 * 明星个人页面
 */
@InjectContentView(R.layout.fragment_star_home)
public class StarHomeFragment extends MyFragment implements ImageStaggeredGridAdapter.OnItemClickListener, PullRefreshLayout.OnRefreshListener, LoadMoreFooterView.OnLoadMoreListener {
    public static final String PARAM_REQUIRED_STRING_STAR_TITLE = "PARAM_REQUIRED_STRING_STAR_TITLE";
    public static final String PARAM_REQUIRED_STRING_STAR_URL = "PARAM_REQUIRED_STRING_STAR_URL";

    @InjectView(R.id.refreshLayout_starHome)
    private PullRefreshLayout pullRefreshLayout;
    @InjectView(R.id.list_starHome)
    private StaggeredGridView staggeredGridView;
    @InjectView(R.id.hintView_starHome)
    private HintView hintView;

    @InjectExtra(PARAM_REQUIRED_STRING_STAR_TITLE)
    private String starTitle;
    @InjectExtra(PARAM_REQUIRED_STRING_STAR_URL)
    private String starHomeUrl;

    private StarImageRequest starImageRequest;
    private HttpRequestFuture refreshRequestFuture;
    private ImageStaggeredGridAdapter starImageAdapter;
    private WindowBackgroundManager.WindowBackgroundLoader windowBackgroundLoader;
    private LoadMoreFooterView loadMoreFooterView;
    private MyImageView headImageView;
    private HttpRequestFuture loadMoreRequestFuture;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity != null && activity instanceof WindowBackgroundManager.OnSetWindowBackgroundListener) {
            windowBackgroundLoader = new WindowBackgroundManager.WindowBackgroundLoader(activity.getBaseContext(), (WindowBackgroundManager.OnSetWindowBackgroundListener) activity);
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

        staggeredGridView.setOnScrollListener(new ScrollingPauseLoadManager(getActivity()));

        if (starImageAdapter == null) {
            pullRefreshLayout.startRefresh();
        } else {
            setAdapter(starImageAdapter);
            if (windowBackgroundLoader != null) {
                windowBackgroundLoader.restore();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        loadMoreFooterView = null;
        headImageView = null;
    }

    @Override
    public void onDetach() {
        if (refreshRequestFuture != null && !refreshRequestFuture.isFinished()) {
            refreshRequestFuture.cancel(true);
        }
        if (windowBackgroundLoader != null) {
            windowBackgroundLoader.detach();
        }
        super.onDetach();
    }

    @Override
    protected void onUserVisibleChanged(boolean isVisibleToUser) {
        if (windowBackgroundLoader != null) {
            windowBackgroundLoader.setUserVisible(isVisibleToUser);
        }
    }

    @Override
    public void onItemClick(int position, StarImageRequest.Image image) {
        DetailActivity.launch(getActivity(), (ArrayList<String>) starImageAdapter.getImageUrlList(), position);
    }

    private void setAdapter(ImageStaggeredGridAdapter adapter) {
        if (loadMoreFooterView == null) {
            loadMoreFooterView = new LoadMoreFooterView(getActivity());
            loadMoreFooterView.setOnLoadMoreListener(this);
            staggeredGridView.setOnGetFooterViewListener(loadMoreFooterView);
            staggeredGridView.addFooterView(loadMoreFooterView);
        }
        staggeredGridView.setAdapter(starImageAdapter = adapter);
        staggeredGridView.scheduleLayoutAnimation();
    }

    @Override
    public void onRefresh() {
        if (refreshRequestFuture != null && !refreshRequestFuture.isFinished()) {
            return;
        }

        if (loadMoreRequestFuture != null && !loadMoreRequestFuture.isFinished()) {
            loadMoreRequestFuture.cancel(true);
        }

        if (loadMoreFooterView != null) {
            loadMoreFooterView.setPause(true);
        }

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

                loadItems(backgroundObject.getBackgroundImageUrl());
            }

            @Override
            public void onFailed(HttpRequest httpRequest, HttpResponse httpResponse, HttpRequest.Failure failure, boolean b, boolean b2) {
                if (getActivity() == null) {
                    return;
                }

                if (loadMoreFooterView != null) {
                    loadMoreFooterView.setPause(false);
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullRefreshLayout.stopRefresh();
                    }
                }, 1000);
                if (starImageAdapter == null) {
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
                if (loadMoreFooterView != null) {
                    loadMoreFooterView.setPause(false);
                }
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

                if (backgroundImageUrl != null) {
                    if (headImageView == null) {
                        View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_heade_image, staggeredGridView, false);
                        headImageView = (MyImageView) view.findViewById(R.id.image_headImageItem);
                        headImageView.setOptionsByName(OptionsType.NORMAL_RECT);

                        ViewGroup.LayoutParams headerParams = headImageView.getLayoutParams();
                        headerParams.width = getActivity().getResources().getDisplayMetrics().widthPixels;
                        headerParams.height = (int) (headerParams.width / 3.2f);
                        headImageView.setLayoutParams(headerParams);

                        staggeredGridView.addHeaderView(headImageView);
                    }
                    headImageView.displayImage(backgroundImageUrl);
                }

                setAdapter(new ImageStaggeredGridAdapter(getActivity(), staggeredGridView, responseObject.getImages(), StarHomeFragment.this));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullRefreshLayout.stopRefresh();
                    }
                }, 1000);

                if (loadMoreFooterView != null) {
                    loadMoreFooterView.setPause(false);
                    if (loadMoreFooterView.isEnd()) {
                        loadMoreFooterView.setEnd(false);
                    }
                }

                if (windowBackgroundLoader != null && responseObject.getImages() != null && responseObject.getImages().size() > 0) {
                    windowBackgroundLoader.load(responseObject.getImages().get(0).getSourceUrl());
                }
            }

            @Override
            public void onFailed(HttpRequest httpRequest, HttpResponse httpResponse, HttpRequest.Failure failure, boolean b, boolean b2) {
                if (getActivity() == null) {
                    return;
                }

                if (loadMoreFooterView != null) {
                    loadMoreFooterView.setPause(false);
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullRefreshLayout.stopRefresh();
                    }
                }, 1000);
                if (starImageAdapter == null) {
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
                if (loadMoreFooterView != null) {
                    loadMoreFooterView.setPause(false);
                }
            }
        }).responseHandleCompletedAfterListener(new StarImageRequest.ResponseHandler()).go();
    }

    @Override
    public void onLoadMore(final LoadMoreFooterView loadMoreFooterView) {
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

                List<StarImageRequest.Image> newImageList = responseObject.getImages();
                if (newImageList != null && newImageList.size() > 0) {
                    starImageAdapter.append(newImageList);
                    if (newImageList.size() < starImageRequest.getSize()) {
                        loadMoreFooterView.setEnd(true);
                        Toast.makeText(getActivity(), "新送达" + newImageList.size() + "个包裹，已全部送完！", Toast.LENGTH_SHORT).show();
                    } else {
                        loadMoreFooterView.loadFinished(true);
                        Toast.makeText(getActivity(), "新送达" + newImageList.size() + "个包裹", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    loadMoreFooterView.setEnd(true);
                    Toast.makeText(getActivity(), "没有您的包裹了", Toast.LENGTH_SHORT).show();
                }
                starImageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(HttpRequest httpRequest, HttpResponse httpResponse, HttpRequest.Failure failure, boolean b, boolean b2) {
                if (getActivity() == null) {
                    return;
                }
                loadMoreFooterView.loadFinished(false);
                Toast.makeText(getActivity(), "快递投递失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCanceled(HttpRequest httpRequest) {
                loadMoreFooterView.loadFinished(false);
            }
        }).responseHandleCompletedAfterListener(new StarImageRequest.ResponseHandler()).go();
    }
}
