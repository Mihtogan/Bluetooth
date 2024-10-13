package com.example.bluetooth.models

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bluetooth.R
import com.example.bluetooth.adapters.ItemDevicesAdapter
import com.example.bluetooth.dataClasses.ItemDevicesList
import com.example.bluetooth.databinding.FragmentDevicesListBinding
import com.example.bluetooth.vievModels.DevicesListViewModel

class DevicesList : Fragment() {
    private val viewModel: DevicesListViewModel by viewModels()
    private var _binding: FragmentDevicesListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDevicesListBinding.inflate(inflater, container, false)
        val view = binding.root

        initRcView()

        return view
    }

    private fun initRcView() {
        val rcView = binding.rcViewPairedDevices
        rcView.layoutManager = LinearLayoutManager(context)
        val adapter = ItemDevicesAdapter()
        rcView.adapter = adapter

        adapter.submitList(
            listOf(
                ItemDevicesList("1", "1"),
                ItemDevicesList("2", "2"),
                ItemDevicesList("3", "3"),
                ItemDevicesList("4", "4"),
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}