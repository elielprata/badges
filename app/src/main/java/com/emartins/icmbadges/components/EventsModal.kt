package com.emartins.icmbadges.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.emartins.icmbadges.screens.home.HomeViewModel

@Composable
fun EventModal(
    viewModel: HomeViewModel = viewModel ()
) {
    val isModalOpen by viewModel.isModalOpen.collectAsState()
    val events by viewModel.events
    val selectedEvent by viewModel.selectedEvent.collectAsState()
    val isLoading by viewModel.isLoadingEvents.collectAsState()

    LaunchedEffect(selectedEvent) {
        if (selectedEvent != null) {
            viewModel.closeModal()
        }
    }

    if (isModalOpen) {
        AlertDialog(
            onDismissRequest = {
                // só fecha se algum evento já foi selecionado
                if (selectedEvent != null) viewModel.closeModal()
            },
            title = { Text("Selecione um evento", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
            text = {
                when {
                    isLoading -> {
                        Text("Carregando...")
                    }

                    events.isNotEmpty() -> {
                        Column(modifier = Modifier.padding(0.dp, 12.dp)) {
                            events.forEach { event ->
                                Button(
                                    onClick = {
                                        viewModel.selectEvent(event)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(event.classes_desc)
                                }
                            }
                        }
                    }
                }

            },
            confirmButton = { }
        )
    }
}