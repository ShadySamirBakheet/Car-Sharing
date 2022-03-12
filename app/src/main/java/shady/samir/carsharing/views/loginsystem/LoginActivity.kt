package shady.samir.carsharing.views.loginsystem

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import shady.samir.carsharing.data.datasources.SharedStorage
import shady.samir.carsharing.data.models.User
import shady.samir.carsharing.databinding.ActivityLoginBinding
import shady.samir.carsharing.viewmodel.NetworkViewModel
import shady.samir.carsharing.viewmodel.UserViewModel
import shady.samir.carsharing.views.home.HomeActivity


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var networkViewModel: NetworkViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        networkViewModel = ViewModelProvider(this)[NetworkViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.progress.visibility = GONE


        binding.forgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        binding.login.setOnClickListener {


            binding.progress.visibility = VISIBLE

            networkViewModel.networkState(this).observe(this) {
                if (it) {
                    signIn()
                } else {
                    binding.progress.visibility = GONE
                    Toast.makeText(this, "Check Network is Available", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.signUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }


    }

    private fun signIn() {
        val phoneNumber = binding.phone.text.toString().trim()
        val password = binding.password.text.toString().trim()

        if (phoneNumber.isNotEmpty() && password.isNotEmpty()) {
            userViewModel.getUserData(phoneNumber).observe(this
            ) { dataSnapshot ->

                if (dataSnapshot != null) {
                    var user: User? = null
                    try {
                        user = dataSnapshot.getValue(User::class.java)
                    } catch (e: Exception) {

                        binding.progress.visibility = GONE

                        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                        Log.e("this", e.message!!)
                    }
                    if (user != null) {
                        SharedStorage.saveLoginData(this, user)
                        FirebaseAuth.getInstance()
                            .signInWithEmailAndPassword(user.userEmail.toString(), password)
                            .addOnSuccessListener {
                                startActivity(Intent(this, HomeActivity::class.java))
                                finish()
                                binding.progress.visibility = GONE
                            }.addOnFailureListener {
                                binding.progress.visibility = GONE
                                Toast.makeText(
                                    this,
                                    "Error ${it.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {

                        binding.progress.visibility = GONE

                        Toast.makeText(this, "Enter Valid Phone Number Or Password", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Enter Valid Phone Number Or Password", Toast.LENGTH_SHORT).show()
                    binding.progress.visibility = GONE
                }

                binding.phone.setText("")
                binding.password.setText("")
            }

        } else {
            binding.progress.visibility = GONE
            Toast.makeText(this, "Error Data", Toast.LENGTH_SHORT).show()
        }
    }

}
