package com.example.locationsharingapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class ButtonFragments : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_buttons, container, false)

        val btnGoogleMaps = view.findViewById<Button>(R.id.btnGoogleMaps)
        val btnSMS = view.findViewById<Button>(R.id.btnSMS)
        val btnEmail = view.findViewById<Button>(R.id.btnEmail)

        btnGoogleMaps.setOnClickListener { onButtonClick(btnGoogleMaps) }
        btnSMS.setOnClickListener { onButtonClick(btnSMS) }
        btnEmail.setOnClickListener{onButtonClick(btnEmail)}

        return view
    }

    private fun onButtonClick(view: View) {
        when (view.id) {
            R.id.btnGoogleMaps -> {
                val mapsFragment = MapsFragment()
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragmentContainerView3, mapsFragment)
                transaction.commit()
            }
            R.id.btnSMS -> {
                val smsFragment = SmsFragment()
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragmentContainerView3, smsFragment)
                transaction.commit()
            }
            R.id.btnEmail -> {
                val emailFragment = EmailFragment()
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragmentContainerView3, emailFragment)
                transaction.commit()

            }
        }
    }



}