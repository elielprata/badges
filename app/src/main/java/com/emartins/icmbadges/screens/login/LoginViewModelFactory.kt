package com.emartins.icmbadges.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.emartins.icmbadges.data.UserPreferences

class LoginViewModelFactory(private val prefs: UserPreferences) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(prefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}