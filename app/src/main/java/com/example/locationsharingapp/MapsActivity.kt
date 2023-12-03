package com.example.locationsharingapp


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.locationsharingapp.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate( savedInstanceState: Bundle? ) {

        super.onCreate( savedInstanceState )

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView( binding.root )

    }

    override fun onMapReady(p0: GoogleMap) {

        TODO("Not yet implemented")

    }


}