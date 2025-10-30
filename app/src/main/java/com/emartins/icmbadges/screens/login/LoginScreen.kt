package com.emartins.icmbadges.screens.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.emartins.icmbadges.components.MaanaimsModal
import com.emartins.icmbadges.data.UserPreferences

@Composable
fun LoginScreen(onLoginSuccess: (String) -> Unit, prefs: UserPreferences) {
    val viewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(prefs)
    )

    var username by viewModel.email
    var password by viewModel.password
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    val maanaims by viewModel.maanaims
    val selectedMaanaim by viewModel.maanaimSelected

    val focusManager = LocalFocusManager.current

    val textFieldModifier = Modifier.fillMaxWidth()

    if (maanaims.isNotEmpty() && selectedMaanaim == null) {
        MaanaimsModal( onLoginSuccess = onLoginSuccess )
    }

    Box(
        Modifier.fillMaxWidth().fillMaxHeight().padding(50.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            Column(Modifier.padding(20.dp)) {
                Text(
                    "Login",
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(20.dp))

                TextField(
                    modifier = textFieldModifier,
                    value = username,
                    onValueChange = { username = it.lowercase() },
                    label = { Text("Email") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )
                TextField(
                    modifier = textFieldModifier,
                    value = password,
                    visualTransformation = PasswordVisualTransformation(),
                    onValueChange = { password = it },
                    label = { Text("Senha") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        //keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            viewModel.login(onLoginSuccess)
                        }
                    )
                )

                Spacer(Modifier.height(20.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    onClick = {
                        viewModel.login(onLoginSuccess)
                    },
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text("Entrar")
                    }
                }

                errorMessage?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            }

        }
    }
}