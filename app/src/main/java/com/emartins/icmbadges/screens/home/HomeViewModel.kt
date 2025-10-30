package com.emartins.icmbadges.screens.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emartins.icmbadges.models.EnrollmentData
import com.emartins.icmbadges.models.GetBadgeRequest
import com.emartins.icmbadges.models.GetEventsResponse
import com.emartins.icmbadges.services.api.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.compose.runtime.State

class HomeViewModel: ViewModel() {
    private val _codNivel = MutableStateFlow<String?>(null)
    val codNivel: StateFlow<String?> = _codNivel

    private val _events = mutableStateOf<List<GetEventsResponse>>(emptyList())
    val events: State<List<GetEventsResponse>> = _events

    private val _selectedEvent = MutableStateFlow<GetEventsResponse?>(null)
    val selectedEvent: StateFlow<GetEventsResponse?> = _selectedEvent

    private val _enrollmentData = MutableStateFlow<EnrollmentData?>(null)
    val enrollmentData: StateFlow<EnrollmentData?> = _enrollmentData

    private val _pdfBytes = MutableStateFlow<ByteArray?>(null)
    val pdfBytes: StateFlow<ByteArray?> = _pdfBytes

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isLoadingEvents = MutableStateFlow(false)
    val isLoadingEvents: StateFlow<Boolean> = _isLoadingEvents

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isModalOpen = MutableStateFlow(false)
    val isModalOpen: StateFlow<Boolean> = _isModalOpen

    fun search(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                selectedEvent.value?.let { event ->
                    val result =
                        ApiService().api.searchEnrollment(event.id.toInt(), query)
                    if (result.isNotEmpty()) {
                        _enrollmentData.value = result[0]
                    } else {
                        _enrollmentData.value =
                            EnrollmentData("-1", "Nenhum resultado encontrado", "", "")
                    }
                } ?: run {
                    // cai aqui se selectedEvent for null
                    _enrollmentData.value = EnrollmentData("-1", "Nenhum evento selecionado", "","")
                }
            } catch (e: Exception) {
                println("RESPONSE $e")
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setCodNivel(codigo: String) {
        _codNivel.value = codigo
    }

    fun selectEvent(event: GetEventsResponse) {
        _selectedEvent.value = event
        _isModalOpen.value = false
    }

    fun openModal() {
        _isModalOpen.value = true
    }

    fun loadEvents() {
        val codigo = _codNivel.value ?: return
        viewModelScope.launch {
            _isLoadingEvents.value = true
            try {
                val events = ApiService().api.getEvents(codigo)
                _events.value = events
            } catch (e: Exception) {
                println("ERROR: ${e.message}")
            } finally {
                _isLoadingEvents.value = false
            }
        }
    }

    fun closeModal() {
        _isModalOpen.value = false
    }

    fun generateBadgePdf() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Aqui você pega o dado do participante que já está no ViewModel
                val enrollment = enrollmentData.value ?: return@launch

                val request = GetBadgeRequest(
                    ids = listOf(enrollment.isem_id)
                )

                selectedEvent.value?.let { event ->
                    // Chamada para sua API que retorna PDF
                    val response = ApiService().api.getBadgePdf(event.id.toInt(), request)

                    if (response.isSuccessful) {
                        response.body()?.let { body ->
                            val pdfBytes = body.bytes()
                            //print(pdfBytes)

                            _pdfBytes.value = pdfBytes
                            _enrollmentData.value = null
                        }

                    } else {
                        response.errorBody()?.let { body ->
                            println("STATUS: ${response.code()}")
                            //println("BODY: ${body.string()}")
                        }
                    }
                }

            } catch (e: Exception) {
                println("TESTE: ${e.message}")
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}