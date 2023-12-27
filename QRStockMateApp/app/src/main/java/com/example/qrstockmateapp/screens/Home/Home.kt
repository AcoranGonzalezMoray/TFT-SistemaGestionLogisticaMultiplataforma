package com.example.qrstockmateapp.screens.Home

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.example.qrstockmateapp.R
import com.example.qrstockmateapp.api.models.Company
import com.example.qrstockmateapp.api.models.Transaction
import com.example.qrstockmateapp.api.models.User
import com.example.qrstockmateapp.api.models.Warehouse
import com.example.qrstockmateapp.api.services.RetrofitInstance
import com.example.qrstockmateapp.navigation.repository.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(navController: NavController) {
    val user = remember { DataRepository.getUser() }
    var isloading by remember { mutableStateOf(false) }
    var warehouses  by remember { mutableStateOf(emptyList<Warehouse>()) }

    val loadWarehouse:()->Unit = {
        GlobalScope.launch(Dispatchers.IO) {
            isloading = true
            val user = DataRepository.getUser()
            if(user!=null){
                val companyResponse = RetrofitInstance.api.getCompanyByUser(user)
                if (companyResponse.isSuccessful) {
                    val company = companyResponse.body()
                    if(company!=null){
                        DataRepository.setCompany(company)
                        val employeesResponse = RetrofitInstance.api.getEmployees(company)
                        val warehouseResponse = RetrofitInstance.api.getWarehouse(company)

                        if (warehouseResponse.isSuccessful){
                            val warehousesIO = warehouseResponse.body()
                            if(warehousesIO!=null ){
                                DataRepository.setWarehouses(warehousesIO)
                                warehouses = warehousesIO
                            }
                        }else {
                            warehouses = emptyList()
                        }

                        if (employeesResponse.isSuccessful ){
                            val employees = employeesResponse.body()
                            if(employees!=null) DataRepository.setEmployees(employees)

                        }

                    }
                } else Log.d("compnayError", "error")


            }

            delay(1100)
            isloading = false
        }
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isloading,
        onRefresh = loadWarehouse
    )

    if(user !=null){
        LaunchedEffect(Unit) {
            isloading = true
            val companyResponse = RetrofitInstance.api.getCompanyByUser(user)
            if (companyResponse.isSuccessful) {
                val company = companyResponse.body()
                if(company!=null){
                    val warehouseResponse = RetrofitInstance.api.getWarehouse(company)
                    if (warehouseResponse.isSuccessful){
                        val warehousesIO = warehouseResponse.body()
                        Log.d("Warehouse", "SI")
                        if(warehousesIO!=null ){
                            Log.d("Warehouse", "${warehousesIO}")
                            DataRepository.setWarehouses(warehousesIO)
                            warehouses = warehousesIO
                        }
                    }else{
                        try {
                            val errorBody = warehouseResponse.errorBody()?.string()
                            Log.d("excepcionWarehouse", errorBody ?: "Error body is null")
                        } catch (e: Exception) {
                            Log.e("excepcionUserB", "Error al obtener el cuerpo del error: $e")
                        }
                    }
                }
            }
            delay(1100)
            isloading = false
        }
    }

    Box(
        modifier = Modifier
            .pullRefresh(pullRefreshState)
    ){
        PullRefreshIndicator(
            refreshing = isloading,
            state = pullRefreshState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(1f),
            backgroundColor =  Color.White,
        )
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            ) {

            // Mostrar la lista de almacenes
            if (warehouses.isNotEmpty()) {
                WarehouseList(warehouses,navController,loadWarehouse)
            }else{
                Box {
                    Text(text = "There are no warehouses available for this company")
                }
            }
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WarehouseList(warehouses: List<Warehouse>,navController: NavController,loadWarehouse: ()->Unit) {
    LazyColumn {
        items(warehouses) { warehouse ->
            WarehouseItem(warehouse,navController, loadWarehouse)
            Spacer(modifier = Modifier.height(8.dp)) // Agrega un espacio entre elementos de la lista
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WarehouseItem(warehouse: Warehouse,navController: NavController, loadWarehouse:()->Unit) {
    var showDialog by remember { mutableStateOf(false) }

    val deleteWarehouse: () -> Unit = {
        GlobalScope.launch(Dispatchers.IO) {
            val id = DataRepository.getCompany()?.id
            if(id!=null){
                Log.d("DATA", "${id}, ${warehouse}")
                val response = RetrofitInstance.api.deleteWarehouse(id,warehouse)
                if(response.isSuccessful){
                    val user = DataRepository.getUser()
                    if(user!=null){
                        val zonedDateTime = ZonedDateTime.now()
                        val formattedDate = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                        val addTransaccion = RetrofitInstance.api.addHistory(
                            Transaction(0,user.name,user.code, "The ${warehouse?.name} warehouse  has been deleted",
                                formattedDate , 3)
                        )
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
                    loadWarehouse()
                }else{
                    try {
                        val errorBody = response.errorBody()?.string()
                        Log.d("excepcionWarehouse", errorBody ?: "Error body is null")
                    } catch (e: Exception) {
                        Log.e("excepcionUserB", "Error al obtener el cuerpo del error: $e")
                    }
                }
            }
        }
    }



    // Muestra los detalles del almacén dentro de un Card
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
            .background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    // Handle dismissal if needed
                    showDialog = false
                },
                title = {
                    Text(text = "Alert")
                },
                text = {
                    Text(text ="Are you sure you want to delete?")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            deleteWarehouse()
                            showDialog = false
                        }
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            // Handle dismissal action (e.g., cancel)
                            showDialog = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val placeholderImage = painterResource(id = R.drawable.warehouse)
            if (warehouse.url.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .shadow(
                            elevation = 5.dp,
                        )
                        .padding(16.dp)
                ){
                    Image(
                        painter = placeholderImage,
                        contentDescription = "Default User Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            } else {
                // Si hay una URL válida, cargar la imagen usando Coil
                val painter = rememberImagePainter(
                    data = warehouse.url,
                    builder = {
                        crossfade(true)
                        placeholder(R.drawable.loading)
                    }
                )
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .shadow(
                            elevation = 5.dp,
                        )
                        .padding(16.dp)
                ){
                    Image(
                        painter = painter,
                        contentDescription = "warehouse Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }


        Spacer(modifier = Modifier.width(16.dp))

            Column {
                // Nombre del almacén
                Text(
                    text = warehouse.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                // Ubicación del almacén
                Text(
                    text = "Location: ${warehouse.location}",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                // Organización del almacén
                Text(
                    text = "Organization: ${warehouse.organization}",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                //Administrador
                Text(
                    text = "Administrator: ${DataRepository.getEmployees()?.find { user -> user.id == warehouse.idAdministrator}?.name}",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Row {
                    if(DataRepository.getUser()?.role==0 || DataRepository.getUser()?.role==1 ) {
                        Button(
                            onClick = {
                                DataRepository.setWarehousePlus(warehouse)
                                navController.navigate("updateWarehouse")
                            },
                            colors = ButtonDefaults.buttonColors(Color.Yellow),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(end = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Create,
                                contentDescription = "",
                                tint = Color.Black
                            )
                        }
                    }
                    if(DataRepository.getUser()?.role==0) {
                        Button(
                            onClick = {showDialog = true},
                            colors = ButtonDefaults.buttonColors(Color.Red),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f) // Este botón también ocupa la mitad del espacio
                                .padding(start = 4.dp) // Agrega espacio a la izquierda del botón
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "",
                                tint = Color.Black
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        DataRepository.setWarehousePlus(warehouse)
                        navController.navigate("openWarehouse")
                              },
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(Color.Black),
                ) {
                    Text(text = "Open", color = Color.White)
                }

            }

        }
    }
}
