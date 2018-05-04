package com.cheheihome.tiast

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.wallstreetcn.mpchart.Data
import kotlinx.android.synthetic.main.activity_main.*
import me.lecc.tiast.Tiast
import java.util.*

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

        val calendar = Calendar.getInstance()

        (0..20)
                .forEachIndexed { index, i ->

                    val data = Data((if (index % 2 == 0) -1 else 1) * 1000f + Math.random().toFloat() * 800f, calendar.get(Calendar.DATE).toString())
                    datas.add(data)
                    calendar.add(Calendar.DATE, -1)
                }

        mp.setData(datas)

    }

    private val ts = mutableListOf<Tiast>()


}
