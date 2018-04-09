package me.lecc.radar

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class RadarView : View {


    private var lineWidth = 1
    private var lineColor = Color.parseColor("#888888")
    private var valueColor = Color.parseColor("#79D4FD")
    private var textColor = Color.parseColor("#808080")
    private var textSize = 15f

    constructor(ctx: Context) : this(ctx, null)
    constructor(ctx: Context, attributeSet: AttributeSet?) : this(ctx, attributeSet, 0)
    constructor(ctx: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(ctx, attributeSet, defStyleAttr) {
        initAttr(attributeSet)
        setup()
    }

    private fun initAttr(attributeSet: AttributeSet?) {

    }


    private val textPaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = (textColor)
            textSize = this@RadarView.textSize
        }
    }
    private val netsPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = lineColor
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


    private fun setup() {

    }

    private var datas: List<Item>? = null

    fun setData(datas: List<Item>) {
        this.datas = datas
    }


    data class Item(val title: String, val precent: Double)
}