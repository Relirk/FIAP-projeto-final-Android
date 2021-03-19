package com.francisco.geovane.marcello.felipe.projetofinalandroid.main.fragment.list

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.francisco.geovane.marcello.felipe.projetofinalandroid.BuildConfig
import com.francisco.geovane.marcello.felipe.projetofinalandroid.R
import com.francisco.geovane.marcello.felipe.projetofinalandroid.main.model.Place
import com.francisco.geovane.marcello.felipe.projetofinalandroid.main.service.FirebasePlaceService
import com.francisco.geovane.marcello.felipe.projetofinalandroid.main.activity.edit.EditPlaceActivity
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.item_place_row.view.*

class ListAdapter(private val context: Context): RecyclerView.Adapter<ListAdapter.AdapterHolder>() {

    private var placeList = mutableListOf<Place>()

    private var bundle: Bundle = Bundle()
    private lateinit var analytics: FirebaseAnalytics

    private var appId: String = BuildConfig.APP_ID
    private var pageId: String = "List"

    private lateinit var btnEdit: Button
    private lateinit var btnShare: Button
    private lateinit var btnCall: Button
    private lateinit var btnDelete: Button

    private val firebasePlaceService = FirebasePlaceService()

    fun setList(list: MutableList<Place>) {
        placeList = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterHolder {
        val row = LayoutInflater.from(context).inflate(R.layout.item_place_row, parent, false)
        return AdapterHolder(row)
    }

    override fun getItemCount(): Int {
        return if (placeList.size > 0) {
            placeList.size
        } else {
            0
        }
    }

    override fun onBindViewHolder(holder: AdapterHolder, position: Int) {
        val place = placeList[position]
        holder.bindView(place, position)
    }

    fun confirmExclusion(id: String): AlertDialog {
        return AlertDialog.Builder(context)
            .setTitle(R.string.title_exclusion)
            .setMessage(R.string.description_exclusion)
            .setIcon(R.drawable.ic_baseline_delete_24)
            .setPositiveButton(R.string.confirm_exclusion) { dialog, _ ->
                firebasePlaceService.deleteLocation(id)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel_exclusion) {
                    dialog, _ -> dialog.dismiss()
            }
            .create()
    }

    inner class AdapterHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindView(place: Place, position: Int) {
            Glide.with(context).load(place.image).into(itemView.imageView)
            itemView.name.text = place.name
            itemView.description.text = place.description
            itemView.phone.text = place.phoneNumber

            btnEdit = itemView.findViewById(R.id.btn_edit)
            btnShare = itemView.findViewById(R.id.btn_share)
            btnCall = itemView.findViewById(R.id.btn_call)
            btnDelete = itemView.findViewById(R.id.btn_delete)

            //Clicar na row e ir para o MapFragment
            itemView.setOnClickListener {

            }

            btnEdit.setOnClickListener {
                //AnalyticsUtils.setClickData(analytics, bundle, appId, pageId, "addPlace")

                val intent = Intent(itemView.context, EditPlaceActivity::class.java)
                intent.putExtra("id", place.id)
                intent.putExtra("image", place.image)
                intent.putExtra("name", place.name)
                intent.putExtra("address", place.address)
                intent.putExtra("description", place.description)
                intent.putExtra("phone", place.phoneNumber)
                intent.putExtra("isVisited", place.isVisited.toString())
                itemView.context.startActivity(intent)
                Log.d(TAG, "Document ID: ${place.id}")
            }

            btnDelete.setOnClickListener {
                //AnalyticsUtils.setClickData(analytics, bundle, appId, pageId, "deletePlace")
                confirmExclusion(place.id).show()
            }

            btnCall.setOnClickListener {
                //AnalyticsUtils.setClickData(analytics, bundle, appId, pageId, "callToPlace")
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:${place.phoneNumber}")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }

            btnShare.setOnClickListener {
                //AnalyticsUtils.setClickData(analytics, bundle, appId, pageId, "sharePlace")
                val intent = Intent(Intent.ACTION_SEND)
                intent.putExtra(
                    Intent.EXTRA_TEXT,
                    """${place.name}
                    |${place.description}
                    |${place.address}
                    |https://www.google.com/maps/search/?api=1&query=${place.lat},${place.lng}""".trimMargin())
                intent.type = "text/plain"
                context.startActivity(Intent.createChooser(intent,"Share To:"))
            }

        }
    }
}