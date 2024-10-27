package com.example.bluetooth.models

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.bluetooth.BluetoothConnection
import com.example.bluetooth.R
import com.example.bluetooth.databinding.FragmentDataTransferBinding
import com.example.bluetooth.vievModels.DataTransferViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataTransferFragment : Fragment() {
    private var _binding: FragmentDataTransferBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DataTransferViewModel by viewModels()

    val args: DataTransferFragmentArgs by navArgs()
    private var btCon: BluetoothConnection? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDataTransferBinding.inflate(inflater, container, false)

        btCon = args.mac?.let { BluetoothConnection(it) }

        if (btCon != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                btCon!!.connectToDevice()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btCon?.receivedData?.observe(viewLifecycleOwner, Observer {
            binding.tVinData.text = it
        })

        lifecycleScope.launch(Dispatchers.IO) {
            btCon?.startReceiveData()
        }

        binding.btForTransmit.setOnClickListener {
            val resText = binding.tVoutData.text.toString()
            binding.tVoutData.text.clear()

            lifecycleScope.launch(Dispatchers.IO) {
                btCon?.sendData(resText)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        btCon?.closeConnection()
    }
}