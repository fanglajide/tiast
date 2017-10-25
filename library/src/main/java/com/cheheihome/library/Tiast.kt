package com.cheheihome.library

import android.content.Context
import android.graphics.Canvas
import android.view.View
import android.view.WindowManager
import android.widget.Toast

/**
 * Created by chanlevel on 2017/10/24.
 */
class Tiast(val ctx: Context) {


    companion object {
        fun show(ctx: Context, str: String) {
            Toast.makeText(ctx, str, Toast.LENGTH_SHORT).show()
        }
    }


    private var view: View? = null
    private val wm: WindowManager by lazy {
        ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }


    init {
    }


    protected fun draw(canvas: Canvas) {


    }


    protected fun calculateRegion(){

    }

    protected fun calculateSize(){

    }

    protected fun anchor(view:View){

    }



}