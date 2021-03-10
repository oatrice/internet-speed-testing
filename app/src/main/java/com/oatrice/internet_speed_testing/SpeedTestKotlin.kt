package com.oatrice.internet_speed_testing

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.internet_speed_testing.InternetSpeedBuilder
import com.example.internet_speed_testing.ProgressionModel
import kotlinx.android.synthetic.main.layout_speedtest_ui.*
import java.math.BigDecimal
import java.text.DecimalFormat

class SpeedTestKotlin : AppCompatActivity() {

    var internetSpeedBuilder:InternetSpeedBuilder?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_speedtest_ui)

        internetSpeedBuilder = InternetSpeedBuilder(this)
        setupView()
    }

    fun setupView(){
        startButton.setOnClickListener {
            buildTestSpeed()

            if(startButton.text == "TEST AGAIN"){
                downloadTextView.text = "0 mb/s"
                uploadTextView.text = "0 mb/s"
                totalTextView.text = "0 mb/s"

                if(downloadTextView.text == "0 mb/s" && uploadTextView.text == "0 mb/s" && totalTextView.text == "0 mb/s"){
                    internetSpeedBuilder = InternetSpeedBuilder(this)
                    buildTestSpeed()
                }

            }
        }
    }

    fun buildTestSpeed(){
        startButton.isEnabled = false
        internetSpeedBuilder?.setOnEventInternetSpeedListener(object: InternetSpeedBuilder.OnEventInternetSpeedListener{
            override fun onProgressCompletion(boolean: Boolean, progressModel: ProgressionModel) {
                if(progressModel.progressDownloadCompletion && progressModel.progressUploadCompletion){
                    runOnUiThread {
                        startButton.text = "TEST AGAIN"
                        startButton.isEnabled = true
                    }
                }
            }

            override fun onTotalProgress(count: Int, progressModel: ProgressionModel) {
                val downloadDecimal = progressModel.downloadSpeed
                val downloadFinal = downloadDecimal.toDouble()

                val uploadDecimal = progressModel.uploadSpeed
                val uploadFinal = uploadDecimal.toDouble()
                val totalSpeedCount = (downloadFinal + uploadFinal) / 2

                runOnUiThread {
                    totalTextView.text = formatFileSize(totalSpeedCount)
                    totalSpeed.text = formatFileSize(totalSpeedCount)
                }
            }

            override fun onUploadProgress(count: Int, progressModel: ProgressionModel) {
                val bd = progressModel.uploadSpeed

                val d = bd.toDouble()

                runOnUiThread {
                    uploadTextView.text = formatFileSize(d)
                }
            }

            override fun onDownloadProgress(count: Int, progressModel: ProgressionModel) {
                val bd = progressModel.downloadSpeed

                val d = bd.toDouble()

                runOnUiThread {
                    downloadTextView.text = formatFileSize(d)
                }
            }

        })
        internetSpeedBuilder?.start("http://ipv4.ikoula.testdebit.info/1M.iso", 1)
    }

    fun formatFileSize(size: Double): String {

        val hrSize: String
        val k = size / 1024.0
        val m = size / 1024.0 / 1024.0
        val g = size / 1024.0 / 1024.0 / 1024.0
        val t = size / 1024.0 / 1024.0 / 1024.0 / 1024.0

        val dec = DecimalFormat("0.00")

        if (t > 1) {
            hrSize = dec.format(t) + " "
        } else if (g > 1) {
            hrSize = dec.format(g)
        } else if (m > 1) {
            hrSize = dec.format(m) + " mb/s"
        } else if (k > 1) {
            hrSize = dec.format(k) + " kb/s"
        } else {
            hrSize = dec.format(size)
        }

        return hrSize
    }
}