package com.example.qrstockmateapp.screens.Auth.JoinWithCode

import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.IconButton
import androidx.compose.material.Snackbar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction

import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.qrstockmateapp.api.models.Company
import com.example.qrstockmateapp.api.models.User
import com.example.qrstockmateapp.api.services.RegistrationBody
import com.example.qrstockmateapp.api.services.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.ui.platform.LocalContext
import com.example.qrstockmateapp.ui.theme.BlueSystem

@Composable
fun JoinWithCodeScreen(navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordMatches by remember { mutableStateOf(true) }
    var phone by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var start by remember {
        mutableStateOf(false)
    }
    val isError = (name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank() || phone.isBlank() || code.isBlank()) && start
    val errorMessage = if (isError) {
        val emptyField = listOf(
            "Name" to name,
            "Email" to email,
            "Password" to password,
            "Confirm Password" to confirmPassword,
            "Phone" to phone,
            "Code" to code
        ).firstOrNull { it.second.isBlank() }

        "${emptyField?.first ?: ""} is required"
    } else null

    val context = LocalContext.current
    val keyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Done
    )
    val keyboardOptionsEmail = KeyboardOptions(
        keyboardType = KeyboardType.Email
    )
    val customTextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        textColor = MaterialTheme.colorScheme.primary,
        backgroundColor = MaterialTheme.colorScheme.outline,
        cursorColor =  BlueSystem,
        focusedBorderColor =  BlueSystem,
        focusedLabelColor = BlueSystem,
        unfocusedBorderColor =  MaterialTheme.colorScheme.secondaryContainer
    )

    val onJoin:() -> Unit = {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                if(code.isEmpty()) code = "000-000"
                val user = User(0,name, email, password,phone,code,"",3)
                val company = Company(0,"","","","","","") //No se va a usar
                val model  = RegistrationBody(user,company)
                val response = RetrofitInstance.api.signUp(model)

                // Cambiar al hilo principal para realizar operaciones en la IU
                withContext(Dispatchers.Main) {
                    Log.d("UIUpdate", "Updating UI after API call")
                    if (response.isSuccessful) {
                        val joinResponse = response.body()
                        if (joinResponse != null){
                            Toast.makeText(context, "Successful Join", Toast.LENGTH_SHORT).show()
                            navController.navigate("login")
                        }
                        else Log.d("excepcionUserA", "jjj")
                    } else{
                        try {
                            val errorBody = response.errorBody()?.string()


                            Toast.makeText(context, "$errorBody", Toast.LENGTH_SHORT).show()


                            Log.d("excepcionUserB", errorBody ?: "Error body is null")
                        } catch (e: Exception) {
                            Log.e("excepcionUserB", "Error al obtener el cuerpo del error: $e")
                        }
                    }
                }
            }catch (e: Exception) {
                Log.d("excepcionUserC","${e}")

            }
        }

    }

    Column(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        ) {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = { navController.navigate("login") }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back to Login", tint = BlueSystem)
                }
            },
            backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
            title = { Text(text = "Join With Code", color = BlueSystem) }
        )
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize().background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally // Alineación central horizontal
        ) {
            if (isError) {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            TextField(
                value = name,
                shape = RoundedCornerShape(8.dp),
                isError = isError,
                onValueChange = { name = it;if(!start)start = true },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth().border(
                    width = 0.5.dp,
                    color =  BlueSystem,
                    shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                ),
                colors = customTextFieldColors
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = email,
                shape = RoundedCornerShape(8.dp),
                isError = isError,
                onValueChange = { email = it;if(!start)start = true },
                label = { Text("Email") },
                keyboardOptions = keyboardOptionsEmail,
                modifier = Modifier.fillMaxWidth().border(
                    width = 0.5.dp,
                    color =  BlueSystem,
                    shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                ),
                colors = customTextFieldColors
            )
            if(!isValidEmail(email) && start)Text("put a valid email", color = Color.Red)
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = password,
                shape = RoundedCornerShape(8.dp),
                isError = isError,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                onValueChange = { password = it;if(!start)start = true },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth().border(
                    width = 0.5.dp,
                    color =  BlueSystem,
                    shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                ),
                colors = customTextFieldColors
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = confirmPassword,
                shape = RoundedCornerShape(8.dp),
                isError = isError,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                onValueChange = {
                    confirmPassword = it
                    passwordMatches = password == it
                },
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth().border(
                    width = 0.5.dp,
                    color =  BlueSystem,
                    shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                ),
                colors = customTextFieldColors
            )
            if (!passwordMatches) {
                Text("Passwords do not match", color = Color.Red)
            }
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = phone,
                shape = RoundedCornerShape(8.dp),
                isError = isError,
                onValueChange = { phone = it;if(!start)start = true },
                label = { Text("Phone") },
                modifier = Modifier.fillMaxWidth().border(
                    width = 0.5.dp,
                    color =  BlueSystem,
                    shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                ),
                colors = customTextFieldColors
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = code,
                shape = RoundedCornerShape(8.dp),
                isError = isError,
                onValueChange = {
                    if (it.length <= 7 && it.matches(Regex("[A-Za-z0-9-]*"))) {
                        val sanitized = it.filter { it.isLetterOrDigit() }.uppercase()
                        if (sanitized.length > 3) {
                            code = "${sanitized.substring(0, 3)}-${sanitized.substring(3)}"
                        } else {
                            code = sanitized
                        }
                    }
                },
                label = { Text("Code") },
                visualTransformation = VisualTransformation.None,
                keyboardOptions = keyboardOptions,
                modifier = Modifier.fillMaxWidth().border(
                    width = 0.5.dp,
                    color =  BlueSystem,
                    shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                ),
                colors = customTextFieldColors
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ElevatedButton(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    onClick = {
                        navController.navigate("login")
                    },
                    colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = 5.dp
                    )
                ){
                    Text("Cancel", color = BlueSystem)
                }
                ElevatedButton(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    onClick = {
                        onJoin()
                    },
                    colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                        containerColor = BlueSystem
                    ),
                    elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = 5.dp
                    )
                ){
                    Text("Join", color = Color.White)
                }
            }
        }
    }
}
fun isValidEmail(email: String): Boolean {
    val pattern = Patterns.EMAIL_ADDRESS
    return pattern.matcher(email).matches()
}
