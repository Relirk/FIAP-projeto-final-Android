package com.francisco.geovane.marcello.felipe.projetofinalandroid.main.ui.list.edit

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.francisco.geovane.marcello.felipe.projetofinalandroid.BuildConfig
import com.francisco.geovane.marcello.felipe.projetofinalandroid.R
import com.francisco.geovane.marcello.felipe.projetofinalandroid.main.model.Place
import com.francisco.geovane.marcello.felipe.projetofinalandroid.main.service.FirebasePlaceService


@SuppressLint("UseSwitchCompatOrMaterialCode")
class EditPlaceActivity : AppCompatActivity() {

    private lateinit var etPlaceImage: ImageView
    private lateinit var etPlaceName: EditText
    private lateinit var etPlaceAddress: EditText
    private lateinit var etPlaceDescription: EditText
    private lateinit var etPlacePhone: EditText
    private lateinit var etPlaceVisited: CheckBox

    private var appId: String = BuildConfig.APP_ID

    private val firebasePlaceService = FirebasePlaceService()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        val action = intent.getStringExtra("action")

        if(action != null) {
            setTitle(R.string.new_place_title)
        } else {
            setTitle(R.string.edit_place_title)
            setDataFields()
        }

        val saveButton = findViewById<Button>(R.id.btnSave)
        saveButton.setOnClickListener {
            val replyIntent = Intent()
            if (etPlaceName.text.toString().trim().isEmpty()) {
                Toast.makeText(
                        applicationContext,
                        R.string.forbidden_empty,
                        Toast.LENGTH_LONG).show()
            } else {
                val id = intent.getStringExtra("id")
                //inserir lat e lng quando a info vier do mapa
                val place = Place(
                        id.toString(),
                        etPlaceName.text.toString(),
                        etPlaceDescription.text.toString(),
                        null,
                        null,
                        etPlaceVisited.isChecked(),
                        etPlacePhone.text.toString(),
                        etPlaceAddress.text.toString(),
                        "",
                        appId
                )
                if (id != null) {
                    firebasePlaceService.saveEditedLocation(id, place)
                } else {
                    firebasePlaceService.saveNewLocation(place)
                }
                setResult(RESULT_OK, replyIntent)
                finish()
            }
        }
    }

    private fun setDataFields() {
        val originalImage = intent.getStringExtra("image")
        etPlaceImage = findViewById(R.id.etPlaceImage)
        Glide.with(applicationContext).load(originalImage).into(etPlaceImage)

        val originalName = intent.getStringExtra("name")
        etPlaceName = findViewById(R.id.etPlaceName)
        etPlaceName.setText(originalName)

        val originalAddress = intent.getStringExtra("address")
        etPlaceAddress = findViewById(R.id.etPlaceAddress)
        etPlaceAddress.setText(originalAddress)

        val originalDescription = intent.getStringExtra("description")
        etPlaceDescription = findViewById(R.id.etPlaceDescription)
        etPlaceDescription.setText(originalDescription)

        val originalPhone = intent.getStringExtra("phone")
        etPlacePhone = findViewById(R.id.etPlacePhone)
        etPlacePhone.setText(originalPhone)

        val originalVisit = intent.getStringExtra("isVisited")
        val isVisited = originalVisit.toBoolean()
        etPlaceVisited = findViewById(R.id.etPlaceVisited)
        etPlaceVisited.setChecked(isVisited)
    }
}