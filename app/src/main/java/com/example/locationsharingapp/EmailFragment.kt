package com.example.locationsharingapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.util.*

class EmailFragment : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var emailAddressEditText: EditText
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_email, container, false)

        emailAddressEditText = view.findViewById(R.id.editTextEmailAddress)

        val sendEmailButton: Button = view.findViewById(R.id.btnSendEmail)
        sendEmailButton.setOnClickListener {
            sendEmailWithLocation()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)

        // Load saved email address when the fragment is created
        emailAddressEditText.setText(loadEmailAddress())

        return view
    }

    private fun sendEmailWithLocation() {
        val emailAddress = emailAddressEditText.text.toString().trim()

        if (emailAddress.isEmpty()) {
            showToast("Email address cannot be empty")
            return
        }

        if (!isValidEmail(emailAddress)) {
            showToast("Invalid email address")
            return
        }

        saveEmailAddress(emailAddress)

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
                    val emailIntent = createEmailIntent(emailAddress, location, address)
                    startActivity(Intent.createChooser(emailIntent, "Send Email"))
                } ?: showToast("Location not available")
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                showToast("Failed to get location: ${e.message}")
            }
    }

    private fun getLocationAddress(location: Location): String {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addresses: MutableList<Address>? = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            )
            if (addresses != null && addresses.isNotEmpty()) {
                return addresses[0].getAddressLine(0) ?: ""
            }
        } catch (e: IOException) {
            e.printStackTrace()
            showToast("Geocoding failed: ${e.message}")
        }
        return ""
    }

    private fun createEmailIntent(
        emailAddress: String,
        location: Location,
        address: String
    ): Intent {
        val emailSubject = "Current Location"
        val emailBody =
            "Latitude: ${location.latitude}\nLongitude: ${location.longitude}\nAddress: $address"

        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "message/rfc822"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject)
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody)

        return emailIntent
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_LOCATION_PERMISSION
        )
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun saveEmailAddress(email: String) {
        with(sharedPreferences.edit()) {
            putString(EMAIL_ADDRESS_KEY, email)
            apply()
        }
    }

    private fun loadEmailAddress(): String {
        return sharedPreferences.getString(EMAIL_ADDRESS_KEY, "") ?: ""
    }

    companion object {
        const val REQUEST_LOCATION_PERMISSION = 123
        private const val EMAIL_ADDRESS_KEY = "email_address"
    }
}
