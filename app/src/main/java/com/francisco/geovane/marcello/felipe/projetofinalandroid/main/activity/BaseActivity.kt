package com.francisco.geovane.marcello.felipe.projetofinalandroid.main.activity

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.francisco.geovane.marcello.felipe.projetofinalandroid.BuildConfig
import com.francisco.geovane.marcello.felipe.projetofinalandroid.main.utils.LocaleUtils
import com.google.firebase.analytics.FirebaseAnalytics
import java.util.*

open class BaseActivity : AppCompatActivity() {

    protected var bundle: Bundle = Bundle()
    protected lateinit var analytics: FirebaseAnalytics

    protected var appId: String = BuildConfig.APP_ID

    private var appDefaultLocale: Locale? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        appDefaultLocale = Locale(LocaleUtils.getPrefLang(this))

        super.onCreate(savedInstanceState)
    }

    override fun attachBaseContext(base: Context) {

        val contextBase = LocaleUtils.presetLocale(base)

        super.attachBaseContext(contextBase)
    }

    override fun onResume() {

        super.onResume()

        val lang = LocaleUtils.getPrefLang(this)

        if (appDefaultLocale != null && !appDefaultLocale.toString().equals(lang, true)) {

            recreate()
        }
    }
}