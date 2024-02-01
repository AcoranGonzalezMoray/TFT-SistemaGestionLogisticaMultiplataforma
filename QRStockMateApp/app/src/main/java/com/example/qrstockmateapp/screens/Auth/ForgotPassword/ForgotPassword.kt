package com.example.qrstockmateapp.screens.Auth.ForgotPassword

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.example.qrstockmateapp.ui.theme.BlueSystem

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
        textColor = MaterialTheme.colorScheme.primary,
        backgroundColor = MaterialTheme.colorScheme.outline,
        cursorColor =  BlueSystem,
        focusedBorderColor =  BlueSystem,
        focusedLabelColor = BlueSystem,
        unfocusedBorderColor =  MaterialTheme.colorScheme.secondaryContainer
    )


    // Función para manejar la selección del usuario
    val onOptionSelected: (String) -> Unit = { option ->
        selectedOption = option
        expanded = false
    }
    Column(
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ){
        // Agregar el icono en la esquina superior izquierda
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = { navController.navigate("login") }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Login", tint = Color(0xff5a79ba))
                }
            },
            backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
            title = { Text(text = "Forgot Password", color = Color(0xff5a79ba)) }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(26.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            TextField(
                value = email,
                shape = RoundedCornerShape(8.dp),
                onValueChange = { email = it },
                label = { Text("Email", color = MaterialTheme.colorScheme.outlineVariant) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth().border(
                    width = 0.5.dp,
                    color =  Color(0xff5a79ba),
                    shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                ),
                colors = customTextFieldColors
            )

            // Selector de opciones (Sí / No)
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = selectedOption,
                    color = MaterialTheme.colorScheme.primary,
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
                    modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(onClick = {
                            onOptionSelected("Option: $option")
                            expanded = false
                        }) {
                            Text(text = option, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }


            ElevatedButton(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    navController.navigate("login")
                },
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                elevation = ButtonDefaults.elevatedButtonElevation(
                    defaultElevation = 5.dp
                )
            ){
                androidx.compose.material.Text("Reset Password", color = Color(0xff5a79ba))
            }
        }
    }

}
