package com.wallstreetcn.mpchart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout


const val DEFAULT_MAX = 1000f

class MPChartView(private val ctx: Context, attributeSet: AttributeSet? = null) : FrameLayout(ctx, attributeSet) {
    var datas = mutableListOf<Data>()

    private val adapter: ChartAdapter by lazy {
        ChartAdapter(ctx)//.apply { setData(datas?:Collections.emptyList()) }
    }

    private val recyclerView by lazy {
        RecyclerView(ctx).apply {

            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, true)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val lastPosition = (recyclerView?.layoutManager as?  LinearLayoutManager)?.findLastVisibleItemPosition()

                    if (!dataFinish && !loading && lastPosition == datas.size - 1) {
                        loading = true
                        callback?.loadMore()
                    }

                }
            })

            this@MPChartView.adapter.setData(datas)
            adapter = this@MPChartView.adapter
        }
    }

    init {

        addView(recyclerView, LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

    }


    private var loading = false
    private var callback: Callback? = null

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    var max = DEFAULT_MAX

    var dataFinish = false


    fun setData(datas: List<Data>) {
        this.datas.clear()
        this.datas.addAll(datas)
        max = calculateMax(datas)
        loading = false
        adapter.max = max
        adapter.notifyDataSetChanged()
    }

    private fun calculateMax(datas: List<Data>): Float {
        return datas.map { Math.abs(it.value) }.max() ?: DEFAULT_MAX
    }


    fun dataFinish(finish: Boolean) {
        this.dataFinish = finish
    }


    interface Callback {
        fun loadMore()

    }


}

class ChartAdapter(private val ctx: Context) : RecyclerView.Adapter<ChartAdapter.VH>() {
    var datas: List<Data>? = null

    var max = DEFAULT_MAX


    fun setData(datas: List<Data>) {
        this.datas = datas
    }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): VH {
        return VH(ctx)
    }

    override fun getItemCount(): Int {
        return datas?.size ?: 0

    }

    override fun onBindViewHolder(holder: VH?, position: Int) {
        holder?.setMax(max)
        holder?.setData(datas!![position])
    }


    class VH(ctx: Context) : RecyclerView.ViewHolder(ItemView(ctx).apply { layoutParams = ViewGroup.LayoutParams(ctx.dip2px(50f), ViewGroup.LayoutParams.MATCH_PARENT) }) {

        fun setMax(max: Float) {
            (itemView as? ItemView)?.max = max
        }

        fun setData(data: Data) {
            (itemView as? ItemView)?.let {
                it.data = data
            }
        }
    }
}


class Data(val value: Float, val time: String)


class ItemView(ctx: Context, attributeSet: AttributeSet? = null) : View(ctx, attributeSet) {

    var data: Data? = null
        set(value) {
            invalidate()
            field = value
        }

    var max: Float = 0f
        set(value) {
            invalidate()
            field = value
        }
        get() = if (field == 0f) Math.abs(data?.value ?: DEFAULT_MAX) * 1.2f else field


    val textPaint: Paint by lazy {
        Paint().apply {
            textSize = 20f
            color = Color.BLACK
            isAntiAlias = true
        }
    }

    val rectPaint by lazy {
        Paint().apply {
            strokeWidth = context.dip2px(2f).toFloat()

        }
    }

    val linePaint by lazy {
        Paint().apply {
            color = Color.parseColor("#d8d8d8")
            pathEffect = DashPathEffect(floatArrayOf(5f, 5f), 0f)
        }
    }

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val padding = context.dip2px(20f)

//        val rectW = measuredWidth * 3f / 4f
        textPaint.textSize = context.sp2px(6f).toFloat()

        val textHeight = textPaint.textSize

        val maxH = (measuredHeight - textHeight - padding * 2) / 2f

        val lineH = padding + maxH



        data?.apply {

            //draw rect

            val rectL = measuredWidth / 5f
            val rectR = measuredWidth * 4 / 5f

            if (value > 0) {

                val t = lineH - value * maxH / max
                rectPaint.color = Color.RED

                canvas.drawRect(rectL, t, rectR, lineH, rectPaint)
                val valueW = textPaint.measureText(value.toString())

                canvas.drawText(value.toString(), measuredWidth / 2 - valueW / 2f, t - textHeight, textPaint)

            } else {
                rectPaint.color = Color.GREEN

                val b = lineH - value * maxH / max
                canvas.drawRect(rectL, lineH, rectR, b, rectPaint)
                val valueW = textPaint.measureText(value.toString())

                // canvas.drawText(value.toString(), measuredWidth / 2 - valueW / 2f, b + textHeight*3/2f, textPaint)

                canvas.drawText(value.toString(), measuredWidth / 2 - valueW / 2f, lineH - textHeight, textPaint)


            }

            //draw time

            textPaint.textSize = context.sp2px(10f).toFloat()

            val tw = textPaint.measureText(time)
            canvas.drawText(time, measuredWidth / 2 - tw / 2f, measuredHeight - textHeight / 2f, textPaint)
            // draw horizontal line
            canvas.drawLine(0f, lineH, measuredWidth.toFloat(), lineH, linePaint)

        }
    }

}


fun Context.dip2px(dpValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}

fun Context.sp2px(spValue: Float): Int {
    val fontScale = this.resources.displayMetrics.scaledDensity
    return (spValue * fontScale + 0.5f).toInt()
}
