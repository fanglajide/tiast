package com.cheheihome.library

import android.content.Context


/**
 * Created by chanlevel on 2017/10/24.
 */
fun Context.screentWidth() = resources.displayMetrics.widthPixels

fun Context.screenHeight() = resources.displayMetrics.heightPixels

fun Context.getDisplayWH(): IntArray {
    val screenWidth: Int
    val screenHeight: Int
    val wh = IntArray(2)
    val dm = resources.displayMetrics
    val density = dm.density // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
    val densityDPI = dm.densityDpi // 屏幕密度（每寸像素：120/160/240/320）
    val xdpi = dm.xdpi
    val ydpi = dm.ydpi

    screenWidth = dm.widthPixels // 屏幕宽（像素，如：480px）
    screenHeight = dm.heightPixels // 屏幕高（像素，如：800px）

    //Logger.d("density:"+density +"\n"+"densityDPI:"+densityDPI+"\n"+"xdpi:"+xdpi+"\n"+"ydpi:"+ydpi +"\n"+"screenWidth:"+screenWidth+"\n"+"screenHeight:"+screenHeight);
    wh[0] = screenWidth
    wh[1] = screenHeight
    return wh
}


fun matchChoose(start: Int, end: Int, num: Int): Int {
    var s = start
    var e = end
    if (s > end) {
        val tmp = s
        e = s
        s = tmp
    }
    return Math.max(s, Math.min(num, e))
}


/**
 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
 */
fun Context.dip2px(dpValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}

/**
 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
 */
fun Context.px2dip(pxValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (pxValue / scale + 0.5f).toInt()
}