package com.oatrice.internet_speed_testing;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.math.BigDecimal;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;

public class MainActivity extends AppCompatActivity {

    int countTestSpeed = 0;
    int LIMIT = 3;

    private RecyclerView recyclerView;
    private Adapter adapter;
    private SpeedModel speedModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerview);

        startTestDownload();

        adapter = new Adapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

    private void startTestDownload() {
        speedModel = new SpeedModel(0, new BigDecimal(0), new BigDecimal(0));
        new SpeedDownloadTestTask().execute();
    }

    private void startTestUpload() {
        new SpeedUploadTestTask().execute();

    }

    public class SpeedDownloadTestTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            SpeedTestSocket speedTestSocket = new SpeedTestSocket();

            // add a listener to wait for speedtest completion and progress
            speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

                @Override
                public void onCompletion(SpeedTestReport report) {
                    // called when download/upload is finished
                    Log.v("speedtest Download" + countTestSpeed, "[COMPLETED] rate in octet/s : " + report.getTransferRateOctet());
                    Log.v("speedtest Download" + countTestSpeed, "[COMPLETED] rate in bit/s   : " + report.getTransferRateBit());

                    speedModel.progress = 50;
                    speedModel.downloadSpeed = report.getTransferRateBit();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.setDataList(countTestSpeed, speedModel);

                        }
                    });

                    startTestUpload();

                }

                @Override
                public void onError(SpeedTestError speedTestError, String errorMessage) {
                    // called when a download/upload error occur
                }

                @Override
                public void onProgress(float percent, SpeedTestReport report) {
                    // called to notify download/upload progress
                    Log.v("speedtest Download" + countTestSpeed, "[PROGRESS] progress : " + percent + "%");
                    Log.v("speedtest Download" + countTestSpeed, "[PROGRESS] rate in octet/s : " + report.getTransferRateOctet());
                    Log.v("speedtest Download" + countTestSpeed, "[PROGRESS] rate in bit/s   : " + report.getTransferRateBit());

                    speedModel.progress = percent / 2;
                    speedModel.downloadSpeed = report.getTransferRateBit();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.setDataList(countTestSpeed, speedModel);

                        }
                    });
                }
            });

            speedTestSocket.startDownload("http://2.testdebit.info/fichiers/1Mo.dat");

            return null;
        }
    }

    public class SpeedUploadTestTask extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... params) {

            SpeedTestSocket speedTestSocket = new SpeedTestSocket();

            // add a listener to wait for speedtest completion and progress
            speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

                @Override
                public void onCompletion(SpeedTestReport report) {
                    // called when download/upload is finished
                    Log.v("speedtest Upload" + countTestSpeed, "[COMPLETED] rate in octet/s : " + report.getTransferRateOctet());
                    Log.v("speedtest Upload" + countTestSpeed, "[COMPLETED] rate in bit/s   : " + report.getTransferRateBit());

                    speedModel.progress = 100;
                    speedModel.uploadSpeed = report.getTransferRateBit();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.setDataList(countTestSpeed, speedModel);

                        }
                    });

                    countTestSpeed++;
                    if (countTestSpeed < LIMIT) {
                        startTestDownload();
                    }
                }

                @Override
                public void onError(SpeedTestError speedTestError, String errorMessage) {
                    // called when a download/upload error occur
                }

                @Override
                public void onProgress(float percent, SpeedTestReport report) {
                    // called to notify download/upload progress
                    Log.v("speedtest Upload" + countTestSpeed, "[PROGRESS] progress : " + percent + "%");
                    Log.v("speedtest Upload" + countTestSpeed, "[PROGRESS] rate in octet/s : " + report.getTransferRateOctet());
                    Log.v("speedtest Upload" + countTestSpeed, "[PROGRESS] rate in bit/s   : " + report.getTransferRateBit());

                    speedModel.progress = (percent / 2) + 50;
                    speedModel.uploadSpeed = report.getTransferRateBit();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.setDataList(countTestSpeed, speedModel);

                        }
                    });
                }
            });

            speedTestSocket.startDownload("http://2.testdebit.info/fichiers/1Mo.dat");

            return null;
        }
    }
}
