package me.xiaopan.spear.sample.util;

import android.content.Context;

/**
 * Created by xiaopan on 14/12/27.
 */
public class DimenUtils {

    /**
     * dp单位转换为px
     * @param context 上下文，需要通过上下文获取到当前屏幕的像素密度
     * @param dpValue dp值
     * @return px值
     */
    public static int dp2px(Context context, float dpValue){
        return (int)(dpValue * (context.getResources().getDisplayMetrics().density) + 0.5f);
    }
}
