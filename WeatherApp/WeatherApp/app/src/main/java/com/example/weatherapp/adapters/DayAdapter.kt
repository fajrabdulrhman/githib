package com.example.weatherapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import com.example.weatherapp.models.Hour

class DayAdapter : RecyclerView.Adapter<DayAdapter.TodayViewHolder>() {

    inner class TodayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivTodayImage: ImageView = itemView.findViewById(R.id.todayIconTv)
        val hour: TextView = itemView.findViewById(R.id.hourTv)
        val temp: TextView = itemView.findViewById(R.id.tempTv)
    }

    private val differCallBack = object : DiffUtil.ItemCallback<Hour>() {
        override fun areItemsTheSame(oldItem: Hour, newItem: Hour): Boolean {
            return oldItem.time == newItem.time
        }

        override fun areContentsTheSame(oldItem: Hour, newItem: Hour): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.today_item, parent, false)
        return TodayViewHolder(view)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: TodayViewHolder, position: Int) {
        val day = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(holder.ivTodayImage.context).load("https:${day.condition.icon}").into(holder.ivTodayImage)
            holder.hour.text = day.time
            holder.temp.text = day.temp_c.toString()
        }
    }
}
