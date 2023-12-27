package com.example.qrstockmateapp.screens.Home.AddWarehouse

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.qrstockmateapp.api.models.Transaction
import com.example.qrstockmateapp.api.models.User
import com.example.qrstockmateapp.api.models.Warehouse
import com.example.qrstockmateapp.api.services.RetrofitInstance
import com.example.qrstockmateapp.navigation.repository.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddWarehouseScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    var organization by remember { mutableStateOf("") }

    var selectedOption by remember { mutableStateOf("Selected an existing administrator to associate with the warehouse") }
    var isMenuExpanded by remember { mutableStateOf(false) }

    var employees by remember { mutableStateOf(emptyList<User>()) } // inicializar con una lista vacía

    val customTextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        cursorColor = Color.Black,
        focusedBorderColor = Color.Black,
        focusedLabelColor = Color.Black,
        unfocusedBorderColor = Color.Black,
        backgroundColor = Color.LightGray
    )

    val addWarehouse:() -> Unit = {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val company = DataRepository.getCompany()
                if(company!=null){
                    Log.d("selected","${selectedOption.split(";")[1].toInt()}")
                    val warehouse = Warehouse(0,name,location, organization,selectedOption.split(";")[1].toInt(),"","")
                    val response = RetrofitInstance.api.createWarehouse(company.id,warehouse)
                    if(response.isSuccessful){
                        val user = DataRepository.getUser()
                        if(user!=null){
                            val zonedDateTime = ZonedDateTime.now()
                            val formattedDate = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                            val addTransaccion = RetrofitInstance.api.addHistory(Transaction(0,user.id.toString(),user.code, "a warehouse has been added",
                                formattedDate , 0))
                            if(addTransaccion.isSuccessful){
                                Log.d("Transaccion", "OK")
                            }else{
                                try {
                                    val errorBody = addTransaccion.errorBody()?.string()
                                    Log.d("Transaccion", errorBody ?: "Error body is null")
                                } catch (e: Exception) {
                                    Log.e("Transaccion", "Error al obtener el cuerpo del error: $e")
                                }
                            }
                        }
                      withContext(Dispatchers.Main){
                          navController.navigate("home")
                      }
                    }else{
                        Log.d("AddWAREHOUSE", "NO")
                    }

                }
            }catch (e: Exception){
                Log.d("ExceptionAddWarehouse", "${e}")
            }
        }
    }




    // Cargar la lista de empleados al inicializar
    LaunchedEffect(Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val company = DataRepository.getCompany()
            if(company!=null){
                val employeesResponse = RetrofitInstance.api.getEmployees(company)
                if (employeesResponse.isSuccessful) {
                    val employeesIO = employeesResponse.body()
                    Log.d("EMPLOYEE", "SI")
                    if(employeesIO!=null){
                        DataRepository.setEmployees(employeesIO)
                        employees = DataRepository.getEmployees()?.filter { it.role == 1 } ?: emptyList()
                    }
                } else Log.d("compnayError", "error")
            }
        }

    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally // Alineación central horizontal
    ) {
        Text(
            text = "Add New Warehouse",
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 20.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            colors = customTextFieldColors,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(10.dp))
        // Campo de entrada para la ubicación
        TextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            colors = customTextFieldColors,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            value = organization,
            onValueChange = { organization = it },
            label = { Text("Organization") },
            colors = customTextFieldColors,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(10.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = selectedOption,
                modifier = Modifier
                    .background(Color.LightGray)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        isMenuExpanded = true
                    }
                    .padding(16.dp)
            )

            DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = { isMenuExpanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                employees.forEach { employee ->
                    DropdownMenuItem(onClick = {
                        selectedOption= "Name: ${employee.name}  Role: Administrator Code: ${employee.code};${employee.id}"
                        Log.d("selected","${selectedOption.split(";")[1].toInt()}")
                        isMenuExpanded = false
                    }) {
                        Text("Name: ${employee.name}  Role: Administrator Code: ${employee.code}" )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { navController.navigate(route = "home") },
                colors = ButtonDefaults.buttonColors(Color.Red),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Text("Cancel", color = Color.White)
            }

            Button(
                onClick = { addWarehouse()},
                colors = ButtonDefaults.buttonColors(Color.Black),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Text("Add", color = Color.White)
            }
        }

    }

}
