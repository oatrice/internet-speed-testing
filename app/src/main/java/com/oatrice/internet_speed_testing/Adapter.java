package com.oatrice.internet_speed_testing;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.internet_speed_testing.ProgressionModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private List<ProgressionModel> dataList = new ArrayList<>();

    public void setDataList(int position, ProgressionModel data) {
        if (dataList.size() <= position) {
            dataList.add(data);
            notifyItemInserted(position);

        } else {
            dataList.set(position, data);
            notifyItemChanged(position);

        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.bind(position, dataList.get(position));
    }

    @Override
    public int getItemCount() {
        if (dataList == null)
            return 0;

        return dataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private final AppCompatTextView tvNumber;
        private final AppCompatTextView tvProgress;
        private final AppCompatTextView tvDownload;
        private final AppCompatTextView tvUpload;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvNumber = itemView.findViewById(R.id.tvNumber);
            tvProgress = itemView.findViewById(R.id.tvProgress);
            tvDownload = itemView.findViewById(R.id.tvDownload);
            tvUpload = itemView.findViewById(R.id.tvUpload);
        }

        void bind(int position, ProgressionModel progressionModel) {
            String format = "%.2f";
            BigDecimal postfixMultiplier = new BigDecimal(1024 * 8);

            String downloadSpeed = String.format(format, progressionModel.getDownloadSpeed().divide(postfixMultiplier));
            String uploadSpeed = String.format(format, progressionModel.getUploadSpeed().divide(postfixMultiplier));

            String downloadDuration = String.format("%.2f", progressionModel.getDownloadDuration() / 1000f);
            String uploadDuration = String.format("%.2f", progressionModel.getUploadDuration() / 1000f);

            String percent = String.format("%.2f", progressionModel.getProgressTotal());

            tvNumber.setText("" + ++position);
            tvDownload.setText(downloadSpeed + " KB/s\n" + downloadDuration + " s");
            tvUpload.setText(uploadSpeed + " KB/s\n" + uploadDuration + " s");
            tvProgress.setText(percent + " %");
        }
    }
}
