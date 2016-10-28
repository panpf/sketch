package me.xiaopan.sketchsample.adapter.itemfactory;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import me.xiaopan.assemblyadapter.AssemblyRecyclerItemFactory;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.activity.MainActivity;
import me.xiaopan.sketchsample.adapter.BindAssemblyRecyclerItem;

public class PageMenuItemFactory extends AssemblyRecyclerItemFactory<PageMenuItemFactory.PageMenuItem> {
    private OnClickItemListener onClickItemListener;

    public PageMenuItemFactory(OnClickItemListener onClickItemListener) {
        this.onClickItemListener = onClickItemListener;
    }

    @Override
    public boolean isTarget(Object o) {
        return o instanceof MainActivity.Page;
    }

    @Override
    public PageMenuItem createAssemblyItem(ViewGroup viewGroup) {
        return new PageMenuItem(R.layout.list_item_page_menu, viewGroup);
    }

    public interface OnClickItemListener {
        void onClickItem(MainActivity.Page page);
    }

    public class PageMenuItem extends BindAssemblyRecyclerItem<MainActivity.Page> {
        @BindView(R.id.text_pageMenuItem)
        TextView textView;

        public PageMenuItem(int itemLayoutId, ViewGroup parent) {
            super(itemLayoutId, parent);
        }

        @OnClick(R.id.text_pageMenuItem)
        void clickItem() {
            onClickItemListener.onClickItem(getData());
        }

        @Override
        protected void onConfigViews(Context context) {

        }

        @Override
        protected void onSetData(int i, MainActivity.Page pageMenu) {
            textView.setText(pageMenu.getName());
        }
    }
}
