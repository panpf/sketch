package me.xiaopan.android.spear.sample.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import me.xiaoapn.android.spear.sample.R;
import me.xiaopan.android.spear.sample.DisplayOptionsType;
import me.xiaopan.android.spear.sample.net.request.IndexRequest;
import me.xiaopan.android.spear.widget.SpearImageView;

/**
 * 首页的图片分类适配器
 */
public class IndexCategoryAdapter extends BaseAdapter{
    private Context context;
    private List<IndexRequest.ImageCategory> imageCategoryList;
    private View.OnClickListener imageClickListener;
    private View.OnClickListener categoryTitleClickListener;

    public IndexCategoryAdapter(Context context, final List<IndexRequest.ImageCategory> imageCategoryList, final OnClickListener onClickListener){
        this.context = context;
        this.imageCategoryList = imageCategoryList;
        this.imageClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int categoryPosition = (Integer) v.getTag(R.id.tagId_categoryPosition);
                if(categoryPosition < imageCategoryList.size() && onClickListener != null){
                    IndexRequest.ImageCategory imageCategory = imageCategoryList.get(categoryPosition);
                    int imagePosition = (Integer) v.getTag(R.id.tagId_imagePosition);
                    if(imagePosition < imageCategory.getImageList().size()){
                        onClickListener.onClickImage(imageCategory, imageCategory.getImageList().get(imagePosition));
                    }
                }
            }
        };

        this.categoryTitleClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int categoryPosition = (Integer) v.getTag(R.id.tagId_categoryPosition);
                if(categoryPosition < imageCategoryList.size() && onClickListener != null){
                    onClickListener.onClickCategoryTitle(imageCategoryList.get(categoryPosition));
                }
            }
        };
    }

    @Override
    public int getCount() {
        return imageCategoryList != null ? imageCategoryList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return imageCategoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position%2==0?0:1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        switch(getItemViewType(position)){
            case 0 :
                FourViewHolder fourViewHolder;
                if(convertView == null){
                    convertView = LayoutInflater.from(context).inflate(R.layout.list_item_category_four, parent, false);
                    fourViewHolder = new FourViewHolder(convertView, this);
                    convertView.setTag(fourViewHolder);
                }else{
                    fourViewHolder = (FourViewHolder) convertView.getTag();
                }

                fourViewHolder.bindData(position);
                break;
            case 1 :
                FiveViewHolder fiveViewHolder;
                if(convertView == null){
                    convertView = LayoutInflater.from(context).inflate(R.layout.list_item_category_five, parent, false);
                    fiveViewHolder = new FiveViewHolder(convertView, this);
                    convertView.setTag(fiveViewHolder);
                }else{
                    fiveViewHolder = (FiveViewHolder) convertView.getTag();
                }

                fiveViewHolder.bindData(position);
                break;
        }

        return convertView;
    }

    private static class FourViewHolder{
        private View categoryTitleLayout;
        private FrameLayout oneCardView;
        private FrameLayout twoCardView;
        private FrameLayout threeCardView;
        private FrameLayout fourCardView;
        private SpearImageView oneSpearImageView;
        private SpearImageView twoSpearImageView;
        private SpearImageView threeSpearImageView;
        private SpearImageView fourSpearImageView;
        private TextView categoryTitleTextView;
        private TextView oneNameTextView;
        private TextView twoNameTextView;
        private TextView threeNameTextView;
        private TextView fourNameTextView;
        private IndexCategoryAdapter adapter;

        public FourViewHolder(View itemView, final IndexCategoryAdapter adapter) {
            this.adapter = adapter;

            categoryTitleLayout = itemView.findViewById(R.id.layout_fourItem_title);
            oneCardView = (FrameLayout) itemView.findViewById(R.id.card_fourItem_one);
            twoCardView = (FrameLayout) itemView.findViewById(R.id.card_fourItem_two);
            threeCardView = (FrameLayout) itemView.findViewById(R.id.card_fourItem_three);
            fourCardView = (FrameLayout) itemView.findViewById(R.id.card_fourItem_four);
            oneSpearImageView = (SpearImageView) itemView.findViewById(R.id.spearImage_fourItem_one);
            twoSpearImageView = (SpearImageView) itemView.findViewById(R.id.spearImage_fourItem_two);
            threeSpearImageView = (SpearImageView) itemView.findViewById(R.id.spearImage_fourItem_three);
            fourSpearImageView = (SpearImageView) itemView.findViewById(R.id.spearImage_fourItem_four);
            categoryTitleTextView = (TextView) itemView.findViewById(R.id.text_fourItem_categoryTitle);
            oneNameTextView = (TextView) itemView.findViewById(R.id.text_fourItem_name_one);
            twoNameTextView = (TextView) itemView.findViewById(R.id.text_fourItem_name_two);
            threeNameTextView = (TextView) itemView.findViewById(R.id.text_fourItem_name_three);
            fourNameTextView = (TextView) itemView.findViewById(R.id.text_fourItem_name_four);

            oneSpearImageView.setDisplayOptions(DisplayOptionsType.INDEX_CATEGORY_ONE);
            twoSpearImageView.setDisplayOptions(DisplayOptionsType.INDEX_CATEGORY_TWO);
            threeSpearImageView.setDisplayOptions(DisplayOptionsType.INDEX_CATEGORY_TWO);
            fourSpearImageView.setDisplayOptions(DisplayOptionsType.INDEX_CATEGORY_ONE);

            int marginBorder = (int) adapter.context.getResources().getDimension(R.dimen.home_category_margin_border);
            int averageWidth = (adapter.context.getResources().getDisplayMetrics().widthPixels - (marginBorder * 4))/5;

            setWidthAndHeight(oneSpearImageView, averageWidth * 2, averageWidth * 2);
            setWidthAndHeight(twoSpearImageView, averageWidth, ((averageWidth * 2) - marginBorder)/2);
            setWidthAndHeight(threeSpearImageView, averageWidth, ((averageWidth * 2) - marginBorder)/2);
            setWidthAndHeight(fourSpearImageView, averageWidth * 2, averageWidth * 2);

            setWidthAndHeight(oneNameTextView, averageWidth * 2, ViewGroup.LayoutParams.WRAP_CONTENT);
            setWidthAndHeight(twoNameTextView, averageWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            setWidthAndHeight(threeNameTextView, averageWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            setWidthAndHeight(fourNameTextView, averageWidth * 2, ViewGroup.LayoutParams.WRAP_CONTENT);

            categoryTitleLayout.setOnClickListener(adapter.categoryTitleClickListener);
            oneCardView.setOnClickListener(adapter.imageClickListener);
            twoCardView.setOnClickListener(adapter.imageClickListener);
            threeCardView.setOnClickListener(adapter.imageClickListener);
            fourCardView.setOnClickListener(adapter.imageClickListener);
        }

        public void bindData(int position){
            IndexRequest.ImageCategory imageCategory = adapter.imageCategoryList.get(position);

            categoryTitleLayout.setTag(R.id.tagId_categoryPosition, position);

            categoryTitleTextView.setText(imageCategory.getName());

            if(imageCategory.getImageList()!=null && imageCategory.getImageList().size() > 0){
                oneNameTextView.setText(imageCategory.getImageList().get(0).getTitle());
                oneSpearImageView.setImageByUri(imageCategory.getImageList().get(0).getUrl());
                oneCardView.setVisibility(View.VISIBLE);
            }else{
                oneCardView.setVisibility(View.INVISIBLE);
            }
            oneCardView.setTag(R.id.tagId_categoryPosition, position);
            oneCardView.setTag(R.id.tagId_imagePosition, 0);

            if(imageCategory.getImageList()!=null && imageCategory.getImageList().size() > 1){
                twoNameTextView.setText(imageCategory.getImageList().get(1).getTitle());
                twoSpearImageView.setImageByUri(imageCategory.getImageList().get(1).getUrl());
                twoCardView.setVisibility(View.VISIBLE);
            }else{
                twoCardView.setVisibility(View.INVISIBLE);
            }
            twoCardView.setTag(R.id.tagId_categoryPosition, position);
            twoCardView.setTag(R.id.tagId_imagePosition, 1);

            if(imageCategory.getImageList()!=null && imageCategory.getImageList().size() > 3){
                threeNameTextView.setText(imageCategory.getImageList().get(3).getTitle());
                threeSpearImageView.setImageByUri(imageCategory.getImageList().get(3).getUrl());
                threeCardView.setVisibility(View.VISIBLE);
            }else{
                threeCardView.setVisibility(View.INVISIBLE);
            }
            threeCardView.setTag(R.id.tagId_categoryPosition, position);
            threeCardView.setTag(R.id.tagId_imagePosition, 3);

            if(imageCategory.getImageList()!=null && imageCategory.getImageList().size() > 2){
                fourNameTextView.setText(imageCategory.getImageList().get(2).getTitle());
                fourSpearImageView.setImageByUri(imageCategory.getImageList().get(2).getUrl());
                fourCardView.setVisibility(View.VISIBLE);
            }else{
                fourCardView.setVisibility(View.INVISIBLE);
            }
            fourCardView.setTag(R.id.tagId_categoryPosition, position);
            fourCardView.setTag(R.id.tagId_imagePosition, 2);
        }
    }

    private static class FiveViewHolder{
        private View categoryTitleLayout;
        private FrameLayout oneCardView;
        private FrameLayout twoCardView;
        private FrameLayout threeCardView;
        private FrameLayout fourCardView;
        private FrameLayout fiveCardView;
        private SpearImageView oneSpearImageView;
        private SpearImageView twoSpearImageView;
        private SpearImageView threeSpearImageView;
        private SpearImageView fourSpearImageView;
        private SpearImageView fiveSpearImageView;
        private TextView categoryTitleTextView;
        private TextView oneNameTextView;
        private TextView twoNameTextView;
        private TextView threeNameTextView;
        private TextView fourNameTextView;
        private TextView fiveNameTextView;
        private IndexCategoryAdapter adapter;

        public FiveViewHolder(View itemView, final IndexCategoryAdapter adapter) {
            this.adapter = adapter;

            categoryTitleLayout = itemView.findViewById(R.id.layout_fiveItem_title);
            oneCardView = (FrameLayout) itemView.findViewById(R.id.card_fiveItem_one);
            twoCardView = (FrameLayout) itemView.findViewById(R.id.card_fiveItem_two);
            threeCardView = (FrameLayout) itemView.findViewById(R.id.card_fiveItem_three);
            fourCardView = (FrameLayout) itemView.findViewById(R.id.card_fiveItem_four);
            fiveCardView = (FrameLayout) itemView.findViewById(R.id.card_fiveItem_five);
            oneSpearImageView = (SpearImageView) itemView.findViewById(R.id.spearImage_fiveItem_one);
            twoSpearImageView = (SpearImageView) itemView.findViewById(R.id.spearImage_fiveItem_two);
            threeSpearImageView = (SpearImageView) itemView.findViewById(R.id.spearImage_fiveItem_three);
            fourSpearImageView = (SpearImageView) itemView.findViewById(R.id.spearImage_fiveItem_four);
            fiveSpearImageView = (SpearImageView) itemView.findViewById(R.id.spearImage_fiveItem_five);
            categoryTitleTextView = (TextView) itemView.findViewById(R.id.text_fiveItem_categoryTitle);
            oneNameTextView = (TextView) itemView.findViewById(R.id.text_fiveItem_name_one);
            twoNameTextView = (TextView) itemView.findViewById(R.id.text_fiveItem_name_two);
            threeNameTextView = (TextView) itemView.findViewById(R.id.text_fiveItem_name_three);
            fourNameTextView = (TextView) itemView.findViewById(R.id.text_fiveItem_name_four);
            fiveNameTextView = (TextView) itemView.findViewById(R.id.text_fiveItem_name_five);

            oneSpearImageView.setDisplayOptions(DisplayOptionsType.INDEX_CATEGORY_ONE);
            twoSpearImageView.setDisplayOptions(DisplayOptionsType.INDEX_CATEGORY_THREE);
            threeSpearImageView.setDisplayOptions(DisplayOptionsType.INDEX_CATEGORY_THREE);
            fourSpearImageView.setDisplayOptions(DisplayOptionsType.INDEX_CATEGORY_TWO);
            fiveSpearImageView.setDisplayOptions(DisplayOptionsType.INDEX_CATEGORY_TWO);

            oneSpearImageView.setShowProgress(true);
            twoSpearImageView.setShowProgress(true);
            threeSpearImageView.setShowProgress(true);
            fourSpearImageView.setShowProgress(true);
            fiveSpearImageView.setShowProgress(true);

            int marginBorder = (int) adapter.context.getResources().getDimension(R.dimen.home_category_margin_border);
            int averageWidth = (adapter.context.getResources().getDisplayMetrics().widthPixels - (marginBorder * 4))/5;

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

            categoryTitleLayout.setOnClickListener(adapter.categoryTitleClickListener);
            oneCardView.setOnClickListener(adapter.imageClickListener);
            twoCardView.setOnClickListener(adapter.imageClickListener);
            threeCardView.setOnClickListener(adapter.imageClickListener);
            fourCardView.setOnClickListener(adapter.imageClickListener);
            fiveCardView.setOnClickListener(adapter.imageClickListener);
        }

        public void bindData(int position){
            IndexRequest.ImageCategory imageCategory = adapter.imageCategoryList.get(position);

            categoryTitleLayout.setTag(R.id.tagId_categoryPosition, position);

            categoryTitleTextView.setText(imageCategory.getName());

            if(imageCategory.getImageList()!=null && imageCategory.getImageList().size() > 0){
                oneNameTextView.setText(imageCategory.getImageList().get(0).getTitle());
                oneSpearImageView.setImageByUri(imageCategory.getImageList().get(0).getUrl());
                oneCardView.setVisibility(View.VISIBLE);
            }else{
                oneCardView.setVisibility(View.INVISIBLE);
            }
            oneCardView.setTag(R.id.tagId_categoryPosition, position);
            oneCardView.setTag(R.id.tagId_imagePosition, 0);

            if(imageCategory.getImageList()!=null && imageCategory.getImageList().size() > 1){
                twoNameTextView.setText(imageCategory.getImageList().get(1).getTitle());
                twoSpearImageView.setImageByUri(imageCategory.getImageList().get(1).getUrl());
                twoCardView.setVisibility(View.VISIBLE);
            }else{
                twoCardView.setVisibility(View.INVISIBLE);
            }
            twoCardView.setTag(R.id.tagId_categoryPosition, position);
            twoCardView.setTag(R.id.tagId_imagePosition, 1);

            if(imageCategory.getImageList()!=null && imageCategory.getImageList().size() > 4){
                threeNameTextView.setText(imageCategory.getImageList().get(4).getTitle());
                threeSpearImageView.setImageByUri(imageCategory.getImageList().get(4).getUrl());
                threeCardView.setVisibility(View.VISIBLE);
            }else{
                threeCardView.setVisibility(View.INVISIBLE);
            }
            threeCardView.setTag(R.id.tagId_categoryPosition, position);
            threeCardView.setTag(R.id.tagId_imagePosition, 4);

            if(imageCategory.getImageList()!=null && imageCategory.getImageList().size() > 3){
                fourNameTextView.setText(imageCategory.getImageList().get(3).getTitle());
                fourSpearImageView.setImageByUri(imageCategory.getImageList().get(3).getUrl());
                fourCardView.setVisibility(View.VISIBLE);
            }else{
                fourCardView.setVisibility(View.INVISIBLE);
            }
            fourCardView.setTag(R.id.tagId_categoryPosition, position);
            fourCardView.setTag(R.id.tagId_imagePosition, 3);

            if(imageCategory.getImageList()!=null && imageCategory.getImageList().size() > 2){
                fiveNameTextView.setText(imageCategory.getImageList().get(2).getTitle());
                fiveSpearImageView.setImageByUri(imageCategory.getImageList().get(2).getUrl());
                fiveCardView.setVisibility(View.VISIBLE);
            }else{
                fiveCardView.setVisibility(View.INVISIBLE);
            }
            fiveCardView.setTag(R.id.tagId_categoryPosition, position);
            fiveCardView.setTag(R.id.tagId_imagePosition, 2);
        }
    }

    private static void setWidthAndHeight(View view, int width, int height){
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        view.setLayoutParams(layoutParams);
    }

    public interface OnClickListener {
        public void onClickImage(IndexRequest.ImageCategory imageCategory, IndexRequest.Image image);
        public void onClickCategoryTitle(IndexRequest.ImageCategory imageCategory);
    }
}
