package com.training.kotlin.openweatherkata.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.training.kotlin.openweatherkata.data.local.WeatherItem
import com.training.kotlin.openweatherkata.databinding.ItemWeatherPreviewBinding

class ListWeatherAdapter : RecyclerView.Adapter<ListWeatherAdapter.WeatherViewHolder>() {

    inner class WeatherViewHolder(val binding: ItemWeatherPreviewBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<WeatherItem>() {
        override fun areItemsTheSame(oldItem: WeatherItem, newItem: WeatherItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WeatherItem, newItem: WeatherItem): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {

        val binding =
            ItemWeatherPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return WeatherViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((WeatherItem) -> Unit)? = null

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val list = differ.currentList[position]

        holder.binding.apply {

            txtTemp.setText(""+list.temp + "\u2103")
            txtCity.text = list.city
            txtDesc.text = list.desc
            txtSunrise.text = "Sunrise : " + list.sunrise
            txtSunset.text = "Sunset : " + list.sunset

            Glide.with(holder.itemView).load(list.imageUrl).into(iconTemp)
            this.root.setOnClickListener {
                onItemClickListener?.let { it(list) }
            }

        }
    }

    fun setOnItemClickListener(listener: (WeatherItem) -> Unit) {
        onItemClickListener = listener
    }
}













