package com.francisco.geovane.marcello.felipe.projetofinalandroid.main.activity.signUp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import com.francisco.geovane.marcello.felipe.projetofinalandroid.R
import com.francisco.geovane.marcello.felipe.projetofinalandroid.main.activity.BaseActivity
import com.francisco.geovane.marcello.felipe.projetofinalandroid.main.activity.login.LoginActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : BaseActivity() {

    private val pageId: String = "SignUp"
    private var TAG: String = "SIGN UP"

    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        supportActionBar?.hide()

        // Firebase
        auth = Firebase.auth
        firebaseAnalytics = Firebase.analytics

        progressBar = findViewById(R.id.sign_up_spinner)
        progressBar.visibility = View.GONE

        btn_sign_up.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val bundle = Bundle()
            bundle.putString("appId", appId)
            bundle.putString("pageId", pageId)
            firebaseAnalytics.logEvent("e_ClickSignUpAction", bundle)
            signUpUser()
        }

        btn_sign_in.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("appId", appId)
            bundle.putString("pageId", pageId)
            firebaseAnalytics.logEvent("e_ClickLoginFromSignUp", bundle)
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun signUpUser() {
        val email: String = tv_username.text.toString()
        val password: String = tv_password.text.toString()
        hideKeyboard(currentFocus ?: View(this))
        if (email.isEmpty()) {
            tv_username.error = resources?.getString(R.string.validate_email)
            tv_username.requestFocus()
            progressBar.visibility = View.GONE
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tv_username.error = resources?.getString(R.string.validate_email_valid)
            tv_username.requestFocus()
            progressBar.visibility = View.GONE
            return
        }

        if (password.isEmpty()) {
            tv_password.error = resources?.getString(R.string.validate_pw_empty)
            tv_password.requestFocus()
            progressBar.visibility = View.GONE
            return
        }

        if(password.length < 6){
            tv_password.error = resources?.getString(R.string.validate_pw_length)
            tv_password.requestFocus()
            progressBar.visibility = View.GONE
            return
        }

        auth.createUserWithEmailAndPassword(tv_username.text.toString(), tv_password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    progressBar.visibility = View.GONE
                } else {
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthUserCollisionException) {
                        tv_username.error = resources?.getString(R.string.validate_user_exists)
                        tv_username.requestFocus()
                        Toast.makeText(
                            baseContext,
                            resources?.getString(R.string.sign_up_error_create),
                            Toast.LENGTH_SHORT
                        ).show()
                        progressBar.visibility = View.GONE
                    } catch (e: Exception) {
                        Toast.makeText(
                            baseContext,
                            resources?.getString(R.string.sign_up_error_create),
                            Toast.LENGTH_SHORT
                        ).show()
                        progressBar.visibility = View.GONE
                    }
                }
            }
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}