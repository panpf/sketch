package com.github.panpf.sketch.sample.widget

import android.annotation.TargetApi
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.widget.LinearLayout
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.databinding.ViewHintBinding
import com.github.panpf.tools4k.lang.asOrNull
import org.apache.http.conn.ConnectTimeoutException
import java.io.FileNotFoundException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * 提示视图
 */
class HintView : LinearLayout {

    private val binding = ViewHintBinding.inflate(LayoutInflater.from(context), this)

    private var mode: Mode? = null

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context) : super(context)

    /**
     * 显示加载中，将使用type格式化“正在加载%s，请稍后…”字符串
     */
    fun loading(message: String?) {
        binding.textHintLoadingHint.text = message
        binding.textHintLoadingHint.visibility =
            if (TextUtils.isEmpty(message)) View.GONE else View.VISIBLE
        setProgress(0, 0)

        if (mode != Mode.LOADING) {
            if (mode == Mode.HINT) {
                binding.viewSwitcherHint.setInAnimation(context, R.anim.slide_to_bottom_in)
                binding.viewSwitcherHint.setOutAnimation(context, R.anim.slide_to_bottom_out)
            } else {
                binding.viewSwitcherHint.inAnimation = null
                binding.viewSwitcherHint.outAnimation = null
            }
            mode = Mode.LOADING
            binding.buttonHintAction.visibility = View.INVISIBLE
            binding.viewSwitcherHint.displayedChild = mode!!.index
            visibility = View.VISIBLE
        }
    }

    fun setProgress(totalLength: Int, completedLength: Int) {
        if (completedLength <= 0) {
            binding.textHintProgress.text = null
        } else {
            val ratio = (completedLength.toFloat() / totalLength * 100).toInt()
            binding.textHintProgress.text = String.format("%d%%", ratio)
        }
    }

    /**
     * 显示提示

     * @param icon              图标ID，如果不想显示图标的话，此参数传-1即可
     * *
     * @param hint            提示信息
     * *
     * @param button          按钮的名称
     * *
     * @param click 按钮的按下事件
     * *
     * @param transparent         是否需要让提示视图变成透明的，透明的提示视图将不再拦截事件
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    fun hint(
        icon: Int = -1,
        hint: String,
        button: String? = null,
        click: OnClickListener? = null,
        transparent: Boolean = false
    ) {
        if (icon > 0) {
            val drawables = binding.textHintHint.compoundDrawables
            binding.textHintHint.setCompoundDrawablesWithIntrinsicBounds(
                drawables[0],
                resources.getDrawable(icon),
                drawables[2],
                drawables[3]
            )
        } else {
            val drawables = binding.textHintHint.compoundDrawables
            binding.textHintHint.setCompoundDrawablesWithIntrinsicBounds(
                drawables[0],
                null,
                drawables[2],
                drawables[3]
            )
        }

        if (isNotEmpty(hint)) {
            binding.textHintHint.text = hint
        } else {
            binding.textHintHint.text = null
        }

        if (isNotEmpty(button) && click != null) {
            binding.buttonHintAction.text = button
            binding.buttonHintAction.setOnClickListener(click)
            visibleViewByAlpha(binding.buttonHintAction, true)
        } else {
            binding.buttonHintAction.text = null
            binding.buttonHintAction.setOnClickListener(null)
            binding.buttonHintAction.visibility = View.INVISIBLE
        }

        isClickable = !transparent

        if (mode != Mode.HINT) {
            if (mode != null) {
                binding.viewSwitcherHint.setInAnimation(context, R.anim.slide_to_top_in)
                binding.viewSwitcherHint.setOutAnimation(context, R.anim.slide_to_top_out)
            } else {
                binding.viewSwitcherHint.inAnimation = null
                binding.viewSwitcherHint.outAnimation = null
            }
            mode = Mode.HINT
            binding.viewSwitcherHint.displayedChild = 1
            visibility = View.VISIBLE
        }
    }

    /**
     * 显示提示

     * @param iconId              图标ID，如果不想显示图标的话，此参数传-1即可
     * @param hintText            提示信息
     * @param buttonName          按钮的名称
     * @param buttonClickListener 按钮的按下事件
     */
    fun hint(
        iconId: Int,
        hintText: String,
        buttonName: String,
        buttonClickListener: OnClickListener
    ) {
        hint(iconId, hintText, buttonName, buttonClickListener, false)
    }

    /**
     * 显示提示

     * @param iconId   图标ID，如果不想显示图标的话，此参数传-1即可
     * *
     * @param hintText 提示信息
     */
    fun hint(iconId: Int, hintText: String) {
        hint(iconId, hintText, null, null, false)
    }

    /**
     * 显示提示

     * @param hintText            提示信息
     * *
     * @param buttonName          按钮的名称
     * *
     * @param buttonClickListener 按钮的按下事件
     * *
     * @param transparent         是否需要让提示视图变成透明的，透明的提示视图将不再拦截事件
     */
    fun hint(
        hintText: String,
        buttonName: String,
        buttonClickListener: OnClickListener,
        transparent: Boolean
    ) {
        hint(-1, hintText, buttonName, buttonClickListener, transparent)
    }

    /**
     * 显示提示

     * @param hintText            提示信息
     * *
     * @param buttonName          按钮的名称
     * *
     * @param buttonClickListener 按钮的按下事件
     */
    fun hint(hintText: String, buttonName: String, buttonClickListener: OnClickListener) {
        hint(-1, hintText, buttonName, buttonClickListener, false)
    }

    /**
     * 显示提示，默认没有图标、没有按钮、背景不透明

     * @param hintText    提示信息
     * *
     * @param transparent 是否需要让提示视图变成透明的，透明的提示视图将不再拦截事件
     */
    fun hint(hintText: String, transparent: Boolean) {
        hint(-1, hintText, null, null, transparent)
    }

    /**
     * 显示提示

     * @param hintText 提示信息
     */
    fun hint(hintText: String) {
        hint(-1, hintText, null, null, false)
    }

    /**
     * 失败

     * @param exception                 失败了
     * *
     * @param reloadButtonClickListener 重新加载按钮点击监听器
     */
    fun failed(exception: Throwable, reloadButtonClickListener: OnClickListener) {
        hint(
            R.drawable.ic_error,
            getCauseByException(context, exception),
            "重试",
            reloadButtonClickListener,
            false
        )
    }

    /**
     * 空

     * @param message
     */
    fun empty(message: String) {
        hint(R.drawable.ic_error, message, null, null, false)
    }

    /**
     * 隐藏
     */
    fun hidden() {
        when (binding.viewSwitcherHint.displayedChild) {
            0 -> goneViewByAlpha(this, true)
            1 -> visibility = View.GONE
        }
        mode = null
    }

    private enum class Mode private constructor(internal var index: Int) {
        LOADING(0),
        HINT(1)
    }

    companion object {

        fun isEmpty(string: String?): Boolean {
            return string == null || "" == string.trim { it <= ' ' }
        }

        fun isNotEmpty(string: String?): Boolean {
            return !isEmpty(string)
        }

        /**
         * 将给定视图渐渐隐去最后从界面中移除（view.setVisibility(View.GONE)）

         * @param view              被处理的视图
         * *
         * @param durationMillis    持续时间，毫秒
         * *
         * @param isBanClick        在执行动画的过程中是否禁止点击
         * *
         * @param animationListener 动画监听器
         */
        fun goneViewByAlpha(
            view: View,
            durationMillis: Long,
            isBanClick: Boolean,
            animationListener: AnimationListener?
        ) {
            if (view.visibility != View.GONE) {
                view.visibility = View.GONE
                val hiddenAlphaAnimation = getHiddenAlphaAnimation(durationMillis)
                hiddenAlphaAnimation.setAnimationListener(object : AnimationListener {
                    override fun onAnimationStart(animation: Animation) {
                        if (isBanClick) {
                            view.isClickable = false
                        }
                        animationListener?.onAnimationStart(animation)
                    }

                    override fun onAnimationRepeat(animation: Animation) {
                        animationListener?.onAnimationRepeat(animation)
                    }

                    override fun onAnimationEnd(animation: Animation) {
                        if (isBanClick) {
                            view.isClickable = true
                        }
                        animationListener?.onAnimationEnd(animation)
                    }
                })
                view.startAnimation(hiddenAlphaAnimation)
            }
        }

        /**
         * 获取一个由完全显示变为不可见的透明度渐变动画

         * @param durationMillis    持续时间
         * *
         * @param animationListener 动画监听器
         * *
         * @return 一个由完全显示变为不可见的透明度渐变动画
         */
        @JvmOverloads
        fun getHiddenAlphaAnimation(
            durationMillis: Long,
            animationListener: AnimationListener? = null
        ): AlphaAnimation {
            return getAlphaAnimation(1.0f, 0.0f, durationMillis, animationListener)
        }

        /**
         * 获取一个透明度渐变动画

         * @param fromAlpha         开始时的透明度
         * *
         * @param toAlpha           结束时的透明度都
         * *
         * @param durationMillis    持续时间
         * *
         * @param animationListener 动画监听器
         * *
         * @return 一个透明度渐变动画
         */
        fun getAlphaAnimation(
            fromAlpha: Float,
            toAlpha: Float,
            durationMillis: Long,
            animationListener: AnimationListener?
        ): AlphaAnimation {
            val alphaAnimation = AlphaAnimation(fromAlpha, toAlpha)
            alphaAnimation.duration = durationMillis
            if (animationListener != null) {
                alphaAnimation.setAnimationListener(animationListener)
            }
            return alphaAnimation
        }

        /**
         * 将给定视图渐渐隐去最后从界面中移除（view.setVisibility(View.GONE)），默认的持续时间为DEFAULT_ALPHA_ANIMATION_DURATION

         * @param view       被处理的视图
         * *
         * @param isBanClick 在执行动画的过程中是否禁止点击
         */
        fun goneViewByAlpha(view: View, isBanClick: Boolean) {
            goneViewByAlpha(view, 400, isBanClick, null)
        }

        /**
         * 将给定视图渐渐显示出来（view.setVisibility(View.VISIBLE)），默认的持续时间为DEFAULT_ALPHA_ANIMATION_DURATION

         * @param view       被处理的视图
         * *
         * @param isBanClick 在执行动画的过程中是否禁止点击
         */
        fun visibleViewByAlpha(view: View, isBanClick: Boolean) {
            visibleViewByAlpha(view, 400, isBanClick, null)
        }

        /**
         * 将给定视图渐渐显示出来（view.setVisibility(View.VISIBLE)）

         * @param view              被处理的视图
         * *
         * @param durationMillis    持续时间，毫秒
         * *
         * @param isBanClick        在执行动画的过程中是否禁止点击
         * *
         * @param animationListener 动画监听器
         */
        fun visibleViewByAlpha(
            view: View,
            durationMillis: Long,
            isBanClick: Boolean,
            animationListener: AnimationListener?
        ) {
            if (view.visibility != View.VISIBLE) {
                view.visibility = View.VISIBLE
                val showAlphaAnimation = getShowAlphaAnimation(durationMillis)
                showAlphaAnimation.setAnimationListener(object : AnimationListener {
                    override fun onAnimationStart(animation: Animation) {
                        if (isBanClick) {
                            view.isClickable = false
                        }
                        animationListener?.onAnimationStart(animation)
                    }

                    override fun onAnimationRepeat(animation: Animation) {
                        animationListener?.onAnimationRepeat(animation)
                    }

                    override fun onAnimationEnd(animation: Animation) {
                        if (isBanClick) {
                            view.isClickable = true
                        }
                        animationListener?.onAnimationEnd(animation)
                    }
                })
                view.startAnimation(showAlphaAnimation)
            }
        }

        /**
         * 获取一个由不可见变为完全显示的透明度渐变动画

         * @param durationMillis 持续时间
         * *
         * @return 一个由不可见变为完全显示的透明度渐变动画
         */
        fun getShowAlphaAnimation(durationMillis: Long): AlphaAnimation {
            return getAlphaAnimation(0.0f, 1.0f, durationMillis, null)
        }

        fun isConnectedByState(context: Context): Boolean {
            val networkInfo =
                context.getSystemService(Context.CONNECTIVITY_SERVICE).asOrNull<ConnectivityManager>()?.activeNetworkInfo
            return networkInfo != null && networkInfo.state == NetworkInfo.State.CONNECTED
        }

        fun getCauseByException(context: Context, exception: Throwable?): String {
            return when (exception) {
                null -> "未知异常"
                is SecurityException -> "网络权限异常：${exception.message}"
                is UnknownHostException -> if (isConnectedByState(context)) "URL Host 异常" else "没有网络连接"
                is SocketTimeoutException, is ConnectTimeoutException -> "网络连接超时"
                is FileNotFoundException -> "网络地址异常"
                else -> "未知异常：$exception"
            }
        }
    }
}