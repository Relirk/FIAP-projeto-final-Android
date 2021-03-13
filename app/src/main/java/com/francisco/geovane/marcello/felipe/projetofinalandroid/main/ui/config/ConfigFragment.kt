package com.francisco.geovane.marcello.felipe.projetofinalandroid.main.ui.config

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.francisco.geovane.marcello.felipe.projetofinalandroid.BuildConfig
import com.francisco.geovane.marcello.felipe.projetofinalandroid.R
import com.francisco.geovane.marcello.felipe.projetofinalandroid.utils.AnalyticsUtils
import com.francisco.geovane.marcello.felipe.projetofinalandroid.utils.LocaleUtils
import com.google.firebase.analytics.FirebaseAnalytics

class ConfigFragment : Fragment() {

    private var bundle: Bundle = Bundle()
    private lateinit var analytics: FirebaseAnalytics

    private var appId: String = BuildConfig.APP_ID
    private var pageId = "Config"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        analytics = FirebaseAnalytics.getInstance(context)
        AnalyticsUtils.setPageData(analytics, bundle, appId, pageId)

        val root = inflater.inflate(R.layout.fragment_config, container, false)

        changeLanguage(root)

        return root
    }

    private fun changeLanguage(root: View) {

        val enUS: ImageView = root.findViewById(R.id.img_flag_enUS)
        var enUSEnabled : Boolean = true

        val ptBR: ImageView = root.findViewById(R.id.img_flag_ptBR)
        var ptBREnabled : Boolean = true

        val lang = resources.configuration.locale.language.toString()

        val cc: TextView = root.findViewById(R.id.text_language_current)
        cc.text = "(${resources.getString(R.string.config_lang_current)}: $lang)"

        val matrixGrey = ColorMatrix()
        val matrixRegular = ColorMatrix()

        matrixGrey.setSaturation(0f)
        matrixRegular.setSaturation(1f)

        val filterGrey = ColorMatrixColorFilter(matrixGrey)
        val filterRegular = ColorMatrixColorFilter(matrixRegular)

        if (lang.equals("pt", true)) {

            ptBREnabled = false
            ptBR.colorFilter = filterRegular
            enUS.colorFilter = filterGrey

        } else {

            enUSEnabled = false
            enUS.colorFilter = filterRegular
            ptBR.colorFilter = filterGrey
        }

        enUS.setOnClickListener { if (enUSEnabled) openConfirmation("en", "Inglês (EUA)") }
        ptBR.setOnClickListener { if (ptBREnabled) openConfirmation("pt", "Portuguese (Brazil)") }
    }

    private fun openConfirmation(localeLang: String, lang: String) {

        val builder = AlertDialog.Builder(requireActivity())
        val text: String = String.format(resources.getString(R.string.config_lang_confirmation), lang)

        builder.setMessage(text)
            .setCancelable(false)
            .setPositiveButton(R.string.txt_yes) { dialog, id ->
                context?.let { it1 -> LocaleUtils.updateLanguage(it1, localeLang) }

                AnalyticsUtils.setClickData(analytics, bundle, appId, pageId,"LanguageChange_$localeLang")
                requireFragmentManager().beginTransaction().detach(this).attach(this).commit()
            }
            .setNegativeButton(R.string.txt_cancel) { dialog, id -> dialog.dismiss() }
        val alert = builder.create()
        alert.show()
    }
}