package com.emartins.icmbadges.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.emartins.icmbadges.screens.home.HomeViewModel

@Composable
fun EnrollmentModal(
    isModalOpen: MutableState<Boolean>,
    onModalClose: () -> Unit,
    viewModel: HomeViewModel = viewModel ()
) {
    val enrollmentData = viewModel.enrollmentData.collectAsState()
    val events by viewModel.events
    val isLoading by viewModel.isLoading.collectAsState()

    if (isModalOpen.value) {
        AlertDialog(
            onDismissRequest = {
                onModalClose()
            },
            title = { Text("Dados do Seminarista", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
            text = {
                Column {
                    when {
                        isLoading && enrollmentData.value?.isem_id.isNullOrEmpty() -> {
                            Text("Buscando dados do seminarista.", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                        }

                        isLoading && !enrollmentData.value?.isem_id.isNullOrEmpty() -> {
                            Text("Gerando Crachá...", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                        }

                        enrollmentData.value != null -> {
                            val enrollment = enrollmentData.value!!
                            if (enrollment.isem_id == "-1") {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = enrollment.nome.orEmpty(),
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(onClick = { onModalClose() }) {
                                        Text("Voltar")
                                    }
                                }
                            } else {

                                Row(modifier = Modifier.padding(15.dp, 0.dp)) {
                                    Column {
                                        Text(text = "NOME:", fontWeight = FontWeight.Bold)

                                        if (!enrollment.nom_igreja.isNullOrBlank()) {
                                            Text(text = "IGREJA:", fontWeight = FontWeight.Bold)
                                        }

                                        if (!enrollment.nom_equipe.isNullOrBlank()) {
                                            Text(text = "EQUIPE:", fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(4.dp))

                                    Column {
                                        Text(text = enrollment.nome.orEmpty())

                                        if (!enrollment.nom_igreja.isNullOrBlank()) {
                                            Text(text = enrollment.nom_igreja.orEmpty())
                                        }

                                        if (!enrollment.nom_equipe.isNullOrBlank()) {
                                            Text(text = enrollment.nom_equipe.orEmpty())
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        viewModel.generateBadgePdf()
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Imprimir Crachá")
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