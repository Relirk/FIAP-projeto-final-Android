package com.francisco.geovane.marcello.felipe.projetofinalandroid.main.fragment.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.francisco.geovane.marcello.felipe.projetofinalandroid.BuildConfig
import com.francisco.geovane.marcello.felipe.projetofinalandroid.R
import com.francisco.geovane.marcello.felipe.projetofinalandroid.main.utils.AnalyticsUtils
import com.google.firebase.analytics.FirebaseAnalytics

class AboutFragment : Fragment() {

    private var bundle: Bundle = Bundle()
    private lateinit var analytics: FirebaseAnalytics

    private var appId: String = BuildConfig.APP_ID
    private var pageId: String = "About"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        analytics = FirebaseAnalytics.getInstance(context)
        AnalyticsUtils.setPageData(analytics, bundle, appId, pageId)

        val root = inflater.inflate(R.layout.fragment_about, container, false)

        setInfos(root)
        setDevelopedBy(root)

        return root
    }

    private fun setInfos(root: View) {

        val version: String = requireContext().packageManager.getPackageInfo(
            requireContext().packageName,
            0
        ).versionName

        val appName: TextView = root.findViewById(R.id.text_app_name)
        //appName.setText(R.string.app_name)

        val appVersion: TextView = root.findViewById(R.id.text_app_version_number)
        appVersion.text = version
    }

    private fun setDevelopedBy(root: View) {

        val list: ListView = root.findViewById(R.id.list_text_app_by_info)

        val arr = listOf(
            "Felipe Oliveira   - RM 338405",
            "Francisco Olvera  - RM 338050",
            "Geovane Medina    - RM 338045",
            "Marcello Chuahy   - RM 337780"
        )

        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            root.context,
            R.layout.list_item,
            R.id.text_info,
            arr
        )
        list.adapter = arrayAdapter

        ajustDevelopedByListHeight(arrayAdapter, list)
    }

    private fun ajustDevelopedByListHeight(arrayAdapter: ArrayAdapter<String>, list: ListView) {

        var listHeight = 0

        for (i in 0 until arrayAdapter.count) {

            val listItem: View = arrayAdapter.getView(i, null, list)
            listItem.measure(0, 0)
            listHeight += listItem.measuredHeight
        }

        val listParam: ViewGroup.LayoutParams = list.layoutParams
        listParam.height = listHeight + list.dividerHeight * (arrayAdapter.count - 1)
        list.layoutParams = listParam
        list.requestLayout()
    }
}