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
import android.app.Dialog
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import android.location.Geocoder
import android.location.Location
import android.view.Window
import android.widget.TextView
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {
    private lateinit var geocoder: Geocoder

    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                enableMyLocation()
            }
        }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
        } else {
            enableMyLocation()
        }
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_LOCATION_PERMISSION
        )
        requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkLocationPermission()
        geocoder = Geocoder(requireContext())
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocation()
                }
            }

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun enableMyLocation() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync { googleMap ->
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                googleMap.isMyLocationEnabled = true
                googleMap.uiSettings.isMyLocationButtonEnabled = true

                val fusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(requireActivity())
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))

                        val marker = googleMap.addMarker(
                            MarkerOptions().position(currentLatLng).title("Current Location")
                        )
                        marker?.tag = location

                        googleMap.setOnMarkerClickListener { marker ->
                            val location = marker.tag as Location
                            displayLocationInfo(location)
                            true
                        }
                    }
                }

                val style = """
                                [
                                    {
                                        "featureType": "landscape",
                                        "elementType": "geometry",
                                        "stylers": [
                                            { "hue": "#FFBB00" },
                                            { "saturation": 43.400000000000006 },
                                            { "lightness": 37.599999999999994 },
                                            { "gamma": 1 }
                                        ]
                                    },
                                    {
                                        "featureType": "road.highway",
                                        "elementType": "geometry.fill",
                                        "stylers": [
                                            { "hue": "#FFC200" },
                                            { "saturation": -61.8 },
                                            { "lightness": 45.599999999999994 },
                                            { "gamma": 1 }
                                        ]
                                    }
                                ]
                            """.trimIndent()
                googleMap.setMapStyle(MapStyleOptions(style))
            } else {
                requestLocationPermission()
            }
        }
    }

    private fun displayLocationInfo(location: Location) {
        val address =
            geocoder.getFromLocation(location.latitude, location.longitude, 1)?.firstOrNull()
                ?.getAddressLine(0)
        val addressString = address ?: "Address not available"
        val latLngString = "Latitude: ${location.latitude}, Longitude: ${location.longitude}"
        val locationInfo = "$latLngString\n$addressString"

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_location_info)
        dialog.setCancelable(true)

        val locationTextView: TextView = dialog.findViewById(R.id.locationDetailsTextView)
        locationTextView.text = locationInfo

        dialog.show()
    }
}
