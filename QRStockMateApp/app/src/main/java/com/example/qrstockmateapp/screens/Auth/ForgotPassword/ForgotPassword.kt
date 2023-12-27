package com.example.qrstockmateapp.screens.Auth.ForgotPassword

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPassword(
    navController: NavController,
    onResetPasswordClicked: (String) -> Unit
) {
    // Variables para almacenar la selección del usuario
    var email by remember { mutableStateOf("") }
    var selectedOption by remember { mutableStateOf("Select an option") }

    // Lista de opciones para el selector
    val options = listOf("Yes", "No")

    // Estado para mostrar u ocultar el menú desplegable
    var expanded by remember { mutableStateOf(false) }

    val customTextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        cursorColor = Color.Black,
        focusedBorderColor = Color.Black,
        focusedLabelColor = Color.Black,
        unfocusedBorderColor = Color.Black,
        backgroundColor = Color.LightGray
    )

    // Función para manejar la selección del usuario
    val onOptionSelected: (String) -> Unit = { option ->
        selectedOption = option
        expanded = false
    }
    Column {
        // Agregar el icono en la esquina superior izquierda
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = { navController.navigate("login") }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back to Login", tint = Color.White)
                }
            },
            backgroundColor = Color.Black,
            title = { Text(text = "Forgot Password", color = Color.White) }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(26.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Forgot Password",
                modifier = Modifier.padding(bottom = 16.dp)
            )

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                colors = customTextFieldColors
            )

            // Selector de opciones (Sí / No)
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = selectedOption,
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            expanded = true
                        }
                        .padding(vertical = 16.dp)
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(onClick = {
                            onOptionSelected("Option: $option")
                            expanded = false
                        }) {
                            Text(text = option)
                        }
                    }
                }
            }

            Button(
                onClick = { onResetPasswordClicked(selectedOption) },
                colors = ButtonDefaults.buttonColors(Color.Black),
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
            ) {
                Text("Reset Password")
            }
        }
    }

}
