package com.francisco.geovane.marcello.felipe.projetofinalandroid.main.activity.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.francisco.geovane.marcello.felipe.projetofinalandroid.main.activity.BaseActivity
import com.francisco.geovane.marcello.felipe.projetofinalandroid.R
import com.francisco.geovane.marcello.felipe.projetofinalandroid.main.activity.login.LoginActivity
import com.francisco.geovane.marcello.felipe.projetofinalandroid.main.utils.AnalyticsUtils
import com.google.firebase.analytics.FirebaseAnalytics

class SplashActivity : BaseActivity() {

    private var pageId: String = "Splash"

    val SPLASH_SCREEN = 5000
    private lateinit var topAnimation:Animation
    private lateinit var bottomAnimation:Animation

    private lateinit var imageView: ImageView
    private lateinit var title_txt: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        analytics = FirebaseAnalytics.getInstance(this)
        AnalyticsUtils.setPageData(analytics, bundle, appId, pageId)

        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        // Animations
        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)

        imageView = findViewById(R.id.hr_image)
        title_txt = findViewById(R.id.title_text)

        imageView.animation = topAnimation
        title_txt.animation = bottomAnimation


        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_SCREEN.toLong())
    }
}