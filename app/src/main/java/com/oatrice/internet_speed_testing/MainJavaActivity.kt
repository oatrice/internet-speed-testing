package com.oatrice.internet_speed_testing

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.internet_speed_testing.InternetSpeedBuilder
import com.example.internet_speed_testing.ProgressionModel
import kotlinx.android.synthetic.main.activity_main.*

class MainJavaActivity : AppCompatActivity() {

    private lateinit var adapter: Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = Adapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val builder = InternetSpeedBuilder(this)
        builder.setOnEventInternetSpeedListener(object : InternetSpeedBuilder.OnEventInternetSpeedListener {
            override fun onDownloadProgress(count: Int, progressModel: ProgressionModel) {

            }

            override fun onUploadProgress(count: Int, progressModel: ProgressionModel) {

            }

            override fun onTotalProgress(count: Int, progressModel: ProgressionModel) {
                adapter.setDataList(count, progressModel)

            }
        })
        builder.start("http://2.testdebit.info/fichiers/1Mo.dat", 20)
    }
}
