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
import com.emartins.icmbadges.screens.login.LoginViewModel

@Composable
fun MaanaimsModal(
    onLoginSuccess: (String) -> Unit,
    viewModel: LoginViewModel = viewModel ()
) {
    val isModalOpen by viewModel.isModalOpen.collectAsState()
    val maanaims by viewModel.maanaims
    val selectedMaanaim by viewModel.maanaimSelected
    val isLoading by viewModel.isLoading

    if (isModalOpen) {
        AlertDialog(
            onDismissRequest = {
                // só fecha se algum evento já foi selecionado
                if (selectedMaanaim != null) viewModel.closeMaanaimsModal()
            },
            title = { Text("Selecione o Maanaim", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
            text = {
                when {
                    isLoading -> {
                        Text("Carregando...")
                    }

                    maanaims.isNotEmpty() -> {
                        Column(modifier = Modifier.padding(0.dp, 12.dp)) {
                            maanaims.forEach { maanaim ->
                                Button(
                                    onClick = {
                                        viewModel.selectMaanaim(maanaim, { onLoginSuccess(maanaim.cod_nivel) })
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(maanaim.descricao)
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