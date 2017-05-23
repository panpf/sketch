package me.xiaopan.sketchsample.bean;

import android.content.Context;
import android.view.View;

import me.xiaopan.assemblyadapter.AssemblyRecyclerAdapter;
import me.xiaopan.sketchsample.util.AppConfig;

public class CheckMenu {
    public String title;
    private AppConfig.Key key;
    private OnCheckedChangedListener onCheckedChangedListener;
    private View.OnClickListener onClickListener;

    private Context context;

    public CheckMenu(Context context, String title, AppConfig.Key key,
                     OnCheckedChangedListener onCheckedChangedListener, View.OnClickListener onClickListener) {
        this.context = context;
        this.title = title;
        this.key = key;
        this.onCheckedChangedListener = onCheckedChangedListener;
        this.onClickListener = onClickListener;
    }

    public boolean isChecked() {
        return AppConfig.getBoolean(context, key);
    }

    public void onClick(AssemblyRecyclerAdapter adapter) {
        boolean newChecked = !isChecked();

        if (onCheckedChangedListener != null) {
            onCheckedChangedListener.onCheckedChangedBefore(newChecked);
        }

        AppConfig.putBoolean(context, key, newChecked);
        adapter.notifyDataSetChanged();

        if (onCheckedChangedListener != null) {
            onCheckedChangedListener.onCheckedChanged(newChecked);
        }
        if (onClickListener != null) {
            onClickListener.onClick(null);
        }
    }

    public interface OnCheckedChangedListener {
        void onCheckedChangedBefore(boolean checked);

        void onCheckedChanged(boolean checked);
    }
}
