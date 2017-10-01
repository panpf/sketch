package me.xiaopan.sketchsample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import me.xiaopan.sketchsample.util.DataTransferStation;

public class BaseFragment extends Fragment {
    private DataTransferStation.PageHelper dataTransferHelper = new DataTransferStation.PageHelper(this);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataTransferHelper.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        BindContentView bindContentView = getClass().getAnnotation(BindContentView.class);
        if (bindContentView != null && bindContentView.value() > 0) {
            return inflater.inflate(bindContentView.value(), container, false);
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint()) {
            onUserVisibleChanged(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            onUserVisibleChanged(true);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isResumed()) {
            onUserVisibleChanged(isVisibleToUser);
        }
    }

    protected void onUserVisibleChanged(boolean isVisibleToUser) {

    }

    public boolean isVisibleToUser() {
        return isResumed() && getUserVisibleHint();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        dataTransferHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dataTransferHelper.onDestroy();
    }

    public DataTransferStation.PageHelper getDataTransferHelper() {
        return dataTransferHelper;
    }
}