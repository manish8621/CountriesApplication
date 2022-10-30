package com.mk.countries.view.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.mk.countries.BuildConfig
import com.mk.countries.R
import com.mk.countries.databinding.FragmentHomeBinding
import com.mk.countries.model.util.LocationUtils
import com.mk.countries.model.util.isConnectedToInternet
import com.mk.countries.view.MainActivity
import com.mk.countries.view.adapter.CountriesViewAdapter
import com.mk.countries.view.adapter.bindImage
import com.mk.countries.viewmodel.HomeViewModel
import com.mk.countries.viewmodel.HomeViewModelFactory
import java.net.URL


class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding:FragmentHomeBinding
    private lateinit var locationUtils: LocationUtils

    //to know if user returned from settings page after granting permission
    private var isSentToSettings = false
    //to track if searching operation in progress
    var searching = false

    private val requestPermissionLauncher by lazy {
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
                isGranted ->run {
                if (isGranted) {
                    handleLocation()
                }
                else {
                    Snackbar.make(binding.root,R.string.permission_denied_msg,Snackbar.LENGTH_SHORT)
                        .setAction(R.string.grant){
                            isSentToSettings = true
                            Toast.makeText(activity, "Grant location permission", Toast.LENGTH_SHORT).show()
                            goToAppInfo()
                        }
                        .show()
                }
            }
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

        //location
        locationUtils = LocationUtils.getInstance((activity as (MainActivity)))

        //factory,view model
        val factory = HomeViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this,factory)[HomeViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val recyclerViewAdapter = CountriesViewAdapter().also {
            it.setOnclickListener{ imageView,countryItem->
                val extras = FragmentNavigatorExtras(imageView to "flagOnScreen2"
                    )
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToDetailsFragment(countryItem.id)
                    ,extras)
            }
        }

        //setOnClickListeners

        //load location when user clicks weather icon
        binding.weatherIv.setOnClickListener{
            if(viewModel.weather.value==null)
                handleLocation()
        }

        //search input listener
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

        //clear edit text focus on start
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

        viewModel.location.observe(viewLifecycleOwner){
            it?.let {

                if(viewModel.weather.value==null) {
//                    Toast.makeText(activity, "[OK] Location\n[Load] Weather", Toast.LENGTH_SHORT).show()
                    //put loading image
//                    setWeatherUiLoading()
                    viewModel.updateWeather(it)
                }
            }
        }

        viewModel.weather.observe(viewLifecycleOwner,Observer{
            val aqiText = "AQI ${it.aqi} "
            val tempText = "${it.temp}°C"
            binding.cityTv.text = it.cityName
            binding.aqiTv.text = aqiText
            binding.tempTv.text = tempText
            binding.weatherTv.text = it.weatherDesc
            bindImage(binding.weatherIv,toIconUrl(it.weatherIcon))
        })

        if(!viewModel.isWeatherLoaded()) {
            handleLocation()
            viewModel.weatherLoaded() //change this name
        }
    }


    private fun toIconUrl(weatherIcon: String): String {
        return "https://www.weatherbit.io/static/img/icons/${weatherIcon}.png"
    }
    private fun setWeatherUiLoading(){
    binding.weatherIv.setImageResource(R.drawable.loading_animation)
}

    private fun handleLocation() {
        try{
            if (isConnectedToInternet(requireContext())) {
                if (locationUtils.checkLocationPermission()) {
                    if (locationUtils.checkLocationEnabled()) {
                        updateGpsSync()
                    } else {
                        Snackbar.make(
                            binding.root,
                            R.string.turn_location_on_msg,
                            Snackbar.LENGTH_SHORT
                        )
                            .setAction(R.string.turn_on) {
                                isSentToSettings = true
                                goToLocationSettings()
                            }
                            .show()
                    }
                } else
                    requestLocationPermission()
            } else
                Toast.makeText(activity, "No internet", Toast.LENGTH_SHORT).show()
        }
        catch(e:Exception)
        {
            Toast.makeText(activity, "Error while connecting to api !\n"+e.message, Toast.LENGTH_SHORT).show()
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

    private fun goToLocationSettings() {
        val settingsIntent = Intent()
        settingsIntent.action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
        settingsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(settingsIntent)
    }


    private fun requestLocationPermission() {
        //min sdk is 23 so no need to check
        //if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    /*
    * location updates to viewModel
    * **/

    private fun updateGpsSync() {
        setWeatherUiLoading()
            locationUtils.requestCurrentLocation {
                //update to view model
                viewModel.location.postValue(it)
                viewModel.weatherLoaded()
            }
    }

    //if user returned from settings page check permission
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
//expecting error
    override fun onDestroy() {
        super.onDestroy()
        if(::locationUtils.isInitialized)
                LocationUtils.destroyInstance()
    }
}