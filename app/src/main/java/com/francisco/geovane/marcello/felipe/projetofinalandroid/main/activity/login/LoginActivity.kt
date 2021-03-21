package com.francisco.geovane.marcello.felipe.projetofinalandroid.main.activity.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import com.francisco.geovane.marcello.felipe.projetofinalandroid.R
import com.francisco.geovane.marcello.felipe.projetofinalandroid.main.activity.BaseActivity
import com.francisco.geovane.marcello.felipe.projetofinalandroid.main.activity.MainActivity
import com.francisco.geovane.marcello.felipe.projetofinalandroid.main.activity.signUp.SignUpActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    private var pageId: String = "Login"
    private var TAG: String = "LOGIN"

    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        // Firebase
        auth = Firebase.auth
        firebaseAnalytics = Firebase.analytics

        progressBar = findViewById(R.id.login_spinner)
        progressBar.visibility = View.GONE

        btn_sign_up.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("appId", appId)
            bundle.putString("pageId", pageId)
            firebaseAnalytics.logEvent("e_ClickSignUpFromLogin", bundle)
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        btn_log_in.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val bundle = Bundle()
            bundle.putString("appId", appId)
            bundle.putString("pageId", pageId)
            firebaseAnalytics.logEvent("e_ClickSignInAction", bundle)
            signInUser()
        }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun signInUser() {
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

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    firebaseAnalytics.setUserId(user.toString())
                    updateUI(user)
                    progressBar.visibility = View.GONE
                } else {
                    Log.d(TAG, task.exception.toString())
                    Toast.makeText(baseContext, resources?.getString(R.string.login_error_create), Toast.LENGTH_SHORT).show()
                    updateUI(null)
                    progressBar.visibility = View.GONE
                }
            }
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
