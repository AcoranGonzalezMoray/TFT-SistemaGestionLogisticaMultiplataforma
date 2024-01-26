package com.example.qrstockmateapp.screens.Auth.Login

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.qrstockmateapp.R
import com.example.qrstockmateapp.api.models.User
import com.example.qrstockmateapp.api.services.ApiService
import com.example.qrstockmateapp.api.services.RetrofitInstance
import com.example.qrstockmateapp.screens.Auth.JoinWithCode.isValidEmail
import com.example.qrstockmateapp.ui.theme.BlueSystem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Login(navController: NavHostController, onLoginSuccess: (Boolean, User, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isloading by remember {
        mutableStateOf<Boolean>(false)
    }
    var start by remember {
        mutableStateOf(false)
    }
    val isError = (email.isBlank() || password.isBlank()) && start
    val errorMessage = if (isError) {
        val emptyField = listOf(
            "Email" to email,
            "Password" to password
        ).firstOrNull { it.second.isBlank() }

        "${emptyField?.first ?: ""} is required"
    } else null


    val context = LocalContext.current

    val customTextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        textColor = MaterialTheme.colorScheme.primary,
        backgroundColor = MaterialTheme.colorScheme.outline,
        cursorColor =  BlueSystem,
        focusedBorderColor =  BlueSystem,
        focusedLabelColor = BlueSystem,
        unfocusedBorderColor =  MaterialTheme.colorScheme.secondaryContainer
    )

    val onLoginClicked: () -> Unit = {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                isloading = true
                val response = RetrofitInstance.api.signIn(email, password)
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        val user = loginResponse.user
                        val token = loginResponse.token
                        RetrofitInstance.updateToken(token)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Successful Login", Toast.LENGTH_SHORT).show()
                        }
                        onLoginSuccess(true, user, token)
                    } else {
                        try {
                            val errorBody = response.errorBody()?.string()
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "$errorBody", Toast.LENGTH_SHORT).show()
                            }
                            Log.d("excepcionUserB", errorBody ?: "Error body is null")
                        } catch (e: Exception) {
                            Log.e("excepcionUserB", "Error al obtener el cuerpo del error: $e")
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        if(response.code() == 400) Toast.makeText(context, "Error the format of the fields are not correct", Toast.LENGTH_SHORT).show()
                        if(response.code() == 404) Toast.makeText(context, "Incorrect Credentials Or Disabled Account", Toast.LENGTH_SHORT).show()
                    }

                    Log.d("errorCode", "Código de error: ${response.code()}")
                }
                delay(1000)
                isloading = false
            } catch (e: Exception) {
                Log.d("excepcionUser","${e}")
            }
        }
    }

    val keyboardOptionsEmail = KeyboardOptions(
        keyboardType = KeyboardType.Email
    )

    if (isloading){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Muestra el círculo de carga
            CircularProgressIndicator(
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.Center),
                color = Color.LightGray,
                backgroundColor = BlueSystem
            )
        }
    }else{
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(26.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.app_icon_removed),
                contentDescription = "Descripción de la imagen",
                modifier = Modifier
                    .size(400.dp)
            )
            Spacer(modifier = Modifier.height(5.dp))
            if (isError) {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            TextField(
                value = email,
                isError = isError,
                onValueChange = { email = it;if(!start)start = true },
                label = { Text("Email", color = MaterialTheme.colorScheme.outlineVariant) },
                keyboardOptions = keyboardOptionsEmail,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 0.5.dp,
                        color = BlueSystem,
                        shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                    ),
                shape = RoundedCornerShape(8.dp),
                colors = customTextFieldColors
            )
            if(!isValidEmail(email) && start) androidx.compose.material.Text(
                "put a valid email",
                color = Color.Red
            )
            Spacer(modifier = Modifier.height(10.dp))
            TextField(
                value = password,
                isError = isError,
                shape = RoundedCornerShape(8.dp),
                onValueChange = { password = it;if(!start)start = true },
                label = { Text("Password", color = MaterialTheme.colorScheme.outlineVariant) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 0.5.dp,
                        color = BlueSystem,
                        shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                    ),
                colors = customTextFieldColors
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Botón para iniciar sesión

            ElevatedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onLoginClicked()
                },
                colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                    containerColor = BlueSystem
                ),
                elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                    defaultElevation = 5.dp
                )
            ){
                Text("Login", color=Color.White)
            }
            Row {
                Text(
                    text = "Forgot password",
                    color = Color.Gray,
                    modifier = Modifier
                        .clickable {
                            navController.navigate("forgotPassword")
                        }
                        .padding(5.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "Join with code",
                    color = Color.Blue,
                    modifier = Modifier
                        .clickable {
                            navController.navigate(route = "joinWithCode")
                        }
                        .padding(5.dp)
                )
                Text(
                    text = "Sign up",
                    color = Color.Blue,
                    modifier = Modifier
                        .clickable {
                            navController.navigate(route = "signUp")
                        }
                        .padding(5.dp)
                )
            }
        }
    }


}