package com.example.locationsharingapp

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.util.Locale

class SmsFragment : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var phoneNumberEditText: EditText

    // Permission launcher for sending SMS
    private val requestSendSmsPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                sendSmsWithLocation()
            } else {
                Toast.makeText(
                    requireContext(),
                    "SMS permission denied. Unable to send SMS.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sms, container, false)

        sharedPreferences =
            requireActivity().getPreferences(Context.MODE_PRIVATE)

        phoneNumberEditText = view.findViewById(R.id.editTextPhone)
        loadPhoneNumber()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Check SMS permission
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request the permission if not granted
            requestSendSmsPermission()
        }

        // Button to send SMS
        val sendSmsButton: Button = view.findViewById(R.id.btnSendSms)
        sendSmsButton.setOnClickListener {
            savePhoneNumber()
            requestSendSmsPermission()
        }

        return view
    }

    private fun requestSendSmsPermission() {
        requestSendSmsPermissionLauncher.launch(Manifest.permission.SEND_SMS)
    }

    private fun sendSmsWithLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val address = getLocationAddress(location)
                    sendSms(location, address)
                } ?: showToast("Location not available")
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                showToast("Failed to get location: ${e.message}")
            }
    }

    private fun sendSms(location: Location, address: String) {
        val phoneNumber = phoneNumberEditText.text.toString()

        if (phoneNumber.isBlank()) {
            showToast("Phone number cannot be empty")
            return
        }

        val message = "Current Location:  \nLatitude: ${location.latitude}\nLongitude: ${location.longitude} \nAddress: $address"

        val uri = Uri.parse("smsto:$phoneNumber?body=${Uri.encode(message)}")
        val smsIntent = Intent(Intent.ACTION_SENDTO, uri)

        val chooserIntent = Intent.createChooser(smsIntent, "Send Location via SMS")

        try {
            startActivity(chooserIntent)
        } catch (e: ActivityNotFoundException) {
            showToast("No SMS app found.")
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            SmsFragment.REQUEST_LOCATION_PERMISSION
        )
    }

    private fun getLocationAddress(location: Location): String {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addresses: MutableList<Address>? = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            )
            if (!addresses.isNullOrEmpty()) {
                return addresses[0].getAddressLine(0) ?: ""
            }
        } catch (e: IOException) {
            e.printStackTrace()
            showToast("Geocoding failed: ${e.message}")
        }
        return ""
    }

    private fun savePhoneNumber() {
        val phoneNumber = phoneNumberEditText.text.toString()
        with(sharedPreferences.edit()) {
            putString(PHONE_NUMBER_KEY, phoneNumber)
            apply()
        }
    }

    private fun loadPhoneNumber() {
        val phoneNumber = sharedPreferences.getString(PHONE_NUMBER_KEY, "")
        phoneNumberEditText.setText(phoneNumber)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val REQUEST_LOCATION_PERMISSION = 123
        private const val PHONE_NUMBER_KEY = "phone_number"
    }
}
