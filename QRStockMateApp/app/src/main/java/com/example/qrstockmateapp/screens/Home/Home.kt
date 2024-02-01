package com.example.qrstockmateapp.screens.Home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.qrstockmateapp.R
import com.example.qrstockmateapp.api.models.Transaction
import com.example.qrstockmateapp.api.models.Warehouse
import com.example.qrstockmateapp.api.services.RetrofitInstance
import com.example.qrstockmateapp.navigation.repository.DataRepository
import com.example.qrstockmateapp.ui.theme.BlueSystem
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterialApi::class, DelicateCoroutinesApi::class)
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
                        if(warehousesIO!=null ){
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
            .background(MaterialTheme.colorScheme.background)
            .pullRefresh(pullRefreshState)
    ){
        PullRefreshIndicator(
            refreshing = isloading,
            state = pullRefreshState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(1f),
            backgroundColor = MaterialTheme.colorScheme.background,
            contentColor = BlueSystem
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
                    Text(
                        text = "There are no warehouses available for this company",
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }

}

@Composable
fun WarehouseList(warehouses: List<Warehouse>,navController: NavController,loadWarehouse: ()->Unit) {
    LazyColumn {
        items(warehouses) { warehouse ->
            WarehouseItem(warehouse,navController, loadWarehouse)
            Spacer(modifier = Modifier.height(8.dp)) // Agrega un espacio entre elementos de la lista
        }
        item { 
            Spacer(modifier = Modifier.padding(bottom = 55.dp))
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun WarehouseItem(warehouse: Warehouse,navController: NavController, loadWarehouse:()->Unit) {
    var showDialog by remember { mutableStateOf(false) }

    val deleteWarehouse: () -> Unit = {
        GlobalScope.launch(Dispatchers.IO) {
            val id = DataRepository.getCompany()?.id
            if(id!=null){
                val response = RetrofitInstance.api.deleteWarehouse(id,warehouse)
                if(response.isSuccessful){
                    val user = DataRepository.getUser()
                    if(user!=null){
                        val zonedDateTime = ZonedDateTime.now()
                        val formattedDate = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                        val addTransaccion = RetrofitInstance.api.addHistory(
                            Transaction(0,user.name,user.code, "The ${warehouse.name} warehouse  has been deleted",
                                formattedDate , 3)
                        )
                        if(addTransaccion.isSuccessful){
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

    var isloading by remember { mutableStateOf(true) }
    val modifierDire =
        if (DataRepository.getUser()?.role==0 || DataRepository.getUser()?.role==1 ){
            Modifier
                .fillMaxSize()
                .height(215.dp)
                .background(Color.White.copy(alpha = 0.8f))
        }else{
            Modifier
                .fillMaxSize()
                .height(160.dp)
                .background(Color.White.copy(alpha = 0.8f))
        }


    LaunchedEffect(Unit){
        delay(1200)
        isloading = false
    }
    // Muestra los detalles del almacén dentro de un Card
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        shape = RoundedCornerShape(16.dp),

    ) {
        if (isloading){
            Box(
                modifier = modifierDire
            ) {
                // Muestra el indicador de carga lineal con efecto de cristal
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .align(Alignment.Center),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.9f), // Ajusta el nivel de opacidad aquí
                    trackColor = BlueSystem.copy(alpha = 0.1f), // Ajusta el nivel de opacidad aquí
                )
            }

        }else{
            if (showDialog) {
                AlertDialog(
                    backgroundColor = MaterialTheme.colorScheme.background,
                    onDismissRequest = {
                        // Handle dismissal if needed
                        showDialog = false
                    },
                    title = {
                        Text(text = "Alert", color = MaterialTheme.colorScheme.primary)
                    },
                    text = {
                        Text(text ="Are you sure you want to delete?", color = MaterialTheme.colorScheme.primary)
                    },
                    confirmButton = {
                        ElevatedButton(
                            onClick = {
                                deleteWarehouse()
                                showDialog = false
                            },
                            colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                                containerColor = BlueSystem
                            ),
                            elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                                defaultElevation = 5.dp
                            )
                        ){
                            Text("Confirm", color = Color.White)
                        }
                    },
                    dismissButton = {
                        ElevatedButton(
                            onClick = {
                                showDialog = false
                            },
                            colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            ),
                            elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                                defaultElevation = 5.dp
                            )
                        ){
                            Text("Cancel", color =  BlueSystem)
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
                    androidx.compose.material3.Card(
                        modifier = Modifier
                            .size(120.dp)
                            .padding(16.dp),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 10.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.background,
                        )
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
                    androidx.compose.material3.Card(
                        modifier = Modifier
                            .size(120.dp)
                            .padding(16.dp),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 10.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.background,
                        )

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
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )

                    // Ubicación del almacén
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)) {
                                append("Location:")
                            }
                            append(" ${warehouse.location}")
                        },
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 4.dp),
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Organización del almacén
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)) {
                                append("Organization:")
                            }
                            append(" ${warehouse.organization}")
                        },
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 4.dp),
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Administrador
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold,color = MaterialTheme.colorScheme.primary)) {
                                append("Administrator:")
                            }
                            append(" ${DataRepository.getEmployees()?.find { user -> user.id == warehouse.idAdministrator}?.name}")
                        },
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 4.dp),
                        color = MaterialTheme.colorScheme.primary
                    )


                    Row {
                        if(DataRepository.getUser()?.role==0 || DataRepository.getUser()?.role==1 ) {
                            ElevatedButton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(end = 4.dp),
                                onClick = {
                                    DataRepository.setWarehousePlus(warehouse)
                                    navController.navigate("updateWarehouse")
                                },
                                colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                                    containerColor = BlueSystem
                                ),
                                elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                                    defaultElevation = 5.dp
                                )
                            ){
                                Icon(
                                    imageVector = Icons.Filled.EditNote,
                                    contentDescription = "",
                                    tint = Color.White
                                )
                            }

                        }
                        if(DataRepository.getUser()?.role==0) {
                            ElevatedButton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(end = 4.dp),
                                onClick = {
                                    showDialog = true
                                },
                                colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                                    containerColor = BlueSystem
                                ),
                                elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                                    defaultElevation = 5.dp
                                )
                            ){
                                Icon(
                                    imageVector = Icons.Filled.DeleteSweep,
                                    contentDescription = "",
                                    tint = Color.White
                                )
                            }


                        }
                    }
                    ElevatedButton(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = {
                            DataRepository.setWarehousePlus(warehouse)
                            navController.navigate("openWarehouse")
                        },
                        colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                            containerColor = BlueSystem
                        ),
                        elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                            defaultElevation = 5.dp
                        )
                    )
                    {
                        Text(text = "Open", color = Color.White)
                    }

                }

            }
        }
    }
}
