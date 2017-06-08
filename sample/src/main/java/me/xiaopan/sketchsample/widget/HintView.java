package me.xiaopan.sketchsample.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.FileNotFoundException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import me.xiaopan.sketchsample.R;

/**
 * 提示视图
 */
public class HintView extends LinearLayout {
    private Button actionButton;
    private TextView loadingHintTextView;
    private TextView hintTextView;
    private TextView progressTextView;
    private ViewSwitcher viewSwitcher;
    private Mode mode;

    public HintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HintView(Context context) {
        super(context);
        init();
    }

    public static boolean isEmpty(String string) {
        return (string == null) || ("".equals(string.trim()));
    }

    public static boolean isNotEmpty(String string) {
        return !isEmpty(string);
    }

    /**
     * 将给定视图渐渐隐去最后从界面中移除（view.setVisibility(View.GONE)）
     *
     * @param view              被处理的视图
     * @param durationMillis    持续时间，毫秒
     * @param isBanClick        在执行动画的过程中是否禁止点击
     * @param animationListener 动画监听器
     */
    public static void goneViewByAlpha(final View view, long durationMillis, final boolean isBanClick, final AnimationListener animationListener) {
        if (view.getVisibility() != View.GONE) {
            view.setVisibility(View.GONE);
            AlphaAnimation hiddenAlphaAnimation = getHiddenAlphaAnimation(durationMillis);
            hiddenAlphaAnimation.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    if (isBanClick) {
                        view.setClickable(false);
                    }
                    if (animationListener != null) {
                        animationListener.onAnimationStart(animation);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    if (animationListener != null) {
                        animationListener.onAnimationRepeat(animation);
                    }
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (isBanClick) {
                        view.setClickable(true);
                    }
                    if (animationListener != null) {
                        animationListener.onAnimationEnd(animation);
                    }
                }
            });
            view.startAnimation(hiddenAlphaAnimation);
        }
    }

    /**
     * 获取一个由完全显示变为不可见的透明度渐变动画
     *
     * @param durationMillis 持续时间
     * @return 一个由完全显示变为不可见的透明度渐变动画
     */
    public static AlphaAnimation getHiddenAlphaAnimation(long durationMillis) {
        return getHiddenAlphaAnimation(durationMillis, null);
    }

    /**
     * 获取一个由完全显示变为不可见的透明度渐变动画
     *
     * @param durationMillis    持续时间
     * @param animationListener 动画监听器
     * @return 一个由完全显示变为不可见的透明度渐变动画
     */
    public static AlphaAnimation getHiddenAlphaAnimation(long durationMillis, AnimationListener animationListener) {
        return getAlphaAnimation(1.0f, 0.0f, durationMillis, animationListener);
    }

    /**
     * 获取一个透明度渐变动画
     *
     * @param fromAlpha         开始时的透明度
     * @param toAlpha           结束时的透明度都
     * @param durationMillis    持续时间
     * @param animationListener 动画监听器
     * @return 一个透明度渐变动画
     */
    public static AlphaAnimation getAlphaAnimation(float fromAlpha, float toAlpha, long durationMillis, AnimationListener animationListener) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(fromAlpha, toAlpha);
        alphaAnimation.setDuration(durationMillis);
        if (animationListener != null) {
            alphaAnimation.setAnimationListener(animationListener);
        }
        return alphaAnimation;
    }

    /**
     * 将给定视图渐渐隐去最后从界面中移除（view.setVisibility(View.GONE)），默认的持续时间为DEFAULT_ALPHA_ANIMATION_DURATION
     *
     * @param view       被处理的视图
     * @param isBanClick 在执行动画的过程中是否禁止点击
     */
    public static void goneViewByAlpha(final View view, final boolean isBanClick) {
        goneViewByAlpha(view, 400, isBanClick, null);
    }

    /**
     * 将给定视图渐渐显示出来（view.setVisibility(View.VISIBLE)），默认的持续时间为DEFAULT_ALPHA_ANIMATION_DURATION
     *
     * @param view       被处理的视图
     * @param isBanClick 在执行动画的过程中是否禁止点击
     */
    public static void visibleViewByAlpha(final View view, final boolean isBanClick) {
        visibleViewByAlpha(view, 400, isBanClick, null);
    }

    /**
     * 将给定视图渐渐显示出来（view.setVisibility(View.VISIBLE)）
     *
     * @param view              被处理的视图
     * @param durationMillis    持续时间，毫秒
     * @param isBanClick        在执行动画的过程中是否禁止点击
     * @param animationListener 动画监听器
     */
    public static void visibleViewByAlpha(final View view, long durationMillis, final boolean isBanClick, final AnimationListener animationListener) {
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
            AlphaAnimation showAlphaAnimation = getShowAlphaAnimation(durationMillis);
            showAlphaAnimation.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    if (isBanClick) {
                        view.setClickable(false);
                    }
                    if (animationListener != null) {
                        animationListener.onAnimationStart(animation);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    if (animationListener != null) {
                        animationListener.onAnimationRepeat(animation);
                    }
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (isBanClick) {
                        view.setClickable(true);
                    }
                    if (animationListener != null) {
                        animationListener.onAnimationEnd(animation);
                    }
                }
            });
            view.startAnimation(showAlphaAnimation);
        }
    }

    /**
     * 获取一个由不可见变为完全显示的透明度渐变动画
     *
     * @param durationMillis 持续时间
     * @return 一个由不可见变为完全显示的透明度渐变动画
     */
    public static AlphaAnimation getShowAlphaAnimation(long durationMillis) {
        return getAlphaAnimation(0.0f, 1.0f, durationMillis, null);
    }

    public static boolean isConnectedByState(Context context) {
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED;
    }

    private void init() {
        try {
            LayoutInflater.from(getContext()).inflate(R.layout.view_hint, this);
            viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher_hint);
            loadingHintTextView = (TextView) findViewById(R.id.text_hint_loadingHint);
            hintTextView = (TextView) findViewById(R.id.text_hint_hint);
            actionButton = (Button) findViewById(R.id.button_hint_action);
            progressTextView = (TextView) findViewById(R.id.text_hint_progress);
            setVisibility(View.GONE);
        } catch (Throwable throwable) {

        }
    }

    /**
     * 显示加载中，将使用type格式化“正在加载%s，请稍后…”字符串
     */
    public void loading(String message) {
        loadingHintTextView.setText(message);
        loadingHintTextView.setVisibility(TextUtils.isEmpty(message) ? GONE : VISIBLE);
        setProgress(0, 0);

        if (mode != Mode.LOADING) {
            if (mode == Mode.HINT) {
                viewSwitcher.setInAnimation(getContext(), R.anim.slide_to_bottom_in);
                viewSwitcher.setOutAnimation(getContext(), R.anim.slide_to_bottom_out);
            } else {
                viewSwitcher.setInAnimation(null);
                viewSwitcher.setOutAnimation(null);
            }
            mode = Mode.LOADING;
            actionButton.setVisibility(View.INVISIBLE);
            viewSwitcher.setDisplayedChild(mode.index);
            setVisibility(View.VISIBLE);
        }
    }

    public void setProgress(int totalLength, int completedLength) {
        if (completedLength <= 0) {
            progressTextView.setText(null);
        } else {
            int ratio = (int) (((float) completedLength / totalLength) * 100);
            Log.e("setProgress", String.format("totalLength=%d, completedLength=%d. %d", totalLength, completedLength, ratio));
            progressTextView.setText(String.format("%d%%", ratio));
        }
    }

    /**
     * 显示提示
     *
     * @param iconId              图标ID，如果不想显示图标的话，此参数传-1即可
     * @param hintText            提示信息
     * @param buttonName          按钮的名称
     * @param buttonClickListener 按钮的按下事件
     * @param transparent         是否需要让提示视图变成透明的，透明的提示视图将不再拦截事件
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressWarnings("deprecation")
    public void hint(int iconId, String hintText, String buttonName, OnClickListener buttonClickListener, boolean transparent) {
        if (iconId > 0) {
            Drawable[] drawables = hintTextView.getCompoundDrawables();
            hintTextView.setCompoundDrawablesWithIntrinsicBounds(drawables[0], getResources().getDrawable(iconId), drawables[2], drawables[3]);
        } else {
            Drawable[] drawables = hintTextView.getCompoundDrawables();
            hintTextView.setCompoundDrawablesWithIntrinsicBounds(drawables[0], null, drawables[2], drawables[3]);
        }

        if (isNotEmpty(hintText)) {
            hintTextView.setText(hintText);
        } else {
            hintTextView.setText(null);
        }

        if (isNotEmpty(buttonName) && buttonClickListener != null) {
            actionButton.setText(buttonName);
            actionButton.setOnClickListener(buttonClickListener);
            visibleViewByAlpha(actionButton, true);
        } else {
            actionButton.setText(null);
            actionButton.setOnClickListener(null);
            actionButton.setVisibility(View.INVISIBLE);
        }

        if (transparent) {
//			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
//				setBackground(null);
//			}else{
//				setBackgroundDrawable(null);
//			}
            setClickable(false);
        } else {
//			setBackgroundColor(getResources().getColor(R.color.page_background));
            setClickable(true);
        }

        if (mode != Mode.HINT) {
            if (mode != null) {
                viewSwitcher.setInAnimation(getContext(), R.anim.slide_to_top_in);
                viewSwitcher.setOutAnimation(getContext(), R.anim.slide_to_top_out);
            } else {
                viewSwitcher.setInAnimation(null);
                viewSwitcher.setOutAnimation(null);
            }
            mode = Mode.HINT;
            viewSwitcher.setDisplayedChild(1);
            setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示提示
     *
     * @param iconId              图标ID，如果不想显示图标的话，此参数传-1即可
     * @param hintText            提示信息
     * @param buttonName          按钮的名称
     * @param buttonClickListener 按钮的按下事件
     */
    public void hint(int iconId, String hintText, String buttonName, OnClickListener buttonClickListener) {
        hint(iconId, hintText, buttonName, buttonClickListener, false);
    }

    /**
     * 显示提示
     *
     * @param iconId   图标ID，如果不想显示图标的话，此参数传-1即可
     * @param hintText 提示信息
     */
    public void hint(int iconId, String hintText) {
        hint(iconId, hintText, null, null, false);
    }

    /**
     * 显示提示
     *
     * @param hintText            提示信息
     * @param buttonName          按钮的名称
     * @param buttonClickListener 按钮的按下事件
     * @param transparent         是否需要让提示视图变成透明的，透明的提示视图将不再拦截事件
     */
    public void hint(String hintText, String buttonName, OnClickListener buttonClickListener, boolean transparent) {
        hint(-1, hintText, buttonName, buttonClickListener, transparent);
    }

    /**
     * 显示提示
     *
     * @param hintText            提示信息
     * @param buttonName          按钮的名称
     * @param buttonClickListener 按钮的按下事件
     */
    public void hint(String hintText, String buttonName, OnClickListener buttonClickListener) {
        hint(-1, hintText, buttonName, buttonClickListener, false);
    }

    /**
     * 显示提示，默认没有图标、没有按钮、背景不透明
     *
     * @param hintText    提示信息
     * @param transparent 是否需要让提示视图变成透明的，透明的提示视图将不再拦截事件
     */
    public void hint(String hintText, boolean transparent) {
        hint(-1, hintText, null, null, transparent);
    }

    /**
     * 显示提示
     *
     * @param hintText 提示信息
     */
    public void hint(String hintText) {
        hint(-1, hintText, null, null, false);
    }

    /**
     * 失败
     *
     * @param exception                 失败了
     * @param reloadButtonClickListener 重新加载按钮点击监听器
     */
    public void failed(Throwable exception, OnClickListener reloadButtonClickListener) {
        String message;
        if (exception == null) {
            message = "网络连接异常【909】";
        } else if (exception instanceof SecurityException) {
            message = "网络连接异常【101】";
        } else if (exception instanceof UnknownHostException) {
            if (isConnectedByState(getContext())) {
                message = "网络连接异常【202】";
            } else {
                message = "没有网络连接";
            }
        } else if (exception instanceof SocketTimeoutException || exception instanceof ConnectTimeoutException) {
            message = "网络连接超时";
        } else if (exception instanceof FileNotFoundException) {
            message = "网络连接异常【404】";
        } else {
            message = "网络连接异常【909】";
        }
        hint(R.drawable.ic_error, message, "重试", reloadButtonClickListener, false);
    }

    /**
     * 空
     *
     * @param message
     */
    public void empty(String message) {
        hint(R.drawable.ic_error, message, null, null, false);
    }

    /**
     * 隐藏
     */
    public void hidden() {
        switch (viewSwitcher.getDisplayedChild()) {
            case 0:
                goneViewByAlpha(this, true);
                break;
            case 1:
                setVisibility(View.GONE);
                break;
        }
        mode = null;
    }

    private enum Mode {
        LOADING(0),
        HINT(1);

        int index;

        private Mode(int index) {
            this.index = index;
        }
    }
}