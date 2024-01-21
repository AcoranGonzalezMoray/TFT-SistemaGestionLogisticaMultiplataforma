package com.example.qrstockmateapp.screens.Carrier.RouteManagement

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Badge
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.qrstockmateapp.R
import com.example.qrstockmateapp.api.models.Transaction
import com.example.qrstockmateapp.api.models.TransportRoute
import com.example.qrstockmateapp.api.models.statusRoleToString
import com.example.qrstockmateapp.api.services.RetrofitInstance
import com.example.qrstockmateapp.navigation.repository.DataRepository
import com.example.qrstockmateapp.screens.Search.SortOrder
import com.example.qrstockmateapp.screens.Search.StateFilter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.hpsf.Date
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun RouteManagementScreen(navController: NavController) {
    var transportRoutes by remember { mutableStateOf(emptyList<TransportRoute>()) }
    var isloading by remember { mutableStateOf(false) }
    var sortOrder by remember { mutableStateOf(SortOrder.ASCENDING) } // Puedes definir un enum SortOrder con ASCENDING y DESCENDING
    var stateFilter by remember { mutableStateOf(StateFilter.NULL) }



    var isDatePickerVisible by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<Triple<Int, Int, Int>?>(Triple(0,0,0)) }
    var filteredItems by remember { mutableStateOf<List<TransportRoute>>(emptyList()) }




    fun filterOption(status: Int):List<TransportRoute>{

        if(status == 5){
            var tmp = if (selectedDate == Triple(0, 0, 0)) {
                transportRoutes
            } else {
                transportRoutes.filter { item ->
                    val (year, month, day) = selectedDate!!
                    val (itemYear, itemMonth, itemDay) = item.date.split("T")[0].split("-").map { it.toInt() }
                    day == itemDay && month == itemMonth && year == itemYear
                }
            }
            return tmp
        }else {
            var tmp = if (selectedDate == Triple(0, 0, 0)) {
                transportRoutes.filter { it.status == status }
            } else {
                transportRoutes.filter { item ->
                    val (year, month, day) = selectedDate!!
                    val (itemYear, itemMonth, itemDay) = item.date.split("T")[0].split("-").map { it.toInt() }
                    day == itemDay && month == itemMonth && year == itemYear
                }.filter { it.status == status }
            }
            return tmp
        }
    }

    filteredItems = filterOption(5)

    val loadRoutes : ()->Unit = {
        GlobalScope.launch(Dispatchers.IO) {
            isloading = true
            val response= RetrofitInstance.api.getTransportRoutes(DataRepository.getUser()!!.code)
            val responseVehicle = RetrofitInstance.api.getVehicles(DataRepository.getUser()!!.code)

            if (response.isSuccessful && responseVehicle.isSuccessful) {
                val transporRoutesResponse = response.body()
                val vehiclesResponse = responseVehicle.body()
                if(transporRoutesResponse!=null && vehiclesResponse !=null ){
                    transportRoutes = transporRoutesResponse
                    DataRepository.setVehicles(vehiclesResponse)
                    Log.d("route", "$transportRoutes")
                }
            } else{
                try {
                    val errorBody = response.errorBody()?.string()
                    Log.d("excepcionROUTE", errorBody ?: "Error body is null")
                } catch (e: Exception) {
                    Log.e("excepcionROUTEB", "Error al obtener el cuerpo del error: $e")
                }
            }
            delay(1100)
            isloading = false
        }
    }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isloading,
        onRefresh = loadRoutes
    )

    LaunchedEffect(Unit){
       loadRoutes()
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
            contentColor = Color(0xff5a79ba)
        )
        Column {
            // Hacer algo con la fecha seleccionada en el componente padre
            selectedDate?.let { (day, month, year) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 15.dp
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor =Color(0xff5a79ba),
                    ),
                    onClick = {isDatePickerVisible = true}
                ) {
                    if(day==0&&month==0&&year==0){
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically, // Alineación vertical
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Icon(imageVector = Icons.Filled.ArrowBackIosNew, contentDescription ="", tint=Color.White)
                            Text(
                                "ALL",
                                modifier = Modifier
                                    .wrapContentSize(Alignment.Center),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp // Ajusta el tamaño de la fuente según tus necesidades
                            )
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription ="",tint=Color.White)
                        }
                    }else{
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically, // Alineación vertical
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Icon(imageVector = Icons.Filled.ArrowBackIosNew, contentDescription ="" ,tint=Color.White)
                            Text(
                                "$day-$month-$year",
                                modifier = Modifier
                                    .wrapContentSize(Alignment.Center),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp // Ajusta el tamaño de la fuente según tus necesidades
                            )
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription ="" ,tint=Color.White)
                        }
                    }
                }

            }
            if (isDatePickerVisible) {
                DatePickerSample(
                    onDateSelected = { date ->
                        selectedDate = date
                    },
                    onClose = {
                        isDatePickerVisible = false
                    },
                    onReset = {
                        selectedDate = Triple(0,0,0)
                        sortOrder = SortOrder.ASCENDING
                        stateFilter = StateFilter.NULL
                    }
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp)
            ){
                ElevatedButton(
                    modifier = Modifier.weight(0.5f),
                    onClick = {
                        sortOrder = if (sortOrder == SortOrder.ASCENDING) SortOrder.DESCENDING else SortOrder.ASCENDING
                        filteredItems = when (sortOrder) {
                            SortOrder.ASCENDING -> {
                                filteredItems.sortedBy { item ->
                                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    dateFormat.parse(item.date)
                                }
                            }
                            SortOrder.DESCENDING -> {
                                filteredItems.sortedByDescending { item ->
                                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    dateFormat.parse(item.date)
                                }
                            }
                        }
                    },
                    colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                        containerColor = Color(0xff5a79ba)
                    ),
                    elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = 5.dp
                    )
                ){
                    androidx.compose.material.Text(
                        color = Color.White,
                        text = if (sortOrder == SortOrder.ASCENDING) "Sort Ascending" else "Sort Descending"
                    )
                    androidx.compose.material.Icon(
                        imageVector = Icons.Filled.SwapVert,
                        contentDescription = "sort",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.padding(16.dp))
                ElevatedButton(
                    modifier = Modifier.weight(0.5f),
                    onClick = {
                        stateFilter = when (stateFilter) {
                            StateFilter.NULL -> StateFilter.PENDING
                            StateFilter.PENDING -> StateFilter.ON_ROUTE
                            StateFilter.ON_ROUTE -> StateFilter.FINALIZED
                            StateFilter.FINALIZED -> StateFilter.NULL
                        }
                        Log.d("FILTER", filteredItems.toString())
                        filteredItems = when (stateFilter) {
                            StateFilter.PENDING -> filterOption(0)
                            StateFilter.ON_ROUTE -> filterOption(1)
                            StateFilter.FINALIZED -> filterOption(2)
                            StateFilter.NULL -> filterOption(5)
                        }
                    },
                    colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                        containerColor = Color(0xff5a79ba)
                    ),
                    elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = 5.dp
                    )
                ){
                    androidx.compose.material.Text(
                        color = Color.White,
                        text = when (stateFilter) {
                            StateFilter.PENDING -> "State: Pending"
                            StateFilter.ON_ROUTE -> "State: On Route"
                            StateFilter.FINALIZED -> "State: Finalized"
                            else -> "All"
                        }
                    )
                    androidx.compose.material.Icon(
                        imageVector = Icons.Filled.FilterList,
                        contentDescription = "sort",
                        tint = Color.White
                    )
                }
            }
            LazyColumn {
                items(filteredItems) { route ->
                    TransportRouteItem(route = route, navController = navController)
                }
                item{
                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp))
                }

            }
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun TransportRouteItem(route: TransportRoute, navController: NavController) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    val deleteRoute : ()->Unit = {
        GlobalScope.launch(Dispatchers.IO) {
            val user = DataRepository.getUser()!!

            val response= RetrofitInstance.api.deleteTransportRoutes(route)

            if (response.isSuccessful) {
                val zonedDateTime = ZonedDateTime.now()
                val formattedDate = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                val addTransaccion = RetrofitInstance.api.addHistory(
                    Transaction(0,user.id.toString(),user.code, "The ${route?.id} route has been deleted",
                        formattedDate , 3)
                )
                if(addTransaccion.isSuccessful){
                    Log.d("Transaccion", "OK")
                    withContext(Dispatchers.Main){
                        Toast.makeText(context, "Route has been deleted", Toast.LENGTH_SHORT).show()
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
            .padding(8.dp)
            .clickable {

            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        shape = RoundedCornerShape(16.dp),
    ) {
        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    // Handle dismissal if needed
                    showDialog = false
                },
                title = {
                    androidx.compose.material.Text(text = "Alert")
                },
                text = {
                    androidx.compose.material.Text(text ="Are you sure you want to delete?")
                },
                confirmButton = {
                    ElevatedButton(
                        onClick = {
                            deleteRoute()
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
                            containerColor = Color.White
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
        Box(
            modifier = Modifier
                .fillMaxWidth(),
        ){
            if (route.status == 0) {
                Badge(
                    modifier = Modifier
                        .height(20.dp)
                        .width(80.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = (-4).dp, y = (5).dp),
                    contentColor = Color.White,
                    backgroundColor = Color.DarkGray
                ) {
                    Text(statusRoleToString(route.status), style = MaterialTheme.typography.labelSmall, color = Color.White)
                }
            }
            if(route.status == 1){
                Badge(
                    modifier = Modifier
                        .height(20.dp)
                        .width(80.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = (-4).dp, y = (5).dp),
                    contentColor = Color.White,
                    backgroundColor = Color(0xFF006400)
                ) {
                    Text(statusRoleToString(route.status), style = MaterialTheme.typography.labelSmall, color = Color.White)
                }

            }
            if (route.status == 2) {
                Badge(
                    modifier = Modifier
                        .height(20.dp)
                        .width(80.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = (-4).dp, y = (5).dp),
                    contentColor = Color.White,
                    backgroundColor = Color.Red
                ) {
                    Text(statusRoleToString(route.status), style = MaterialTheme.typography.labelSmall, color = Color.White)
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, top = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono a la izquierda
            Image(
                painter = painterResource(id = R.drawable.maps), // Reemplaza con tu imagen desde drawable
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp) // Ajusta el tamaño según tus necesidades
                    .clip(shape = RoundedCornerShape(8.dp))
                    .padding(4.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Columna de texto a la derecha
            Column {
                Text(
                    text = "Date: ${route.date.split('T')[0]}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp, // Ajusta el tamaño de la fuente según tus necesidades
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Start Location: ${DataRepository.getWarehouses()!!.find { warehouse -> warehouse.id == route.startLocation.toInt()}?.name}", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "End Location: ${DataRepository.getWarehouses()!!.find { warehouse -> warehouse.id == route.endLocation.toInt()}?.name}", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Carrier: ${DataRepository.getEmployees()?.filter { employee -> employee.id == 33 }
                    ?.get(0)?.name}", fontSize = 16.sp)

                Row(
                ){
                    ElevatedButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(5.dp)
                            .height(40.dp),
                        onClick = {
                            if(route.status==0){
                                DataRepository.setRoutePlus(route)
                                navController.navigate("updateRoute")
                            }else{
                                Toast.makeText(context, "Route in progress or completed", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors =  ButtonDefaults.elevatedButtonColors(
                            containerColor = Color(0xff5a79ba)
                        ),
                        elevation = ButtonDefaults.elevatedButtonElevation(
                            defaultElevation = 5.dp
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.EditNote,
                            contentDescription = "",
                            tint = Color.White
                        )
                    }
                    ElevatedButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(5.dp)
                            .height(40.dp),
                        onClick = {
                              if(route.status!=1){
                                  showDialog = true
                              }else{
                                  Toast.makeText(context, "Route in progress", Toast.LENGTH_SHORT).show()
                              }
                        },
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = Color(0xff5a79ba)
                        ),
                        elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                            defaultElevation = 5.dp
                        )
                    ){
                        androidx.compose.material.Icon(
                            imageVector = Icons.Filled.DeleteSweep,
                            contentDescription = "",
                            tint = Color.White
                        )
                    }
                }
                ElevatedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .height(40.dp),
                    onClick = {
                        Log.d("status", route.status.toString())
                        if(route.status == 1){
                            DataRepository.setRoutePlus(route)
                            navController.navigate("routeMinus")
                        }else{
                            Toast.makeText(context, "The route has not started", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors =  ButtonDefaults.elevatedButtonColors(
                        containerColor = Color(0xff5a79ba)
                    ),
                    elevation = ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = 5.dp
                    )


                ) {
                    Text("Open", color = Color.White)
                }

            }
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerSample(onDateSelected: (Triple<Int, Int, Int>) -> Unit, onClose: () -> Unit, onReset: () -> Unit) {
    val todayMillis = Calendar.getInstance().timeInMillis

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = todayMillis)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 9.dp, start = 9.dp, bottom = 18.dp, top = 8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(0.05f.dp)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onReset) {
                Icon(imageVector = Icons.Default.DateRange, contentDescription = "Close")
            }
            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(0.8f.dp)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            DatePicker(datePickerState = datePickerState, modifier = Modifier.weight(1f),
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = Color(0xff5a79ba),
                    disabledSelectedDayContainerColor = Color(0xff5a79ba)
                )
            )
        }

        // Obtener los componentes de la fecha después de la selección
        val selectedDateMillis = datePickerState.selectedDateMillis
        val selectedDate = selectedDateMillis?.let {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it
            //Triple(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR))
            Triple(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH) + 1,  calendar.get(Calendar.DAY_OF_MONTH))
        }

        // Llamar a la función de devolución de llamada con la fecha seleccionada
        selectedDate?.let {
            onDateSelected(it)
        }
    }
}
