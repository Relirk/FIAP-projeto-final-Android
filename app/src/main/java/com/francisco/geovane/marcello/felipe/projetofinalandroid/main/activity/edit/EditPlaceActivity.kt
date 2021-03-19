package com.francisco.geovane.marcello.felipe.projetofinalandroid.main.activity.edit

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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

    private val OPERATION_CHOOSE_PHOTO: Int = 2

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        supportActionBar?.hide()
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        setFields()

        val action = intent.getStringExtra("action")
        if(action != null) {
            toolbar.title = resources?.getString(R.string.new_place_title)
        } else {
            toolbar.title = resources?.getString(R.string.edit_place_title)
            setDataFields()
        }

        setSupportActionBar(toolbar)

        val uploadButton = findViewById<Button>(R.id.btnUpload)
        uploadButton.setOnClickListener {
            // PHOTO
            /*val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            Log.d("IMAGE", "$photoPickerIntent")
            startActivityForResult(photoPickerIntent, SELECT_PHOTO)*/
             */

            //GET IMAGE
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            startActivityForResult(intent, OPERATION_CHOOSE_PHOTO)
        }

        val saveButton = findViewById<Button>(R.id.btnSave)
        saveButton.setOnClickListener {
            val replyIntent = Intent()
            if (etPlaceName.text.toString().trim().isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    R.string.forbidden_empty,
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val id = intent.getStringExtra("id")
                //inserir lat e lng quando a info vier do mapa
                val place = Place(
                    id.toString(),
                    etPlaceName.text.toString(),
                    etPlaceDescription.text.toString(),
                    null,
                    null,
                    etPlaceVisited.isChecked,
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            OPERATION_CHOOSE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        handleImage(data)
                    }
                }
        }
    }

    private fun renderImage(imagePath: String?){
        if (imagePath != null) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            etPlaceImage?.setImageBitmap(bitmap)
        }
        else {
            Toast.makeText(
                applicationContext,
                "Image is null",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun getImagePath(uri: Uri?, selection: String?): String {
        var path: String? = null
        val cursor = uri?.let {
            contentResolver.query(it, null, selection, null, null ) }
        if (cursor != null){
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path!!
    }

    private fun handleImage(data: Intent) {
        var imagePath: String? = null
        val uri = data.data

        if (DocumentsContract.isDocumentUri(this, uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            if (uri != null) {
                if ("com.android.providers.media.documents" == uri.authority) {
                    val id = docId.split(":")[1]
                    val storage = MediaStore.Images.Media._ID + "=" + id
                    imagePath = getImagePath(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        storage
                    )
                    Log.d("Image pathhh", "$imagePath")
                }
            }
        }
    }

    private fun setFields() {
        etPlaceImage = findViewById(R.id.etPlaceImage)
        etPlaceName = findViewById(R.id.etPlaceName)
        etPlaceAddress = findViewById(R.id.etPlaceAddress)
        etPlaceDescription = findViewById(R.id.etPlaceDescription)
        etPlacePhone = findViewById(R.id.etPlacePhone)
        etPlaceVisited = findViewById(R.id.etPlaceVisited)
    }

    private fun setDataFields() {
        val originalImage = intent.getStringExtra("image")
        Glide.with(applicationContext).load(originalImage).into(etPlaceImage)

        val originalName = intent.getStringExtra("name")
        etPlaceName.setText(originalName)

        val originalAddress = intent.getStringExtra("address")
        etPlaceAddress.setText(originalAddress)

        val originalDescription = intent.getStringExtra("description")
        etPlaceDescription.setText(originalDescription)

        val originalPhone = intent.getStringExtra("phone")
        etPlacePhone.setText(originalPhone)

        val originalVisit = intent.getStringExtra("isVisited")
        val isVisited = originalVisit.toBoolean()
        etPlaceVisited.setChecked(isVisited)
    }
}