package com.francisco.geovane.marcello.felipe.projetofinalandroid.main.activity.edit

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.francisco.geovane.marcello.felipe.projetofinalandroid.R
import com.francisco.geovane.marcello.felipe.projetofinalandroid.main.model.LocationObj
import com.francisco.geovane.marcello.felipe.projetofinalandroid.main.service.FirebasePlaceService
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


@SuppressLint("UseSwitchCompatOrMaterialCode")
class EditPlaceActivity : AppCompatActivity() {

    private lateinit var etPlaceImage: ImageView
    private lateinit var etPlaceName: EditText
    private lateinit var etPlaceAddress: EditText
    private lateinit var etPlaceDescription: EditText
    private lateinit var etPlacePhone: EditText
    private lateinit var etPlaceLat: EditText
    private lateinit var etPlaceLng: EditText
    private lateinit var etPlaceFlavor: EditText
    private lateinit var etPlaceVisited: CheckBox
    private lateinit var progressBar: ProgressBar
    private lateinit var auth: FirebaseAuth

    private var id: String? = null
    private var name: String? = null
    private var phoneNumber:String? = null
    private var address: String? = null
    private var lat: String? = null
    private var lng: String? = null
    private var flavor: String? = null
    private val firebasePlaceService = FirebasePlaceService()

    private var newUrlUriImage: Task<Uri>? = null
    private lateinit var localPathNewImage: Uri

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        supportActionBar?.hide()
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        progressBar = findViewById(R.id.list_spinner)
        etPlaceImage = findViewById(R.id.etPlaceImage)
        etPlaceName = findViewById(R.id.etPlaceName)
        etPlaceAddress = findViewById(R.id.etPlaceAddress)
        etPlaceDescription = findViewById(R.id.etPlaceDescription)
        etPlacePhone = findViewById(R.id.etPlacePhone)
        etPlaceVisited = findViewById(R.id.etPlaceVisited)
        etPlaceLat = findViewById(R.id.etPlaceLat)
        etPlaceLng= findViewById(R.id.etPlaceLng)
        etPlaceFlavor = findViewById(R.id.etPlaceFlavor)

        etPlaceLat.inputType = InputType.TYPE_NULL
        etPlaceLng.inputType = InputType.TYPE_NULL
        etPlaceFlavor.inputType = InputType.TYPE_NULL

        // Firebase
        auth = Firebase.auth
        progressBar.visibility = View.GONE

        val action = intent.getStringExtra("action")
        if(action != null) {
            toolbar.title = resources?.getString(R.string.new_place_title)
            setCreationFields(intent)
        } else {
            toolbar.title = resources?.getString(R.string.edit_place_title)
            setDataFields()
        }

        setSupportActionBar(toolbar)

        val uploadButton = findViewById<Button>(R.id.btnUpload)
        uploadButton.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }

        val saveButton = findViewById<Button>(R.id.btnSave)
        saveButton.setOnClickListener {
            val imagePath = if(action == null) {
                if (newUrlUriImage?.result != null) newUrlUriImage?.result else intent.getStringExtra("image")
            } else {
                newUrlUriImage?.result
            }

            if (etPlaceName.text.toString().trim().isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    R.string.forbidden_empty,
                    Toast.LENGTH_LONG
                ).show()
            } else {

                val place = LocationObj(
                    id.toString(),
                    etPlaceName.text.toString(),
                    etPlaceDescription.text.toString(),
                    etPlaceLat.text.toString(),
                    etPlaceLng.text.toString(),
                    etPlaceVisited.isChecked,
                    etPlacePhone.text.toString(),
                    etPlaceAddress.text.toString(),
                    imagePath.toString(),
                    etPlaceFlavor.text.toString(),
                    auth.currentUser?.uid
                )
                if ( action != null) {
                    firebasePlaceService.saveNewLocation(place)
                } else {
                    val id = intent.getStringExtra("id")
                    firebasePlaceService.saveEditedLocation(id, place)
                }
                finish()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_close_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.clear -> {
            finish()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun setUploadData(Uri: Uri) {
        newUrlUriImage = firebasePlaceService.uploadImage(Uri)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            progressBar.visibility = View.VISIBLE
            etPlaceImage.visibility = View.GONE

            val fileUri = data?.data
            if (fileUri != null) {
                localPathNewImage = fileUri
            }
            setUploadData(localPathNewImage)
            Handler().postDelayed(Runnable {
                progressBar.visibility = View.GONE
                etPlaceImage.visibility = View.VISIBLE
                Glide.with(applicationContext).load(fileUri).into(etPlaceImage) }, 5000)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setDataFields() {
        val originalImage = intent.getStringExtra("image")
        val originalName = intent.getStringExtra("name")
        val originalAddress = intent.getStringExtra("address")
        val originalDescription = intent.getStringExtra("description")
        val originalPhone = intent.getStringExtra("phone")
        val originalVisit = intent.getStringExtra("isVisited").toBoolean()
        lat = intent.getStringExtra("lat")
        lng = intent.getStringExtra("lng")
        flavor = intent.getStringExtra("flavor")

        Glide.with(applicationContext).load(originalImage).into(etPlaceImage)
        etPlaceName.setText(originalName)
        etPlaceAddress.setText(originalAddress)
        etPlaceDescription.setText(originalDescription)
        etPlacePhone.setText(originalPhone)
        etPlaceVisited.isChecked = originalVisit
        etPlaceLat.setText(lat)
        etPlaceLng.setText(lng)
        etPlaceFlavor.setText(flavor)
    }

    private fun setCreationFields(intent: Intent) {
        id = intent.getStringExtra("id")
        name = intent.getStringExtra("name")
        phoneNumber = intent.getStringExtra("phoneNumber")
        address = intent.getStringExtra("address")
        lat = intent.getStringExtra("lat")
        lng = intent.getStringExtra("lng")
        flavor = intent.getStringExtra("flavor")

        etPlaceName.setText(name)
        etPlacePhone.setText(phoneNumber)
        etPlaceAddress.setText(address)
        etPlaceLat.setText(lat)
        etPlaceLng.setText(lng)
        etPlaceFlavor.setText(flavor)
    }
}