package com.mk.countries.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mk.countries.R
import com.mk.countries.databinding.FragmentDetailsBinding
import com.mk.countries.databinding.FragmentHomeBinding
import com.mk.countries.viewmodel.DetailsViewModel
import com.mk.countries.viewmodel.HomeViewModel
import com.mk.countries.viewmodel.HomeViewModelFactory


class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding:FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater,container,false)


        //factory
        val factory = HomeViewModelFactory(requireActivity().application)
        //view model
        viewModel = ViewModelProvider(this,factory)[HomeViewModel::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.countryItemsList.observe(viewLifecycleOwner,Observer{
            Toast.makeText(activity, it.toString() , Toast.LENGTH_SHORT).show()
        })
        setOnClickListeners()
        return binding.root
    }

    private fun setOnClickListeners() {
        binding.searchBtn.setOnClickListener{
            Toast.makeText(
                activity,
                viewModel.countryItemsList.value?.get(0)?.currencies?.get(0),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}