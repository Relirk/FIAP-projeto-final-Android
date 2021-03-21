package com.francisco.geovane.marcello.felipe.projetofinalandroid.main.fragment.logout.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LogoutViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is logout Fragment"
    }
    val text: LiveData<String> = _text
}