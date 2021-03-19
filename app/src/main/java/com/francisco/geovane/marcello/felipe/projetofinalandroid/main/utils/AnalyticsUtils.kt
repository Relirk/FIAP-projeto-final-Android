package com.francisco.geovane.marcello.felipe.projetofinalandroid.main.utils

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class AnalyticsUtils {

    companion object {

        fun setPageData(analytics:FirebaseAnalytics, bundle:Bundle, appId:String, pageId:String) {

            bundle.clear()
            bundle.putString("app_id", appId)
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, pageId)

            analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }

        fun setClickData(analytics:FirebaseAnalytics, bundle:Bundle, appId:String, pageId:String, event:String = "") {

            val tag = "$appId:$pageId" + (if(event.isNotEmpty()) ":$event" else "")

            bundle.clear()
            bundle.putString("app_id", appId)
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, pageId)
            bundle.putString("tag", tag)

            analytics.logEvent("button_click", bundle)
        }

    }
}
