package com.francisco.geovane.marcello.felipe.projetofinalandroid.main.fragment.list

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.francisco.geovane.marcello.felipe.projetofinalandroid.BuildConfig
import com.francisco.geovane.marcello.felipe.projetofinalandroid.R
import com.francisco.geovane.marcello.felipe.projetofinalandroid.main.activity.edit.EditPlaceActivity
import com.francisco.geovane.marcello.felipe.projetofinalandroid.main.utils.AnalyticsUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.analytics.FirebaseAnalytics

class ListFragment : Fragment() {

    private var bundle: Bundle = Bundle()
    private lateinit var analytics: FirebaseAnalytics

    private var appId: String = BuildConfig.APP_ID
    private var pageId: String = "List"

    private lateinit var adapter: ListAdapter
    private lateinit var listViewModel: ListViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        analytics = FirebaseAnalytics.getInstance(context)
        AnalyticsUtils.setPageData(analytics, bundle, appId, pageId)

        //requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        val root = inflater.inflate(R.layout.fragment_list, container, false)

        val recyclerView = root.findViewById<RecyclerView>(R.id.ListView)
        adapter = ListAdapter(root.context)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(root.context)
        listViewModel = ViewModelProvider(this).get(ListViewModel::class.java)
        bringItens()

        val btnAdd: FloatingActionButton = root.findViewById(R.id.btn_add)
        btnAdd.setOnClickListener {
            AnalyticsUtils.setClickData(analytics, bundle, appId, pageId, "AddNew")

            val intent = Intent(root.context, EditPlaceActivity::class.java)
            intent.putExtra("action", "ADD")
            root.context.startActivity(intent)
        }

        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //super.startActivityForResult(data, resultCode)

        if (resultCode == Activity.RESULT_OK) {
            Log.d(ContentValues.TAG, "Document return: $data")
        }
        /*for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }*/
    }

    fun bringItens() {
        listViewModel.fetchPlaces().observe(viewLifecycleOwner, Observer {
            adapter.setList(it)
            adapter.notifyDataSetChanged()
        })
    }

    override fun onDetach() {
        super.onDetach()
        //requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }
}