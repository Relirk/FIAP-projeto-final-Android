package com.francisco.geovane.marcello.felipe.projetofinalandroid.utils

import android.content.Context
import android.preference.PreferenceManager
import java.util.*

object LocaleUtils {

    private const val PREF_LANG = "language"

    fun presetLocale(context: Context): Context {

        val lang = getPrefLang(context)

        return setLocale(context, lang!!)
    }

    fun setLocale(context: Context, language: String): Context {

        setPrefLang(context, language)

        val locale = Locale(language)
        Locale.setDefault(locale)

        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        return context.createConfigurationContext(configuration)
    }

    fun getPrefLang(context: Context?, defaultLanguage: String = "en"): String? {

        val preferences = PreferenceManager.getDefaultSharedPreferences(context)

        return preferences.getString(PREF_LANG, defaultLanguage)
    }

    private fun setPrefLang(context: Context, language: String) {

        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()

        editor.putString(PREF_LANG, language)
        editor.apply()
    }
}