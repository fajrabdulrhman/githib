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
import com.example.weatherapp.models.Location
import com.example.weatherapp.models.WeatherResponse

class SavedAdapter :RecyclerView.Adapter<SavedAdapter.SavedViewHolder>(){

    inner  class SavedViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val ivImage=itemView.findViewById<ImageView>(R.id.weatherImage)
        val countryTv=itemView.findViewById<TextView>(R.id.countryTv)
        val degreeTv=itemView.findViewById<TextView>(R.id.degreeTv)
        val windTv=itemView.findViewById<TextView>(R.id.windT)
        val tempTv=itemView.findViewById<TextView>(R.id.tempTv)
        val humTv=itemView.findViewById<TextView>(R.id.humTv)


    }
    private val differCallBack = object : DiffUtil.ItemCallback<WeatherResponse>() {
        override fun areItemsTheSame(oldItem: WeatherResponse, newItem: WeatherResponse): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: WeatherResponse, newItem: WeatherResponse): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallBack)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedViewHolder {
        return SavedViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.saved_item,parent,false
        ))
    }

    override fun getItemCount(): Int {
         return differ.currentList.size
    }
    private var onItemClickListener: ((WeatherResponse) -> Unit)? = null
    override fun onBindViewHolder(holder: SavedViewHolder, position: Int) {
        val weather=differ.currentList[position]
        holder.itemView.apply {
          holder.countryTv.text=weather.location.name
            holder.tempTv.text=weather.current.temp_c.toString()
            holder.windTv.text=weather.current.wind_kph.toString()
            Glide.with(holder.ivImage.context).load("https:${weather.current.condition.icon}").into(holder.ivImage)
            holder.humTv.text=weather.forecast.forecastday[0].day.avghumidity.toString()

            setOnClickListener {
                onItemClickListener?.let { it(weather) }
            }
        }
    }
    fun setOnItemClickListener(listener: (WeatherResponse) -> Unit) {
        onItemClickListener = listener
    }
}