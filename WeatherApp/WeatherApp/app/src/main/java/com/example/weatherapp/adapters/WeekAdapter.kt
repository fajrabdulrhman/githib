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
import com.example.weatherapp.models.Forecastday

class WeekAdapter:RecyclerView.Adapter<WeekAdapter.WeekViewHolder>() {

    inner class WeekViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


    }
    private val differCallBack = object : DiffUtil.ItemCallback<Forecastday>() {
        override fun areItemsTheSame(oldItem: Forecastday, newItem: Forecastday): Boolean {
            return oldItem.day == newItem.day
        }

        override fun areContentsTheSame(oldItem: Forecastday, newItem: Forecastday): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallBack)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekViewHolder {
       return WeekViewHolder(LayoutInflater.from(parent.context).inflate(
           R.layout.week_item,parent,false
       ))

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: WeekViewHolder, position: Int) {
        val week=differ.currentList[position]
        holder.itemView.apply {
            val ivImage: ImageView=findViewById(R.id.todayIconTv)

           Glide.with(this).load("https:${week.day.condition.icon}").into((ivImage))
            val temp: TextView =findViewById(R.id.tempTv)
            temp.text= week.day.avgtemp_c.toString()
//            val day:TextView=findViewById(R.id.dayTv)
//            day.text=week.day.condition.

        }
    }
}