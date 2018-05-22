package com.wallstreetcn.mpchart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class IndicatorView(ctx: Context, attributeSet: AttributeSet?) : View(ctx, attributeSet) {

    val colorTxt = Color.parseColor("#4d4d4d")
    val sizeTxt = context.sp2px(12f)
    val colors = listOf("#02b388", "#8dddc9", "#f7f7f7", "#fbaeae", "#ff4040").map { Color.parseColor(it) }
    val keys = listOf("强烈卖出", "卖出", "中立", "买入", "强烈买入")

//    val keys=(0..4).map { "agsdfgdsfdgs555555" }
    val panelPaddingBottom = context.dip2px(10f)

    val stoke=context.dip2px(5f).toFloat()
    val paint by lazy {
        Paint().apply {
            isAntiAlias = true
            strokeWidth =stoke
            style = Paint.Style.STROKE
        }
    }

    val arcRect = RectF()

    init {
    }

    var radius = 0f
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)
        val w = MeasureSpec.getSize(widthMeasureSpec)

        radius = Math.min((h - panelPaddingBottom) / 1f, w / 2f) * 2 / 3

        arcRect.set((w / 2f - radius), (h - radius), (w / 2f + radius), (h - panelPaddingBottom + radius).toFloat())
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawPanel(canvas)
        drawText(canvas)
    }


    private fun drawPanel(canvas: Canvas) {


        val size = colors.size

        val per = 180 / size

        colors.forEachIndexed { index, i ->

            val start = -180 + per * index

            paint.color = i

            canvas.drawArc(arcRect, start.toFloat(), per.toFloat(), false, paint)
        }

    }

    private val textPaint by lazy {
        Paint().apply {
            textSize = context.sp2px(10f).toFloat()
            color = colorTxt

        }
    }

    private fun drawText(canvas: Canvas) {
        canvas.save()
        val fontMetrics = textPaint.fontMetrics
        val fontHeight = fontMetrics.descent - fontMetrics.ascent

        val centerX = width / 2f
        val centerY = height - panelPaddingBottom

        canvas.translate(centerX, centerY.toFloat())

        val angle = Math.PI / keys.size

        keys.forEachIndexed { index, s ->

            val tw = textPaint.measureText(s)

            val d = when (index) {0 -> Math.PI / 2
                keys.size - 1 -> Math.PI + Math.PI / 2
                else -> Math.PI / 2 + angle * (index + 1 / 2f)
            }

            val distance=radius+stoke //+fontHeight*2
            var x = (distance * Math.sin(d)).toFloat()

            x = when {
                x < 0 -> x- tw
                Math.abs(x) <2f ->  - tw / 2f
                else -> x
            }

            val y = (distance * Math.cos(d)).toFloat()

            canvas.drawText(s, x  , y, textPaint)

        }
        val ss="alafgsdfgsdgfsdgl"

        canvas.drawText(ss,0f-textPaint.measureText(ss)/2,0f,textPaint)

//        val x = ((radius + fontHeight * 2) * Math.sin((d))).toFloat()
//        val y = ((radius + fontHeight * 2) * Math.cos((d))).toFloat()
//
//
//        canvas.drawText("lalal", x, y , textPaint)

        canvas.restore()
    }

    private fun drawIndicator(canvas: Canvas) {

    }


    fun Context.dip2px(dpValue: Float): Int {
        val scale = resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun Context.sp2px(spValue: Float): Int {
        val fontScale = this.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }


}
