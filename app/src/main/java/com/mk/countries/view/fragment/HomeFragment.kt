package com.mk.countries.view.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.mk.countries.BuildConfig
import com.mk.countries.R
import com.mk.countries.databinding.FragmentHomeBinding
import com.mk.countries.model.util.LocationUtils
import com.mk.countries.view.MainActivity
import com.mk.countries.view.adapter.CountriesViewAdapter
import com.mk.countries.view.adapter.bindImage
import com.mk.countries.viewmodel.HomeViewModel
import com.mk.countries.viewmodel.HomeViewModelFactory
import kotlin.contracts.contract


class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding:FragmentHomeBinding
    private lateinit var locationUtils: LocationUtils
    private var isSentToSettings = false

    private val requestPermissionLauncher by lazy {
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
                isGranted ->run {
                if (isGranted) {
                    Toast.makeText(activity, "OK GRANTED", Toast.LENGTH_SHORT).show()
                    updateGpsSync()
                }
                else {
                    Snackbar.make(binding.root,R.string.permission_denied_msg,Snackbar.LENGTH_SHORT)
                        .setAction(R.string.settings){
                            isSentToSettings = true
                            Toast.makeText(activity, "Grant location permission", Toast.LENGTH_SHORT).show()
                            goToAppInfo()
                        }
                        .show()
                }
            }
        }
    }

    private fun goToAppInfo() {
        val settingsIntent = Intent()
        settingsIntent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package",BuildConfig.APPLICATION_ID,null)
        settingsIntent.data =uri
        settingsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(settingsIntent)
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
        //et
//        binding.searchEt.addTextChangedListener(object:TextWatcher{
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                viewModel.searchInList(s.toString())
//                searching = true
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//
//            }
//
//        })
        //load location when user clicks weather icon
        binding.weatherIv.setOnClickListener{
            handleLocation()
        }
        binding.searchV.setOnQueryTextListener(object:OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.searchInList(binding.searchV.query.toString())
                searching = true
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.searchInList(binding.searchV.query.toString())
                    searching = true
                return true
            }

        })
        binding.searchV.clearFocus()
        binding.recyclerView.adapter = recyclerViewAdapter

        //setObservers
        viewModel.countryItemsList.observe(viewLifecycleOwner) {
            if(it.isEmpty()) {
                if (searching) {
                    Toast.makeText(activity, "no result found", Toast.LENGTH_LONG).show()
                    recyclerViewAdapter.submitList(it)
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
//                    Toast.makeText(activity, "[OK] Location\n[Load] Weather", Toast.LENGTH_SHORT).show()
                    //put loading image
                    binding.weatherIv.setImageResource(R.drawable.loading_animation)
//                    viewModel.getWeather(it)
                }
            }
        }

        //location
        locationUtils = LocationUtils.getInstance((activity as (MainActivity)))

        //handleLocation()
        //if weather not already loaded
        if(!viewModel.isWeatherLoaded()) {
//            Toast.makeText(activity, "Weather load started", Toast.LENGTH_SHORT).show()
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

    /*
    * gets the location updates ton viewModel
    * **/
    fun updateGpsSync() {
//            Toast.makeText(activity, "Getting Location....", Toast.LENGTH_SHORT).show()
            //location
            locationUtils.requestCurrentLocation {
                //update to viewmodel
                viewModel.location.postValue(it)
                viewModel.weatherLoaded()
            }
    }

    override fun onStart() {
        super.onStart()
        if(isSentToSettings) {
            if (locationUtils.isLocationUsable)
                updateGpsSync()
            isSentToSettings= false
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        locationUtils.stopRequestingLocation()
    }
    }