package shady.samir.carsharing.views.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import shady.samir.carsharing.R
import shady.samir.carsharing.databinding.ActivityMainBinding
import shady.samir.carsharing.views.loginsystem.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setAnimation()

        Handler().postDelayed({
            if (FirebaseAuth.getInstance().currentUser == null){
                startActivity(Intent(this, LoginActivity::class.java))
            }else{
                startActivity(Intent(this, HomeActivity::class.java))
            }
            finish()
        },3000)

    }


    private fun setAnimation() {

        val animation: Animation = AnimationUtils.loadAnimation(this, R.anim.in_right)
        animation.duration = 2000
        binding.startImg.startAnimation(animation)
    }

}