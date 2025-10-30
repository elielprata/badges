package com.emartins.icmbadges.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun MaskedTextField(
    modifier: Modifier,
    label: String,
    mask: String, // Ex: "###.###.###-##"
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit
) {
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(text = value))
    }

    LaunchedEffect(value) {
        if (value != textFieldValue.text) {
            textFieldValue = TextFieldValue(value, TextRange(value.length))
        }
    }

    TextField(
        modifier = modifier,
        value = textFieldValue,
        onValueChange = { newValue ->
            // Pega só os números
            val digits = newValue.text.filter { it.isDigit() }

            // Aplica máscara
            val masked = buildString {
                var digitIndex = 0
                for (char in mask) {
                    if (char == '#') {
                        if (digitIndex < digits.length) {
                            append(digits[digitIndex])
                            digitIndex++
                        } else break
                    } else if (digitIndex < digits.length) {
                        append(char)
                    }
                }
            }

            // Calcula posição do cursor corretamente
            val cursorPosition = masked.length.coerceAtMost(masked.length)
            textFieldValue = TextFieldValue(
                text = masked,
                selection = TextRange(cursorPosition)
            )
            onValueChange(masked)
        },
        placeholder = { Text(placeholder) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        label = { Text(label) },
        singleLine = true
    )
}
