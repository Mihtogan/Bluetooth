package com.example.bluetooth.models

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.bluetooth.R
import com.example.bluetooth.databinding.FragmentDevicesListBinding
import com.example.bluetooth.databinding.FragmentStartBinding
import com.example.bluetooth.vievModels.StartViewModel

class StartFragment : Fragment() {

    private val viewModel: StartViewModel by viewModels()
    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.butDevicesListFragment.setOnClickListener {
            findNavController().navigate(StartFragmentDirections.actionStartFragmentToDevicesList())
        }
    }
}