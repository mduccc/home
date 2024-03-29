package com.indieteam.home.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageInfo
import android.graphics.Point
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import com.example.duc25.activity.R
import com.indieteam.home.config.UriApi
import com.indieteam.home.custom_adapter.CustomAdapter
import com.indieteam.home.custom_adapter.FieldValue
import com.indieteam.home.fragment.dialog.Confim
import com.indieteam.home.modules.HomeService
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.content_home.*
import kotlinx.android.synthetic.main.listview.*
import kotlinx.android.synthetic.main.nav_header_home.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException


@Suppress("DEPRECATION")
open class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var jsonDataUser: JSONObject
    lateinit var update: String
    lateinit var data: JSONObject
    private var closeApp = 2
    private lateinit var packageInfo: PackageInfo
    private lateinit var versionName: String
    var width = 0f
    var height = 0f
    private lateinit var light_name: TextView
    private lateinit var light_status: TextView
    private lateinit var light_on_off: TextView
    var isNet = -1

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)
        setSize()
        setUI()
        getDataJson()
        val uriApiMyhome = UriApi(null, null, null, null).uriApiMyhome + getToken()

        swipeResfresh.setOnRefreshListener {
            swipeResfresh.isRefreshing = false
            startActivity(intent)
            finish()
        }

        rl_content_home.setOnClickListener {
            if(isNet == 0) {
                startActivity(intent)
                finish()
            }
        }
        Okhttp().request(uriApiMyhome)
    }

    private fun setUI(){
        swipeResfresh.visibility = View.GONE
        light_name = TextView(this)
        light_status = TextView(this)
        light_on_off = TextView(this)

        light_name.text = ""
        light_on_off.text = ""
        light_status.text = ""

        light_name.textSize = 18f
        light_status.textSize = 18f
        light_on_off.textSize = 18f

        light_name.y = height*7f
        light_status.y = height*7f
        light_on_off.y = height*7f

        light_name.typeface = Typeface.DEFAULT_BOLD
        light_status.typeface = Typeface.DEFAULT_BOLD
        light_on_off.typeface = Typeface.DEFAULT_BOLD

        light_name.setTextColor(resources.getColor(R.color.colorOrange1))
        light_status.setTextColor(resources.getColor(R.color.colorOrange1))
        light_on_off.setTextColor(resources.getColor(R.color.colorOrange1))

        rl_content_home.addView(light_name)
        rl_content_home.addView(light_status)
        rl_content_home.addView(light_on_off)
    }

    private fun updateUI(){
        light_name.measure(0, 0)
        light_status.measure(0,0)
        light_on_off.measure(0, 0)
        light_name.x = width*5
        light_status.x = width*50 - light_status.measuredWidth/2
        light_on_off.x = width*100 - light_on_off.measuredWidth - width*5
    }

    private fun setSize(){
        val manager = windowManager.defaultDisplay
        val size = Point()
        manager.getSize(size)
        width = size.x/100f
        height = size.y/100f
    }

    private fun getDataJson(){
        val jsonUsers = intent.getStringExtra("json")
        val jsonObj = JSONObject(jsonUsers)
        val info: String = jsonObj.getString("user_info")
        val jsonObjInfo = JSONObject(info)
        jsonDataUser = jsonObjInfo
    }

    private fun getToken(): String{
        return jsonDataUser.getString("token")
    }

    private fun setInfo(){
        val username: String = jsonDataUser.getString("username")
        val email: String = jsonDataUser.getString("email")
        profile_user_name.text = username
        profile_user_email.text = email
    }

    override fun onBackPressed() {
        closeApp--
        if(closeApp > 0){
            Toast.makeText(this, "Nhấn $closeApp lần nữa để thoát", Toast.LENGTH_SHORT).show()
        }
        if(closeApp <=0 ){
            finish()
        }
        return
    }

    @SuppressLint("SetTextI18n")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val jsonArr = data.getJSONArray("light_status")

        when (item.itemId) {
            R.id.nav_light -> {
                swipeResfresh.visibility = View.VISIBLE
                val switchBtn = Switch(this)

                var arrayView = listOf<FieldValue>()
                for(i in 0 until jsonArr.length()) {
                    val idLight = jsonArr.getJSONObject(i).getString("id_light")
                    val status = jsonArr.getJSONObject(i).getString("status")
                    var change: String
                    change = if(status == "0"){
                        "Off"
                    }else{
                        "On"
                    }
                    arrayView += FieldValue(idLight, change, switchBtn)

                }
                list_view.adapter = CustomAdapter(this, R.layout.field_listview, arrayView, "đèn", getToken())
                process_update.visibility = View.GONE
                light_name.text = "Đèn"
                light_status.text = "Trạng thái"
                light_on_off.text = "Công tắc"
                title = "Đèn"
                updateUI()
            }
            R.id.about -> {
                swipeResfresh.visibility = View.GONE
                process_update.visibility = View.VISIBLE
                try {
                    packageInfo = packageManager.getPackageInfo(packageName, 0)
                    versionName = packageInfo.versionName
                    process_update.text = "App Name: " + resources.getString(R.string.app_name) +
                            "\n Version: $versionName (private beta)" +
                            "\n Developer: Nguyen Minh Duc " +
                            "\n Team: " + resources.getString(R.string.textview8) +
                            "\n\n What news: \n" + resources.getString(R.string.whatnew)
                } catch (e: Exception) {
                }
                light_name.text = ""
                light_on_off.text = ""
                light_status.text = ""
                title = "About"
            }
            R.id.nav_music -> {
                swipeResfresh.visibility = View.VISIBLE
                val switchBtn = Switch(this)

                val arrayView = listOf(
                        FieldValue("Tell me you ...","Play", switchBtn),
                        FieldValue("Galaxy","Stop", switchBtn),
                        FieldValue("Mr.Chu","Stop", switchBtn),
                        FieldValue("Back","Stop", switchBtn),
                        FieldValue("Fix me","Stop", switchBtn),
                        FieldValue("Blue","Stop", switchBtn),
                        FieldValue("Pick me","Stop", switchBtn),
                        FieldValue("So so","Stop", switchBtn),
                        FieldValue("First Love","Stop", switchBtn),
                        FieldValue("Not him","Stop", switchBtn)
                )
                list_view.adapter = CustomAdapter(this, R.layout.field_listview, arrayView, "bài", getToken())
                process_update.visibility = View.GONE
                light_name.text = "Bài hát"
                light_status.text = "Trạng thái"
                light_on_off.text = "Chơi/Dừng"
                title = "Chơi nhạc"
                updateUI()
            }
            R.id.for_developer -> {
                if(update == "updated") {
                    swipeResfresh.visibility = View.GONE
                    process_update.text = ""
                    light_name.text = ""
                    light_on_off.text = ""
                    light_status.text = ""
                    var temp = ""
                    for(i in 0 until jsonArr.length()) {
                        val idLight = jsonArr.getJSONObject(i).getString("id_light")
                        val status = jsonArr.getJSONObject(i).getString("status")
                        temp += "id_light: $idLight, status: $status \n"
                    }
                    title = "Raw Json"
                    process_update.visibility = View.VISIBLE
                    process_update.text = jsonDataUser.toString() + "\n\n" + data.toString()
                    Toasty.success(this, temp, Toast.LENGTH_SHORT, true).show()
                }
            }
            R.id.logout -> {
                Confim(this).show(fragmentManager, "logout")
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    inner class Okhttp {

        private val client = OkHttpClient()

        fun request(url: String) {
            val rq = Request.Builder()
                    .url(url)
                    .build()

            client.newCall(rq).enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    isNet = 0
                    this@HomeActivity.runOnUiThread {
                        process_update.text = "Lỗi mạng, chạm để thử lại"
                        process_update.setTextColor(resources.getColor(R.color.ColorSecondary))                    }
                }

                override fun onResponse(call: Call?, response: Response?) {
                    isNet = 1
                    val body = JSONObject(response?.body()?.string())
                    if (body.getString("status") == "true") {
                        data = body
                        //start service
                        val i = Intent(this@HomeActivity, HomeService::class.java)
                        i.putExtra("content", getToken())
                        startService(i)

                        //update view
                        this@HomeActivity.runOnUiThread {
                            update = "updated"
                            process_update.visibility = View.VISIBLE
                            process_update.text = "Dữ liệu đã được cập nhật"
                            val toggle = ActionBarDrawerToggle(
                                    this@HomeActivity, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
                            drawer_layout.addDrawerListener(toggle)
                            toggle.syncState()
                            nav_view.setNavigationItemSelectedListener(this@HomeActivity)
                            drawer_layout.openDrawer(Gravity.LEFT)
                            setInfo()
                        }
                    }
                }
            })
        }
    }
}
