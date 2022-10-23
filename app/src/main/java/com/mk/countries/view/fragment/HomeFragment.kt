package com.mk.countries.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.mk.countries.databinding.FragmentHomeBinding
import com.mk.countries.view.adapter.CountriesViewAdapter
import com.mk.countries.viewmodel.HomeViewModel
import com.mk.countries.viewmodel.HomeViewModelFactory


class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding:FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater,container,false)


        //factory
        val factory = HomeViewModelFactory(requireActivity().application)
        //view model
        viewModel = ViewModelProvider(this,factory)[HomeViewModel::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.searchBtn.setOnClickListener {

        }
        val recyclerViewAdapter = CountriesViewAdapter().also {
            it.setOnclickListener{ countryItem->
                Toast.makeText(activity, countryItem.name, Toast.LENGTH_SHORT).show()
            }
        }

        //setOnClickListeners
        binding.searchBtn.setOnClickListener {
            viewModel.searchInList(binding.searchEt.text.toString())
        }

        binding.recyclerView.adapter = recyclerViewAdapter

        //setObservers
        viewModel.countryItemsList.observe(viewLifecycleOwner) {
            recyclerViewAdapter.submitList(it)
        }

        return binding.root
    }



}