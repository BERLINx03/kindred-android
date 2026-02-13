package com.example.kindred.ui.theme

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SplashViewModel: ViewModel() {
    private val _isLoaded = MutableStateFlow<Boolean>(false)
    val isLoaded = _isLoaded.asStateFlow()

    fun setIsLoaded(done: Boolean) {
        _isLoaded.value = done
    }
}