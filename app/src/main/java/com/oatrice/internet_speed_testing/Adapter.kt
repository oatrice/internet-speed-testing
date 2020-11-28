package com.oatrice.internet_speed_testing

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.internet_speed_testing.ProgressionModel
import kotlinx.android.synthetic.main.view_item.view.*
import java.util.*

class Adapter : RecyclerView.Adapter<MyViewHolder>() {

    private val dataList = ArrayList<ProgressionModel>()

    fun setDataList(position: Int, data: ProgressionModel) {
        if (dataList.size <= position) {
            dataList.add(data)

        } else {
            dataList[position] = data

        }

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int = dataList.size

}

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    @SuppressLint("SetTextI18n")
    fun bind(progressionModel: ProgressionModel) {
        itemView.tvProgress.text = progressionModel.progressTotal.toString()
        itemView.tvDownload.text = progressionModel.downloadSpeed.toString()
        itemView.tvUpload.text = progressionModel.uploadSpeed.toString()
    }

}
