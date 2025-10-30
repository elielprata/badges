package com.emartins.icmbadges.screens.login

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emartins.icmbadges.data.UserPreferences
import com.emartins.icmbadges.models.GetEventsResponse
import com.emartins.icmbadges.models.GetModuleResponse
import com.emartins.icmbadges.models.SignInRequest
import com.emartins.icmbadges.services.api.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val prefs: UserPreferences): ViewModel() {
    var email = mutableStateOf("")
    var password = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)
    val maanaimSelected = mutableStateOf<GetModuleResponse?>(null)

    private val _maanaims = mutableStateOf<List<GetModuleResponse>>(emptyList())
    val maanaims: State<List<GetModuleResponse>> = _maanaims

    private val _isModalOpen = MutableStateFlow(false)
    val isModalOpen: StateFlow<Boolean> = _isModalOpen

    fun saveLogin(accessToken: String) {
        viewModelScope.launch {
            prefs.saveLogin(email.value, accessToken)
        }
    }

    fun loadLogin() {
        viewModelScope.launch {
            prefs.loginFlow.collect { (savedEmail, token) ->
                email.value = savedEmail
                // Se precisar usar token, guarda em outro estado
            }
        }
    }

    fun login(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            try {
                val apiService = ApiService()
                val loginResponse = apiService.api.signIn(SignInRequest(email.value, password.value))
                println("Login sucesso: $loginResponse")

                val linkResponse = apiService.api.getLink("Bearer ${loginResponse.accessToken}")
                println("Link sucesso: $linkResponse")
                apiService.fetchFinalLink(linkResponse.url)
                val responseModule = ApiService().api.getModuleInfo()
                println("Module sucesso: ${responseModule[0]}")

                _maanaims.value = responseModule

                openMaanaimsModal()

            } catch (e: Exception) {
                println("ERROR ${e.message}")
                errorMessage.value = e.message
            }


            isLoading.value = false
        }
    }

    fun selectMaanaim(maanaim: GetModuleResponse, onSuccess: (String) -> Unit) {
        maanaimSelected.value = maanaim
        onSuccess(maanaim.cod_nivel)

        closeMaanaimsModal()
    }

    fun openMaanaimsModal() {
        _isModalOpen.value = true
    }

    fun closeMaanaimsModal() {
        _isModalOpen.value = false
    }
}