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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mk.countries.R
import com.mk.countries.databinding.FragmentHomeBinding
import com.mk.countries.model.util.LocationUtils
import com.mk.countries.view.MainActivity
import com.mk.countries.view.adapter.CountriesViewAdapter
import com.mk.countries.view.adapter.bindImage
import com.mk.countries.viewmodel.HomeViewModel
import com.mk.countries.viewmodel.HomeViewModelFactory
import kotlinx.coroutines.*
import java.util.jar.Manifest


class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding:FragmentHomeBinding
    private lateinit var locationUtils: LocationUtils

    private val requestPermissionLauncher by lazy {
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
                isGranted ->run {
            if (isGranted) {
                Toast.makeText(activity, "OK GRANTED", Toast.LENGTH_SHORT).show()
                updateGpsSync()
            }
            else {
                Toast.makeText(activity, "Permission not granted", Toast.LENGTH_LONG).show()

            }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("TAG","onCreate")
        Toast.makeText(activity, "onCreate", Toast.LENGTH_SHORT).show()
    }
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
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToDetailsFragment(countryItem.id))
            }
        }

        //setOnClickListeners
        binding.searchBtn.setOnClickListener {
            viewModel.searchInList(binding.searchEt.text.toString())
            searching = true
        }
        //load location when user clicks weather icon
        binding.weatherIv.setOnClickListener{
            handleLocation()
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

        viewModel.weather.observe(viewLifecycleOwner,Observer{
            val text = "AirQualityIndex ${it.aqi} "
            binding.currentCityTv.text = it.cityName
            binding.airQualityTv.text = text
            bindImage(binding.weatherIv,toIconUrl(it.weatherIcon))
//            Toast.makeText(activity, "weather : ${it.weatherIcon}", Toast.LENGTH_SHORT).show()
        })



        viewModel.location.observe(viewLifecycleOwner){
            it?.let {

                if(viewModel.weather.value==null) {
                    Toast.makeText(activity, "[OK] Location\n[Load] Weather", Toast.LENGTH_SHORT).show()
                    //put loading image
                    binding.weatherIv.setImageResource(R.drawable.loading_animation)
                    viewModel.getWeather(it)
                }
            }
        }

        //location
        locationUtils = LocationUtils.getInstance((activity as (MainActivity)))

        //permissions

        //handleLocation()
        //if weather not already loaded
        if(!viewModel.isWeatherLoaded()) {
            Toast.makeText(activity, "Weather load started", Toast.LENGTH_SHORT).show()
            handleLocation()
            viewModel.weatherLoaded()
        }

        return binding.root
    }

    private fun toIconUrl(weatherIcon: String): String {
        return "https://www.weatherbit.io/static/img/icons/${weatherIcon}.png"
    }

    private fun handleLocation() {
        if (locationUtils.checkLocationPermission()) {
            if (locationUtils.checkLocationEnabled()) {
                updateGpsSync()
            } else {
                Toast.makeText(activity, "Please turn on the location", Toast.LENGTH_SHORT).show()
            }
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    fun updateGpsSync() {
            Toast.makeText(activity, "Getting Location....", Toast.LENGTH_SHORT).show()
            //location
            locationUtils.requestCurrentLocation {
                //update to viewmodel
                viewModel.location.postValue(it)
                viewModel.weatherLoaded()
            }
    }


//        override fun onRequestPermissionsResult(
//            requestCode: Int,
//            permissions: Array<out String>,
//            grantResults: IntArray
//        ) {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//            when (requestCode) {
//                locationUtils.PERMISSION_ID -> {
//                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                        updateGpsSync()
//                    } else {
//                        Toast.makeText(
//                            activity,
//                            "Continuing without location features",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            }
//        }


    override fun onDestroyView() {
        super.onDestroyView()
        locationUtils.stopRequestingLocation()
    }
    }