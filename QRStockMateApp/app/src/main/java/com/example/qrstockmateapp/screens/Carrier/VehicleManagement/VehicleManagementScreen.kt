package com.example.qrstockmateapp.screens.Carrier.VehicleManagement

import android.util.Log
import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.qrstockmateapp.R
import com.example.qrstockmateapp.api.models.Transaction
import com.example.qrstockmateapp.api.models.TransportRoute
import com.example.qrstockmateapp.api.models.Vehicle
import com.example.qrstockmateapp.api.services.RetrofitInstance
import com.example.qrstockmateapp.navigation.repository.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun VehicleManagementScreen(navController: NavController) {

    var vehicles by remember { mutableStateOf(emptyList<Vehicle>()) }
    var isloading by remember { mutableStateOf(false) }



    val loadVehicles : ()->Unit = {
        GlobalScope.launch(Dispatchers.IO) {
            isloading = true
            val responseVehicle = RetrofitInstance.api.getVehicles(DataRepository.getUser()!!.code)

            if (responseVehicle.isSuccessful) {
                val vehiclesResponse = responseVehicle.body()
                if(vehiclesResponse !=null ){
                    DataRepository.setVehicles(vehiclesResponse)
                    vehicles = vehiclesResponse
                }
            } else{
                try {
                    val errorBody = responseVehicle.errorBody()?.string()
                    Log.d("excepcionROUTE", errorBody ?: "Error body is null")
                } catch (e: Exception) {
                    Log.e("excepcionROUTEB", "Error al obtener el cuerpo del error: $e")
                }
            }
            delay(1100)
            isloading = false
        }
    }

    LaunchedEffect(Unit){
        loadVehicles()
    }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isloading,
        onRefresh = loadVehicles
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .pullRefresh(pullRefreshState)
    ) {
        PullRefreshIndicator(
            refreshing = isloading,
            state = pullRefreshState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(1f),
            backgroundColor = MaterialTheme.colorScheme.background,
            contentColor = Color(0xff5a79ba)
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            items(vehicles) { vehicle ->
                VehicleItem(navController, vehicle = vehicle, onDeleted = {
                    loadVehicles()
                })
                // Agrega un espacio entre elementos si lo deseas
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

}

@Composable
fun VehicleItem(navController: NavController, vehicle: Vehicle, onDeleted:()->Unit) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var isloading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit){
        delay(1200)
        isloading = false
    }

    val deleteVehicle : ()->Unit = {
        GlobalScope.launch(Dispatchers.IO) {
            val user = DataRepository.getUser()!!

            val response= RetrofitInstance.api.deleteVehicle(vehicle)

            if (response.isSuccessful) {
                val zonedDateTime = ZonedDateTime.now()
                val formattedDate = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                val addTransaccion = RetrofitInstance.api.addHistory(
                    Transaction(0,user.id.toString(),user.code, "The ${vehicle.licensePlate} vehicle has been deleted",
                        formattedDate , 3)
                )
                if(addTransaccion.isSuccessful){
                    Log.d("Transaccion", "OK")
                    withContext(Dispatchers.Main){
                        Toast.makeText(context, "Vehicle has been deleted", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    try {
                        val errorBody = addTransaccion.errorBody()?.string()
                        Log.d("Transaccion", errorBody ?: "Error body is null")
                    } catch (e: Exception) {
                        Log.e("Transaccion", "Error al obtener el cuerpo del error: $e")
                    }
                }
            } else{
                try {
                    val errorBody = response.errorBody()?.string()
                    Log.d("excepcionROUTE", errorBody ?: "Error body is null")
                } catch (e: Exception) {
                    Log.e("excepcionROUTEB", "Error al obtener el cuerpo del error: $e")
                }
            }
        }
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer,),
        shape = RoundedCornerShape(16.dp),
    ) {
        if (isloading){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .height(150.dp)
                    .background(Color.White.copy(alpha = 0.8f)) // Ajusta el nivel de opacidad aquí
            ) {
                // Muestra el indicador de carga lineal con efecto de cristal
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .align(Alignment.Center),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.9f), // Ajusta el nivel de opacidad aquí
                    trackColor = Color(0xff5a79ba).copy(alpha = 0.1f), // Ajusta el nivel de opacidad aquí
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
                        androidx.compose.material.Text(text = "Alert", color = MaterialTheme.colorScheme.primary)
                    },
                    text = {
                        androidx.compose.material.Text(text ="Are you sure you want to delete?", color = MaterialTheme.colorScheme.primary)
                    },
                    confirmButton = {
                        ElevatedButton(
                            onClick = {
                                deleteVehicle()
                                onDeleted()
                                showDialog = false
                            },
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = Color(0xff5a79ba)
                            ),
                            elevation = ButtonDefaults.elevatedButtonElevation(
                                defaultElevation = 5.dp
                            )
                        ){
                            androidx.compose.material.Text("Confirm", color = Color.White)
                        }
                    },
                    dismissButton = {
                        ElevatedButton(
                            onClick = {
                                showDialog = false
                            },
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            ),
                            elevation = ButtonDefaults.elevatedButtonElevation(
                                defaultElevation = 5.dp
                            )
                        ){
                            androidx.compose.material.Text("Cancel", color =  Color(0xff5a79ba))
                        }
                    }
                )
            }
            // Contenido de la tarjeta para cada vehículo
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Imagen a la izquierda
                Image(
                    painter = painterResource(id = R.drawable.carrierbl), // Reemplaza con tu imagen desde drawable
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .weight(4f)
                        .clip(RoundedCornerShape(16.dp))
                )

                // Espacio entre la imagen y el texto
                Spacer(modifier = Modifier.width(16.dp))

                // Columna para el texto a la derecha de la imagen
                Column(
                    modifier = Modifier
                        .weight(6f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Puedes personalizar el contenido según tus necesidades
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, )) {
                                append("Make/Model:")
                            }
                            append(" ${vehicle.make} ${vehicle.model}")
                        },
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Otros detalles del vehículo
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)) {
                                append("Year:")
                            }
                            append(" ${vehicle.year}")
                        },
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 4.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)) {
                                append("Color:")
                            }
                            append(" ${vehicle.color}")
                        },
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 4.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)) {
                                append("License Plate:")
                            }
                            append(" ${vehicle.licensePlate}")
                        },
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)) {
                                append("Max Load:")
                            }
                            append(" ${vehicle.maxLoad} kg")
                        },
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 4.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row {
                        ElevatedButton(
                            modifier = Modifier
                                .weight(5f)
                                .fillMaxWidth(),
                            onClick = {
                                DataRepository.setVehiclePlus(vehicle)
                                navController.navigate("updateVehicle")
                            },
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = Color(0xff5a79ba)
                            ),
                            elevation = ButtonDefaults.elevatedButtonElevation(
                                defaultElevation = 5.dp
                            )
                        )
                        {
                            Icon(
                                imageVector = Icons.Filled.EditNote,
                                contentDescription = "",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.padding(5.dp))
                        ElevatedButton(
                            modifier = Modifier
                                .weight(5f)
                                .fillMaxWidth(),
                            onClick = {
                                showDialog = true
                            },
                            colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                                containerColor = Color(0xff5a79ba)
                            ),
                            elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                                defaultElevation = 5.dp
                            )
                        )
                        {
                            Icon(
                                imageVector = Icons.Filled.DeleteSweep,
                                contentDescription = "",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
