package com.cheheihome.library

import android.content.Context
import android.graphics.*
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View


/**
 * Created by chanlevel on 2017/10/25.
 */
class SimpleTipView : View {

    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attributes: AttributeSet) : super(ctx, attributes)

    val ARROW_TOP_LEFT = 0X000001
    val ARROW_TOP_RIGHT = 0X000010
    val ARROW_BOTTOM_LEFT = 0X000100
    val ARROW_BOTTOM_RIGHT = 0X001000


    private val arrow_height = 30
    private val color = Color.WHITE
    private val shadowColor = 0x77000000
    private val rectRadius = 20f
    private val shadowRadius = 5f
    private val dx = 5f
    private val dy = 5f
    private var drawRect: RectF

    private var maxWidth = 0
    private var minWidth = 0
    private var minHeight = 0
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

        maxWidth = context.screenWidth() / 3
        minHeight = context.dip2px(20f)
        minWidth = context.dip2px(20f)

    }

    private var content: CharSequence? = ""

    fun setText(str: CharSequence) {
        this.content = str
    }

    var txtWidth = 0f
    var txtHeight = 0f
    var layout: Layout? = null
    val padding by lazy {
        context.dip2px(10f).toFloat()
    }

    public fun calculateSize(): IntArray {

        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        textPaint.density = context.resources.displayMetrics.density
        textPaint.color = Color.BLACK
        textPaint.style = Paint.Style.FILL_AND_STROKE
        textPaint.textSize = context.dip2px(15f).toFloat()

        txtWidth = textPaint.measureText(content.toString())

        txtWidth = matchChoose(minWidth, maxWidth, txtWidth.toInt()).toFloat()

        layout = StaticLayout(content, textPaint, txtWidth.toInt(), Layout.Alignment.ALIGN_NORMAL, 1.1f, 1.0f, true)


        txtHeight = Math.max(minHeight.toFloat(), (layout as StaticLayout).height.toFloat())

        return intArrayOf((txtWidth + padding * 2).toInt(), (txtHeight + padding * 2 + arrow_height).toInt())

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val arr = calculateSize()


        val w = MeasureSpec.makeMeasureSpec(arr[0], MeasureSpec.EXACTLY)
        val h = MeasureSpec.makeMeasureSpec(arr[1], MeasureSpec.EXACTLY)

        setMeasuredDimension(w, h)

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
        drawText(canvas)
    }


    fun drawText(canvas: Canvas) {
        canvas.translate(padding, padding)

        layout?.let {

            it.draw(canvas)
        }
    }


}