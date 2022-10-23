package com.mk.countries.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.mk.countries.R
import com.mk.countries.databinding.FragmentDetailsBinding
import com.mk.countries.view.adapter.bindImage
import com.mk.countries.viewmodel.DetailsViewModel
import com.mk.countries.viewmodel.DetailsViewModelFactory

class DetailsFragment : Fragment() {
    private lateinit var binding:FragmentDetailsBinding
    private lateinit var viewModel:DetailsViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailsBinding.inflate(inflater,container,false)
        //safearg
        val args:DetailsFragmentArgs by navArgs()
        val application = requireActivity().application
        //factory
        val factory = DetailsViewModelFactory(args.id,application)
        //view model
        viewModel = ViewModelProvider(this,factory)[DetailsViewModel::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        viewModel.countryDetails.observe(viewLifecycleOwner,Observer{
            Toast.makeText(activity, it?.toString()?:"v v", Toast.LENGTH_SHORT).show()
            if(it.flag.isNotEmpty())
                bindImage(binding.countryFlagIv,it.flag)
        })
        Toast.makeText(activity, args.id.toString(), Toast.LENGTH_SHORT).show()
        return binding.root
    }

}