package com.example.qrstockmateapp.screens.Auth.SignUp

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.qrstockmateapp.api.models.Company
import com.example.qrstockmateapp.api.models.User
import com.example.qrstockmateapp.api.services.RegistrationBody
import com.example.qrstockmateapp.api.services.RetrofitInstance
import com.example.qrstockmateapp.screens.Auth.JoinWithCode.isValidEmail
import com.example.qrstockmateapp.ui.theme.BlueSystem
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun SignUpScreen(navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordMatches by remember { mutableStateOf(true) }
    var phone by remember { mutableStateOf("") }
    var start by remember {
        mutableStateOf(false)
    }

    val isError = (name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank() || phone.isBlank() || companyName.isBlank()) && start
    val errorMessage = if (isError) {
        val emptyField = listOf(
            "Name" to name,
            "Email" to email,
            "Password" to password,
            "Confirm Password" to confirmPassword,
            "Phone" to phone,
            "Company Name" to companyName
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
    val onSignUp:() -> Unit = {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val user = User(0,name, email, password,phone,"","",0)
                val company = Company(0,companyName,name,"","","","")
                val model  = RegistrationBody(user,company)

                val response = RetrofitInstance.api.signUp(model)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val joinResponse = response.body()
                        if (joinResponse != null){
                            Toast.makeText(context, "Successful Registration", Toast.LENGTH_SHORT).show()
                            navController.navigate("login")
                        }
                        else Log.d("excepcionUserA", "jjj")
                    } else {
                        try {
                            val errorBody = response.errorBody()?.string()

                            Toast.makeText(context, "$errorBody", Toast.LENGTH_SHORT).show()

                            Log.d("excepcionUserB", errorBody ?: "Error body is null")
                        } catch (e: Exception) {
                            Log.e("excepcionUserB", "Error al obtener el cuerpo del error: $e")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d("excepcionUserC","$e")

            }
        }

    }
    val keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Email
    )
   Column(
       modifier = Modifier.background(MaterialTheme.colorScheme.background)
   ){
       TopAppBar(
           navigationIcon = {
               IconButton(onClick = { navController.navigate("login") }) {
                   Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Login", tint = BlueSystem)
               }
           },
           backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
           title = { Text(text = "Sign Up", color = BlueSystem) }
       )
       Column(
           modifier = Modifier.padding(16.dp).fillMaxSize() ,
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
               isError = isError,
               onValueChange = { name = it;if(!start)start = true },
               label = { Text("Name", color = MaterialTheme.colorScheme.outlineVariant) },
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
               isError = isError,
               onValueChange = { email = it;if(!start)start = true },
               label = { Text("Email", color = MaterialTheme.colorScheme.outlineVariant) },
               keyboardOptions = keyboardOptions,
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
               isError = isError,
               visualTransformation = PasswordVisualTransformation(),
               keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
               onValueChange = { password = it;if(!start)start = true },
               label = { Text("Password", color = MaterialTheme.colorScheme.outlineVariant) },
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
               isError = isError,
               visualTransformation = PasswordVisualTransformation(),
               keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
               onValueChange = {
                   confirmPassword = it
                   passwordMatches = password == it
               },
               label = { Text("Confirm Password", color = MaterialTheme.colorScheme.outlineVariant) },
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
               isError = isError,
               onValueChange = { phone = it;if(!start)start = true },
               label = { Text("Phone", color = MaterialTheme.colorScheme.outlineVariant) },
               modifier = Modifier.fillMaxWidth().border(
                   width = 0.5.dp,
                   color =  BlueSystem,
                   shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

               ),
               colors = customTextFieldColors
           )
           Spacer(modifier = Modifier.height(16.dp))
           TextField(
               value = companyName ,
               isError = isError,
               onValueChange = {companyName  = it;if(!start)start = true },
               label = { Text("Company Name", color = MaterialTheme.colorScheme.outlineVariant) },
               modifier = Modifier.fillMaxWidth().border(
                   width = 0.5.dp,
                   color =  BlueSystem,
                   shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

               ),
               colors = customTextFieldColors
           )
           Spacer(modifier = Modifier.height(16.dp))
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
                   colors = ButtonDefaults.elevatedButtonColors(
                       containerColor = MaterialTheme.colorScheme.secondaryContainer
                   ),
                   elevation = ButtonDefaults.elevatedButtonElevation(
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
                       onSignUp()
                   },
                   colors = ButtonDefaults.elevatedButtonColors(
                       containerColor = BlueSystem
                   ),
                   elevation = ButtonDefaults.elevatedButtonElevation(
                       defaultElevation = 5.dp
                   )
               ){
                   Text("Join", color = Color.White)
               }
           }
       }
   }
}