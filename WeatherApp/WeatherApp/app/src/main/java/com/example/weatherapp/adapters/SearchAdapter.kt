package com.example.weatherapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.models.Current
import com.example.weatherapp.models.Forecast
import com.example.weatherapp.models.Location
import com.example.weatherapp.models.SearchResponse
import com.example.weatherapp.models.SearchResponseItem
import com.example.weatherapp.models.WeatherResponse


class SearchAdapter:RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {


    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cityName: TextView = itemView.findViewById(R.id.cityName)

    }

    private val differCallBack = object : DiffUtil.ItemCallback<SearchResponseItem>() {
        override fun areItemsTheSame(
            oldItem: SearchResponseItem,
            newItem: SearchResponseItem
        ): Boolean {
            return oldItem.lat == newItem.lat && oldItem.lon == newItem.lon
        }

        override fun areContentsTheSame(
            oldItem: SearchResponseItem,
            newItem: SearchResponseItem
        ): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_item, parent, false)
        return SearchViewHolder(view)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((SearchResponseItem) -> Unit)? = null

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val searchResponseItem = differ.currentList[position]
        holder.itemView.apply {
            holder.cityName.text = searchResponseItem.name



            setOnClickListener {
                onItemClickListener?.let {

                    it(searchResponseItem)
                }
            }
        }

    }

        fun setOnItemClickListener(listener: (SearchResponseItem) -> Unit) {
            onItemClickListener = listener
        }


}



