package com.example.locationsharingapp

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.activity.result.contract.ActivityResultContracts

/**
 * A simple [Fragment] subclass.
 * Use the [SmsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SmsFragment : Fragment() {

    var latitude = 0.0
    var longitude = 0.0

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var phoneNumberEditText: EditText

    // Permission launcher for sending SMS
    private val requestSendSmsPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                sendSms()
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

    private fun sendSms() {
        val phoneNumber = phoneNumberEditText.text.toString()

        if (phoneNumber.isBlank()) {
            Toast.makeText(
                requireContext(),
                "Phone number cannot be empty",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val message =
            "Current Location: \nLatitude: $latitude\nLongitude: $longitude"

        val uri = Uri.parse("smsto:$phoneNumber?body=${Uri.encode(message)}")
        val smsIntent = Intent(Intent.ACTION_SENDTO, uri)

        val chooserIntent = Intent.createChooser(smsIntent, "Send Location via SMS")

        try {
            startActivity(chooserIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                requireContext(),
                "No SMS app found.",
                Toast.LENGTH_SHORT
            ).show()
        }
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

    companion object {
        private const val PHONE_NUMBER_KEY = "phone_number"
    }
}
