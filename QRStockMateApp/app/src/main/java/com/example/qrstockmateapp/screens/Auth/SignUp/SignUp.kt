package com.example.qrstockmateapp.screens.Auth.SignUp

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.qrstockmateapp.api.models.Company
import com.example.qrstockmateapp.api.models.User
import com.example.qrstockmateapp.api.services.RegistrationBody
import com.example.qrstockmateapp.api.services.RetrofitInstance
import com.example.qrstockmateapp.screens.Auth.JoinWithCode.isValidEmail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SignUpScreen(navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordMatches by remember { mutableStateOf(true) }
    var phone by remember { mutableStateOf("") }


    val isError = name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank() || phone.isBlank() || companyName.isBlank()
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
        cursorColor = Color.Black,
        focusedBorderColor = Color.Black,
        focusedLabelColor = Color.Black,
        unfocusedBorderColor = Color.Black,
        backgroundColor = Color.LightGray
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
                Log.d("excepcionUserC","${e}")

            }
        }

    }
    val keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Email
    )
    Column(
        modifier = Modifier.padding(16.dp) ,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally // Alineación central horizontal
    ) {
        Text(
            text = "Sign Up",
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 20.dp)
        )
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
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            colors = customTextFieldColors
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = email,
            isError = isError,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = keyboardOptions,
            modifier = Modifier.fillMaxWidth(),
            colors = customTextFieldColors
        )
        if(!isValidEmail(email))Text("put a valid email", color = Color.Red)
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = password,
            isError = isError,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
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
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            colors = customTextFieldColors
        )
        if (!passwordMatches) {
            Text("Passwords do not match", color = Color.Red)
        }
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = phone,
            isError = isError,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth(),
            colors = customTextFieldColors
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = companyName ,
            isError = isError,
            onValueChange = {companyName  = it },
            label = { Text("Company Name") },
            modifier = Modifier.fillMaxWidth(),
            colors = customTextFieldColors
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { navController.navigate("login")},
                colors = ButtonDefaults.buttonColors(Color.Red),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Text("Cancel", color = Color.White)
            }

            Button(
                onClick = {onSignUp()},
                colors = ButtonDefaults.buttonColors(Color.Black),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Text("Join", color = Color.White)
            }
        }
    }
}