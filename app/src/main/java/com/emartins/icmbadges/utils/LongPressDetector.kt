package com.emartins.icmbadges.utils

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job

@Composable
fun LongPressDetector(
    durationMillis: Long,
    onLongPressConfirmed: () -> Unit,
    content: @Composable () -> Unit
) {
    var isPressing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope() // <--- Obtém o CoroutineScope
    var longPressJob by remember { mutableStateOf<Job?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown()
                    isPressing = true

                    // Lança a corrotina usando o escopo que lembramos
                    longPressJob = coroutineScope.launch {
                        delay(durationMillis)
                        if (isPressing) {
                            onLongPressConfirmed()
                        }
                    }

                    waitForUpOrCancellation()
                    isPressing = false

                    // Cancela o Job se o toque for solto antes
                    longPressJob?.cancel()
                }
            }
    ) {
        content()
    }
}
