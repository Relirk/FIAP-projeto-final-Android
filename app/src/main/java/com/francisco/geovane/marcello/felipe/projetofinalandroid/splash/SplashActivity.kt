package com.francisco.geovane.marcello.felipe.projetofinalandroid.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.francisco.geovane.marcello.felipe.projetofinalandroid.R
import com.francisco.geovane.marcello.felipe.projetofinalandroid.login.LoginActivity

class SplashActivity : AppCompatActivity() {

    val SPLASH_SCREEN = 5000
    private lateinit var topAnimation:Animation
    private lateinit var bottomAnimation:Animation

    private lateinit var imageView: ImageView
    private lateinit var title_txt: TextView
    private lateinit var description_txt: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Hide status bar
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.activity_splash)

        val actionBar = supportActionBar
        actionBar!!.hide()

        // Animations

        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)

        imageView = findViewById(R.id.hr_image)
        title_txt = findViewById(R.id.title_text)
        description_txt = findViewById(R.id.title_description)

        imageView.animation = topAnimation
        title_txt.animation = bottomAnimation
        description_txt.animation = bottomAnimation


        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_SCREEN.toLong())
    }
}