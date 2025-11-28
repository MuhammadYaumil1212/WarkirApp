package com.warkir.warkirapp.permission.presentations

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.warkir.warkirapp.R
import com.warkir.warkirapp.databinding.FragmentPermissionBinding

class PermissionFragment : Fragment() {
    private var _binding: FragmentPermissionBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPermissionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnGrantPermission.setOnClickListener { launchPermissionRequest() }
    }

    override fun onDestroyView() {
        super.onDestroy()
        binding.btnGrantPermission.setOnClickListener(null)
        _binding = null
    }

    private fun launchPermissionRequest() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        val isFineLocationGranted =
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)
        val isCoarseLocationGranted =
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)

        when {
            isFineLocationGranted || isCoarseLocationGranted -> {
                findNavController().navigate(R.id.action_permissionFragment_to_locationFragments)
            }

            else -> showPermissionDeniedSnackbar()
        }
    }

    private fun showPermissionDeniedSnackbar() {
        val snackbar = Snackbar.make(
            binding.root,
            "Aplikasi butuh lokasi agar bisa berjalan. Mohon izinkan di Pengaturan.",
            Snackbar.LENGTH_LONG
        )
        snackbar.setAction("Buka Pengaturan") {
            openAppSettings()
        }

        val actionColor = androidx.core.content.ContextCompat.getColor(
            requireContext(),
            R.color.white
        )
        snackbar.setActionTextColor(actionColor)

        val backgroundColor =
            androidx.core.content.ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark)
        snackbar.setBackgroundTint(backgroundColor)

        val messageColor =
            androidx.core.content.ContextCompat.getColor(requireContext(), R.color.white)
        snackbar.setTextColor(messageColor)

        snackbar.show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        startActivity(intent)
    }
}