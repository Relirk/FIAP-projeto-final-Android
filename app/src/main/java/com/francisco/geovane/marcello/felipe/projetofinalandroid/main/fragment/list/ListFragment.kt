package com.francisco.geovane.marcello.felipe.projetofinalandroid.main.fragment.list

import android.R.attr
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.francisco.geovane.marcello.felipe.projetofinalandroid.R

class ListFragment : Fragment() {

//    private var pageId: String = "List"
//    private var bundle: Bundle = Bundle()
//    private var appId: String = BuildConfig.APP_ID
//
//    private val firebasePlaceService = FirebasePlaceService()

    private lateinit var adapter: ListAdapter
    private lateinit var listViewModel: ListViewModel
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        listViewModel = ViewModelProvider(this).get(ListViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_list, container, false)

        progressBar = root.findViewById(R.id.list_spinner)
        recyclerView = root.findViewById(R.id.ListView)

        adapter = ListAdapter(root.context)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(root.context)


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        swipeLayout = view.findViewById<View>(R.id.refresher) as SwipeRefreshLayout
        swipeLayout.setOnRefreshListener { bringItens() }
        swipeLayout.setColorScheme(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10001 && resultCode == Activity.RESULT_OK) {
            val ft: FragmentTransaction = requireFragmentManager().beginTransaction()
            ft.detach(this).attach(this).commit();
        }
    }

    override fun onResume() {
        super.onResume()
        bringItens()
    }

    override fun onPause() {
        super.onPause()
        bringItens()
    }

     fun bringItens() {
        listViewModel.fetchPlaces().observe(viewLifecycleOwner, Observer {
            adapter.setList(it)
            adapter.notifyDataSetChanged()
            progressBar.visibility = View.GONE
        })
         Handler().postDelayed(Runnable { swipeLayout.isRefreshing = false }, 3000)
    }
}