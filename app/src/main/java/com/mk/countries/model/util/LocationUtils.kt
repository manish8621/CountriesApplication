package com.mk.countries.model.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.util.Log
import com.google.android.gms.location.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.util.Objects

private const val REQUEST_INTERVEL = 500L

/**
 * used to get location and its related functions
 */
class LocationUtils private constructor(private val activity: AppCompatActivity) {
    val PERMISSION_ID = 1202
    private val locationManager =
        activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private var fusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(activity)

    private var locationRequest: LocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,
        REQUEST_INTERVEL).build()
    var isLocationRequsting = false
    lateinit var locationCallback : LocationCallback


    val isLocationUsable: Boolean
        get() = checkLocationPermission() && checkLocationEnabled()

    init {

    }

    fun checkLocationPermission(): Boolean = (
            ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(
                        activity,Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
            )

    fun checkLocationEnabled(): Boolean =(
        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            )

    /**
     * just requests permission
     */
    fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            activity, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), PERMISSION_ID
        )
    }

    fun requestLastLocation(onSuccess: (location: Location) -> Unit) {
        if(checkLocationPermission())
            fusedLocationProviderClient.lastLocation.addOnSuccessListener(activity) {
                onSuccess(it)
            }
    }

    /**
     * requests for location get location once,
     * send callback as parameter
     *
     * */

    fun requestCurrentLocation(onSuccess: (location: Location) -> Unit) {
        isLocationRequsting = true
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                onSuccess(result.locations[0])
                stopRequestingLocation()
            }
        }
        if(checkLocationPermission())
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }
    /**
     * requests for location continuously,
     * send callback as parameter
     *
     * */
    fun startRequestingCurrentLocation(onSuccess: (location: Location) -> Unit) {
        isLocationRequsting = true
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                onSuccess(result.locations.get(0))
            }
        }
        if(checkLocationPermission())
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    /**
     * use this function to stop requesting location
     * */
    fun stopRequestingLocation() {
        if (::locationCallback.isInitialized) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            isLocationRequsting = false
        }
    }
    //add geocoder to convert the co ordinate to city and vice versa

    /**
     * co ordinate to address(nullable)
     * */
    fun geoCoderConverter(latitude:Double,longitude:Double):Address?{
        val geocoder = Geocoder(activity)
        var address:Address?=null
        try {
            address = geocoder.getFromLocation(latitude,longitude,1).get(0)
        }
        catch (e:Exception)
        {
            Log.i("TAG",e.message.toString())
        }
        return address
    }
    /**
     * city name to co ordinate(nullable)
     * */
    fun geoCoderConverter(city:String):Address?{
        val geocoder = Geocoder(activity)
        var address:Address?=null
        try {
            address = geocoder.getFromLocationName(city,1).get(0)
        }
        catch (e:Exception)
        {
            Log.i("TAG",e.message.toString())
        }
        return address
    }

    companion object{
        private var INSTANCE:LocationUtils? = null
        fun getInstance(_activity:AppCompatActivity):LocationUtils{
            if (INSTANCE==null)
            {
                INSTANCE = LocationUtils(_activity)
            }
            return INSTANCE as LocationUtils
        }
        //do i have to clear this
        fun destroyInstance(){
            INSTANCE = null
        }
    }
}

/**
 * simple but throws exception when app is minimized
 * java.lang.AbstractMethodError: abstract method "void android.location.LocationListener.onStatusChanged
 * pass the lambda and use the location via lambda callback
 */

//old method not efficient
//    @SuppressLint("MissingPermission")
//    fun getCurrentLocation(onSuccess:(location:Location)->Unit){
//        if (isLocationUsable) {
//            locationManager.requestLocationUpdates(
//                LocationManager.GPS_PROVIDER,
//                10L,
//                1.0f
//            ) { location ->
//                onSuccess(location)
//                locationManager.removeUpdates {};
//                Log.i("TAG", "Location services stopped")
//            }
//        }
//    }