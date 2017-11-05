package me.lecc.tiast

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

    // val ARROW_TOP_LEFT = 0X000001
    // val ARROW_TOP_RIGHT = 0X000010
    // val ARROW_BOTTOM_LEFT = 0X000100
    // val ARROW_BOTTOM_RIGHT = 0X001000


    private var arrow_height = 20
    private val color = Color.WHITE
    private val shadowColor = Color.parseColor("#77000000")
    private val rectRadius = 15f
    private var shadowRadius = 2f
    private var dx = 5f
    private var dy = 5f
    private var drawRect: RectF

    private var maxWidth = 0
    private var minWidth = 0
    private var minHeight = 0
    val paint = Paint()


    init {

        shadowRadius = context.dip2px(2f).toFloat()
        dx = context.dip2px(0.5f).toFloat()
        dy = context.dip2px(0.5f).toFloat()
        arrow_height=context.dip2px(10f)

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
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)

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

    fun calculateSize(): IntArray {

        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        textPaint.density = context.resources.displayMetrics.density
        textPaint.color = Color.BLACK
        textPaint.style = Paint.Style.FILL_AND_STROKE
        textPaint.textSize = context.dip2px(14f).toFloat()

        txtWidth = textPaint.measureText(content.toString())

        txtWidth = matchChoose(minWidth, maxWidth, txtWidth.toInt()).toFloat()

        layout = StaticLayout(content, textPaint, txtWidth.toInt(), Layout.Alignment.ALIGN_NORMAL, 1.1f, 1.0f, true)


        txtHeight = Math.max(minHeight.toFloat(), (layout as StaticLayout).height.toFloat())

        return intArrayOf((txtWidth + padding * 2 + dx * 2 + shadowRadius * 2).toInt(), (txtHeight + padding * 2 + arrow_height + dy * 2 + shadowRadius * 2).toInt())

    }

    private var arrowOffset = 0
    private var anchor: View? = null
    fun anchor(view: View?) {
        this.anchor = view
    }

    private fun calculateArrowOffset(): Int {
        anchor?.let {
            val arr = IntArray(2)
            it.getLocationOnScreen(arr)
            val x = arr[0] + it.measuredWidth / 2

            val size = calculateSize();
            val right = x + size[0] / 2
            val left = x - size[0] / 2

            when {
                right > context.screenWidth() -> return (right - context.screenWidth())
                left < 0 -> return left
                else -> return 0
            }

        }
        return 0
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val arr = calculateSize()


        val w = MeasureSpec.makeMeasureSpec(arr[0], MeasureSpec.EXACTLY)
        val h = MeasureSpec.makeMeasureSpec(arr[1], MeasureSpec.EXACTLY)

        setMeasuredDimension(w, h)

        arrowOffset = calculateArrowOffset()
    }


    private val path = Path()
    private val pathRect = Path()
    // private val srcOut = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas!!)

        val height = measuredHeight.toFloat() - arrow_height - dy * 2 - shadowRadius * 2
        val width = measuredWidth.toFloat() - dx * 2 - shadowRadius * 2

        path.reset()
        pathRect.reset()

        val rect = RectF(dx, dy, dx + width, dy + height)

        pathRect.addRoundRect(rect, rectRadius, rectRadius, Path.Direction.CCW)

        path.moveTo(arrowOffset + dx + width / 2 + arrow_height, dy + height)

        path.lineTo(arrowOffset + dx + width / 2, dy + height + arrow_height)

        path.lineTo(arrowOffset + dx + width / 2 - arrow_height, dy + height)

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