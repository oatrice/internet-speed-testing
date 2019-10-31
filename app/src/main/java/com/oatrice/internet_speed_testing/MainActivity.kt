package com.oatrice.internet_speed_testing

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.internet_speed_testing.InternetSpeedBuilder
import com.example.internet_speed_testing.InternetSpeedBuilder.*
import com.example.internet_speed_testing.ProgressionModel

/**
 * Created by Ajay Deepak on 30-10-2019, 21:04
 */

class MainActivity : AppCompatActivity(){

    private var recyclerView: RecyclerView? = null
    private var adapter: Adapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recyclerview)
        adapter = Adapter()
        recyclerView?.apply {
            adapter = adapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        val builder = InternetSpeedBuilder(this)
        builder.setOnEventInternetSpeedListener(object : OnEventInternetSpeedListener {
            override fun onDownloadProgress(count: Int, progressModel: ProgressionModel) {

            }

            override fun onUploadProgress(count: Int, progressModel: ProgressionModel) {

            }

            override fun onTotalProgress(count: Int, progressModel: ProgressionModel) {
                adapter!!.setDataList(count, progressModel)

            }
        })
        builder.start("http://2.testdebit.info/fichiers/1Mo.dat", 20)
    }
}