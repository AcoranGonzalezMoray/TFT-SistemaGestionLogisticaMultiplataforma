package com.example.qrstockmateapp.screens.Carrier.VehicleManagement.UpdateVehicle

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.qrstockmateapp.R
import com.example.qrstockmateapp.api.models.Transaction
import com.example.qrstockmateapp.api.services.RetrofitInstance
import com.example.qrstockmateapp.navigation.repository.DataRepository
import com.example.qrstockmateapp.screens.Home.UpdateWarehouse.ShowDialog
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun UpdateVehicleScreen(navController: NavController){
    var vehicle = remember { DataRepository.getVehiclePlus() }

    var isloading by remember { mutableStateOf<Boolean>(false) }

    val context = LocalContext.current

    val updateVehicle : () -> Unit = {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                if(vehicle!=null){

                    val response =  RetrofitInstance.api.updateVehicle(vehicle)

                    if (response.isSuccessful) {
                        val wResponse = response.body()
                        val user = DataRepository.getUser()
                        if(user!=null){
                            val zonedDateTime = ZonedDateTime.now()
                            val formattedDate = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                            val addTransaccion = RetrofitInstance.api.addHistory(
                                Transaction(0,user.id.toString(),user.code, "The data of the ${vehicle.licensePlate} vehicle has been modified",
                                    formattedDate , 2)
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
                        withContext(Dispatchers.Main){
                            Toast.makeText(context, "Vehicle successfully updated", Toast.LENGTH_SHORT).show()

                            navController.popBackStack()
                        }
                    }else{
                    }
                }
            }catch (e: Exception) {
                Log.d("excepcionWarehouse","${e}")
            }
        }

    }
    val customTextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        cursorColor =  Color(0xff5a79ba),
        focusedBorderColor =  Color(0xff5a79ba),
        focusedLabelColor = Color(0xff5a79ba),
        backgroundColor = Color(0xfff5f6f7),
        unfocusedBorderColor =  Color.White,
    )


    if (isloading){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray)
        ) {
            // Muestra el círculo de carga
            CircularProgressIndicator(
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.Center),
                color = Color.Black,
                backgroundColor = Color.White
            )
        }
    }else {
        Column {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back to Login",
                            tint = Color(0xff5a79ba)
                        )
                    }
                },
                backgroundColor = Color.White,
                title = { Text(text = "Update Vehicle", color = Color(0xff5a79ba)) }
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .height(300.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.carrierbl), // Reemplaza con tu lógica para cargar la imagen
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    vehicle?.let {
                        var make by remember { mutableStateOf(it.make) }
                        var model by remember { mutableStateOf(it.model) }
                        var year by remember { mutableStateOf(it.year.toString()) }
                        var color by remember { mutableStateOf(it.color) }
                        var licensePlate by remember { mutableStateOf(it.licensePlate) }
                        var maxLoad by remember { mutableStateOf(it.maxLoad.toString()) }

                        TextField(
                            value = make,
                            label = { Text("Make") },
                            onValueChange = { make = it },
                            colors = customTextFieldColors ,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                                .border(
                                    width = 0.5.dp,
                                    color = Color(0xff5a79ba),
                                    shape = RoundedCornerShape(8.dp)
                                )
                        )

                        TextField(
                            value = model,
                            label = { Text("Model") },
                            onValueChange = { model = it },
                            colors = customTextFieldColors,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                                .border(
                                    width = 0.5.dp,
                                    color = Color(0xff5a79ba),
                                    shape = RoundedCornerShape(8.dp)
                                )
                        )

                        TextField(
                            value = year,
                            label = { Text("Year") },
                            onValueChange = { year = it },
                            colors = customTextFieldColors ,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                                .border(
                                    width = 0.5.dp,
                                    color = Color(0xff5a79ba),
                                    shape = RoundedCornerShape(8.dp)
                                )
                        )

                        TextField(
                            value = color,
                            label = { Text("Color") },
                            onValueChange = { color = it },
                            colors = customTextFieldColors ,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                                .border(
                                    width = 0.5.dp,
                                    color = Color(0xff5a79ba),
                                    shape = RoundedCornerShape(8.dp)
                                )
                        )

                        TextField(
                            value = licensePlate,
                            label = { Text("License Plate") },
                            onValueChange = { licensePlate = it },
                            colors = customTextFieldColors ,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                                .border(
                                    width = 0.5.dp,
                                    color = Color(0xff5a79ba),
                                    shape = RoundedCornerShape(8.dp)
                                )
                        )

                        TextField(
                            value = maxLoad,
                            label = { Text("Max Load") },
                            onValueChange = { maxLoad = it },
                            colors = customTextFieldColors ,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                                .border(
                                    width = 0.5.dp,
                                    color = Color(0xff5a79ba),
                                    shape = RoundedCornerShape(8.dp)
                                )
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ){
                            ElevatedButton(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                onClick = {
                                    navController.popBackStack()
                                },
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = Color.White
                                ),
                                elevation = ButtonDefaults.elevatedButtonElevation(
                                    defaultElevation = 5.dp
                                )
                            ){
                                androidx.compose.material3.Text(text = "Cancel", color = Color(0xff5a79ba))
                            }

                            ElevatedButton(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                onClick = {
                                    vehicle.licensePlate = licensePlate
                                    vehicle.make = make
                                    vehicle.maxLoad = maxLoad.toDouble()
                                    vehicle.model = model
                                    vehicle.year = year.toInt()
                                    vehicle.color = color
                                    updateVehicle()
                                },
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = Color(0xff5a79ba)
                                ),
                                elevation = ButtonDefaults.elevatedButtonElevation(
                                    defaultElevation = 5.dp
                                )
                            ){
                                androidx.compose.material3.Text(text = "Update", color = Color.White)
                            }
                        }
                    }

                }
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp))
            }

        }

    }
}