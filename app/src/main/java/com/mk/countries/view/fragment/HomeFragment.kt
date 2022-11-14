package com.mk.countries.view.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.mk.countries.BuildConfig
import com.mk.countries.R
import com.mk.countries.databinding.FragmentHomeBinding
import com.mk.countries.util.isConnectedToInternet
import com.mk.countries.view.MainActivity
import com.mk.countries.view.adapter.CountriesViewAdapter
import com.mk.countries.view.adapter.bindImage
import com.mk.countries.viewmodel.HomeViewModel
import com.mk.countries.viewmodel.HomeViewModelFactory

private const val REQUEST_INTERVEL = 500L

class HomeFragment : Fragment() {


    private lateinit var viewModel: HomeViewModel
    private lateinit var binding:FragmentHomeBinding





    //Location related

    private val PERMISSION_ID = 1202
    private lateinit var locationManager:LocationManager
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback : LocationCallback

    private var locationRequest: LocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,REQUEST_INTERVEL).build()

    private lateinit var requestPermissionLauncher:ActivityResultLauncher<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionLauncher  = registerForActivityResult(ActivityResultContracts.RequestPermission()){
                    isGranted -> run {
                if (isGranted) {
                    handleLocation()
                }
                else {
                    Snackbar.make(binding.root,R.string.permission_denied_msg,Snackbar.LENGTH_SHORT)
                        .setAction(R.string.grant){
                            viewModel.isSentToSettings = true
                            Toast.makeText(activity, "Grant location permission", Toast.LENGTH_SHORT).show()
                            goToAppInfo()
                        }
                        .show()
                }
            }
        }
        locationManager = (requireActivity() as MainActivity).getSystemService(Context.LOCATION_SERVICE) as LocationManager
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient((requireActivity() as MainActivity))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //factory,view model
        val factory = HomeViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this,factory)[HomeViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        //clear edit text focus on start
        binding.searchV.clearFocus()

        val recyclerViewAdapter = CountriesViewAdapter().also {
            it.setOnclickListener{ imageView,countryItem->
                val extras = FragmentNavigatorExtras(imageView to "flagOnScreen2"
                    )
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToDetailsFragment(countryItem.id)
                    ,extras)
            }
        }

        //setOnClickListeners

        //refresh button
        binding.refreshIb.setOnClickListener{
            if(isConnectedToInternet(requireContext())) {
                viewModel.setLoadingStatus(true)
                viewModel.refreshCountriesList()
                hideRefreshBtn()
            }
            else
                Toast.makeText(activity, "No internet", Toast.LENGTH_SHORT).show()
        }

        //load location when user clicks weather icon
        binding.weatherIv.setOnClickListener{
            if(viewModel.weather.value==null)
                handleLocation()
        }

        //search input listener
        binding.searchV.setOnQueryTextListener(object:OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.searchInList(binding.searchV.query.toString())
                viewModel.searching = true
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.searchInList(binding.searchV.query.toString())
                    viewModel.searching = true
                return true
            }
        })

        binding.recyclerView.adapter = recyclerViewAdapter

        //setObservers
        viewModel.countryItemsList.observe(viewLifecycleOwner) {
            if(it.isEmpty()) {
                if (viewModel.searching) {
                    Toast.makeText(activity, "no result found", Toast.LENGTH_LONG).show()
                    recyclerViewAdapter.submitList(it)
                    viewModel.searching = false
                }
                else if (!isConnectedToInternet(requireContext()))
                {
                    showRefreshBtn()
                    Toast.makeText(activity, "No internet\nclick refresh after getting internet", Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                hideRefreshBtn()
                recyclerViewAdapter.submitList(it)
                viewModel.setLoadingStatus(false)
            }
        }

        viewModel.location.observe(viewLifecycleOwner){
            it?.let {

                if(viewModel.weather.value==null)
                    viewModel.updateWeather(it)
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

        if(!viewModel.isWeatherLoadRequested()) {
            handleLocation()
            viewModel.weatherLoadRequested() //change this name
        }

    }

    private fun showRefreshBtn(){binding.refreshIb.visibility = View.VISIBLE}
    private fun hideRefreshBtn(){binding.refreshIb.visibility = View.GONE}
    private fun toIconUrl(weatherIcon: String): String {
        return "https://www.weatherbit.io/static/img/icons/${weatherIcon}.png"
    }
    private fun setWeatherUiLoading(){
    binding.weatherIv.setImageResource(R.drawable.loading_animation)
    }

    private fun handleLocation() {
        try{
            if (isConnectedToInternet(requireContext())) {
                if (checkLocationPermission()) {
                    if (checkLocationEnabled()) {
                        updateGpsSync()
                    } else {
                        Snackbar.make(
                            binding.root,
                            R.string.turn_location_on_msg,
                            Snackbar.LENGTH_SHORT
                        )
                            .setAction(R.string.turn_on) {
                                viewModel.isSentToSettings = true
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

    private fun updateGpsSync() {
        setWeatherUiLoading()
        requestCurrentLocation {
            //update to view model
            viewModel.location.postValue(it)
            viewModel.weatherLoadRequested()
        }
    }

    private fun requestLocationPermission() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
    }
    //Location related
    fun checkLocationPermission(): Boolean = (
            ActivityCompat.checkSelfPermission(
                (activity as MainActivity),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(
                        (activity as MainActivity), Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
            )
    fun checkLocationEnabled(): Boolean =(
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            )
    fun requestCurrentLocation(onSuccess: (location: Location) -> Unit) {
        if(!(::locationCallback.isInitialized)) {
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    onSuccess(result.locations[0])
                    stopRequestingLocation()
                }

            }
        }
        if(checkLocationPermission() && checkLocationEnabled())
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }
    fun stopRequestingLocation() {
        if (::locationCallback.isInitialized) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    //if user returned from settings page check permission
    override fun onStart() {
        super.onStart()
        if(viewModel.isSentToSettings) {
            if (checkLocationPermission()&&checkLocationEnabled())
                updateGpsSync()
            //TODO:replace with handleLocation()
            viewModel.isSentToSettings= false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopRequestingLocation()
    }

}