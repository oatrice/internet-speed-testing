package com.example.internet_speed_testing

import android.os.AsyncTask
import android.util.Log
import fr.bmartel.speedtest.SpeedTestReport
import fr.bmartel.speedtest.SpeedTestSocket
import fr.bmartel.speedtest.inter.ISpeedTestListener
import fr.bmartel.speedtest.model.SpeedTestError

class InternetSpeedBuilder {

    private var countTestSpeed = 0

    var LIMIT = 3
    lateinit var url: String
    lateinit var javaListener: OnEventInternetSpeedListener
    lateinit var onDownloadProgressListener: ()->Unit
    lateinit var onUploadProgressListener: ()->Unit
    lateinit var onTotalProgressListener: ()->Unit

    fun start(url: String) {
        startTestDownload()
    }

    fun setOnEventInternetSpeedListener(javaListener: OnEventInternetSpeedListener) {
        this.javaListener = javaListener
    }

    fun setOnEventInternetSpeedListener(onDownloadProgress: ()->Unit, onUploadProgress: ()->Unit, onTotalProgress: ()->Unit) {
        this.onDownloadProgressListener = onDownloadProgress
        this.onUploadProgressListener = onUploadProgress
        this.onTotalProgressListener = onTotalProgress
    }


    private fun startTestDownload() {
//        speedModel = ProgressionModel(0f, 0f, 0f,  BigDecimal(0), BigDecimal(0))
        SpeedDownloadTestTask().execute()
    }

    private fun startTestUpload() {
        SpeedUploadTestTask().execute()

    }

    interface OnEventInternetSpeedListener {
        fun onDownloadProgress(progressModel: ProgressionModel)
        fun onUploadProgress(progressModel: ProgressionModel)
        fun onTotalProgress(progressModel: ProgressionModel)
    }

    inner class SpeedDownloadTestTask : AsyncTask<Void, Void, String>() {

        private val downloadProgressModel = ProgressionModel()
        private val totalProgressModel = ProgressionModel()

        override fun doInBackground(vararg params: Void): String? {

            val speedTestSocket = SpeedTestSocket()

            // add a listener to wait for speedtest completion and progress
            speedTestSocket.addSpeedTestListener(object : ISpeedTestListener {

                override fun onCompletion(report: SpeedTestReport) {
                    // called when download/upload is finished
                    Log.v("speedtest Download" + countTestSpeed, "[COMPLETED] rate in octet/s : " + report.transferRateOctet)
                    Log.v("speedtest Download" + countTestSpeed, "[COMPLETED] rate in bit/s   : " + report.transferRateBit)

                    downloadProgressModel.progressTotal = 50f
                    downloadProgressModel.progressDownload = 100f
                    downloadProgressModel.downloadSpeed = report.transferRateBit

                    totalProgressModel.progressTotal = 50f
                    totalProgressModel.progressDownload = 100f
                    totalProgressModel.downloadSpeed = report.transferRateBit

                    javaListener.onDownloadProgress(downloadProgressModel)
                    javaListener.onTotalProgress(totalProgressModel)

                    startTestUpload()

                }

                override fun onError(speedTestError: SpeedTestError, errorMessage: String) {
                    // called when a download/upload error occur
                }

                override fun onProgress(percent: Float, report: SpeedTestReport) {
                    // called to notify download/upload progress
                    Log.v("speedtest Download" + countTestSpeed, "[PROGRESS] progress : $percent%")
                    Log.v("speedtest Download" + countTestSpeed, "[PROGRESS] rate in octet/s : " + report.transferRateOctet)
                    Log.v("speedtest Download" + countTestSpeed, "[PROGRESS] rate in bit/s   : " + report.transferRateBit)

                    val downloadProgressModel = ProgressionModel()
                    downloadProgressModel.progressTotal = percent / 2
                    downloadProgressModel.progressDownload = percent
                    downloadProgressModel.downloadSpeed = report.transferRateBit

                    val totalProgressModel = ProgressionModel()
                    totalProgressModel.progressTotal = percent / 2
                    totalProgressModel.progressDownload = percent
                    totalProgressModel.downloadSpeed = report.transferRateBit

                    javaListener.onDownloadProgress(downloadProgressModel)
                    javaListener.onTotalProgress(totalProgressModel)
                }
            })

            speedTestSocket.startDownload(url)

            return null
        }
    }

    inner class SpeedUploadTestTask : AsyncTask<Void, Void, Void>() {

        private val progressModel = ProgressionModel()

        override fun doInBackground(vararg params: Void): Void? {

            val speedTestSocket = SpeedTestSocket()

            // add a listener to wait for speedtest completion and progress
            speedTestSocket.addSpeedTestListener(object : ISpeedTestListener {

                override fun onCompletion(report: SpeedTestReport) {
                    // called when download/upload is finished
                    Log.v("speedtest Upload" + countTestSpeed, "[COMPLETED] rate in octet/s : " + report.transferRateOctet)
                    Log.v("speedtest Upload" + countTestSpeed, "[COMPLETED] rate in bit/s   : " + report.transferRateBit)

                    progressModel.progressTotal = 100f
                    progressModel.progressDownload = 100f
                    progressModel.downloadSpeed = report.transferRateBit

                    javaListener.onUploadProgress(progressModel)
                    javaListener.onTotalProgress(progressModel)

                    countTestSpeed++
                    if (countTestSpeed < LIMIT) {
                        startTestDownload()
                    }
                }

                override fun onError(speedTestError: SpeedTestError, errorMessage: String) {
                    // called when a download/upload error occur
                }

                override fun onProgress(percent: Float, report: SpeedTestReport) {
                    // called to notify download/upload progress
                    Log.v("speedtest Upload" + countTestSpeed, "[PROGRESS] progress : $percent%")
                    Log.v("speedtest Upload" + countTestSpeed, "[PROGRESS] rate in octet/s : " + report.transferRateOctet)
                    Log.v("speedtest Upload" + countTestSpeed, "[PROGRESS] rate in bit/s   : " + report.transferRateBit)

                    progressModel.progressTotal = percent / 2 + 50
                    progressModel.progressUpload= percent
                    progressModel.downloadSpeed = report.transferRateBit

                    javaListener.onUploadProgress(progressModel)
                    javaListener.onTotalProgress(progressModel)
                }
            })

            speedTestSocket.startDownload(url)

            return null
        }
    }

}