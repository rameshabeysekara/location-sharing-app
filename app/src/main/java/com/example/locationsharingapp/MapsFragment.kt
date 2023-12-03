package com.example.locationsharingapp

import android.content.pm.PackageManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class MapsFragment : Fragment() {


    private val callback = OnMapReadyCallback { googleMap ->

        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isCompassEnabled = true

//        var fanshawe = LatLng( 43.012440,-81.200180 )
//        googleMap.addMarker( MarkerOptions().position( fanshawe ).title( "Fanshawe College" ) )
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(fanshawe))
    }

    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {

                enableMyLocation()

            }

        }

    private fun checkLocationPermission() {

        if ( ContextCompat.checkSelfPermission( requireContext(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            requestLocationPermission()

        } else {

            enableMyLocation()

        }

    }


    companion object {

        private const val REQUEST_LOCATION_PERMISSION = 1

    }

    private fun requestLocationPermission() {

        ActivityCompat.requestPermissions( requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION )
        requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {

        return inflater.inflate( R.layout.fragment_maps, container, false )

    }

    override fun onViewCreated( view: View, savedInstanceState: Bundle? ) {

        super.onViewCreated( view, savedInstanceState )
        checkLocationPermission()

//        val mapFragment = childFragmentManager.findFragmentById( R.id.map ) as SupportMapFragment?
//        mapFragment?.getMapAsync( callback )

    }

    @Deprecated("Deprecated in Java" )
    @Suppress("DEPRECATION" )
    override fun onRequestPermissionsResult( requestCode: Int, permissions: Array<out String>, grantResults: IntArray ) {

        when ( requestCode ) {

            REQUEST_LOCATION_PERMISSION -> {

                if ( grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {

                    enableMyLocation()

                }

            }

            else -> super.onRequestPermissionsResult( requestCode, permissions, grantResults )

        }

    }

    private fun enableMyLocation() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment

        mapFragment?.getMapAsync { googleMap ->
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                googleMap.isMyLocationEnabled = true
                googleMap.uiSettings.isMyLocationButtonEnabled = true

                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    }
                }
            } else {
                requestLocationPermission()
            }
        }
    }


}