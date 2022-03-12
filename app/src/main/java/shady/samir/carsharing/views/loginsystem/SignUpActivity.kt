package shady.samir.carsharing.views.loginsystem

import android.app.Dialog
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.Window
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import shady.samir.carsharing.R
import shady.samir.carsharing.R.color.txtColor1
import shady.samir.carsharing.R.color.txtColor2
import shady.samir.carsharing.adapters.CodeAdapter
import shady.samir.carsharing.data.datasources.SharedStorage
import shady.samir.carsharing.data.models.User
import shady.samir.carsharing.databinding.ActivitySignUpBinding
import shady.samir.carsharing.viewmodel.NetworkViewModel
import shady.samir.carsharing.viewmodel.UserViewModel
import shady.samir.carsharing.views.home.HomeActivity

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private lateinit var networkViewModel: NetworkViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var auth: FirebaseAuth

    var carOwner = true

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        networkViewModel = ViewModelProvider(this)[NetworkViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.signUp.isEnabled = binding.accept.isChecked
        binding.progress.visibility = GONE


        binding.yes.setOnClickListener {
            carOwner = true

            binding.yes.setBackgroundResource( R.drawable.check1)
            binding.yes.setTextColor(getColor(txtColor2))

            binding.no.setBackgroundResource( R.drawable.check2)
            binding.no.setTextColor(getColor(txtColor1))
        }

        binding.no.setOnClickListener {
            carOwner = false

            binding.yes.setBackgroundResource( R.drawable.check2)
            binding.yes.setTextColor(getColor(txtColor1))

            binding.no.setBackgroundResource( R.drawable.check1)
            binding.no.setTextColor(getColor(txtColor2))
        }

        binding.accept.setOnCheckedChangeListener { _, isCheck ->
            binding.signUp.isEnabled = isCheck
        }

        binding.signUp.setOnClickListener {

            binding.progress.visibility = VISIBLE

            networkViewModel.networkState(this).observe(this) {
                if (it) {
                    signUp()
                } else {

                    binding.progress.visibility = GONE

                    Toast.makeText(this, "Check Network is Available", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun signUp() {
        val phoneNumber = binding.phone.text.toString().trim()
        val password = binding.password.text.toString().trim()
        val confirmPassword = binding.confirmPassword.text.toString().trim()
        val userEmail = binding.userEmail.text.toString().trim().lowercase()
        val userName = binding.userName.text.toString().trim()
        val isAccept = binding.accept.isChecked

        if (phoneNumber.isNotEmpty() && password.isNotEmpty() &&
            userEmail.isNotEmpty() && userName.isNotEmpty() &&
            confirmPassword.isNotEmpty() && password.equals(confirmPassword) &&
             isAccept){

            auth.createUserWithEmailAndPassword(userEmail,password).addOnSuccessListener {

                val user = User(auth.uid,userName,null,userEmail, phoneNumber,carOwner)

                userViewModel.setUserData(user).addOnSuccessListener {
                    SharedStorage.saveLoginData(this,user)
                    binding.progress.visibility = GONE
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }.addOnFailureListener{
                    binding.progress.visibility = GONE
                    Toast.makeText(this, "Error ${it.message} ", Toast.LENGTH_SHORT).show()
                }


            }.addOnFailureListener {

                binding.progress.visibility = GONE

                Toast.makeText(this, "Error ${it.message}", Toast.LENGTH_SHORT).show()
            }

        }else{
            Toast.makeText(this, "Error Data", Toast.LENGTH_SHORT).show()
        }
    }

}