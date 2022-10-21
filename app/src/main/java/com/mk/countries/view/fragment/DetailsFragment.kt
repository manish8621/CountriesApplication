package com.mk.countries.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.mk.countries.R
import com.mk.countries.databinding.FragmentDetailsBinding
import com.mk.countries.viewmodel.DetailsViewModel

class DetailsFragment : Fragment() {
    private lateinit var binding:FragmentDetailsBinding
    private lateinit var viewModel:DetailsViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailsBinding.inflate(inflater,container,false)
        //safeargs

        //factory

        //view model
        viewModel = ViewModelProvider(this)[DetailsViewModel::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = this




        return binding.root
    }

}