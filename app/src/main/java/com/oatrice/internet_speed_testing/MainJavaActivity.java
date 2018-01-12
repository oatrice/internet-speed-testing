package com.oatrice.internet_speed_testing;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.internet_speed_testing.InternetSpeedBuilder;
import com.example.internet_speed_testing.ProgressionModel;

import org.jetbrains.annotations.NotNull;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.IRepeatListener;
import fr.bmartel.speedtest.model.SpeedTestError;

public class MainJavaActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerview);

        adapter = new Adapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        testSpeed();

//        testDownloadSpeedByOriginal();
//        testUploadSpeedByOriginal();

    }

    private void testSpeed() {
        InternetSpeedBuilder builder = new InternetSpeedBuilder(this);
        builder.setOnEventInternetSpeedListener(new InternetSpeedBuilder.OnEventInternetSpeedListener() {

            @Override
            public void onDownloadProgress(int count, ProgressionModel progressModel) {
                adapter.setDataList(count, progressModel);

            }

            @Override
            public void onUploadProgress(int count, ProgressionModel progressModel) {
                adapter.setDataList(count, progressModel);

            }

            @Override
            public void onTotalProgress(int count, ProgressionModel progressModel) {

            }

            @Override
            public void onUploadError(@NotNull SpeedTestError speedTestError, @NotNull String errorMessage) {

            }

            @Override
            public void onDownloadError(@NotNull SpeedTestError speedTestError, @NotNull String errorMessage) {

            }
        });

        /*builder.startDownloadUpload(
                "http://2.testdebit.info/fichiers/1Mo.dat",
                "http://2.testdebit.info/",
                20);*/

        // Download and Upload 20 times
        // Fixed duration 3 second
        builder.startDownloadUpload(
                "http://2.testdebit.info/fichiers/1Mo.dat",
                "http://2.testdebit.info/",
                20,
                5000);
    }

    private void testDownloadSpeedByOriginal() {
        Log.v("Speedtest Download: " + 000, "testDownloadSpeedByOriginal");
        new DownloadSpeedTestTask().execute();
    }

    private void testUploadSpeedByOriginal() {
        Log.v("Speedtest Upload: " + 000, "testUploadSpeedByOriginal");
        new UploadSpeedTestTask().execute();
    }

    public class DownloadSpeedTestTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            SpeedTestSocket speedTestSocket = new SpeedTestSocket();

            speedTestSocket.startDownloadRepeat("http://2.testdebit.info/fichiers/1Mo.dat",
                    20000, 2000, new
                            IRepeatListener() {
                                @Override
                                public void onCompletion(final SpeedTestReport report) {
                                    // called when repeat task is finished
                                    Log.v("Speedtest Download: " + report.getRequestNum(), "[onCompletion] rate in octet/s : " + report.getTransferRateOctet());

                                }

                                @Override
                                public void onReport(final SpeedTestReport report) {
                                    // called when a download report is dispatched
                                    Log.v("Speedtest Download: " + report.getRequestNum(), "[onReport] rate in octet/s : " + report.getTransferRateOctet());

                                }
                            });

            return null;
        }
    }

    public class UploadSpeedTestTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            SpeedTestSocket speedTestSocket = new SpeedTestSocket();

            speedTestSocket.startUploadRepeat("http://2.testdebit.info/", 10000,
                    100, 5000000, new
                            IRepeatListener() {
                                @Override
                                public void onCompletion(final SpeedTestReport report) {
                                    // called when repeat task is finished
                                    Log.v("Speedtest Upload: " + report.getRequestNum(), "[onCompletion] getProgressPercent : " + report.getProgressPercent());
                                    Log.v("Speedtest Upload: " + report.getRequestNum(), "[onCompletion] rate in octet/s : " + report.getTransferRateOctet());
                                    Log.v("Speedtest Upload: " + report.getRequestNum(), "[onCompletion] Time : " + (report.getReportTime() - report.getStartTime()));

                                }

                                @Override
                                public void onReport(final SpeedTestReport report) {
                                    // called when an upload report is dispatched
                                    Log.v("Speedtest Upload: " + report.getRequestNum(), "[onReport] getProgressPercent : " + report.getProgressPercent());
                                    Log.v("Speedtest Upload: " + report.getRequestNum(), "[onReport] rate in octet/s : " + report.getTransferRateOctet());
                                    Log.v("Speedtest Upload: " + report.getRequestNum(), "[onReport] Time : " + (report.getReportTime() - report.getStartTime()));

                                }
                            });

            return null;
        }
    }

}
