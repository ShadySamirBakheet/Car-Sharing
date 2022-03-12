package shady.samir.carsharing.views.loginsystem

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.Window
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import shady.samir.carsharing.R
import shady.samir.carsharing.adapters.CodeAdapter
import shady.samir.carsharing.data.models.User
import shady.samir.carsharing.databinding.ActivityForgotPasswordBinding
import shady.samir.carsharing.viewmodel.NetworkViewModel
import shady.samir.carsharing.viewmodel.UserViewModel
import java.lang.Exception

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    private lateinit var networkViewModel: NetworkViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        networkViewModel = ViewModelProvider(this)[NetworkViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.changePassword.setOnClickListener {
            finish()
        }

        binding.progress.visibility = GONE

        binding.forgotPassword.setOnClickListener {

            binding.progress.visibility = VISIBLE

            networkViewModel.networkState(this).observe(this) {
                if (it) {
                    forgotPassword()
                } else {

                    binding.progress.visibility = GONE

                    Toast.makeText(this, "Check Network is Available", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }


    private fun forgotPassword() {
        val phoneNumber = binding.phone.text.toString().trim()

        if (phoneNumber.isNotEmpty() ){
            val liveData = userViewModel.getUserData(phoneNumber)

            liveData.observe(this) { dataSnapshot ->
                if (dataSnapshot != null) {
                    var user: User?=null
                    try {
                        user = dataSnapshot.getValue(User::class.java)
                    }catch (e: Exception){
                        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                        Log.e("this", e.message!!)
                    }
                    if (user != null){
                        FirebaseAuth.getInstance().sendPasswordResetEmail(user.userEmail.toString()).addOnSuccessListener {
                            binding.checkNumber.visibility = GONE
                            binding.changePasswordLay.visibility = VISIBLE

                            binding.progress.visibility = GONE

                        }.addOnFailureListener {

                            binding.progress.visibility = GONE

                            Toast.makeText(this, "Error Data or Not Found User ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this, "Error Data or Not Found", Toast.LENGTH_SHORT).show()
                    }
                }

                binding.progress.visibility = GONE

                binding.phone.setText("")
            }

        }else{
            Toast.makeText(this, "Error Data", Toast.LENGTH_SHORT).show()
        }
    }


}