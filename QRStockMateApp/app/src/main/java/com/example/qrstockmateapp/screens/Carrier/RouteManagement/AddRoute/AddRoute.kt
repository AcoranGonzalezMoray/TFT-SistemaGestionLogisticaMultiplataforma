package com.example.qrstockmateapp.screens.Carrier.RouteManagement.AddRoute


import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.qrstockmateapp.R
import com.example.qrstockmateapp.api.models.Item
import com.example.qrstockmateapp.api.models.Transaction
import com.example.qrstockmateapp.api.models.TransportRoute
import com.example.qrstockmateapp.api.models.User
import com.example.qrstockmateapp.api.models.Vehicle
import com.example.qrstockmateapp.api.models.Warehouse
import com.example.qrstockmateapp.api.models.userRoleToString
import com.example.qrstockmateapp.api.services.RetrofitInstance
import com.example.qrstockmateapp.navigation.repository.DataRepository
import com.example.qrstockmateapp.ui.theme.BlueSystem
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


val myMap = mutableMapOf<Int, String>()

private fun parsePalets(paletsString: String):  Pair<List<Map<Int, String>>, Double> {
    val mapList = mutableListOf<Map<Int, String>>()
    // [{46=46:2:201.3;, 49=49:1:1.0;}, {46=46:2:201.3;}] 1
    // [{46=46:2:201.3;}, {49=49:1:1.0;}] 2


    var trimmedListString= paletsString.trim('[', ']')
    var trimmedStringComplete = trimmedListString.split("},") // [{46=46:2:201.3;, 49=49:1:1.0; , {46=46:2:201.3;}]

    var total: Double = 0.0

    for (i in trimmedStringComplete){
        var trimmedString = i.toString()
        trimmedString = trimmedString.replace("{", "").replace("}", "").replace(",", "").trim()// 46=46:2:201.3;49=49:1:1.0; | 46=46:2:201.3;

        val mapStrings = trimmedString.split(";")

        val map = mutableMapOf<Int, String>()
        // Iterar sobre los mapas en la lista
        for (mapStr in mapStrings) {

            if(mapStr.split("=")[0].trim()!="" &&  mapStr.split("=")[1]!=""){
                val key = mapStr.split("=")[0].trim().toInt()
                var value = mapStr.split("=")[1].trim()

                total+=value.split(":")[2].toDouble()

                value +=";"

                map[key] = value

            }

        }
        mapList.add(map)
    }


    return Pair(mapList, total)
}



@OptIn(DelicateCoroutinesApi::class)
@Composable
fun AddRouteScreen(navController: NavController){
    var showDialog by remember { mutableStateOf(false) }
    var route = remember { mutableStateOf(TransportRoute(
        id = 0,
        code = DataRepository.getUser()!!.code,
        startLocation = "",
        endLocation = "",
        departureTime = "2024-01-18T21:43:44.484Z",
        arrivalTime = "2024-01-18T21:43:44.484Z",
        palets = "",
        assignedVehicleId = 0,
        carrierId = 0,
        date = "2024-01-18T21:43:44.484Z",
        status = 0,
        route  = ""
    ))}

    var dates = (0 until 30).map { LocalDate.now().plusDays(it.toLong()) }
    var listaItems by remember { mutableStateOf( mutableListOf<Item>()) };


    var itemsFormat by remember { mutableStateOf("") }
    var mapEuroPalet by rememberSaveable { mutableStateOf<List<Map<Int, String>>>(emptyList()) }
    var totalWeight by remember { mutableStateOf(0.0) }


    var selectedOptionCarrier by remember { mutableStateOf("Select an existing carrier to associate with the vehicle") }
    var isMenuExpandedCarrier by remember { mutableStateOf(false) }


    var selectedOptionVehicle by remember { mutableStateOf("Select an existing vehicle to associate with the route") }
    var isMenuExpandedVehicle by remember { mutableStateOf(false) }


    var selectedOptionStartLocation by remember { mutableStateOf("Select an existing warehouse to associate with the start location") }
    var isMenuExpandedStartLocation by remember { mutableStateOf(false) }
    var selectedOptionEndLocation by remember { mutableStateOf("Select an existing warehouse to associate with the end location") }
    var isMenuExpandedEndLocation by remember { mutableStateOf(false) }

    val init = "Select an existing warehouse to associate with the start location"
    var selectedOptionDate by remember { mutableStateOf("Select the date on which transportation will be carried out") }
    var isMenuExpandedDate by remember { mutableStateOf(false) }

    var isloading by remember { mutableStateOf<Boolean>(false) }

    var employees = remember { DataRepository.getEmployees()?.filter { it.role == 4 } ?: emptyList() }
    var vehicles = remember {DataRepository.getVehicles()}
    var warehouses = remember {DataRepository.getWarehouses()}

    val context = LocalContext.current


    val loadItems : ()->Unit = {


        GlobalScope.launch(Dispatchers.IO) {
            isloading = true
            listaItems = mutableListOf()
            mapEuroPalet = mutableListOf()
            totalWeight = 0.0
            myMap.clear()
            if (route.value!!.startLocation!=""){
                var warehouse = DataRepository.getWarehouses()!!.filter { warehouse -> warehouse.id == route.value!!.startLocation.toInt() }.firstOrNull()

                if (warehouse != null) {
                    try {
                        val itemResponse = RetrofitInstance.api.getItems(warehouse.id);
                        if (itemResponse.isSuccessful) {
                            val item = itemResponse.body()
                            if (item != null){
                                listaItems.addAll(item.toMutableList())
                            };

                        } else {
                            Log.d("ItemsNotSuccessful", "NO")
                        }

                    } catch (e: Exception) {
                        Log.d("ExceptionItems", "${e.message}")
                    }
                }
            }else{
                Toast.makeText(context, "Select a start warehouse", Toast.LENGTH_SHORT).show()
            }

            delay(1100)
            isloading = false
        }

    }

    LaunchedEffect(Unit){

        // [{46=46:2:201.3;, 49=49:1:1.0;}]
        if(route.value!!.carrierId!=0){
            val em = employees.find { user: User ->  user.id == route.value!!.carrierId}
            selectedOptionCarrier = "Name: ${em?.name};  Role: ${userRoleToString(em!!.role)}"

            selectedOptionDate = "Date: ${route.value!!.date.split("T")[0]}"
        }
        if(vehicles!=null && route.value!!.assignedVehicleId!=0){
            val ve = vehicles.find { vehicle: Vehicle ->  vehicle.id == route.value!!.assignedVehicleId}
            selectedOptionVehicle = "License Plate: ${ve?.licensePlate}; Year: ${ve?.year}; MaxLoad: ${ve?.maxLoad}" // ${selectedOptionVehicle.split(';')[2].split(':')[1]} Kg): ")
        }
        if(warehouses!=null && route.value!!.startLocation!=""){
            val waStart = warehouses.find { warehouse: Warehouse->  warehouse.id == route.value!!.startLocation.toInt()}
            selectedOptionStartLocation= "Warehouse:  ${waStart!!.name}; Latitude: ${waStart.latitude}; Longitude: ${waStart.longitude} "

            val waEnd = warehouses.find { warehouse: Warehouse->  warehouse.id == route.value!!.endLocation.toInt()}
            selectedOptionEndLocation= "Warehouse:  ${waEnd!!.name}; Latitude: ${waEnd!!.latitude}; Longitude: ${waEnd!!.longitude} "
        }

    }


    val addRoute : ()->Unit = {
        itemsFormat = mapEuroPalet.toString()
        route.value!!.palets = itemsFormat
        GlobalScope.launch(Dispatchers.IO) {
            val user = DataRepository.getUser()!!
            Log.d("FUNCIONORUTA", route.value!!.toString())
            val response= RetrofitInstance.api.addTransportRoutes(transportRoute = route.value!!)
            if (response.isSuccessful) {
                val transporRoutesResponse = response.body()
                if(transporRoutesResponse!=null){

                    val zonedDateTime = ZonedDateTime.now()
                    val formattedDate = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    val addTransaccion = RetrofitInstance.api.addHistory(
                        Transaction(0,user.id.toString(),user.code, "a route has been added",
                            formattedDate , 0)
                    )
                    if(addTransaccion.isSuccessful){
                        Log.d("Transaccion", "OK")
                        withContext(Dispatchers.Main){
                            Toast.makeText(context, "Route has been added", Toast.LENGTH_SHORT).show()
                            navController.navigate("routeManagement")
                        }
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
                .background( color = MaterialTheme.colorScheme.background)
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
            modifier = Modifier.background(color = MaterialTheme.colorScheme.background)
        ) {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        androidx.compose.material3.Icon(Icons.Default.ArrowBack, contentDescription = "Back to Login", tint = BlueSystem)
                    }
                },
                backgroundColor =  MaterialTheme.colorScheme.secondaryContainer,
                title = { androidx.compose.material.Text(text = "Add Route", color = BlueSystem) }
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
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
                        Text(text = "Carrier: ", color = MaterialTheme.colorScheme.primary)
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xfff5f6f7))
                            .border(
                                width = 0.5.dp,
                                color = BlueSystem,
                                shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                            )
                        ) {
                            Row(
                                modifier = Modifier.background(color = MaterialTheme.colorScheme.outline),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedOptionCarrier,
                                    modifier = Modifier
                                        .weight(9f)
                                        .background(color = MaterialTheme.colorScheme.outline)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            isMenuExpandedCarrier = true
                                        }
                                        .padding(16.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )

                                DropdownMenu(
                                    expanded = isMenuExpandedCarrier,
                                    onDismissRequest = { isMenuExpandedCarrier = false },
                                    modifier = Modifier.fillMaxWidth().background(color = MaterialTheme.colorScheme.secondaryContainer)
                                ) {
                                    employees?.forEach { employee ->
                                        DropdownMenuItem(onClick = {
                                            selectedOptionCarrier= "Name: ${employee.name}; Role: Carrier"
                                            route.value!!.carrierId = employee.id
                                            isMenuExpandedCarrier = false
                                        }, modifier = Modifier
                                            .padding(5.dp)
                                            .background(color = MaterialTheme.colorScheme.secondaryContainer)
                                            .border(
                                                width = 0.5.dp,
                                                color = BlueSystem,
                                                shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                                            )) {
                                            Text( "Name: ${employee.name}  Role: Carrier", color = MaterialTheme.colorScheme.primary,)
                                        }
                                    }
                                }
                                Icon(modifier = Modifier.weight(1f), imageVector = Icons.Filled.ArrowDropDown, contentDescription = null, tint =BlueSystem)

                            }

                        }
                        Spacer(modifier = Modifier.padding(5.dp))
                        //Date
                        Text(text = "Date: ", color = MaterialTheme.colorScheme.primary)
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xfff5f6f7))
                            .border(
                                width = 0.5.dp,
                                color = BlueSystem,
                                shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                            )) {
                            Row(
                                modifier = Modifier.background(color = MaterialTheme.colorScheme.outline),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedOptionDate,
                                    modifier = Modifier
                                        .weight(9f)
                                        .background(color = MaterialTheme.colorScheme.outline)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            isMenuExpandedDate = true
                                        }
                                        .padding(16.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )

                                DropdownMenu(
                                    expanded = isMenuExpandedDate,
                                    onDismissRequest = { isMenuExpandedDate = false },
                                    modifier = Modifier.fillMaxWidth().background(color = MaterialTheme.colorScheme.secondaryContainer)
                                ) {
                                    dates?.forEach { date->
                                        DropdownMenuItem(onClick = {
                                            selectedOptionDate = "Date: ${date}"
                                            route.value!!.date = date.toString()
                                            isMenuExpandedDate= false
                                        }, modifier = Modifier
                                            .padding(5.dp)
                                            .background(color = MaterialTheme.colorScheme.secondaryContainer)
                                            .border(
                                                width = 0.5.dp,
                                                color = BlueSystem,
                                                shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                                            )) {
                                            Text( "Date: ${date}", color = MaterialTheme.colorScheme.primary )
                                        }
                                    }
                                }
                                Icon(modifier = Modifier.weight(1f), imageVector = Icons.Filled.ArrowDropDown, contentDescription = null, tint =BlueSystem)

                            }
                        }

                        Spacer(modifier = Modifier.padding(5.dp))
                        //Vehicle
                        Text(text = "Vehicle: ",color = MaterialTheme.colorScheme.primary)
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xfff5f6f7))
                            .border(
                                width = 0.5.dp,
                                color = BlueSystem,
                                shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                            )) {
                           Row(
                               modifier = Modifier.background(color = MaterialTheme.colorScheme.outline),
                               verticalAlignment = Alignment.CenterVertically
                           ){
                               Text(
                                   text = selectedOptionVehicle,
                                   modifier = Modifier
                                         .weight(9f)
                                       .background(color = MaterialTheme.colorScheme.outline)
                                       .clickable(
                                           interactionSource = remember { MutableInteractionSource() },
                                           indication = null
                                       ) {
                                           isMenuExpandedVehicle = true
                                       }
                                       .padding(16.dp),
                                   color = MaterialTheme.colorScheme.primary
                               )

                               DropdownMenu(
                                   expanded = isMenuExpandedVehicle,
                                   onDismissRequest = { isMenuExpandedVehicle = false },
                                   modifier = Modifier.fillMaxWidth().background(color = MaterialTheme.colorScheme.secondaryContainer)
                               ) {
                                   vehicles?.forEach { ve ->
                                       DropdownMenuItem(onClick = {
                                           selectedOptionVehicle= "License Plate: ${ve.licensePlate};Year: ${ve.year}; Max Load: ${ve.maxLoad}"
                                           route.value!!.assignedVehicleId = ve.id
                                           isMenuExpandedVehicle = false
                                       }, modifier = Modifier
                                           .padding(5.dp)
                                           .background(color = MaterialTheme.colorScheme.secondaryContainer)
                                           .border(
                                               width = 0.5.dp,
                                               color = BlueSystem,
                                               shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                                           )) {
                                           Text( "License Plate: ${ve.licensePlate}  Max Load: ${ve.maxLoad} Year: ${ve.year}",  color = MaterialTheme.colorScheme.primary )
                                       }
                                   }
                               }
                               Icon(modifier = Modifier.weight(1f), imageVector = Icons.Filled.ArrowDropDown, contentDescription = null, tint =BlueSystem)

                           }
                        }
                        Spacer(modifier = Modifier.padding(5.dp))
                        //StartLocatiopn
                        Text(text = "Start Location: ",color = MaterialTheme.colorScheme.primary)
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xfff5f6f7))
                            .border(
                                width = 0.5.dp,
                                color = BlueSystem,
                                shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                            )) {
                            Row(
                                modifier = Modifier.background(color = MaterialTheme.colorScheme.outline),
                                verticalAlignment = Alignment.CenterVertically){
                                Text(
                                    text = selectedOptionStartLocation,
                                    modifier = Modifier
                                          .weight(9f)
                                        .background(color = MaterialTheme.colorScheme.outline)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            isMenuExpandedStartLocation = true
                                        }
                                        .padding(16.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )

                                DropdownMenu(
                                    expanded = isMenuExpandedStartLocation,
                                    onDismissRequest = { isMenuExpandedStartLocation = false },
                                    modifier = Modifier.fillMaxWidth().background(color = MaterialTheme.colorScheme.secondaryContainer)
                                ) {
                                    warehouses?.forEach { waStart->
                                        DropdownMenuItem(onClick = {
                                            selectedOptionStartLocation = "Warehouse:  ${waStart!!.name}; Latitude: ${waStart.latitude}; Longitude: ${waStart.longitude}"
                                            route.value!!.startLocation = waStart.id.toString()
                                            loadItems()
                                            mapEuroPalet = emptyList()
                                            totalWeight = 0.0
                                            isMenuExpandedStartLocation= false
                                        }, modifier = Modifier
                                            .padding(5.dp)
                                            .background(color = MaterialTheme.colorScheme.secondaryContainer)
                                            .border(
                                                width = 0.5.dp,
                                                color = BlueSystem,
                                                shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                                            )) {
                                            Text( "Warehouse:  ${waStart!!.name} Latitude: ${waStart.latitude} Longitude: ${waStart.longitude}", color = MaterialTheme.colorScheme.primary  )
                                        }
                                    }
                                }
                                Icon(modifier = Modifier.weight(1f), imageVector = Icons.Filled.ArrowDropDown, contentDescription = null, tint =BlueSystem)

                            }
                        }
                        Spacer(modifier = Modifier.padding(5.dp))
                        if(selectedOptionStartLocation!=init){
                            //Palets
                            Text(text = "Shipload (${"%.2f".format(totalWeight).replace(",", ".").toDouble()} / ${selectedOptionVehicle.split(';')[2].split(':')[1]} Kg): ", color = MaterialTheme.colorScheme.primary )
                            Box(modifier = Modifier.fillMaxWidth()) {
                                ElevatedButton(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    onClick = {
                                        showDialog = true
                                    },
                                    colors = ButtonDefaults.elevatedButtonColors(
                                        containerColor = BlueSystem
                                    ),
                                    elevation = ButtonDefaults.elevatedButtonElevation(
                                        defaultElevation = 5.dp
                                    )
                                ){
                                    Icon(imageVector = Icons.Filled.Add, contentDescription = null, tint = Color.White )
                                }

                                // Diálogo con la lista de elementos
                                if (showDialog) {
                                    ShowListDialog(listaItems,
                                        onDismiss = { showDialog = false; myMap.clear() },
                                        onSuccessfully = {
                                            showDialog = false;
                                            //=======================
                                            myMap.forEach { (key, value) ->
                                                totalWeight += "%.2f".format(value.split(":")[2].replace(";", "").toDouble()).replace(",", ".").toDouble()
                                                val itemIndex = listaItems.indexOfFirst { it.id == key }
                                                val count = value.split(":")[1].toInt()
                                                if (itemIndex != -1) listaItems[itemIndex] = listaItems[itemIndex].copy(stock = listaItems[itemIndex].stock - count)

                                            }
                                            //=======================
                                            mapEuroPalet += myMap.filterValues { value ->
                                                !(value is String && value.contains(":0;"))
                                            }

                                            Log.d("EroPalte", mapEuroPalet.toString())
                                            myMap.clear()

                                        })
                                }
                            }

                        }
                        Column {
                            mapEuroPalet.forEachIndexed { index, map ->
                                PaletTemplate(map = map, onDelete = { weight ->
                                    totalWeight -= "%.2f".format(weight).replace(",", ".").toDouble()

                                    map.forEach{(key, value)->
                                        val itemIndex = listaItems.indexOfFirst { it.id == key}
                                        val count = value.split(":")[1].toInt()
                                        if (itemIndex != -1) listaItems[itemIndex] = listaItems[itemIndex].copy(stock = listaItems[itemIndex].stock + count)
                                    }

                                    // Encuentra el índice del mapa en la lista
                                    val mapIndex = mapEuroPalet.indexOf(map)

                                    // Asegúrate de que el mapa esté realmente presente en la lista antes de intentar eliminarlo
                                    if (mapIndex != -1) {
                                        mapEuroPalet = mapEuroPalet.toMutableList().apply {
                                            removeAt(mapIndex)
                                        }
                                        Log.d("EroPalte", mapEuroPalet.toString())
                                    }



                                    Log.d("EroPalte", mapEuroPalet.toString())
                                })
                            }

                        }
                        Spacer(modifier = Modifier.padding(bottom = 10.dp))
                        //EndLocatiopn
                        Text(text = "End Location: ",color = MaterialTheme.colorScheme.primary)
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xfff5f6f7))
                            .border(
                                width = 0.5.dp,
                                color = BlueSystem,
                                shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                            )) {
                           Row(
                               modifier = Modifier.background(color = MaterialTheme.colorScheme.outline),
                               verticalAlignment = Alignment.CenterVertically) {
                               Text(
                                   text = selectedOptionEndLocation,
                                   modifier = Modifier
                                       .weight(9f)
                                       .background(color = MaterialTheme.colorScheme.outline)
                                       .clickable(
                                           interactionSource = remember { MutableInteractionSource() },
                                           indication = null
                                       ) {
                                           isMenuExpandedEndLocation = true
                                       }
                                       .padding(16.dp),
                                   color = MaterialTheme.colorScheme.primary
                               )

                               DropdownMenu(
                                   expanded = isMenuExpandedEndLocation,
                                   onDismissRequest = { isMenuExpandedEndLocation = false },
                                   modifier = Modifier.fillMaxWidth().background(color = MaterialTheme.colorScheme.secondaryContainer)
                               ) {
                                   warehouses?.forEach { waEnd->
                                       DropdownMenuItem(onClick = {
                                           selectedOptionEndLocation = "Warehouse:  ${waEnd!!.name}; Latitude: ${waEnd.latitude}; Longitude: ${waEnd.longitude}"
                                           route.value!!.endLocation = waEnd.id.toString()
                                           isMenuExpandedEndLocation= false
                                       }, modifier = Modifier
                                           .padding(5.dp)
                                           .background(color = MaterialTheme.colorScheme.secondaryContainer)
                                           .border(
                                               width = 0.5.dp,
                                               color = BlueSystem,
                                               shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                                           )) {
                                           Text( "Warehouse:  ${waEnd!!.name} Latitude: ${waEnd.latitude} Longitude: ${waEnd.longitude}", color = MaterialTheme.colorScheme.primary  )
                                       }
                                   }
                               }
                               Icon(modifier = Modifier.weight(1f), imageVector = Icons.Filled.ArrowDropDown, contentDescription = null, tint =BlueSystem)

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
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                ),
                                elevation = ButtonDefaults.elevatedButtonElevation(
                                    defaultElevation = 5.dp
                                )
                            ){
                                Text(text = "Cancel", color = BlueSystem)
                            }

                            ElevatedButton(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                onClick = {
                                    //warehouse.name =name
                                    addRoute()
                                },
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = BlueSystem
                                ),
                                elevation = ButtonDefaults.elevatedButtonElevation(
                                    defaultElevation = 5.dp
                                )
                            ){
                                Text(text = "Add", color = Color.White)
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

}


@Composable
fun ShowListDialog(listaItems: List<Item>, onDismiss: () -> Unit, onSuccessfully: () ->Unit) {

    var totalWeight by rememberSaveable { mutableStateOf(0.0) }

    val roundedTotalWeight = remember(totalWeight) {
        String.format("%.2f", Math.round(totalWeight * 100.0) / 100.0).replace(",", ".").toDouble()
    }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        ),
        content = {
            // Contenido del diálogo con la lista de elementos
            // Puedes personalizar y agregar tu propia lógica aquí
            Column(
                modifier = Modifier
                    .background( MaterialTheme.colorScheme.background )
                    .fillMaxWidth()
                    .padding(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(text = "EuroPalet", fontWeight = FontWeight.Bold,  color = MaterialTheme.colorScheme.primary )
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
                    ) {
                        Icon(imageVector = Icons.Filled.Clear, contentDescription = null, tint = Color.Gray)
                        Text("Cancel", color = MaterialTheme.colorScheme.primary )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ){
                    Text(text = "Total weight: ${roundedTotalWeight}/1500 Kg", color = MaterialTheme.colorScheme.primary )
                    ElevatedButton(
                        onClick = {
                            onSuccessfully()
                        },
                        colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                            containerColor = BlueSystem
                        ),
                        elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                            defaultElevation = 5.dp
                        )
                    ) {
                        androidx.compose.material3.Icon(imageVector = Icons.Filled.Inventory,contentDescription = null, tint = Color.White)
                    }
                }

                LazyColumn{
                    items(listaItems) { product ->
                        itemTemplate(product,
                            onCountStateChanged = { newCount ->
                                if(newCount!=0.00){
                                    totalWeight = 0.00
                                    Log.d("DICCIONARIO", myMap.toString())
                                    myMap.forEach{(key, value) ->
                                        totalWeight += value.split(":")[2].replace(";", "").toDouble()
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    )


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun itemTemplate(item: Item, onCountStateChanged: (Double) -> Unit){
    var count by rememberSaveable { mutableStateOf(0) }
    val countState = rememberUpdatedState(count)
    val customTextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        textColor = MaterialTheme.colorScheme.primary,
        backgroundColor = MaterialTheme.colorScheme.outline,
        cursorColor =  BlueSystem,
        focusedBorderColor =  BlueSystem,
        focusedLabelColor = BlueSystem,
        unfocusedBorderColor =  BlueSystem
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(color = MaterialTheme.colorScheme.background )
    ) {
        // Imagen del producto a la izquierda (puedes personalizar esto según tus necesidades)
        Image(
            painter = painterResource(id = R.drawable.item), // Reemplaza con tu recurso de imagen
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Contenido a la derecha (nombre, peso por unidad y botones)
        Column(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        ) {
            Text(text = "Name: "+item.name, color = MaterialTheme.colorScheme.primary )
            Text(text = "Weight Per Unit: ${item.weightPerUnit} Kg", color = MaterialTheme.colorScheme.primary )
            Text(text = "Available: ${item.stock-countState.value} units", color = MaterialTheme.colorScheme.primary )
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

                    myMap[item.id] = "${item.id}:${count}:${count * item.weightPerUnit};"

                    onCountStateChanged(count * item.weightPerUnit)

                }
            },
            colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                containerColor = BlueSystem
            ),
            elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                defaultElevation = 5.dp
            )
        ) {
            androidx.compose.material.Text("-", color = Color.White)
        }
        TextField(
            colors = customTextFieldColors,
            value = countState.value.toString(),
            onValueChange = { newValue ->
                val value = newValue.toIntOrNull() ?: 0
                if(value>=0 && value<=item.stock){
                    count = value

                    myMap[item.id] = "${item.id}:${count}:${count * item.weightPerUnit};"

                    onCountStateChanged(count * item.weightPerUnit)
                }
            },
            modifier = Modifier
                .padding(8.dp)
                .width(60.dp)
                .height(45.dp)
                .border(
                    width = 0.5.dp,
                    color =  BlueSystem,
                    shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                )

        )
        ElevatedButton(
            onClick = {
                if (countState.value<item.stock){
                    count++

                    myMap[item.id] = "${item.id}:${count}:${count * item.weightPerUnit};"

                    onCountStateChanged(count * item.weightPerUnit)

                }
            },
            colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                containerColor = BlueSystem
            ),
            elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                defaultElevation = 5.dp
            )
        ) {
            androidx.compose.material.Text("+", color = Color.White)
        }


    }
}

@Composable
fun PaletTemplate(map: Map<Int, String>, onDelete: (Double) -> Unit) {
    var weight by remember(map) { mutableStateOf(0.0) }

    // Calcular el peso cada vez que cambia el mapa
    LaunchedEffect(map) {
        weight = map.values.sumByDouble {
            "%.2f".format(it.split(":")[2].replace(";", "").toDouble()).replace(",", ".").toDouble()
        }
    }

    Spacer(modifier = Modifier.padding(top = 15.dp))
    ElevatedButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 25.dp, end = 25.dp),
        onClick = { /* Acción al hacer clic en el botón */ },
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 5.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = R.drawable.pallet),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))

            // Texto a la derecha
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Nombre: ",
                    color = BlueSystem,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Peso: ${ String.format("%.2f", Math.round(weight* 100.0) / 100.0).toDouble()} Kg",
                    color = BlueSystem,
                )
            }
            ElevatedButton(
                onClick = {
                    onDelete(weight)
                },
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = BlueSystem
                ),
            ) {
                Icon(imageVector = Icons.Filled.DeleteSweep, contentDescription = null, tint = Color.White, )
            }
        }
    }
}

