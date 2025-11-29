package com.warkir.warkirapp.Location.presentations

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.warkir.warkirapp.databinding.ItemLocationResultBinding
import com.warkir.warkirapp.permission.domain.entity.LocationModel

class LocationAdapter(
    private val locations: List<LocationModel>,
    private val onItemClick: (LocationModel) -> Unit
) : RecyclerView.Adapter<LocationAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemLocationResultBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemLocationResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = locations[position]
        holder.binding.tvPlaceName.text = item.name
        holder.binding.tvPlaceAddress.text = item.address

        // Klik item list
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount() = locations.size
}