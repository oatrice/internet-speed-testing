package com.oatrice.internet_speed_testing;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.internet_speed_testing.InternetSpeedBuilder;
import com.example.internet_speed_testing.ProgressionModel;

import org.jetbrains.annotations.NotNull;

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

        InternetSpeedBuilder builder = new InternetSpeedBuilder(this);
        builder.setOnEventInternetSpeedListener(new InternetSpeedBuilder.OnEventInternetSpeedListener() {

            @Override
            public void onDownloadProgress(int count, ProgressionModel progressModel) {

            }

            @Override
            public void onUploadProgress(int count, ProgressionModel progressModel) {

            }

            @Override
            public void onTotalProgress(int count, ProgressionModel progressModel) {
                adapter.setDataList(count, progressModel);

            }

            @Override
            public void onUploadError(@NotNull SpeedTestError speedTestError, @NotNull String errorMessage) {

            }

            @Override
            public void onDownloadError(@NotNull SpeedTestError speedTestError, @NotNull String errorMessage) {

            }
        });

        builder.startDownloadUpload(
                "http://2.testdebit.info/fichiers/1Mo.dat",
                "http://2.testdebit.info/",
                20);

        // Download and Upload 20 times
        // Fixed duration 3 second
        /*builder.startDownloadUpload(
                "http://2.testdebit.info/fichiers/1Mo.dat",
                "http://2.testdebit.info/",
                20,
                10000);*/

    }

}
