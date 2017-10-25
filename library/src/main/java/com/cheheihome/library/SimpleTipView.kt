package com.cheheihome.library

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View


/**
 * Created by chanlevel on 2017/10/25.
 */
class SimpleTipView : View {

    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attributes: AttributeSet) : super(ctx, attributes)

    public val ARROW_TOP_LEFT = 0X000001
    public val ARROW_TOP_RIGHT = 0X000010
    public val ARROW_BOTTOM_LEFT = 0X000100
    public val ARROW_BOTTOM_RIGHT = 0X001000


    private val arrow_height = 30
    private val color = Color.RED
    private val shadowColor = 0x77000000
    private val rectRadius = 5f
    private val shadowRadius = 5f
    private val dx = 5f
    private val dy = 5f
    private var drawRect: RectF

    val paint = Paint()


    init {


        paint.isAntiAlias = true
        /**
         * 解决旋转时的锯齿问题
         */
        paint.isFilterBitmap = true
        paint.isDither = true
        paint.style = Paint.Style.FILL
        paint.color = color
        /**
         * 设置阴影
         */
        paint.setShadowLayer(shadowRadius, dx, dy, shadowColor)

        drawRect = RectF()

    }

    private var content: CharSequence? = null
    fun setText(str: CharSequence) {
        this.content = str
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    }

    private fun calculateSize() {

    }


    private val path = Path()
    private val pathRect = Path()
    // private val srcOut = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas!!)

        val height = measuredHeight.toFloat() - arrow_height - dy * 2
        val width = measuredWidth.toFloat() - dx * 2

        path.reset()
        pathRect.reset()

        val rect = RectF(dx, dy, dx + width, dy + height)

        pathRect.addRoundRect(rect, rectRadius, rectRadius, Path.Direction.CCW)

        path.moveTo(dx + width / 2 + arrow_height, dy + height)

        path.lineTo(dx + width / 2, dy + height + arrow_height)

        path.lineTo(dx + width / 2 - arrow_height, dy + height)

//        path.lineTo(dy, height)

        path.close()

        path.op(pathRect, Path.Op.UNION)

        canvas.drawPath(path, paint)
    }


    fun drawText(canvas: Canvas) {

    }


}