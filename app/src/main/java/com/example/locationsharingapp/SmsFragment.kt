package com.example.locationsharingapp

import android.Manifest
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


/**
 * A simple [Fragment] subclass.
 * Use the [SmsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SmsFragment : Fragment() {

    var latitude = 0.0 // Set your default latitude here
    var longitude = 0.0 // Set your default longitude here

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var phoneNumberEditText: EditText

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
            requestPermissions(
                arrayOf(Manifest.permission.SEND_SMS),
                SMS_PERMISSION_REQUEST_CODE
            )
        }

        // Button to send SMS
        val sendSmsButton: Button = view.findViewById(R.id.btnSendSms)
        sendSmsButton.setOnClickListener {
            savePhoneNumber()
            sendSms()
        }

        return view
    }

    private fun sendSms() {
        val smsIntent = Intent(Intent.ACTION_VIEW)
        smsIntent.data = Uri.parse("smsto:")
        smsIntent.type = "vnd.android-dir/mms-sms"
        smsIntent.putExtra(
            "sms_body",
            "Current Location: \nLatitude: $latitude\nLongitude: $longitude"
        )

        // Check if there is an activity that can handle the SMS intent
        if (smsIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(smsIntent)
        } else {
            // Handle the case where there is no activity to handle the SMS intent
            Toast.makeText(
                requireContext(),
                "No app found to handle SMS.",
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
        private const val SMS_PERMISSION_REQUEST_CODE = 123
        private const val PHONE_NUMBER_KEY = "phone_number"
    }
}