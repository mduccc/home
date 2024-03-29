package com.indieteam.home.custom_adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.indieteam.home.activity.HomeActivity
import com.indieteam.home.config.UriApi
import com.tapadoo.alerter.Alerter
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.field_listview.view.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class CustomAdapter (val context: HomeActivity, val layout: Int, val array: List<FieldValue>, val lable: String, val token: String): BaseAdapter() {

    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val layout_dong = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view: View = layout_dong.inflate(layout,null)

        val arr: FieldValue = array[p0]

        val textView = TextView(context)
        val textView5 = TextView(context)
        val switch1 = Switch(context)

        textView.text = arr.field1
        textView5.text = arr.field2

        textView.textSize = 18f
        textView5.textSize = 18f

        textView.typeface = Typeface.DEFAULT_BOLD
        textView5.typeface = Typeface.DEFAULT_BOLD

        switch1.showText = false

        textView.measure(0,0)
        textView5.measure(0,0)
        switch1.measure(0, 0)

        view.rl_value.addView(textView)
        view.rl_value.addView(textView5)
        view.rl_value.addView(switch1)

        textView.x = context.width*5
        textView5.x = context.width*50 - textView5.measuredWidth/2
        switch1.x = context.width*100 - switch1.measuredWidth - context.width*5

        val states = arrayOf(intArrayOf(-android.R.attr.state_checked), intArrayOf(android.R.attr.state_checked))

        val thumbColors = intArrayOf(Color.GRAY, Color.parseColor("#fdc51162"))
        val trackColors = intArrayOf(Color.GRAY, Color.parseColor("#fdc51162"))
        DrawableCompat.setTintList(DrawableCompat.wrap(switch1.thumbDrawable), ColorStateList(states, thumbColors))
        DrawableCompat.setTintList(DrawableCompat.wrap(switch1.trackDrawable), ColorStateList(states, trackColors))

        val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        params.setMargins(0, (context.height*2).toInt(), 0, 0)
        params.width = textView.measuredWidth
        textView.layoutParams = params
        params.width = textView5.measuredWidth
        textView5.layoutParams = params
        params.width = switch1.measuredWidth
        switch1.layoutParams = params

        switch1.isChecked = textView5.text == "On" || textView5.text == "Play"

        switch1.setOnCheckedChangeListener { _, _ ->
            if (switch1.isChecked) {
                when(lable){
                    "đèn" -> {
                        Okhttp().request((UriApi(null,null, arr.field1.toInt(), 1).uriAPiRemote + token))
                        textView5.text = "On"
                    }
                    "bài" -> {
                        Toast.makeText(context, "Play " + lable + " " + arr.field1, Toast.LENGTH_SHORT).show()
                        textView5.text = "Play"
                    }
                    else -> {
                    }
                }
            } else {
                when(lable){
                    "đèn" -> {
                        Okhttp().request((UriApi(null,null, arr.field1.toInt(), 0).uriAPiRemote + token))
                        textView5.text = "Off"
                    }
                    "bài" -> {
                        Toast.makeText(context, "Stop " + lable + " " + arr.field1, Toast.LENGTH_SHORT).show()
                        textView5.text = "Stop"
                    }
                    else -> {
                        textView5.text = "Off"
                    }
                }
            }
        }
        return view
    }

    override fun getItem(p0: Int): Any = null!!

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return array.size
    }

    inner class Okhttp {

        private val client = OkHttpClient()

        fun request(url: String) {
            val rq = Request.Builder()
                    .url(url)
                    .build()

            client.newCall(rq).enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    context.runOnUiThread {
                        Toasty.error(context, "Lỗi mạng", Toast.LENGTH_SHORT, true).show()
                    }
                }

                override fun onResponse(call: Call?, response: Response?) {
                    val body = JSONObject(response?.body()?.string())
                    if(body.getString("status") == "true"){
                        Okhttp_().request(UriApi(null, null, null, null).uriApiMyhome+token)
                        Alerter.create(context)
                                .setTitle("Done")
                                .setText("")
                                .setBackgroundColorInt(Color.parseColor("#fdc51162"))
                                .enableSwipeToDismiss()
                                .show()
                    }
                }
            })
        }
    }

    inner class Okhttp_{

        private val client = OkHttpClient()

        fun request(url: String) {
            val rq = Request.Builder()
                    .url(url)
                    .build()

            client.newCall(rq).enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                }

                override fun onResponse(call: Call?, response: Response?) {
                    val body = JSONObject(response?.body()?.string())
                    if(body.getString("status") == "true"){
                        context.data = body
                    }
                }
            })
        }
    }
}