package com.example.qrstockmateapp.screens.Home.AddWarehouse

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.qrstockmateapp.api.models.Transaction
import com.example.qrstockmateapp.api.models.User
import com.example.qrstockmateapp.api.models.Warehouse
import com.example.qrstockmateapp.api.services.RetrofitInstance
import com.example.qrstockmateapp.navigation.repository.DataRepository
import com.example.qrstockmateapp.screens.Home.UpdateWarehouse.ShowDialog
import com.google.android.gms.maps.model.LatLng
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
    var showDialog by remember { mutableStateOf(false) }
    var pinLocation by remember { mutableStateOf<LatLng?>(null) }
    var organization by remember { mutableStateOf("") }

    var selectedOption by remember { mutableStateOf("Select an existing administrator to associate with the warehouse") }
    var isMenuExpanded by remember { mutableStateOf(false) }

    var employees by remember { mutableStateOf(emptyList<User>()) } // inicializar con una lista vacía
    val context = LocalContext.current

    val customTextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        cursorColor =  Color(0xff5a79ba),
        focusedBorderColor =  Color(0xff5a79ba),
        focusedLabelColor = Color(0xff5a79ba),
        backgroundColor = Color(0xfff5f6f7),
        unfocusedBorderColor =  Color.White,
    )

    val addWarehouse:() -> Unit = {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val company = DataRepository.getCompany()
                if(company!=null){
                    Log.d("selected","${selectedOption.split(";")[1].toInt()}")

                    val warehouse = Warehouse(0,name,location, organization,selectedOption.split(";")[1].toInt(),"","", pinLocation!!.latitude,  pinLocation!!.longitude)
                    val response = RetrofitInstance.api.createWarehouse(company.id,warehouse)
                    if(response.isSuccessful){
                        val user = DataRepository.getUser()
                        if(user!=null){
                            val zonedDateTime = ZonedDateTime.now()
                            val formattedDate = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                            val addTransaccion = RetrofitInstance.api.addHistory(Transaction(0,user.id.toString(),user.code, "a ${warehouse.name} warehouse has been added",
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
                          Toast.makeText(context, "Warehouse successfully added", Toast.LENGTH_SHORT).show()

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
    Column {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    androidx.compose.material3.Icon(Icons.Default.ArrowBack, contentDescription = "Back to Login", tint = Color(0xff5a79ba))
                }
            },
            backgroundColor = Color.White,
            title = { androidx.compose.material.Text(text = "Add New Warehouse", color = Color(0xff5a79ba)) }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally // Alineación central horizontal
        ) {
            if (showDialog) {
                ShowDialog(
                    onDismiss = { showDialog = false},
                    onSuccessfully = {
                        showDialog = false

                        pinLocation = it
                    }
                )
            }
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                colors = customTextFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 0.5.dp,
                        color = Color(0xff5a79ba),
                        shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                    ),
            )
            Spacer(modifier = Modifier.height(10.dp))
            // Campo de entrada para la ubicación
            TextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                colors = customTextFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 0.5.dp,
                        color = Color(0xff5a79ba),
                        shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                    ),
            )
            Spacer(modifier = Modifier.height(10.dp))
            TextField(
                value = organization,
                onValueChange = { organization = it },
                label = { Text("Organization") },
                colors = customTextFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 0.5.dp,
                        color = Color(0xff5a79ba),
                        shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                    ),
            )
            Spacer(modifier = Modifier.height(10.dp))
            ElevatedButton(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    showDialog = true
                },
                colors =  androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                    containerColor = Color(0xff5a79ba)
                ),
                elevation =  androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                    defaultElevation = 5.dp
                )
            ){
                Icon(imageVector = Icons.Filled.Add, contentDescription = null, tint = Color.White )
            }
            Spacer(modifier = Modifier.height(10.dp))
            if(pinLocation!=null){
                ElevatedButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = {

                    },
                    colors =  androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                        containerColor = Color.White
                    ),
                    elevation =  androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = 5.dp
                    )
                ){
                    Text("${pinLocation}", color = Color(0xff5a79ba))
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(Color(0xfff5f6f7))
                    .border(
                        width = 0.5.dp,
                        color = Color(0xff5a79ba),
                        shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedOption,
                        modifier = Modifier
                            .weight(9f)
                            .background(Color(0xfff5f6f7))
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

                            }, modifier = Modifier
                                .padding(5.dp)
                                .border(
                                    width = 0.5.dp,
                                    color = Color(0xff5a79ba),
                                    shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                                )
                            ) {
                                Text("Name: ${employee.name}  Role: Administrator Code: ${employee.code}" )
                            }
                        }
                    }
                    Icon(modifier = Modifier.weight(1f), imageVector = Icons.Filled.ArrowDropDown, contentDescription = null, tint =Color(0xff5a79ba))

                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ElevatedButton(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    onClick = {
                        navController.navigate(route = "home")
                    },
                    colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                        containerColor = Color.White
                    ),
                    elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = 5.dp
                    )
                ){
                    Text("Cancel", color = Color(0xff5a79ba))
                }

                ElevatedButton(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    onClick = {
                        addWarehouse()
                    },
                    colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                        containerColor = Color(0xff5a79ba)
                    ),
                    elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = 5.dp
                    )
                ){
                    Text("Add", color = Color.White)
                }
            }

        }
    }

}
