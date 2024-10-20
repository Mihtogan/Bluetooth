package com.example.bluetooth.models

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bluetooth.R
import com.example.bluetooth.adapters.ItemDevicesAdapter
import com.example.bluetooth.dataClasses.ItemDevicesList
import com.example.bluetooth.databinding.FragmentDevicesListBinding
import com.example.bluetooth.vievModels.DevicesListViewModel


class DevicesList : Fragment() {
    private val viewModel: DevicesListViewModel by viewModels()
    private var _binding: FragmentDevicesListBinding? = null
    private val binding get() = _binding!!

    private var bluetoothAdapter: BluetoothAdapter? = null
    private val searchDevicesList = MutableLiveData<List<BluetoothDevice>>()

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent?) {
            if (BluetoothDevice.ACTION_FOUND == intent?.action) {
                val newDevice: BluetoothDevice? =
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                if (newDevice != null) {
                    searchDevicesList.value =
                        searchDevicesList.value.orEmpty() + listOf(newDevice)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDevicesListBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        val adapterRcViewPairedDevices = getRcViewAdapter(binding.rcViewPairedDevices)
        val adapterRcViewSearchDevices = getRcViewAdapter(binding.rcViewSearchDevices)

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.BLUETOOTH_ADVERTISE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (bluetoothAdapter?.isEnabled == true) {
                adapterRcViewPairedDevices.submitList(
                    bluetoothAdapter!!.bondedDevices
                        .map { ItemDevicesList(it.name, it.address) })
            }
        }

        binding.butSearchDevices.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.BLUETOOTH_SCAN
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                searchDevicesList.observe(viewLifecycleOwner) { devicesList ->
                    adapterRcViewSearchDevices.submitList(
                        devicesList.map {
                            ItemDevicesList(
                                it.name ?: requireContext().getString(R.string.device_without_name),
                                it.address
                            )
                        })
                }
                startDiscovery()
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onDestroyView() {
        super.onDestroyView()
        requireContext().unregisterReceiver(receiver)
        bluetoothAdapter?.cancelDiscovery()
        _binding = null
    }

    private fun getRcViewAdapter(rcView: RecyclerView): ItemDevicesAdapter {
        rcView.layoutManager = LinearLayoutManager(context)
        val adapter = ItemDevicesAdapter()
        rcView.adapter = adapter
        return adapter
    }

    @SuppressLint("MissingPermission")
    private fun startDiscovery() {
        searchDevicesList.value = emptyList()

        if (bluetoothAdapter?.isDiscovering == true) {
            bluetoothAdapter!!.cancelDiscovery()
        }
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        requireContext().registerReceiver(receiver, filter)

        bluetoothAdapter?.startDiscovery()
    }
}