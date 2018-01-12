package com.example.internet_speed_testing

import android.app.Activity
import android.os.AsyncTask
import android.util.Log
import fr.bmartel.speedtest.SpeedTestReport
import fr.bmartel.speedtest.SpeedTestSocket
import fr.bmartel.speedtest.inter.ISpeedTestListener
import fr.bmartel.speedtest.model.SpeedTestError

class InternetSpeedBuilder(var activity: Activity) {

    private val UPLOAD_FILE_SIZE = 1 * 1024 * 1024 // 10 MB
    private val NOT_FIXED_DURATION = -1

    private var countTestSpeed = 0
    private var LIMIT = 3
    private var fixedDuration = 10

    lateinit var downloadUrl: String
    lateinit var uploadUrl: String
    lateinit var javaListener: OnEventInternetSpeedListener
    lateinit var onDownloadProgressListener: ()->Unit
    lateinit var onUploadProgressListener: ()->Unit
    lateinit var onTotalProgressListener: ()->Unit

    private lateinit var progressModel: ProgressionModel

    fun startDownloadUpload(downloadUrl: String, uploadUrl: String, limitCount: Int) {
        this.downloadUrl = downloadUrl
        this.uploadUrl = uploadUrl
        this.LIMIT = limitCount
        this.fixedDuration = NOT_FIXED_DURATION
        startTestDownload()
    }

    fun startDownloadUpload(downloadUrl: String, uploadUrl: String, limitCount: Int, fixedDuration: Int) {
        this.downloadUrl = downloadUrl
        this.uploadUrl = uploadUrl
        this.LIMIT = limitCount
        this.fixedDuration = fixedDuration
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
        progressModel = ProgressionModel()
        SpeedDownloadTestTask().execute()
    }

    private fun startTestUpload() {
        SpeedUploadTestTask().execute()

    }

    interface OnEventInternetSpeedListener {
        fun onDownloadProgress(count: Int, progressModel: ProgressionModel)
        fun onUploadProgress(count: Int, progressModel: ProgressionModel)
        fun onTotalProgress(count: Int, progressModel: ProgressionModel)
        fun onDownloadError(speedTestError: SpeedTestError, errorMessage: String)
        fun onUploadError(speedTestError: SpeedTestError, errorMessage: String)
    }

    inner class SpeedDownloadTestTask : AsyncTask<Void, Void, String>() {

        val startTime = System.currentTimeMillis()

        override fun doInBackground(vararg params: Void): String? {

            val speedTestSocket = SpeedTestSocket()

            // add a listener to wait for speedtest completion and progress
            speedTestSocket.addSpeedTestListener(object : ISpeedTestListener {

                override fun onCompletion(report: SpeedTestReport) {
                    // called when download/upload is finished
                    Log.v("Speedtest Download: " + countTestSpeed, "[COMPLETED] rate in octet/s : " + report.transferRateOctet)
                    Log.v("Speedtest Download: " + countTestSpeed, "[COMPLETED] rate in bit/s   : " + report.transferRateBit)

                    /*downloadProgressModel.progressTotal = 50f
                    downloadProgressModel.progressDownload = 100f
                    downloadProgressModel.downloadSpeed = report.transferRateBit

                    totalProgressModel.progressTotal = 50f
                    totalProgressModel.progressDownload = 100f
                    totalProgressModel.downloadSpeed = report.transferRateBit

                    activity.runOnUiThread {
                        javaListener.onDownloadProgress(countTestSpeed, downloadProgressModel)
                        javaListener.onTotalProgress(countTestSpeed, totalProgressModel)

                    }*/

                    startTestUpload()

                }

                override fun onError(speedTestError: SpeedTestError, errorMessage: String) {
                    // called when a download/upload error occur
                    Log.e("Speedtest Download: " + countTestSpeed, "[ERROR] SpeedTestError : ${speedTestError.name}")
                    Log.e("Speedtest Download: " + countTestSpeed, "[ERROR] ErrorMessage : ${errorMessage}")

                    javaListener.onDownloadError(speedTestError, errorMessage)
                }

                override fun onProgress(percent: Float, report: SpeedTestReport) {
                    // called to notify download/upload progress
                    Log.d("Speedtest Download: " + countTestSpeed, "[PROGRESS] progress : $percent%")
                    Log.d("Speedtest Download: " + countTestSpeed, "[PROGRESS] rate in octet/s : " + report.transferRateOctet)
                    Log.d("Speedtest Download: " + countTestSpeed, "[PROGRESS] rate in bit/s   : " + report.transferRateBit)


                    val endTime = System.currentTimeMillis()
                    val diffTime = endTime - startTime

                    progressModel.count = countTestSpeed
                    progressModel.progressTotal = percent / 2
                    progressModel.progressDownload = percent
                    progressModel.downloadSpeed = report.transferRateBit
                    progressModel.downloadDuration = diffTime

                    activity.runOnUiThread {
                        javaListener.onDownloadProgress(countTestSpeed, progressModel)
                        javaListener.onTotalProgress(countTestSpeed, progressModel)
                    }

                }
            })

            if (fixedDuration == NOT_FIXED_DURATION) {
                speedTestSocket.startDownload(downloadUrl)

            } else {
                speedTestSocket.startFixedDownload(downloadUrl, fixedDuration)

            }

            return null
        }
    }

    inner class SpeedUploadTestTask : AsyncTask<Void, Void, Void>() {

        val startTime = System.currentTimeMillis()

        override fun doInBackground(vararg params: Void): Void? {

            val speedTestSocket = SpeedTestSocket()

            // add a listener to wait for speedtest completion and progress
            speedTestSocket.addSpeedTestListener(object : ISpeedTestListener {

                override fun onCompletion(report: SpeedTestReport) {
                    // called when download/upload is finished
                    Log.v("Speedtest Uploadload: " + countTestSpeed, "[COMPLETED] rate in octet/s : " + report.transferRateOctet)
                    Log.v("Speedtest Uploadload: " + countTestSpeed, "[COMPLETED] rate in bit/s   : " + report.transferRateBit)

                    /*progressModel.progressTotal = 100f
                    progressModel.progressUpload= 100f
                    progressModel.uploadSpeed = report.transferRateBit

                    activity.runOnUiThread {
                        javaListener.onUploadProgress(countTestSpeed, progressModel)
                        javaListener.onTotalProgress(countTestSpeed, progressModel)
                    }*/


                    countTestSpeed++
                    if (countTestSpeed < LIMIT) {
                        startTestDownload()
                    }
                }

                override fun onError(speedTestError: SpeedTestError, errorMessage: String) {
                    // called when a download/upload error occur

                    Log.e("Speedtest Uploadload: " + countTestSpeed, "[ERROR] SpeedTestError : ${speedTestError.name}")
                    Log.e("Speedtest Uploadload: " + countTestSpeed, "[ERROR] ErrorMessage : ${errorMessage}")
                    
                    javaListener.onUploadError(speedTestError, errorMessage)
                }

                override fun onProgress(percent: Float, report: SpeedTestReport) {
                    // called to notify download/upload progress
                    Log.d("Speedtest Uploadload: " + countTestSpeed, "[PROGRESS] progress : $percent%")
                    Log.d("Speedtest Uploadload: " + countTestSpeed, "[PROGRESS] rate in octet/s : " + report.transferRateOctet)
                    Log.d("Speedtest Uploadload: " + countTestSpeed, "[PROGRESS] rate in bit/s   : " + report.transferRateBit)

                    val endTime = System.currentTimeMillis()
                    val diffTime = endTime - startTime

                    progressModel.progressTotal = percent / 2 + 50
                    progressModel.progressUpload = percent
                    progressModel.uploadSpeed= report.transferRateBit
                    progressModel.uploadDuration= diffTime

                    activity.runOnUiThread {

                        if (countTestSpeed < LIMIT) {
                            javaListener.onUploadProgress(countTestSpeed, progressModel)
                            javaListener.onTotalProgress(countTestSpeed, progressModel)

                        }
                    }

                }
            })

            if (fixedDuration == NOT_FIXED_DURATION) {
                speedTestSocket.startUpload(uploadUrl, UPLOAD_FILE_SIZE)

            } else {
                speedTestSocket.startFixedUpload(uploadUrl, UPLOAD_FILE_SIZE, fixedDuration)

            }

            return null
        }
    }

}