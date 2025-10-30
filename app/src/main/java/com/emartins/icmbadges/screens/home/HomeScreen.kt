package com.emartins.icmbadges.screens.home

import android.app.Activity
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.emartins.icmbadges.components.EnrollmentModal
import com.emartins.icmbadges.components.EventModal
import com.emartins.icmbadges.components.SearchCard
import com.emartins.icmbadges.services.print.ElginM10Printer
import com.emartins.icmbadges.ui.theme.NeutralGrey
import com.emartins.icmbadges.utils.PdfUtils
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap


@Composable
fun HomeScreen(codNivel: String, viewModel: HomeViewModel = viewModel()) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val enrollmentData = viewModel.enrollmentData.collectAsState()
    val selectedEvent = viewModel.selectedEvent.collectAsState()
    val error = viewModel.error.collectAsState()
    val pdfBytes = viewModel.pdfBytes.collectAsState()

    val isModalEnrollmentOpen = remember { mutableStateOf(false) }

    val printer = ElginM10Printer(context as Activity)

    var pdfBitmap by remember { mutableStateOf<Bitmap?>(null) }

    fun onModalEnrollmentClose() {
        isModalEnrollmentOpen.value = false
    }

    LaunchedEffect(codNivel) {
        viewModel.setCodNivel(codNivel)
    }

    LaunchedEffect(Unit) {
        viewModel.openModal()
        viewModel.loadEvents()
    }

    EventModal()

    EnrollmentModal(isModalEnrollmentOpen, ::onModalEnrollmentClose)

    LaunchedEffect(pdfBytes.value) {
        pdfBytes.value?.let { bytes ->
            isModalEnrollmentOpen.value = false
            println("BYTES $bytes")
            //val badgeBitmap
            pdfBitmap = PdfUtils.convertPdfToBitmap(context, bytes)

            printer.printBadge(pdfBitmap!!)
            printer.cutPaper()
        }
    }

    Box(
            modifier = Modifier.fillMaxSize().padding(12.dp),          // ocupa toda a tela
            contentAlignment = Alignment.Center // centraliza conteúdo
        ) {
        Column(modifier = Modifier.width(500.dp).padding(24.dp)) {
            Box(modifier = Modifier.fillMaxWidth()) {


                Column {
                    Text(
                        "Imprima Seu Crachá do Evento:",
                        fontSize = 20.sp,
                        color = NeutralGrey,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    selectedEvent.value?.let {
                        Text(
                            selectedEvent.value!!.classes_desc,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))

            SearchCard { query ->
                focusManager.clearFocus(true)

                if (query.isNotEmpty()) {
                    isModalEnrollmentOpen.value = true
                    viewModel.search(query)
                } else {
                    Toast.makeText(context, "Digite o CPF do Participante", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            error.value?.let { Text("Erro: $it") }

            when {
                error.value != null -> {
                    Text("Erro: ${error.value}")
                }

                /*isLoading.value -> {
                    Text("Carregando...")
                }

                enrollmentData.value != null -> {
                    val enrollment = enrollmentData.value!!
                    if (enrollment.isem_id == "-1") {
                        Text(
                            text = enrollment.nome,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    } else {

                        Text(text = enrollment.nome)
                        Text(text = enrollment.dsc_classe)

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
                }*/
            }

        }
    }
}
