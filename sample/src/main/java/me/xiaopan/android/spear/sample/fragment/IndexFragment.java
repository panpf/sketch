package me.xiaopan.android.spear.sample.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import me.xiaopan.android.spear.sample.net.request.HomeRequest;
import me.xiaopan.android.spear.sample.widget.HintView;
import me.xiaopan.android.spear.widget.SpearImageView;
import me.xiaopan.android.widget.PullRefreshLayout;

/**
 * 百度图片首页
 */
@InjectContentView(R.layout.fragment_index)
public class IndexFragment extends InjectFragment implements PullRefreshLayout.OnRefreshListener{
    @InjectView(R.id.pullRefresh_index) private PullRefreshLayout pullRefreshLayout;
    @InjectView(R.id.hint_index) private HintView hintView;
    @InjectView(R.id.layout_index_content) private ViewGroup conetentViewGroup;
    private HttpRequestFuture indexRequestFuture;
    private HomeRequest.Home home;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pullRefreshLayout.setOnRefreshListener(this);

        if(home == null){
            pullRefreshLayout.startRefresh();
        }else{
            showContent(home);
        }
    }

    @Override
    public void onDetach() {
        if(indexRequestFuture != null && !indexRequestFuture.isFinished()){
            indexRequestFuture.cancel(true);
        }
        super.onDetach();
    }

    private void showContent(HomeRequest.Home home){
        Log.e("ghghkj", "hgkjgjhgj");
        conetentViewGroup.removeAllViews();
        int number = 0;
        for(HomeRequest.ImageCategory imageCategory : home.getImageCategories()){
            if((number++ % 2) == 0) {
                conetentViewGroup.addView(createFourCategoryItemView(imageCategory));
            }else {
                conetentViewGroup.addView(createFiveCategoryItemView(imageCategory));
            }
        }
    }

    private View createFourCategoryItemView(HomeRequest.ImageCategory imageCategory){
        View categoryItemView = LayoutInflater.from(getActivity()).inflate(R.layout.item_four, null);
        SpearImageView oneSpearImageView = (SpearImageView) categoryItemView.findViewById(R.id.spearImage_fourItem_one);
        SpearImageView twoSpearImageView = (SpearImageView) categoryItemView.findViewById(R.id.spearImage_fourItem_two);
        SpearImageView threeSpearImageView = (SpearImageView) categoryItemView.findViewById(R.id.spearImage_fourItem_three);
        SpearImageView fourSpearImageView = (SpearImageView) categoryItemView.findViewById(R.id.spearImage_fourItem_four);
        TextView categoryTitleTextView = (TextView) categoryItemView.findViewById(R.id.text_fourItem_categoryTitle);
        TextView oneNameTextView = (TextView) categoryItemView.findViewById(R.id.text_fourItem_name_one);
        TextView twoNameTextView = (TextView) categoryItemView.findViewById(R.id.text_fourItem_name_two);
        TextView threeNameTextView = (TextView) categoryItemView.findViewById(R.id.text_fourItem_name_three);
        TextView fourNameTextView = (TextView) categoryItemView.findViewById(R.id.text_fourItem_name_four);

        int marginBorder = (int) getResources().getDimension(R.dimen.home_category_margin_border);
        int averageWidth = (getResources().getDisplayMetrics().widthPixels - (marginBorder * 4))/5;

        setWidthAndHeight(oneSpearImageView, averageWidth * 2, averageWidth * 2);
        setWidthAndHeight(twoSpearImageView, averageWidth, ((averageWidth * 2) - marginBorder)/2);
        setWidthAndHeight(threeSpearImageView, averageWidth, ((averageWidth * 2) - marginBorder)/2);
        setWidthAndHeight(fourSpearImageView, averageWidth * 2, averageWidth * 2);

        setWidthAndHeight(oneNameTextView, averageWidth * 2, ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidthAndHeight(twoNameTextView, averageWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidthAndHeight(threeNameTextView, averageWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidthAndHeight(fourNameTextView, averageWidth * 2, ViewGroup.LayoutParams.WRAP_CONTENT);

        categoryTitleTextView.setText(imageCategory.getName());

        oneNameTextView.setText(imageCategory.getImageList().get(0).getTitle());
        twoNameTextView.setText(imageCategory.getImageList().get(1).getTitle());
        threeNameTextView.setText(imageCategory.getImageList().get(3).getTitle());
        fourNameTextView.setText(imageCategory.getImageList().get(2).getTitle());

        oneSpearImageView.setImageByUri(imageCategory.getImageList().get(0).getUrl());
        twoSpearImageView.setImageByUri(imageCategory.getImageList().get(1).getUrl());
        threeSpearImageView.setImageByUri(imageCategory.getImageList().get(3).getUrl());
        fourSpearImageView.setImageByUri(imageCategory.getImageList().get(2).getUrl());

        return categoryItemView;
    }

    private void setWidthAndHeight(View view, int width, int height){
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        view.setLayoutParams(layoutParams);
    }

    private View createFiveCategoryItemView(HomeRequest.ImageCategory imageCategory){
        View categoryItemView = LayoutInflater.from(getActivity()).inflate(R.layout.item_five, null);
        SpearImageView oneSpearImageView = (SpearImageView) categoryItemView.findViewById(R.id.spearImage_fiveItem_one);
        SpearImageView twoSpearImageView = (SpearImageView) categoryItemView.findViewById(R.id.spearImage_fiveItem_two);
        SpearImageView threeSpearImageView = (SpearImageView) categoryItemView.findViewById(R.id.spearImage_fiveItem_three);
        SpearImageView fourSpearImageView = (SpearImageView) categoryItemView.findViewById(R.id.spearImage_fiveItem_four);
        SpearImageView fiveSpearImageView = (SpearImageView) categoryItemView.findViewById(R.id.spearImage_fiveItem_five);
        TextView categoryTitleTextView = (TextView) categoryItemView.findViewById(R.id.text_fiveItem_categoryTitle);
        TextView oneNameTextView = (TextView) categoryItemView.findViewById(R.id.text_fiveItem_name_one);
        TextView twoNameTextView = (TextView) categoryItemView.findViewById(R.id.text_fiveItem_name_two);
        TextView threeNameTextView = (TextView) categoryItemView.findViewById(R.id.text_fiveItem_name_three);
        TextView fourNameTextView = (TextView) categoryItemView.findViewById(R.id.text_fiveItem_name_four);
        TextView fiveNameTextView = (TextView) categoryItemView.findViewById(R.id.text_fiveItem_name_five);

        int marginBorder = (int) getResources().getDimension(R.dimen.home_category_margin_border);
        int averageWidth = (getResources().getDisplayMetrics().widthPixels - (marginBorder * 4))/5;

        setWidthAndHeight(oneSpearImageView, averageWidth * 2, averageWidth * 2);
        setWidthAndHeight(twoSpearImageView, averageWidth * 2, ((averageWidth * 2) - marginBorder)/2);
        setWidthAndHeight(threeSpearImageView, averageWidth * 2, ((averageWidth * 2) - marginBorder)/2);
        setWidthAndHeight(fourSpearImageView, averageWidth, ((averageWidth * 2) - marginBorder)/2);
        setWidthAndHeight(fiveSpearImageView, averageWidth, ((averageWidth * 2) - marginBorder)/2);

        setWidthAndHeight(oneNameTextView, averageWidth * 2, ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidthAndHeight(twoNameTextView, averageWidth * 2, ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidthAndHeight(threeNameTextView, averageWidth * 2, ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidthAndHeight(fourNameTextView, averageWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidthAndHeight(fiveNameTextView, averageWidth, ViewGroup.LayoutParams.WRAP_CONTENT);

        categoryTitleTextView.setText(imageCategory.getName());

        oneNameTextView.setText(imageCategory.getImageList().get(0).getTitle());
        twoNameTextView.setText(imageCategory.getImageList().get(1).getTitle());
        threeNameTextView.setText(imageCategory.getImageList().get(4).getTitle());
        fourNameTextView.setText(imageCategory.getImageList().get(3).getTitle());
        fiveNameTextView.setText(imageCategory.getImageList().get(2).getTitle());

        oneSpearImageView.setImageByUri(imageCategory.getImageList().get(0).getUrl());
        twoSpearImageView.setImageByUri(imageCategory.getImageList().get(1).getUrl());
        threeSpearImageView.setImageByUri(imageCategory.getImageList().get(4).getUrl());
        fourSpearImageView.setImageByUri(imageCategory.getImageList().get(3).getUrl());
        fiveSpearImageView.setImageByUri(imageCategory.getImageList().get(2).getUrl());

        return categoryItemView;
    }

    @Override
    public void onRefresh() {
        if(indexRequestFuture != null && !indexRequestFuture.isFinished()){
            return;
        }

        indexRequestFuture = GoHttp.with(getActivity()).newRequest(new HomeRequest(), new StringHttpResponseHandler(), new HttpRequest.Listener<HomeRequest.Home>() {
            @Override
            public void onStarted(HttpRequest httpRequest) {
                hintView.hidden();
            }

            @Override
            public void onCompleted(HttpRequest httpRequest, HttpResponse httpResponse, HomeRequest.Home response, boolean b, boolean b2) {
                showContent(home = response);
                pullRefreshLayout.stopRefresh();
            }

            @Override
            public void onFailed(HttpRequest httpRequest, HttpResponse httpResponse, HttpRequest.Failure failure, boolean b, boolean b2) {
                pullRefreshLayout.stopRefresh();
                if (home == null) {
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
        }).responseHandleCompletedAfterListener(new HomeRequest.HomeRequestResponseHandle()).go();
    }
}
