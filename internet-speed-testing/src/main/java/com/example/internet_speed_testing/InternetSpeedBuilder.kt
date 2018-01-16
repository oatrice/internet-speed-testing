package com.example.internet_speed_testing

import android.app.Activity
import android.os.AsyncTask
import android.util.Log
import com.stealthcopter.networktools.Ping
import com.stealthcopter.networktools.ping.PingResult
import com.stealthcopter.networktools.ping.PingStats
import fr.bmartel.speedtest.SpeedTestReport
import fr.bmartel.speedtest.SpeedTestSocket
import fr.bmartel.speedtest.inter.IRepeatListener
import fr.bmartel.speedtest.model.SpeedTestError
import java.net.UnknownHostException

class InternetSpeedBuilder(var activity: Activity) {

    private val UPLOAD_FILE_SIZE = 3 * 1024 * 1024 // 3 MB
    private val NOT_FIXED_DURATION = -1

    private var countTestSpeed = 0
    private var LIMIT = 3
    private var fixedDownloadDuration = 10
    private var fixedUploadDuration = 10

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
        this.fixedDownloadDuration = NOT_FIXED_DURATION
        this.fixedUploadDuration = NOT_FIXED_DURATION
        startTestDownload()
    }

    fun startDownloadUpload(downloadUrl: String, uploadUrl: String, limitCount: Int, fixedDuration: Int) {
        this.downloadUrl = downloadUrl
        this.uploadUrl = uploadUrl
        this.LIMIT = limitCount
        this.fixedDownloadDuration = fixedDuration
        this.fixedUploadDuration = fixedDuration
        startTestDownload()
    }

    fun startDownloadUpload(downloadUrl: String, uploadUrl: String, limitCount: Int, fixedDownloadDuration: Int, fixedUploadDuration: Int) {
        this.downloadUrl = downloadUrl
        this.uploadUrl = uploadUrl
        this.LIMIT = limitCount
        this.fixedDownloadDuration = fixedDownloadDuration
        this.fixedUploadDuration = fixedUploadDuration

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
        startTestPing()
    }

    private fun startTestUpload() {
        SpeedUploadTestTask().execute()

    }

    private fun startTestPing() {
        SpeedTestPing().execute()
    }

    interface OnEventInternetSpeedListener {
        fun onDownloadProgress(count: Int, progressModel: ProgressionModel)
        fun onUploadProgress(count: Int, progressModel: ProgressionModel)
        fun onPingProgress(pingResult: ProgressionModel)
        fun onTotalProgress(count: Int, progressModel: ProgressionModel)
        fun onDownloadError(speedTestError: SpeedTestError, errorMessage: String)
        fun onUploadError(speedTestError: SpeedTestError, errorMessage: String)
    }

    inner class SpeedDownloadTestTask : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void): String? {

            val speedTestSocket = SpeedTestSocket()

            speedTestSocket.startDownloadRepeat(downloadUrl, fixedDownloadDuration,
                    100, object : IRepeatListener {
                override fun onCompletion(report: SpeedTestReport) {
                    // called when repeat task is finished
                    Log.v("Speedtest Upload: " + report.requestNum, "[onCompletion] getProgressPercent : " + report.progressPercent)
                    Log.v("Speedtest Upload: " + report.requestNum, "[onCompletion] rate in octet/s : " + report.transferRateOctet)
                    Log.v("Speedtest Upload: " + report.requestNum, "[onCompletion] Time : " + (report.reportTime - report.startTime))

                    sendDownloadData(report)
                    startTestUpload()
                }

                override fun onReport(report: SpeedTestReport) {
                    // called when an upload report is dispatched
                    Log.d("Speedtest Upload: " + report.requestNum, "[onReport] getProgressPercent : " + report.progressPercent)
                    Log.d("Speedtest Upload: " + report.requestNum, "[onReport] rate in octet/s : " + report.transferRateOctet)
                    Log.d("Speedtest Upload: " + report.requestNum, "[onReport] Time : " + (report.reportTime - report.startTime))

                    sendDownloadData(report)
                }
            })

            return null
        }
    }

    private fun sendDownloadData(report: SpeedTestReport) {
        val diffTime = report.reportTime - report.startTime

        progressModel.count = countTestSpeed
        progressModel.progressTotal = report.progressPercent / 2
        progressModel.progressDownload = report.progressPercent
        progressModel.downloadSpeed = report.transferRateBit
        progressModel.downloadDuration = diffTime

        activity.runOnUiThread {
            javaListener.onDownloadProgress(countTestSpeed, progressModel)
            javaListener.onTotalProgress(countTestSpeed, progressModel)
        }
    }

    inner class SpeedUploadTestTask : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg params: Void): Void? {

            val speedTestSocket = SpeedTestSocket()

            speedTestSocket.startUploadRepeat(uploadUrl, fixedUploadDuration,
                    100, UPLOAD_FILE_SIZE, object : IRepeatListener {
                override fun onCompletion(report: SpeedTestReport) {
                    // called when repeat task is finished
                    Log.v("Speedtest Upload: " + report.requestNum, "[onCompletion] getProgressPercent : " + report.progressPercent)
                    Log.v("Speedtest Upload: " + report.requestNum, "[onCompletion] rate in octet/s : " + report.transferRateOctet)
                    Log.v("Speedtest Upload: " + report.requestNum, "[onCompletion] Time : " + (report.reportTime - report.startTime))

                    sendUploadData(report)

                    countTestSpeed++
                    if (countTestSpeed < LIMIT) {
                        startTestDownload()
                    }
                }

                override fun onReport(report: SpeedTestReport) {
                    // called when an upload report is dispatched
                    Log.d("Speedtest Upload: " + report.requestNum, "[onReport] getProgressPercent : " + report.progressPercent)
                    Log.d("Speedtest Upload: " + report.requestNum, "[onReport] rate in octet/s : " + report.transferRateOctet)
                    Log.d("Speedtest Upload: " + report.requestNum, "[onReport] Time : " + (report.reportTime - report.startTime))

                    sendUploadData(report)
                }
            })

            return null
        }
    }

    private fun sendUploadData(report: SpeedTestReport) {
        val diffTime = report.reportTime - report.startTime

        progressModel.progressTotal = report.progressPercent / 2 + 50
        progressModel.progressUpload = report.progressPercent
        progressModel.uploadSpeed = report.transferRateBit
        progressModel.uploadDuration = diffTime

        activity.runOnUiThread {

            if (countTestSpeed < LIMIT) {
                javaListener.onUploadProgress(countTestSpeed, progressModel)
                javaListener.onTotalProgress(countTestSpeed, progressModel)

            }
        }
    }

    inner class SpeedTestPing : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg voids: Void): String? {

            try {
                Ping.onAddress("www.baidu.com").setTimeOutMillis(1000).setTimes(5).doPing(object : Ping.PingListener {
                    override fun onResult(pingResult: PingResult) {
                        sendPingData(pingResult)
                        Log.e("onPing3 : ", "Ping" + pingResult.getTimeTaken())
                    }

                    override fun onFinished(pingStats: PingStats) {

                        Log.e("onPing2 : ", "Ping" + pingStats)
                    }
                })
            } catch (e: UnknownHostException) {
                e.printStackTrace()
            }

            return null
        }
    }

    private fun sendPingData(pingResult: PingResult) {
        activity.runOnUiThread {
            progressModel.pingDuration = pingResult.timeTaken
            javaListener.onPingProgress(progressModel)
        }
    }
}