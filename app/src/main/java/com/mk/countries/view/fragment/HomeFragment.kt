package com.mk.countries.view.fragment

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mk.countries.databinding.FragmentHomeBinding
import com.mk.countries.model.util.LocationUtils
import com.mk.countries.view.MainActivity
import com.mk.countries.view.adapter.CountriesViewAdapter
import com.mk.countries.viewmodel.HomeViewModel
import com.mk.countries.viewmodel.HomeViewModelFactory
import kotlinx.coroutines.*


class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding:FragmentHomeBinding
    private lateinit var locationUtils: LocationUtils

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
        binding.lifecycleOwner = viewLifecycleOwner

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
        viewModel.address.observe(viewLifecycleOwner,Observer{
//                 Toast.makeText(activity, "lat:"+it.toString(), Toast.LENGTH_SHORT).show()
            binding.currentCityTv.text = it.subAdminArea
            Toast.makeText(activity, "getting air quality..", Toast.LENGTH_SHORT).show()
            viewModel.getAirQuality(it)
        })

        viewModel.airQualityIndex.observe(viewLifecycleOwner,Observer{
            val text = "AirQuality"+it.toString()
            binding.airQualityTv.text = text
        })

        //location
        locationUtils = LocationUtils.getInstance(activity as (MainActivity))

        handleLocation()
        return binding.root
    }

    private fun handleLocation() {
        if (locationUtils.checkLocationPermission()) {
            if (locationUtils.checkLocationEnabled()) {
                updateGpsSync()
            } else {
                Toast.makeText(activity, "Please turn on the location", Toast.LENGTH_SHORT).show()
            }
        } else {
            locationUtils.requestLocationPermission()
//            //observe and update gps location
//            CoroutineScope(Dispatchers.IO).launch {
//                Log.i("TAG","Updating location in background...")
//                //timeout
//                repeat(20)
//                {
//                    if(locationUtils.isLocationUsable)
//                    {
////                        updateGps()
//                        updateGpsSync()
//                        return@launch
//                    }
//                    delay(2000L)
//                }
//            }
//        }
        }
    }
        suspend fun updateGpsAsync() {
//        withContext(Dispatchers.Main){
//            Toast.makeText(activity, "Getting Location....", Toast.LENGTH_SHORT).show()
//        }
            //location
            locationUtils.requestCurrentLocation {
                //update to viewmodel
//            viewModel.location.postValue(it)
                // ui
            }

        }

        fun updateGpsSync() {
            Toast.makeText(activity, "Getting Location....", Toast.LENGTH_SHORT).show()
            //location
            locationUtils.requestCurrentLocation {
                //update to viewmodel

                val address = locationUtils.geoCoderConverter(it.latitude, it.longitude)
                viewModel.address.postValue(address)


                // ui
            }
        }


        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            when (requestCode) {
                locationUtils.PERMISSION_ID -> {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        updateGpsSync()
                    } else {
                        Toast.makeText(
                            activity,
                            "Continuing without location features",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        locationUtils.stopRequestingLocation()
    }
    }