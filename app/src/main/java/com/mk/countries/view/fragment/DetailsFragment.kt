package com.mk.countries.view.fragment

import android.os.Bundle
import android.transition.TransitionInflater
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
import com.mk.countries.model.util.LocationUtils
import com.mk.countries.view.MainActivity
import com.mk.countries.view.adapter.bindImage
import com.mk.countries.viewmodel.DetailsViewModel
import com.mk.countries.viewmodel.DetailsViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

lateinit var locationUtils: LocationUtils
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
            android.R.transition.explode
        ).also {
            it.duration = 2000L
            it.startDelay=2000L
        }

//        sharedElementEnterTransition = animation
//        sharedElementReturnTransition = animation.also {
//            it.duration=2000L
//        }

        //safearg
        val args:DetailsFragmentArgs by navArgs()
        val application = requireActivity().application
        //factory
        val factory = DetailsViewModelFactory(args.id,application)
        //view model
        viewModel = ViewModelProvider(this,factory)[DetailsViewModel::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        //observer

        //when we got country detail get the co ordinate
        viewModel.countryDetails.observe(viewLifecycleOwner,Observer{
            it?.let{
//                Toast.makeText(activity, it.toString() , Toast.LENGTH_SHORT).show()
                if (it.flag.isNotEmpty()) {
                    bindImage(binding.countryFlagIv, it.flag)

                }
                if (it.capital == "no-capital") {
                    val noCapital = true
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.address.postValue(locationUtils.geoCoderConverter(it.capital))
                    }
                }
            }
        })
        //when we got the address
        viewModel.address.observe(viewLifecycleOwner){
            it?.let{
//                Toast.makeText(activity, it.toString(), Toast.LENGTH_SHORT).show()//put loading image
                binding.weatherIv.setImageResource(R.drawable.loading_animation)
                viewModel.requestWeather(it.latitude, it.longitude)
            }
        }

        //when we got the weather
        viewModel.weather.observe(viewLifecycleOwner){
            it?.let{
//                Toast.makeText(activity, it.toString(), Toast.LENGTH_LONG).show()
                binding.airQualityTv.text = "AQI:${it.aqi}"
                binding.currentCityTv.text = it.cityName
                bindImage(binding.weatherIv, toIconUrl(it.weatherIcon))
            }
        }

        locationUtils =LocationUtils.getInstance((activity as MainActivity))
        return binding.root
    }
    private fun toIconUrl(weatherIcon: String): String {
        return "https://www.weatherbit.io/static/img/icons/${weatherIcon}.png"
    }
}