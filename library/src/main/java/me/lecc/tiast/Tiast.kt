package me.lecc.tiast

import android.content.Context
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.WindowManager


/**
 * Created by lec on 2017/10/24.
 */
class Tiast(private val ctx: Context) {

    companion object {

        fun make(ctx: Context, str: String, mills: Int = 4000): Tiast {
            return Tiast(ctx).apply {
                setContent(str)
                duration = mills
            }
        }

    }


    private var anchor: View? = null
    private var tip: SimpleTipView? = null

    private val wm: WindowManager by lazy {
        ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }


    init {
    }

    private var duration = 2000
    private var content: String? = null

    fun setContent(str: String) {
        content = str
        tip = SimpleTipView(ctx).apply {
            setText(str)
        }
    }

    private fun calculateRegion(): IntArray {

        var x = ctx.screenWidth() / 2
        var y = ctx.screenHeight() / 2

        anchor?.let {
            val arr = IntArray(2)
            it.getLocationOnScreen(arr)
            x = arr[0] + it.measuredWidth / 2
            y = arr[1]
        }

        return intArrayOf(x, y)
    }


    fun anchor(view: View): Tiast {
        this.anchor = view

        return this
    }

    fun show(): Tiast {
        if (tip == null && content != null) {
            setContent(content!!)
        }

        tip?.apply {
            anchor(anchor)
            anchor?.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                //  Log.d("Tiast", "anchor change")
                Looper.myQueue()
                        .addIdleHandler {
                            update()
                            false
                        }

            }
        }


        wm.addView(tip, params())
        handler
                .postDelayed(
                        {
                            dismiss()
                        },
                        duration.toLong())

        return this
    }

    private fun params(): WindowManager.LayoutParams {
        val arr = calculateRegion()
        val size = (tip as SimpleTipView).calculateSize()
        val lp = WindowManager.LayoutParams()
        lp.width = ctx.dip2px(size[0].toFloat())
        lp.height = ctx.dip2px(size[1].toFloat())
        lp.format = PixelFormat.TRANSLUCENT
        lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL
        lp.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        lp.gravity = Gravity.TOP or Gravity.LEFT or Gravity.START

        lp.x = arr[0] - size[0] / 2
        lp.y = arr[1] - size[1]
        return lp
    }

    private fun update() {
        if (tip?.isAttachedToWindow == true)
            wm.updateViewLayout(tip, params())

    }

    private val handler = Handler()

    fun dismiss(): Tiast {
        tip?.let {
            if (it.isAttachedToWindow)
                wm.removeView(it)
        }
        return this
    }


}
fun Context.dip2px(dpValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}
fun Context.px2dip(pxValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (pxValue / scale + 0.5f).toInt()
}
fun Context.screenWidth() = resources.displayMetrics.widthPixels

fun Context.screenHeight() = resources.displayMetrics.heightPixels

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