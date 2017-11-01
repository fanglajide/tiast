package com.cheheihome.library

import android.content.Context
import android.graphics.PixelFormat
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.view.WindowManager


/**
 * Created by chanlevel on 2017/10/24.
 */
class Tiast(val ctx: Context) {

    companion object {

        fun make(ctx: Context, str: String, mills: Int = 4000): Tiast {
            return Tiast(ctx).apply {
                setContent(str)
                duration = mills
            }
        }

    }


    private var anchor: View? = null
    private var tip: View? = null
    private val wm: WindowManager by lazy {
        ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }


    init {
    }

    private var duration = 2000
    private var content: String? = null

    public fun setContent(str: String) {
        content = str
        tip = SimpleTipView(ctx).apply {
            setText(str)
        }
    }

    protected fun calculateRegion(): IntArray {

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


    public fun anchor(view: View): Tiast {
        this.anchor = view
        return this
    }

    public fun show(): Tiast {
        if (tip == null && content != null) {
            setContent(content!!)
        }
        val arr = calculateRegion()

        val size = (tip as SimpleTipView).apply { anchor(anchor) }.calculateSize()


        val lp = WindowManager.LayoutParams()
        lp.width = ctx.dip2px(size[0].toFloat())
        lp.height = ctx.dip2px(size[1].toFloat())
        lp.format = PixelFormat.TRANSLUCENT
        lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL
        lp.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        lp.gravity = Gravity.TOP or Gravity.LEFT or Gravity.START;

        lp.x = arr[0] - size[0] / 2
        lp.y = arr[1] - size[1]
        wm.addView(tip, lp)

        handler
                .postDelayed(
                        {
                            dismiss()
                        },
                        duration.toLong())

        return this
    }

    private val handler = Handler()

    public fun dismiss(): Tiast {
        tip?.let {
            if (it.isAttachedToWindow)
                wm.removeView(it)
        }
        return this
    }


}