package com.francisco.geovane.marcello.felipe.projetofinalandroid.main.fragment.logout

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.francisco.geovane.marcello.felipe.projetofinalandroid.BuildConfig
import com.francisco.geovane.marcello.felipe.projetofinalandroid.R
import com.francisco.geovane.marcello.felipe.projetofinalandroid.main.activity.login.LoginActivity
import com.francisco.geovane.marcello.felipe.projetofinalandroid.main.activity.MainActivity
import com.francisco.geovane.marcello.felipe.projetofinalandroid.main.fragment.logout.viewModel.LogoutViewModel
import com.francisco.geovane.marcello.felipe.projetofinalandroid.main.utils.AnalyticsUtils
import com.francisco.geovane.marcello.felipe.projetofinalandroidlib.components.customdialog.CustomDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class LogoutFragment : Fragment() {

    private var bundle: Bundle = Bundle()
    private lateinit var analytics: FirebaseAnalytics

    private var appId: String = BuildConfig.APP_ID
    private var pageId: String = "Logout"

    private lateinit var logoutViewModel: LogoutViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        auth = Firebase.auth
        analytics = FirebaseAnalytics.getInstance(context)
        AnalyticsUtils.setPageData(analytics, bundle, appId, pageId)


        val customDialog = CustomDialog()
        val logoutTitle = context?.resources?.getString(R.string.logout_title)
        val logoutDescription = context?.resources?.getString(R.string.logout_description)
        val logoutCancel =  context?.resources?.getString(R.string.logout_cancel)
        val logoutConfirm =  context?.resources?.getString(R.string.logout_confirm)

        val (title, subtitle) = Pair(logoutTitle, logoutDescription)

        customDialog.showDialog(
            requireActivity(), R.drawable.logo, title, subtitle,
            logoutConfirm,
            {
                AnalyticsUtils.setClickData(analytics, bundle, appId, pageId, "LogOut")
                auth.signOut()
                val currentUser: FirebaseUser? = auth.currentUser
                Log.d("USER", currentUser.toString())
                if (currentUser == null){
                    customDialog.dismissDialog()
                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
            },
            logoutCancel,
            {
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
                activity?.finish()
                customDialog.dismissDialog()
            },
            false
        )

        logoutViewModel = ViewModelProvider(this).get(LogoutViewModel::class.java)

        return inflater.inflate(R.layout.fragment_logout, container, false)
    }
}