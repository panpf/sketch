package me.xiaopan.sketchsample;

import me.xiaopan.android.inject.app.InjectFragment;

public class MyFragment extends InjectFragment {

    @Override
    public void onPause() {
        super.onPause();
        if(getUserVisibleHint()){
            onUserVisibleChanged(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getUserVisibleHint()){
            onUserVisibleChanged(true);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isResumed()){
            onUserVisibleChanged(isVisibleToUser);
        }
    }

    protected void onUserVisibleChanged(boolean isVisibleToUser){

    }
}