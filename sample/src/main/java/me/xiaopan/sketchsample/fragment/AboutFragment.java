package me.xiaopan.sketchsample.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import me.xiaopan.android.inject.InjectContentView;
import me.xiaopan.android.inject.InjectView;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;

/**
 * 关于Fragment
 */
@InjectContentView(R.layout.fragment_about)
public class AboutFragment extends MyFragment {
    @InjectView(R.id.text_about_gifIntro) TextView gifIntroTextView;
    private TogglePageListener togglePageListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(activity instanceof TogglePageListener){
            togglePageListener = (TogglePageListener) activity;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SpannableString string = new SpannableString("（点击查看示例）");
        string.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if(togglePageListener != null){
                    togglePageListener.onToggleToGifSample();
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(Color.parseColor("#0000ff"));
                super.updateDrawState(ds);
            }
        }, 0, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        gifIntroTextView.setMovementMethod(LinkMovementMethod.getInstance());
        gifIntroTextView.append(string);
    }

    public interface TogglePageListener{
        void onToggleToGifSample();
    }
}
