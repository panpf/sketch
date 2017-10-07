package me.xiaopan.sketchsample.util

import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation

object AnimationUtils {

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
    @JvmStatic @JvmOverloads fun visibleViewByAlpha(view: View, durationMillis: Long = 400, isBanClick: Boolean = true, animationListener: Animation.AnimationListener? = null) {
        if (view.visibility != View.VISIBLE) {
            view.visibility = View.VISIBLE
            val showAlphaAnimation = getShowAlphaAnimation(durationMillis)
            showAlphaAnimation.setAnimationListener(object : Animation.AnimationListener {
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
     * 将给定视图渐渐隐去最后从界面中移除（view.setVisibility(View.GONE)）

     * @param view              被处理的视图
     * *
     * @param durationMillis    持续时间，毫秒
     * *
     * @param isBanClick        在执行动画的过程中是否禁止点击
     * *
     * @param animationListener 动画监听器
     */
    @JvmStatic @JvmOverloads fun goneViewByAlpha(view: View, durationMillis: Long = 400, isBanClick: Boolean = true, animationListener: Animation.AnimationListener? = null) {
        if (view.visibility != View.GONE) {
            val hiddenAlphaAnimation = getHiddenAlphaAnimation(durationMillis)
            hiddenAlphaAnimation.setAnimationListener(object : Animation.AnimationListener {
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
                    view.visibility = View.GONE
                    animationListener?.onAnimationEnd(animation)
                }
            })
            view.startAnimation(hiddenAlphaAnimation)
        }
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
    @JvmStatic @JvmOverloads fun invisibleViewByAlpha(view: View, durationMillis: Long = 400, isBanClick: Boolean = true, animationListener: Animation.AnimationListener? = null) {
        if (view.visibility != View.INVISIBLE) {
            val hiddenAlphaAnimation = getHiddenAlphaAnimation(durationMillis)
            hiddenAlphaAnimation.setAnimationListener(object : Animation.AnimationListener {
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
                    view.visibility = View.INVISIBLE
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
    @JvmStatic @JvmOverloads fun getHiddenAlphaAnimation(durationMillis: Long, animationListener: Animation.AnimationListener? = null): AlphaAnimation {
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
    @JvmStatic fun getAlphaAnimation(fromAlpha: Float, toAlpha: Float, durationMillis: Long, animationListener: Animation.AnimationListener?): AlphaAnimation {
        val alphaAnimation = AlphaAnimation(fromAlpha, toAlpha)
        alphaAnimation.duration = durationMillis
        if (animationListener != null) {
            alphaAnimation.setAnimationListener(animationListener)
        }
        return alphaAnimation
    }

    /**
     * 获取一个由不可见变为完全显示的透明度渐变动画

     * @param durationMillis 持续时间
     * *
     * @return 一个由不可见变为完全显示的透明度渐变动画
     */
    @JvmStatic fun getShowAlphaAnimation(durationMillis: Long): AlphaAnimation {
        return getAlphaAnimation(0.0f, 1.0f, durationMillis, null)
    }
}
