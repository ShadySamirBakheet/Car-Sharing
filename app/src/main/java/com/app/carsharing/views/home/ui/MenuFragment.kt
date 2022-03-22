package com.app.carsharing.views.home.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.app.carsharing.databinding.FragmentMenuBinding
import com.app.carsharing.views.loginsystem.LoginActivity
import com.app.carsharing.views.profile.ProfileActivity
import com.app.carsharing.views.trips.MyTripsActivity

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.goToMyTrip.setOnClickListener {
            startActivity(Intent(context, MyTripsActivity::class.java))
        }

        binding.goToProfile.setOnClickListener {
            startActivity(Intent(context, ProfileActivity::class.java))
        }


        binding.logOut.setOnClickListener {
            showSign()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun showSign() {
        val builder = AlertDialog.Builder(context)

        builder.setTitle("Sign Out")
        .setMessage("Are you want to SignOut")
            .setPositiveButton("Ok") { dialogInterface, _ ->
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(context, LoginActivity::class.java))

                activity?.finish()
                dialogInterface.dismiss()
            }
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
        builder.create().show()
    }
}