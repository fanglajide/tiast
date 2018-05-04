package com.jeanboy.radarview.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View


/**
 * Created by jeanboy on 2016/10/17.
 */

class RadarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private var dataList: List<RadarData>? = null

    private var count = 6//雷达网圈数
    private var angle: Float = 0.toFloat()//多边形弧度
    private var radius: Float = 0.toFloat()
    private val maxValue = 100f


    private val mainColor = -0x777778//雷达区颜色
    private val valueColor = -0x862b03//数据区颜色
    private val textColor = -0x7f7f80//文本颜色

    private val mainLineWidth = 0.5f//雷达网线宽度dp
    private val valueLineWidth = 1f//数据区边宽度dp
    private val valuePointRadius = 2f//数据区圆点半径dp
    private val textSize = 14f//字体大小sp

    private var mWidth: Int = 0
    private var mHeight: Int = 0


    private val isDataListValid: Boolean
        get() = dataList != null && dataList!!.size >= 3


    private val mainPaint by lazy {
        Paint()
                .apply {
                    isAntiAlias = true
                    color = mainColor
                    style = Paint.Style.STROKE
                }

    }
    private val valuePaint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = valueColor
            style = Paint.Style.FILL_AND_STROKE

        }
    }

    private val textPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = textColor

        }
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = Math.min(h, w) / 2 * 0.6f
        mWidth = w
        mHeight = h
        postInvalidate()
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.translate((mWidth / 2).toFloat(), (mHeight / 2).toFloat())

        if (isDataListValid) {
            drawSpiderweb(canvas)
            drawText(canvas)
            drawRegion(canvas)
        }
    }

    /**
     * 绘制蜘蛛网
     *
     * @param canvas
     */
    private fun drawSpiderweb(canvas: Canvas) {
        mainPaint.strokeWidth = dip2px(context, mainLineWidth).toFloat()
        val webPath = Path()
        val linePath = Path()
        val r = radius / (count - 1)//蜘蛛丝之间的间距
        for (i in 0 until count) {
            val curR = r * i//当前半径
            webPath.reset()
            for (j in 0 until count) {
                val x = (curR * Math.sin((angle / 2 + angle * j).toDouble())).toFloat()
                val y = (curR * Math.cos((angle / 2 + angle * j).toDouble())).toFloat()
                if (j == 0) {
                    webPath.moveTo(x, y)
                } else {
                    webPath.lineTo(x, y)
                }
                if (i == count - 1) {//当绘制最后一环时绘制连接线
                    linePath.reset()
                    linePath.moveTo(0f, 0f)
                    linePath.lineTo(x, y)
                    canvas.drawPath(linePath, mainPaint)
                }
            }
            webPath.close()
            canvas.drawPath(webPath, mainPaint)
        }
    }

    /**
     * 绘制文本
     *
     * @param canvas
     */
    private fun drawText(canvas: Canvas) {
        textPaint.textSize = sp2px(context, textSize).toFloat()
        val fontMetrics = textPaint.fontMetrics
        val fontHeight = fontMetrics.descent - fontMetrics.ascent
        for (i in 0 until count) {
            val x = ((radius + fontHeight * 2) * Math.sin((angle / 2 + angle * i).toDouble())).toFloat()
            val y = ((radius + fontHeight * 2) * Math.cos((angle / 2 + angle * i).toDouble())).toFloat()
            val title = dataList!![i].title
            val dis = textPaint.measureText(title)//文本长度
            canvas.drawText(title, x - dis / 2, y, textPaint)
        }
    }

    /**
     * 绘制区域
     *
     * @param canvas
     */
    private fun drawRegion(canvas: Canvas) {
        valuePaint.strokeWidth = dip2px(context, valueLineWidth).toFloat()
        val path = Path()
        valuePaint.alpha = 255
        path.reset()

        for (i in 0 until count) {
            val percent = dataList!![i].percentage / maxValue
            val x = (radius.toDouble() * Math.sin((angle / 2 + angle * i).toDouble()) * percent).toFloat()
            val y = (radius.toDouble() * Math.cos((angle / 2 + angle * i).toDouble()) * percent).toFloat()
            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
            //绘制小圆点
            canvas.drawCircle(x, y, dip2px(context, valuePointRadius).toFloat(), valuePaint)
        }
        path.close()
        valuePaint.style = Paint.Style.STROKE
        canvas.drawPath(path, valuePaint)
        valuePaint.alpha = 128
        //绘制填充区域
        valuePaint.style = Paint.Style.FILL_AND_STROKE
        canvas.drawPath(path, valuePaint)
    }


    fun setDataList(dataList: List<RadarData>?) {
        if (dataList == null || dataList.size < 3) {
            throw RuntimeException("The number of data can not be less than 3")
        } else {
            this.dataList = dataList
            count = dataList.size//圈数等于数据个数，默认为6
            angle = (Math.PI * 2 / count).toFloat()
            invalidate()
        }
    }

    companion object {

        private val TAG = RadarView::class.java.simpleName


        fun dip2px(context: Context, dipValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dipValue * scale + 0.5f).toInt()
        }

        fun sp2px(context: Context, spValue: Float): Int {
            val fontScale = context.resources.displayMetrics.scaledDensity
            return (spValue * fontScale + 0.5f).toInt()
        }
    }

    inner class RadarData(val title: String, val percentage: Double)
}