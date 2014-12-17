package me.xiaopan.android.spear.sample.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;

import me.xiaoapn.android.spear.sample.R;
import me.xiaopan.android.gohttp.GoHttp;
import me.xiaopan.android.gohttp.HttpRequest;
import me.xiaopan.android.gohttp.HttpRequestFuture;
import me.xiaopan.android.gohttp.StringHttpResponseHandler;
import me.xiaopan.android.inject.InjectContentView;
import me.xiaopan.android.inject.InjectView;
import me.xiaopan.android.inject.app.InjectFragment;
import me.xiaopan.android.spear.sample.activity.StarActivity;
import me.xiaopan.android.spear.sample.activity.StarHomeActivity;
import me.xiaopan.android.spear.sample.adapter.IndexCategoryAdapter;
import me.xiaopan.android.spear.sample.net.request.IndexRequest;
import me.xiaopan.android.spear.sample.widget.HintView;
import me.xiaopan.android.widget.PullRefreshLayout;

/**
 * 百度图片首页
 */
@InjectContentView(R.layout.fragment_index)
public class IndexFragment extends InjectFragment implements PullRefreshLayout.OnRefreshListener, IndexCategoryAdapter.OnClickListener {
    @InjectView(R.id.refreshLayout_index)
    private PullRefreshLayout pullRefreshLayout;
    @InjectView(R.id.hint_index)
    private HintView hintView;
    @InjectView(R.id.list_index_content)
    private ListView contentListView;
    private HttpRequestFuture indexRequestFuture;
    private IndexRequest.Response response;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pullRefreshLayout.setOnRefreshListener(this);

        if (response == null) {
            pullRefreshLayout.startRefresh();
        } else {
            showContent(response);
        }
    }

    @Override
    public void onDetach() {
        if (indexRequestFuture != null && !indexRequestFuture.isFinished()) {
            indexRequestFuture.cancel(true);
        }
        super.onDetach();
    }

    private void showContent(IndexRequest.Response response) {
        contentListView.setAdapter(new IndexCategoryAdapter(getActivity(), response.getImageCategories(), this));
    }

    @Override
    public void onRefresh() {
        if (indexRequestFuture != null && !indexRequestFuture.isFinished()) {
            return;
        }

        indexRequestFuture = GoHttp.with(getActivity()).newRequest(new IndexRequest(), new StringHttpResponseHandler(), new HttpRequest.Listener<IndexRequest.Response>() {
            @Override
            public void onStarted(HttpRequest httpRequest) {
                hintView.hidden();
            }

            @Override
            public void onCompleted(HttpRequest httpRequest, HttpResponse httpResponse, IndexRequest.Response response, boolean b, boolean b2) {
                if (getActivity() == null) {
                    return;
                }

                showContent(IndexFragment.this.response = response);
                pullRefreshLayout.stopRefresh();
            }

            @Override
            public void onFailed(HttpRequest httpRequest, HttpResponse httpResponse, HttpRequest.Failure failure, boolean b, boolean b2) {
                if (getActivity() == null) {
                    return;
                }

                pullRefreshLayout.stopRefresh();
                if (response == null) {
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
        }).responseHandleCompletedAfterListener(new IndexRequest.ResponseHandler()).go();
    }

    @Override
    public void onClickImage(IndexRequest.ImageCategory imageCategory, IndexRequest.Image image) {
        Intent intent = null;
        if ("明星".equals(imageCategory.getName())) {
            intent = new Intent(getActivity(), StarHomeActivity.class);
            intent.putExtra(StarHomeFragment.PARAM_REQUIRED_STRING_STAR_TITLE, image.getTitle());
            intent.putExtra(StarHomeFragment.PARAM_REQUIRED_STRING_STAR_URL, image.getLink());
        }

        if (intent != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onClickCategoryTitle(IndexRequest.ImageCategory imageCategory) {
        Intent intent = null;
        if ("明星".equals(imageCategory.getName())) {
            intent = new Intent(getActivity(), StarActivity.class);
        }

        if (intent != null) {
            startActivity(intent);
        }
    }
}
