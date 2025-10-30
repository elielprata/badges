package com.emartins.icmbadges

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elgin.e1.Core.Utils
import com.emartins.icmbadges.components.SearchCard
import com.emartins.icmbadges.navigation.MyApp
import com.emartins.icmbadges.services.print.SK210Printer
import com.emartins.icmbadges.ui.theme.ICMBadgesTheme
import com.emartins.icmbadges.utils.PdfUtils
import com.emartins.icmbadges.screens.home.HomeViewModel
import com.emartins.icmbadges.utils.LongPressDetector

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val deviceBrand = Build.BRAND
        val deviceModel = Build.MODEL

        var isInKioskMode = false
        var isM10Device = (deviceBrand == "K-touch" && deviceModel == "MiniPDV M10")

        val setKioskMode = {
            if (isM10Device) {
                Utils(this).desabilitaBotaoPower()
                Utils(this).desabilitaBarraStatus()
                Utils(this).desabilitaBarraNavegacao()
            }
        }

        val onLongPressAction = {
            if (isM10Device) {
                if (isInKioskMode) {
                    Utils(this).habilitaBotaoPower()
                    Utils(this).habilitaBarraStatus()
                    Utils(this).habilitaBarraNavegacao()
                    isInKioskMode = false
                } else {
                    setKioskMode()
                    isInKioskMode = true
                }
            }
        }

        setKioskMode()

        setContent {
            ICMBadgesTheme {
                LongPressDetector(
                    durationMillis = 10 * 1000, // 10 segundos
                    onLongPressConfirmed = onLongPressAction
                ) {
                    MyApp()
                }
                /*Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Content(
                        viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }*/
            }
        }
    }
}

@Composable
fun Content(viewModel: HomeViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val queryResult = viewModel.enrollmentData.collectAsState()
    val isLoading = viewModel.isLoading.collectAsState()
    val error = viewModel.error.collectAsState()
    val pdfBytes = viewModel.pdfBytes.collectAsState()

    val printer = SK210Printer(context)

    var pdfBitmap by remember { mutableStateOf<Bitmap?>(null) }

    fun close() {

    }

    LaunchedEffect(pdfBytes.value) {
        pdfBytes.value?.let { bytes ->
            println("BYTES $bytes")
            //val badgeBitmap
            pdfBitmap = PdfUtils.convertPdfToBitmap(context, bytes)

            printer.printBadge(pdfBitmap!!)
            printer.cutPaper()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().padding(12.dp),          // ocupa toda a tela
        contentAlignment = androidx.compose.ui.Alignment.Center // centraliza conteúdo
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text("Imprima Seu Crachá",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            SearchCard { query ->
                focusManager.clearFocus(true)

                if (query.isNotEmpty()) {
                    viewModel.search(query)
                } else {
                    Toast.makeText(context, "Digite o CPF do Participante", Toast.LENGTH_SHORT).show()
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            error.value?.let { Text("Erro: $it") }

            when {
                isLoading.value -> {
                    Text("Carregando...")
                }

                error.value != null -> {
                    Text("Erro: ${error.value}")
                }

                queryResult.value != null -> {
                    val enrollment = queryResult.value!!
                    Text(text = enrollment.nome)
                    Text(text = enrollment.dsc_classe)
                }

                else -> {
                    Text(text = "Nenhum resultado encontrado")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.generateBadgePdf() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Gerar Crachá")
            }

            pdfBitmap?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Preview do Crachá",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }


}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ICMBadgesTheme {
        Content(HomeViewModel())
    }


}