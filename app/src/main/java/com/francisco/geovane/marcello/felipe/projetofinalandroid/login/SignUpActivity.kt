package com.francisco.geovane.marcello.felipe.projetofinalandroid.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.francisco.geovane.marcello.felipe.projetofinalandroid.BaseActivity
import com.francisco.geovane.marcello.felipe.projetofinalandroid.R
import com.francisco.geovane.marcello.felipe.projetofinalandroid.utils.AnalyticsUtils
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth

    private val pageId: String = "SignUp"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        analytics = FirebaseAnalytics.getInstance(this)
        supportActionBar?.hide()
        setContentView(R.layout.activity_sign_up)
        auth = Firebase.auth

        btn_sign_up.setOnClickListener {

            AnalyticsUtils.setClickData(analytics, bundle, appId, pageId, "btnSingIn")
            signUpUser()
        }

        btn_sign_in.setOnClickListener {

            AnalyticsUtils.setClickData(analytics, bundle, appId, pageId, "btnLogin")
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun signUpUser() {
        val email: String = tv_username.text.toString()
        val password: String = tv_password.text.toString()
        if (email.isEmpty()) {
            tv_username.error = "Por favor insira um e-mail"
            tv_username.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tv_username.error = "Por favor insira um e-mail válido"
            tv_username.requestFocus()
            return
        }

        if (password.isEmpty()) {
            tv_password.error = "Por favor insira uma senha"
            tv_password.requestFocus()
            return
        }

        if(password.length < 6){
            tv_password.error = "A senha precisa ter no mínimo 6 caractéres"
            tv_password.requestFocus()
            return
        }

        auth.createUserWithEmailAndPassword(tv_username.text.toString(), tv_password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    Log.w("TASKERROR", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Sign Up failed. Try again after some time.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}