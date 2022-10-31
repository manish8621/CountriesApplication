package com.mk.countries.view.fragment

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
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
import com.mk.countries.util.isConnectedToInternet
import com.mk.countries.view.MainActivity
import com.mk.countries.view.adapter.bindImage
import com.mk.countries.viewmodel.DetailsViewModel
import com.mk.countries.viewmodel.DetailsViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailsFragment : Fragment() {
    private lateinit var binding:FragmentDetailsBinding
    private lateinit var viewModel:DetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailsBinding.inflate(inflater,container,false)

        //shared view
        val animation = TransitionInflater.from(requireContext()).inflateTransition(
            android.R.transition.move
        ).also {
            it.duration = 200L
        }
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation

        //safe arg
        val args:DetailsFragmentArgs by navArgs()

        //factory ,view model
        val factory = DetailsViewModelFactory(args.id, requireActivity().application)
        viewModel = ViewModelProvider(this,factory)[DetailsViewModel::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        //onClickListener
        binding.weatherIv.setOnClickListener{
                viewModel.countryDetails.value?.let {
                if(viewModel.weather.value==null)
                    loadWeather(it.capital)
            }
        }

        //observer
        //when we got country detail get the co ordinate
        viewModel.countryDetails.observe(viewLifecycleOwner,Observer{
            it?.let{
                if (it.flag.isNotEmpty()) { bindImage(binding.countryFlagIv, it.flag)}
                loadWeather(it.capital)
            }
        })

        //when we got the address
        viewModel.address.observe(viewLifecycleOwner){
            it?.let{
                viewModel.requestWeather(it.latitude, it.longitude)
            }
        }

        //when we got the weather
        viewModel.weather.observe(viewLifecycleOwner){
            it?.let{
                val aqiText = "AQI ${it.aqi} "
                val tempText = "${it.temp}°C"
                binding.cityTv.text = it.cityName
                binding.aqiTv.text = aqiText
                binding.tempTv.text = tempText
                binding.weatherTv.text = it.weatherDesc
                bindImage(binding.weatherIv, toIconUrl(it.weatherIcon))
            }
        }

        return binding.root
    }

    private fun setWeatherUiLoading() {
        binding.weatherIv.setImageResource(R.drawable.loading_animation)
    }

    private fun toIconUrl(weatherIcon: String): String {
        return "https://www.weatherbit.io/static/img/icons/${weatherIcon}.png"
    }

    //Location
    fun geoCoderConverter(city:String): Address?{
        val geocoder = Geocoder(activity as MainActivity)
        var address: Address?=null
        try {
            address = geocoder.getFromLocationName(city,1).get(0)
        }
        catch (e:Exception)
        {
            Log.i("TAG",e.message.toString())
        }
        return address
    }

    private fun loadWeather(capital:String) {
        if (capital == "no-capital") {
            Toast.makeText(activity, "No capital for this country", Toast.LENGTH_SHORT).show()
            //try load with co ordinates
        }
        else if(isConnectedToInternet(requireContext()).not())
            Toast.makeText(activity, "No internet", Toast.LENGTH_SHORT).show()
        else {
            setWeatherUiLoading()
            requestCoOrdinates(capital)
        }
    }
    private fun requestCoOrdinates(city:String){
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.address.postValue(geoCoderConverter(city))
        }
    }


}