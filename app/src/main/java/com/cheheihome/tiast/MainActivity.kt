package com.cheheihome.tiast

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.wallstreetcn.mpchart.Data
import com.wallstreetcn.mpchart.MPChartView
import kotlinx.android.synthetic.main.activity_main.*
import me.lecc.tiast.Tiast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listOf<TextView>(button, button2, button3, button4, button5, textView)
                .forEach { v ->
                    v.setOnClickListener {
                        // Toast.makeText(this@MainActivity, v.text, Toast.LENGTH_SHORT).show()

                        Tiast.make(this@MainActivity, "Hello World,Hello Android")
                                .anchor(v)
                                .show()
                    }
                }
        mp()
    }


    private fun mp() {
        val datas = mutableListOf<Data>()


        datas.addAll(fakeData())

        mp.setData(datas)

        mp.setCallback(object : MPChartView.Callback {

            override fun loadMore() {
                datas.addAll(fakeData())
                mp.setData(datas)
                if (datas.size > 100) mp.dataFinish(true)
            }

        })

    }

    private var cc = 1
    private fun fakeData(): List<Data> {
        val datas = mutableListOf<Data>()

        (0..20)
                .forEachIndexed { index, i ->

                    val data = Data((if (index % 2 == 0) -1 else 1) * 1000f + Math.random().toFloat() * 800f, cc++.toString())
                    datas.add(data)

                }
        return datas
    }


    private val ts = mutableListOf<Tiast>()


}
