package com.oatrice.internet_speed_testing

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button
import com.example.internet_speed_testing.InternetSpeedBuilder
import com.example.internet_speed_testing.ProgressionModel

class MainKotlinActivity : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    private var adapter: Adapter? = null

    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private var started: Boolean = false

    private val builder = InternetSpeedBuilder(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerview)
        startButton = findViewById(R.id.start_button)
        stopButton = findViewById(R.id.stop_button)

        adapter = Adapter()
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.adapter = adapter

        startButton.setOnClickListener { view ->
            if(started) {
                started = false
                adapter!!.clearData()
            }
            startSpeedTest()

            // TODO[Restart properly]: Find a proper way to stop both socket instead of using this hack
            Handler().postDelayed({
                enableStopButton()
            },10000)
        }

        stopButton.setOnClickListener { view ->
            stopSpeedTest()
        }
    }

    private fun startSpeedTest() {
        builder.setOnEventInternetSpeedListener(object : InternetSpeedBuilder.OnEventInternetSpeedListener {
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

    private fun stopSpeedTest() {
        builder.stop()
    }

    private fun enableStopButton() {
        stopButton.isEnabled = true
    }
}