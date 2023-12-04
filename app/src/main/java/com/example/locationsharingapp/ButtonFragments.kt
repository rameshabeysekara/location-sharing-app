package com.example.locationsharingapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast


/**
 * A simple [Fragment] subclass.
 * Use the [ButtonFragments.newInstance] factory method to
 * create an instance of this fragment.
 */
class ButtonFragments : Fragment() {


    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {

        return inflater.inflate( R.layout.fragment_buttons, container, false )

    }





    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    fun onButtonClick(view: View) {
        when (view.id) {
            R.id.btnGoogleMaps -> {
                val mapsFragment = MapsFragment()
                val transaction = childFragmentManager.beginTransaction()
                transaction.replace(R.id.fragmentContainerView3, mapsFragment)
                transaction.addToBackStack(null)
                transaction.commit()

                showToast("Maps button clicked")
            }
            R.id.btnSMS -> {
                val smsFragment = SmsFragment()
                val transaction = childFragmentManager.beginTransaction()
                transaction.replace(R.id.fragmentContainerView3, smsFragment)
                transaction.addToBackStack(null)
                transaction.commit()

                showToast("SMS button clicked")
            }
            R.id.btnEmail -> {
                val emailFragment = EmailFragment()
                val transaction = childFragmentManager.beginTransaction()
                transaction.replace(R.id.fragmentContainerView3, emailFragment)
                transaction.addToBackStack(null)
                transaction.commit()

                showToast("Email button clicked")
            }
        }
    }



}