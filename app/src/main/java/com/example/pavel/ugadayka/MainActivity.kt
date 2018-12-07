package com.example.pavel.ugadayka

import android.content.Context
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import android.widget.Toast
import com.example.pavel.ugadayka.Adapter.CategoryAdapter
import com.example.pavel.ugadayka.Common.Common
import com.example.pavel.ugadayka.Common.SpacesItemDecoration
import com.example.pavel.ugadayka.DBHelper.DBHelper
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar.title="EnWY"
        setSupportActionBar(toolbar)
        if(!isOnline(this))
        {
            Toast.makeText(this, "Нет соединения с интеренетом", Toast.LENGTH_LONG).show();
           // this@MainActivity.finish();
        }


        Paper.init(this@MainActivity)

        Common.isOnline = Paper.book().read(Common.KEY_ONLINE_MODE,false)

        recycler_category.setHasFixedSize(true)
        recycler_category.layoutManager = GridLayoutManager(this,2)
        val adapter = CategoryAdapter(this,DBHelper.getInstance(this).allCategory)
        recycler_category.addItemDecoration(SpacesItemDecoration(4))
        recycler_category.adapter = adapter

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.setting_menu,menu)
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId == R.id.menu_settings)
            showSettings()
        return true
    }

    private fun showSettings() {
        val setting_layout = LayoutInflater.from(this)
                .inflate(R.layout.setting_layout,null)
        val ckb_online_mode = setting_layout.findViewById<CheckBox>(R.id.ckb_online_mode) as CheckBox

        ckb_online_mode.isChecked = Paper.book().read(Common.KEY_ONLINE_MODE,false)

        MaterialStyledDialog.Builder(this@MainActivity)
                .setIcon(R.drawable.ic_settings_black_24dp)
                .setTitle("Settings")
                .setDescription("Please choose action")
                .setCustomView(setting_layout)
                .setNegativeText("Close")
                .onNegative { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveText("Save")
                .onPositive { dialog, which ->
                    Common.isOnline = ckb_online_mode.isChecked
                    Paper.book().write(Common.KEY_ONLINE_MODE,ckb_online_mode.isChecked)
                }.show()
    }

    companion object {
        fun isOnline(activity:AppCompatActivity):Boolean{
            val connectivityManager=activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo=connectivityManager.activeNetworkInfo
            return  networkInfo!=null && networkInfo.isConnected
        }
    }

}
