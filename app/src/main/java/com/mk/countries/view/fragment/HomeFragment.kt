package com.mk.countries.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
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
        var searching = false
        binding = FragmentHomeBinding.inflate(inflater,container,false)


        //factory
        val factory = HomeViewModelFactory(requireActivity().application)
        //view model
        viewModel = ViewModelProvider(this,factory)[HomeViewModel::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val recyclerViewAdapter = CountriesViewAdapter().also {
            it.setOnclickListener{ countryItem->
//                Toast.makeText(activity, countryItem.name, Toast.LENGTH_SHORT).show()
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToDetailsFragment(countryItem.id))
            }
        }

        //setOnClickListeners
        binding.searchBtn.setOnClickListener {
            viewModel.searchInList(binding.searchEt.text.toString())
            searching = true
        }

        binding.recyclerView.adapter = recyclerViewAdapter

        //setObservers
        viewModel.countryItemsList.observe(viewLifecycleOwner) {
            if(it.isEmpty()) {
                if (searching) {
                    Toast.makeText(activity, "no result found", Toast.LENGTH_LONG).show()
                }
                searching = false
            }
            else
            {
                recyclerViewAdapter.submitList(it)
                viewModel.setLoadingStatus(false)
            }

        }
        return binding.root
    }



}