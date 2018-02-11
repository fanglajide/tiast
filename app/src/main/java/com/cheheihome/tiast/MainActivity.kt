package com.cheheihome.tiast

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
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

                        Tiast.make(this@MainActivity, "Hello World")
                                .anchor(v)
                                .show()
                    }
                }

    }


}
