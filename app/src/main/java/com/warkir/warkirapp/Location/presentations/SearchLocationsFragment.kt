package com.warkir.warkirapp.Location.presentations

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.warkir.warkirapp.BuildConfig
import com.warkir.warkirapp.databinding.FragmentSearchLocationsBinding
import com.warkir.warkirapp.permission.domain.entity.LocationModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchLocationFragment : Fragment() {

    private var _binding: FragmentSearchLocationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var placesClient: PlacesClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var sessionToken: AutocompleteSessionToken? = null
    private var currentDeviceLocation: LatLng? = null
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchLocationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Init Google Places
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), BuildConfig.MAPS_API_KEY)
        }
        placesClient = Places.createClient(requireContext())
        sessionToken = AutocompleteSessionToken.newInstance()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getCurrentLocationForBias()

        binding.etSearchQuery.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                if (query.isNotEmpty()) {
                    performSearch(query)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.btnSelectLocation.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.etSearchQuery.requestFocus()
    }

    private fun getCurrentLocationForBias() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    currentDeviceLocation = LatLng(location.latitude, location.longitude)
                }
            }
        }
    }

    private fun performSearch(query: String) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            delay(500)
            val requestBuilder = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(sessionToken)
                .setQuery(query)

            if (currentDeviceLocation != null) {
                requestBuilder.setOrigin(currentDeviceLocation)
            }

            placesClient.findAutocompletePredictions(requestBuilder.build())
                .addOnSuccessListener { response ->
                    val predictions = response.autocompletePredictions
                    val locationList = predictions.map { prediction ->
                        LocationModel(
                            name = prediction.getPrimaryText(null).toString(),
                            address = prediction.getSecondaryText(null).toString(),
                            placeId = prediction.placeId
                        )
                    }
                    setupRecyclerView(locationList)
                }
                .addOnFailureListener { exception ->
                    exception.printStackTrace()
                    Log.e("ERROR MAPS", exception.message.toString())
                }
        }
    }

    private fun setupRecyclerView(data: List<LocationModel>) {
        val adapter = LocationAdapter(data) { selectedItem ->
            getPlaceCoordinates(selectedItem)
        }
        binding.rvLocations.adapter = adapter
        binding.rvLocations.layoutManager = LinearLayoutManager(context)
    }

    private fun getPlaceCoordinates(item: LocationModel) {
        val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
        val request = FetchPlaceRequest.builder(item.placeId, placeFields)
            .setSessionToken(sessionToken)
            .build()

        placesClient.fetchPlace(request).addOnSuccessListener { response ->
            val place = response.place
            val latLng = place.latLng

            if (latLng != null) {
                setFragmentResult(
                    "requestKey", bundleOf(
                        "lat" to latLng.latitude,
                        "lng" to latLng.longitude,
                        "name" to place.name
                    )
                )
                findNavController().popBackStack()
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Gagal mengambil detail lokasi", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}