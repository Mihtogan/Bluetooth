package com.example.bluetooth.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bluetooth.R
import com.example.bluetooth.dataClasses.ItemDevicesList
import com.example.bluetooth.databinding.ItemListDevicesBinding
import com.example.bluetooth.interfaces.onClickListenerBtDev

class ItemDevicesAdapter(private val listener: (String) -> Unit) :
    ListAdapter<ItemDevicesList, ItemDevicesAdapter.Holder>(Comparator()) {

    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        private val viewBinding = ItemListDevicesBinding.bind(view)

        fun bind(item: ItemDevicesList) = with(viewBinding) {
            root.setOnClickListener {
                listener(item.mac)
            }
            textDevicesName.text = item.name
            textDevicesMac.text = item.mac
        }
    }

    class Comparator : DiffUtil.ItemCallback<ItemDevicesList>() {
        override fun areItemsTheSame(
            oldItem: ItemDevicesList,
            newItem: ItemDevicesList
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: ItemDevicesList,
            newItem: ItemDevicesList
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_devices, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }
}