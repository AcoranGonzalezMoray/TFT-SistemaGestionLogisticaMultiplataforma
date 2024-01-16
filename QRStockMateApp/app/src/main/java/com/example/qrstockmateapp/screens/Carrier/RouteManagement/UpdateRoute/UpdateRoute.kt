package com.example.qrstockmateapp.screens.Carrier.RouteManagement.UpdateRoute

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.qrstockmateapp.R
import com.example.qrstockmateapp.api.models.Item
import com.example.qrstockmateapp.api.models.Transaction
import com.example.qrstockmateapp.api.models.User
import com.example.qrstockmateapp.api.models.Vehicle
import com.example.qrstockmateapp.api.models.Warehouse
import com.example.qrstockmateapp.api.models.userRoleToString
import com.example.qrstockmateapp.api.services.RetrofitInstance
import com.example.qrstockmateapp.navigation.repository.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.apache.commons.math3.stat.descriptive.summary.Product
import java.io.File
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


@Composable
fun UpdateRouteScreen(navController: NavController){
    var showDialog by remember { mutableStateOf(false) }
    var route = remember { DataRepository.getRoutePlus() }
    var dates = (0 until 30).map { LocalDate.now().plusDays(it.toLong()) }
    var listaItems by remember { mutableStateOf( mutableListOf<Item>()) };

    var selectedOptionCarrier by remember { mutableStateOf("Select an existing carrier to associate with the vehicle") }
    var isMenuExpandedCarrier by remember { mutableStateOf(false) }


    var selectedOptionVehicle by remember { mutableStateOf("Select an existing vehicle to associate with the route") }
    var isMenuExpandedVehicle by remember { mutableStateOf(false) }


    var selectedOptionStartLocation by remember { mutableStateOf("Select an existing warehouse to associate with the start location") }
    var isMenuExpandedStartLocation by remember { mutableStateOf(false) }
    var selectedOptionEndLocation by remember { mutableStateOf("Select an existing warehouse to associate with the end location") }
    var isMenuExpandedEndLocation by remember { mutableStateOf(false) }

    var selectedOptionDate by remember { mutableStateOf("Select the date on which transportation will be carried out") }
    var isMenuExpandedDate by remember { mutableStateOf(false) }

    var isloading by remember { mutableStateOf<Boolean>(false) }

    val context = LocalContext.current




    var employees = remember { DataRepository.getEmployees()?.filter { it.role == 4 } ?: emptyList() }
    var vehicles = remember {DataRepository.getVehicles()}
    var warehouses = remember {DataRepository.getWarehouses()}

    LaunchedEffect(Unit){
        if(employees!=null && route!=null){
            val em = employees.find { user: User ->  user.id == route!!.carrierId}
            selectedOptionCarrier = "Name: ${em?.name};  Role: ${userRoleToString(em!!.role)}"
        }
        if(vehicles!=null && route!=null){
            val ve = vehicles.find { vehicle: Vehicle ->  vehicle.id == route!!.assignedVehicleId}
            selectedOptionVehicle = "License Plate: ${ve?.licensePlate}; Year: ${ve?.year}; MaxLoad: ${ve?.maxLoad}"
        }
        if(warehouses!=null && route!=null){
            val waStart = warehouses.find { warehouse: Warehouse->  warehouse.id == route!!.startLocation.toInt()}
            selectedOptionStartLocation= "Warehouse:  ${waStart!!.name}; Latitude: ${waStart.latitude}; Longitude: ${waStart.longitude} "

            val waEnd = warehouses.find { warehouse: Warehouse->  warehouse.id == route!!.endLocation.toInt()}
            selectedOptionEndLocation= "Warehouse:  ${waEnd!!.name}; Latitude: ${waEnd!!.latitude}; Longitude: ${waEnd!!.longitude} "
        }
        if(dates!=null  && route !=null){
            selectedOptionDate = "Date: ${route!!.date.split("T")[0]}"
        }

        GlobalScope.launch(Dispatchers.IO) {
            for(warehouse in DataRepository.getWarehouses()!!) {
                if (warehouse != null) {
                    try {
                        val itemResponse = RetrofitInstance.api.getItems(warehouse.id);
                        if (itemResponse.isSuccessful) {
                            val item = itemResponse.body()
                            if (item != null) listaItems.addAll(item.toMutableList());

                        } else {
                            Log.d("ItemsNotSuccessful", "NO")
                        }

                    } catch (e: Exception) {
                        Log.d("ExceptionItems", "${e.message}")
                    }
                }
            }
        }
    }


    val updateRoute : ()->Unit = {
        GlobalScope.launch(Dispatchers.IO) {
            val user = DataRepository.getUser()!!
            val response= RetrofitInstance.api.putTransportRoutes(transportRoute = route!!)
            if (response.isSuccessful) {
                val transporRoutesResponse = response.body()
                if(transporRoutesResponse!=null){
                    route = transporRoutesResponse
                    val zonedDateTime = ZonedDateTime.now()
                    val formattedDate = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    val addTransaccion = RetrofitInstance.api.addHistory(
                        Transaction(0,user.id.toString(),user.code, "The data of the ${route?.id} route has been modified",
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


    val customTextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        cursorColor = Color.Black,
        focusedBorderColor = Color.Black,
        focusedLabelColor = Color.Black,
        unfocusedBorderColor = Color.Black,
        backgroundColor = Color.LightGray
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
    }else{
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.maps), // Reemplaza con tu lógica para cargar la imagen
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.25f) // La imagen ocupa la mitad de la pantalla
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {

                route?.let {
                    //Carrier
                    Text(text = "Carrier: ")
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = selectedOptionCarrier,
                            modifier = Modifier
                                .background(Color.LightGray)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    isMenuExpandedCarrier = true
                                }
                                .padding(16.dp)
                        )

                        DropdownMenu(
                            expanded = isMenuExpandedCarrier,
                            onDismissRequest = { isMenuExpandedCarrier = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            employees?.forEach { employee ->
                                DropdownMenuItem(onClick = {
                                    selectedOptionCarrier= "Name: ${employee.name};Role: Carrier"
                                    isMenuExpandedCarrier = false
                                }) {
                                    Text( "Name: ${employee.name}  Role: Carrier")
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.padding(5.dp))
                    //Date
                    Text(text = "Date: ")
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = selectedOptionDate,
                            modifier = Modifier
                                .background(Color.LightGray)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    isMenuExpandedDate = true
                                }
                                .padding(16.dp)
                        )

                        DropdownMenu(
                            expanded = isMenuExpandedDate,
                            onDismissRequest = { isMenuExpandedDate = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            dates?.forEach { date->
                                DropdownMenuItem(onClick = {
                                    selectedOptionDate = "Date: ${date}"
                                    isMenuExpandedDate= false
                                }) {
                                    Text( "Date: ${date}" )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.padding(5.dp))
                    //Vehicle
                    Text(text = "Vehicle: ")
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = selectedOptionVehicle,
                            modifier = Modifier
                                .background(Color.LightGray)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    isMenuExpandedVehicle = true
                                }
                                .padding(16.dp)
                        )

                        DropdownMenu(
                            expanded = isMenuExpandedVehicle,
                            onDismissRequest = { isMenuExpandedVehicle = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            vehicles?.forEach { ve ->
                                DropdownMenuItem(onClick = {
                                    selectedOptionVehicle= "License Plate: ${ve.licensePlate};Max Load: ${ve.maxLoad};Year: ${ve.year}"
                                    isMenuExpandedVehicle = false
                                }) {
                                    Text( "License Plate: ${ve.licensePlate}  Max Load: ${ve.maxLoad} Year: ${ve.year}" )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.padding(5.dp))

                    if(selectedOptionVehicle[0]!='S'){
                        //Palets
                        Text(text = "Shipload (Maxload - ${selectedOptionVehicle.split(';')[2].split(':')[1]}Kg): ")
                        Box(modifier = Modifier.fillMaxWidth()) {
                            ElevatedButton(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                onClick = {
                                    showDialog = true
                                },
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = Color(0xff5a79ba)
                                ),
                                elevation = ButtonDefaults.elevatedButtonElevation(
                                    defaultElevation = 5.dp
                                )
                            ){
                                Icon(imageVector = Icons.Filled.Add, contentDescription = null, tint = Color.White )
                            }
                            // Diálogo con la lista de elementos
                            if (showDialog) {
                                ShowListDialog(listaItems, onDismiss = { showDialog = false })
                            }
                        }
                    }


                    //StartLocatiopn
                    Text(text = "Start Location: ")
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = selectedOptionStartLocation,
                            modifier = Modifier
                                .background(Color.LightGray)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    isMenuExpandedStartLocation = true
                                }
                                .padding(16.dp)
                        )

                        DropdownMenu(
                            expanded = isMenuExpandedStartLocation,
                            onDismissRequest = { isMenuExpandedStartLocation = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            warehouses?.forEach { waStart->
                                DropdownMenuItem(onClick = {
                                    selectedOptionStartLocation = "Warehouse:  ${waStart!!.name}; Latitude: ${waStart.latitude}; Longitude: ${waStart.longitude}"
                                    isMenuExpandedStartLocation= false
                                }) {
                                    Text( "Warehouse:  ${waStart!!.name} Latitude: ${waStart.latitude} Longitude: ${waStart.longitude}" )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.padding(5.dp))
                    //EndLocatiopn
                    Text(text = "End Location: ")
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = selectedOptionEndLocation,
                            modifier = Modifier
                                .background(Color.LightGray)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    isMenuExpandedEndLocation = true
                                }
                                .padding(16.dp)
                        )

                        DropdownMenu(
                            expanded = isMenuExpandedEndLocation,
                            onDismissRequest = { isMenuExpandedEndLocation = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            warehouses?.forEach { waEnd->
                                DropdownMenuItem(onClick = {
                                    selectedOptionEndLocation = "Warehouse:  ${waEnd!!.name}; Latitude: ${waEnd.latitude}; Longitude: ${waEnd.longitude}"
                                    isMenuExpandedEndLocation= false
                                }) {
                                    Text( "Warehouse:  ${waEnd!!.name} Latitude: ${waEnd.latitude} Longitude: ${waEnd.longitude}" )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ){
                        ElevatedButton(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            onClick = {
                                navController.navigate("routeManagement")
                            },
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = Color.White
                            ),
                            elevation = ButtonDefaults.elevatedButtonElevation(
                                defaultElevation = 5.dp
                            )
                        ){
                            Text(text = "Cancel", color = Color(0xff5a79ba))
                        }

                        ElevatedButton(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            onClick = {
                                //warehouse.name =name
                                updateRoute()
                            },
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = Color(0xff5a79ba)
                            ),
                            elevation = ButtonDefaults.elevatedButtonElevation(
                                defaultElevation = 5.dp
                            )
                        ){
                            Text(text = "Update", color = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp))
                }
            }

        }
    }
}
@Composable
fun ShowListDialog(listaItems: List<Item>, onDismiss: () -> Unit) {
    var totalWeight by remember { mutableStateOf(0.0) }
    val roundedTotalWeight = remember(totalWeight) {
        Math.round(totalWeight * 100.0) / 100.0
    }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        )
    ) {
        // Contenido del diálogo con la lista de elementos
        // Puedes personalizar y agregar tu propia lógica aquí
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(text = "EuroPalet", fontWeight = FontWeight.Bold)
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
                ) {
                    Icon(imageVector = Icons.Filled.Clear, contentDescription = null)
                    Text("Cancel")
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ){
                Text(text = "Total weight: ${roundedTotalWeight}/1500 Kg")
            }

            LazyColumn{
                items(listaItems) { product ->
                    itemTemplate(product,
                        onCountStateChanged = { newCount ->
                        totalWeight = newCount }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun itemTemplate(item: Item, onCountStateChanged: (Double) -> Unit){
    var count by remember { mutableStateOf(0) }
    val countState = rememberUpdatedState(count)
    DisposableEffect(count) {
        onCountStateChanged(countState.value * item.weightPerUnit)
        onDispose { /* limpiar recursos si es necesario */ }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Imagen del producto a la izquierda (puedes personalizar esto según tus necesidades)
        Image(
            painter = painterResource(id = R.drawable.item), // Reemplaza con tu recurso de imagen
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Contenido a la derecha (nombre, peso por unidad y botones)
        Column(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        ) {
            Text(text = "Name: "+item.name)
            Text(text = "Weight Per Unit: ${item.weightPerUnit} Kg")
            Text(text = "Available: ${item.stock-countState.value} units")
        }
        Spacer(modifier = Modifier.height(8.dp))


    }
    // Botones para añadir y reducir stock
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
            ElevatedButton(
                onClick = {
                    if (countState.value>0){
                        count--
                    }
                },
                colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                    containerColor = Color(0xff5a79ba)
                ),
                elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                    defaultElevation = 5.dp
                )
            ) {
                androidx.compose.material.Text("-", color = Color.White)
            }
            TextField(
                value = countState.value.toString(),
                onValueChange = { newValue ->
                    val value = newValue.toIntOrNull() ?: 0
                    if(value>=0 && value<=item.stock){
                        count = value
                    }
                },
                modifier = Modifier
                    .padding(8.dp)
                    .width(60.dp)
                    .height(45.dp)

            )
            ElevatedButton(
                onClick = {
                    if (countState.value<item.stock){
                        count++
                    }
                },
                colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                    containerColor = Color(0xff5a79ba)
                ),
                elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                    defaultElevation = 5.dp
                )
            ) {
                androidx.compose.material.Text("+", color = Color.White)
            }


    }
}