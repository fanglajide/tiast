package com.wallstreetcn.mpchart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import java.util.*

const val DEFAULT_MAX=1000f

class MPChartView(ctx: Context, attributeSet: AttributeSet? = null) : RecyclerView(ctx, attributeSet) {
    var datas = Collections.emptyList<Data>()

    private val adapter: ChartAdapter by lazy {
        ChartAdapter(context).apply { setData(datas!!) }
    }

    init {
        layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, true)

        addOnScrollListener(object : OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastPosition = (recyclerView?.layoutManager as?  LinearLayoutManager)?.findLastVisibleItemPosition()

                if (!loading && lastPosition == datas?.size ?: 0) {
                    loading = true
                    callback?.loadMore()
                }

            }
        })

        setAdapter(adapter)
    }


    private var loading = false
    private var callback: Callback? = null


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
        fun top()
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


    class VH(ctx: Context) : RecyclerView.ViewHolder(ItemView(ctx)) {

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
        get() = if (field == 0f) Math.abs(data?.value ?: 1000f) * 1.2f else field


    val textPaint: Paint by lazy {
        Paint().apply {
            textSize = 17f
            color = Color.BLACK
        }
    }

    val rectPaint by lazy {
        Paint().apply {
            strokeWidth = context.dip2px(2f).toFloat()
            color = Color.BLACK
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val padding = context.dip2px(20f)

        val rectW = measuredWidth * 3f / 4f
        val textHeight = 17

        val maxH = (measuredHeight - textHeight - padding * 2) / 2f

        val lineH = padding + maxH



        data?.apply {

            // draw horizontal line
            canvas.drawLine(0f, lineH, measuredWidth.toFloat(), lineH, rectPaint)

            //draw rect

            val rectL = measuredWidth / 8f
            val rectR = measuredHeight * 7 / 8f

            if (value > 0) {

                val t = lineH - value * max / maxH
                rectPaint.color = Color.RED

                canvas.drawRect(rectL, t, rectR, lineH, rectPaint)

                canvas.drawText(value.toString(), rectL, t - textHeight, textPaint)

            } else {
                rectPaint.color = Color.GRAY

                val b = lineH - value * max / maxH
                canvas.drawRect(rectL, lineH, rectR, b, rectPaint)

                canvas.drawText(value.toString(), rectL, b + textHeight, textPaint)

            }

            //draw time
            val tw = textPaint.measureText(time)
            canvas.drawText(time, measuredWidth / 2 - tw, measuredHeight - textHeight / 2f, textPaint)

        }
    }

}


fun Context.dip2px(dpValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}


