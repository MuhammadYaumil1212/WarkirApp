package com.warkir.warkirapp.Location.presentations

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.warkir.warkirapp.Location.data.dataSource.local.LocationDataStore
import com.warkir.warkirapp.R
import com.warkir.warkirapp.databinding.FragmentLocationBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class LocationFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder
    private lateinit var locationDataStore: LocationDataStore
    private var selectedLatLng: LatLng? = null
    private var selectedAddressString: String = ""

    override fun onStart() {
        super.onStart()
        checkLocationAndRedirect()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        locationDataStore = LocationDataStore(requireContext())
        geocoder = Geocoder(requireContext(), Locale.getDefault())
        binding.btnConfirmLocation.setOnClickListener {
            if (selectedLatLng != null) {
                saveToDataStoreAndNavigate()
            } else {
                Toast.makeText(context, "Mohon tunggu lokasi terdeteksi", Toast.LENGTH_SHORT).show()
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val bottomSheetLayout = binding.bottomSheetLayout
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
        bottomSheetBehavior.isHideable = false

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        binding.ivSearch.setOnClickListener {
            findNavController().navigate(R.id.action_locationFragments_to_searchLocationFragments)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getAddressFromLatLng(latLng: LatLng) {
        binding.tvAddressTitle.text = getString(R.string.looking_address_progress)
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val fullAddress = address.getAddressLine(0)
                    selectedAddressString = fullAddress
                    withContext(Dispatchers.Main) {
                        binding.tvAddressTitle.text = fullAddress
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        binding.tvAddressTitle.text = getString(R.string.address_notfound)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    binding.tvAddressTitle.text = getString(R.string.failed_loading_address)
                }
            }
        }
    }

    private fun getCurrentDeviceLocation() {
        val fineLocationGranted = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineLocationGranted || coarseLocationGranted) {
            mMap.isMyLocationEnabled = true
            mMap.setPadding(0, 0, 0, 300)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    updateLocationUI(currentLatLng)
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                } else {
                    requestNewLiveLocation()
                }
            }
        } else {
            Toast.makeText(context, getString(R.string.grant_permission_needed), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun updateLocationUI(latLng: LatLng) {
        selectedLatLng = latLng
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(latLng))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
        getAddressFromLatLng(latLng)
    }

    private fun requestNewLiveLocation() {
        val fineLocationGranted = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineLocationGranted || coarseLocationGranted) {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        updateLocationUI(currentLatLng)
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }
        }
    }

    private fun checkLocationAndRedirect() {
        lifecycleScope.launch {
            locationDataStore.getLocationFlow.collect { userData ->
                if (userData.isLocationSet)
                    findNavController().navigate(
                        R.id.action_locationFragments_to_welcomeScreenFragments
                    )
            }
        }
    }

    private fun saveToDataStoreAndNavigate() {
        lifecycleScope.launch {
            val latitude = selectedLatLng!!.latitude
            val longitude = selectedLatLng!!.longitude

            locationDataStore.saveLocation(selectedAddressString, latitude, longitude)
            findNavController().navigate(R.id.action_locationFragments_to_welcomeScreenFragments)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true

        mMap.setOnMapClickListener { latLng ->
            updateLocationUI(latLng)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        getCurrentDeviceLocation()
    }

}