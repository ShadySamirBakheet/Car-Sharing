package com.app.carsharing.views.profile

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import log.apps.makkahsandwich.Utils.PermissionCheck
import com.app.carsharing.R
import com.app.carsharing.data.datasources.SharedStorage
import com.app.carsharing.data.models.User
import com.app.carsharing.databinding.ActivityProfileBinding
import com.app.carsharing.utils.FileUtils
import com.app.carsharing.viewmodel.NetworkViewModel
import com.app.carsharing.viewmodel.UserViewModel
import java.io.File


class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private lateinit var networkViewModel: NetworkViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var auth: FirebaseAuth


    var sendingFile: File? = null
    var isEdit = false

    var imagePath :String?=null

    var user: User?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        networkViewModel = ViewModelProvider(this)[NetworkViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getData()

        binding.goBack.setOnClickListener {
            finish()
        }

        binding.getImage.setOnClickListener {
            PermissionCheck.readAndWriteExternalStorage(this)
            loadImage()
        }

        disableEdit()


        binding.edit.setOnClickListener {
            if (isEdit){
                saveChange()
            }else{
                enableEdit()
            }
        }

        binding.saveEdit.setOnClickListener {
            if (isEdit){
                saveChange()
            }else{
                enableEdit()
            }
        }

    }

    private fun getData() {
        binding.progress.visibility = VISIBLE
        userViewModel.getUserData(SharedStorage.getLoginPhoneData(this).toString()).observe(this){

            binding.progress.visibility = GONE
            if (it != null) {

                try {
                    user = it.getValue(User::class.java)
                }catch (e: Exception){
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    Log.e("this", e.message!!)
                }
                if (user != null){
                    binding.userName.setText(user!!.userName)
                    binding.userEmail.setText(user!!.userEmail)
                    binding.userPhone.setText(user!!.userPhone)
                    setImage(user!!.userImg)

                }else{
                    Toast.makeText(this, "Error Data or Not Found", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun setImage(userImg: String?) {
        if (userImg != null){
            Glide.with(this).load(userImg)
                .placeholder(R.drawable.user)
                .into(binding.setImage)
        }
    }

    private fun enableEdit() {
        isEdit = true

        binding.userName.isEnabled = true
        binding.userEmail.isEnabled = true
        binding.userPhone.isEnabled = true
        binding.getImage.isEnabled = true
        binding.isCar.isEnabled = true
        binding.saveEdit.isEnabled = true


        binding.edit.setImageResource(R.drawable.done_mark)
    }

    private fun disableEdit(){

        isEdit = false

        binding.userName.isEnabled = false
        binding.userEmail.isEnabled = false
        binding.userPhone.isEnabled = false
        binding.getImage.isEnabled = false
        binding.isCar.isEnabled = false
        binding.saveEdit.isEnabled = false

        binding.edit.setImageResource(R.drawable.ic_edit)
    }

    private fun saveChange() {
        binding.progress.visibility = VISIBLE
        networkViewModel.networkState(this).observe(this) {
            if (it) {
                saveChangedImage()
            } else {
                Toast.makeText(this, "Check Network is Available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private  fun saveChangedImage() {
        if (sendingFile != null){
           val ref = FirebaseStorage.getInstance().reference.child("users_images").child(user?.userPhone+".png")

            var file= sendingFile
             GlobalScope.launch {
             file = Compressor.compress(this@ProfileActivity, sendingFile!!){
                    default(format =  Bitmap.CompressFormat.PNG)
                }
            }

            ref.putFile(file!!.toUri()).addOnCompleteListener {
                 it.result!!.storage.downloadUrl.addOnSuccessListener {uri->
                     if (uri != null) {
                         imagePath = uri.toString()
                     }
                     saveChangedDate()
                 }
            }.addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }

        }else{
            saveChangedDate()
        }
    }

    private fun saveChangedDate() {
        val phoneNumber = binding.userPhone.text.toString().trim()
        val userEmail = binding.userEmail.text.toString().trim().lowercase()
        val userName = binding.userName.text.toString().trim()

        user!!.userName = userName
        user!!.userEmail = userEmail
        user!!.userPhone = phoneNumber
        user!!.haveCar = user!!.haveCar

        if (imagePath != null){
            user!!.userImg = imagePath
        }

        userViewModel.setUserData(user!!).addOnCompleteListener {
            SharedStorage.saveLoginData(this, user!!)
            binding.progress.visibility = GONE
            disableEdit()
        }.addOnFailureListener{
            binding.progress.visibility = GONE
            disableEdit()
        }

    }

    private fun loadImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.apply {
            type = "image/*"
        }
        startActivityForResult(intent, 1)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            when(requestCode){
                1->{
                    loadImageFun(data)
                }

            }
        }else{
            Toast.makeText(this,"Error", Toast.LENGTH_LONG).show()
        }
    }


    private fun loadImageFun(data: Intent) {
        if (data.data != null){
            val uri = data.data!!


            sendingFile = File(FileUtils.getSmartFilePath(this, data.data!!) ?: "")

            binding.setImage.setImageURI(uri)

            Toast.makeText(this,"Send File", Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(this,"Error File", Toast.LENGTH_LONG).show()
        }
    }

}