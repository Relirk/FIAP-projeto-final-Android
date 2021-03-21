package com.francisco.geovane.marcello.felipe.projetofinalandroid.main.fragment.list.adapter

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.francisco.geovane.marcello.felipe.projetofinalandroid.R
import com.francisco.geovane.marcello.felipe.projetofinalandroid.main.activity.edit.EditPlaceActivity
import com.francisco.geovane.marcello.felipe.projetofinalandroid.main.model.LocationObj
import com.francisco.geovane.marcello.felipe.projetofinalandroid.main.service.FirebasePlaceService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.item_place_row.view.*


class ListAdapter(private val context: Context): RecyclerView.Adapter<ListAdapter.AdapterHolder>() {

    private var placeList = mutableListOf<LocationObj>()

    private lateinit var btnEdit: CardView
    private lateinit var btnShare: FloatingActionButton
    private lateinit var btnCall: FloatingActionButton
    private lateinit var btnDelete: FloatingActionButton

    private val firebasePlaceService = FirebasePlaceService()

    private val REQUEST_CODE = 200
    private var PERMISSIONS = arrayOf(
            Manifest.permission.CALL_PHONE
    )

    fun setList(list: MutableList<LocationObj>) {
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

    fun confirmExclusion(id: String?, itemView: View): AlertDialog {
        val stringId: String = id.toString()
        return AlertDialog.Builder(context)
            .setTitle(R.string.title_exclusion)
            .setMessage(R.string.description_exclusion)
            .setIcon(R.drawable.ic_baseline_delete_24)
            .setPositiveButton(R.string.confirm_exclusion) { dialog, _ ->
                firebasePlaceService.deleteLocation(stringId)
                (context as Activity).recreate()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel_exclusion) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }

    inner class AdapterHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindView(place: LocationObj, position: Int) {
            Glide.with(context).load(place.image).into(itemView.imageView)
            itemView.name.text = place.name
            itemView.description.text = place.description
            itemView.phone.text = place.phoneNumber

            btnEdit = itemView.findViewById(R.id.go_to_edit)
            btnShare = itemView.findViewById(R.id.btn_share)
            btnCall = itemView.findViewById(R.id.btn_call)
            btnDelete = itemView.findViewById(R.id.btn_delete)

            btnEdit.setOnClickListener {
                val intent = Intent(itemView.context, EditPlaceActivity::class.java)
                intent.putExtra("id", place.id)
                intent.putExtra("image", place.image)
                intent.putExtra("name", place.name)
                intent.putExtra("address", place.address)
                intent.putExtra("description", place.description)
                intent.putExtra("phone", place.phoneNumber)
                intent.putExtra("isVisited", place.isVisited.toString())
                intent.putExtra("lat", place.lat.toString())
                intent.putExtra("lng", place.lng.toString())
                intent.putExtra("flavor", place.flavor.toString())
                itemView.context.startActivity(intent)
                Log.d(TAG, "Document ID: ${place.id}")
            }

            btnDelete.setOnClickListener {
                confirmExclusion(place.id, itemView).show()
            }

            btnCall.setOnClickListener {
                if (ContextCompat.checkSelfPermission(context.applicationContext, PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions((context as Activity), PERMISSIONS, REQUEST_CODE)
                } else {
                    if (ContextCompat.checkSelfPermission(
                                    context.applicationContext,
                                    PERMISSIONS[0]
                            ) == PackageManager.PERMISSION_GRANTED) {
                        val intent = Intent(Intent.ACTION_CALL)
                        intent.data = Uri.parse("tel:${place.phoneNumber}")
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    }
                }
            }

            btnShare.setOnClickListener {
                val intent = Intent(Intent.ACTION_SEND)
                intent.putExtra(
                    Intent.EXTRA_TEXT,
                    """${place.name}
                    |${place.description}
                    |${place.address}
                    |https://www.google.com/maps/search/?api=1&query=${place.lat},${place.lng}""".trimMargin()
                )
                intent.type = "text/plain"
                context.startActivity(Intent.createChooser(intent, "Share To:"))
            }

        }
    }
}