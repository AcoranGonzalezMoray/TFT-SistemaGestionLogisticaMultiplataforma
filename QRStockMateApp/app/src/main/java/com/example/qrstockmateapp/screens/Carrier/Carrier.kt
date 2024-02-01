package com.example.qrstockmateapp.screens.Carrier


import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Badge
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import com.example.qrstockmateapp.api.models.TransportRoute
import com.example.qrstockmateapp.api.models.statusRoleToString
import com.example.qrstockmateapp.api.services.RetrofitInstance
import com.example.qrstockmateapp.navigation.repository.DataRepository
import com.example.qrstockmateapp.screens.Carrier.RouteManagement.DatePickerSample
import com.example.qrstockmateapp.screens.Search.SortOrder
import com.example.qrstockmateapp.screens.Search.StateFilter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class,
    DelicateCoroutinesApi::class
)
@Composable
fun CarrierScreen(navController: NavController) {
    // Supongamos que tienes una lista de objetos TransportRouteModel
    var transportRoutes by remember { mutableStateOf(emptyList<TransportRoute>()) }
    var isloading by remember { mutableStateOf(false) }

    var isDatePickerVisible by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<Triple<Int, Int, Int>?>(Triple(0,0,0)) }
    var sortOrder by remember { mutableStateOf(SortOrder.ASCENDING) } // Puedes definir un enum SortOrder con ASCENDING y DESCENDING
    var stateFilter by remember { mutableStateOf(StateFilter.NULL) }

    var filteredItems by remember { mutableStateOf<List<TransportRoute>>(emptyList()) }

    fun filterOption(status: Int):List<TransportRoute>{

        if(status == 5){
            val tmp = if (selectedDate == Triple(0, 0, 0)) {
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
            val tmp = if (selectedDate == Triple(0, 0, 0)) {
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
                    transportRoutes = transporRoutesResponse.filter { transportRoute: TransportRoute -> transportRoute.carrierId == DataRepository.getUser()!!.id }
                    DataRepository.setVehicles(vehiclesResponse)
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
            .background(MaterialTheme.colorScheme.background)
    ){
        PullRefreshIndicator(
            refreshing = isloading,
            state = pullRefreshState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(1f),
            backgroundColor =  MaterialTheme.colorScheme.background,
            contentColor = Color(0xff5a79ba)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
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
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(filteredItems.chunked(2)) { routesPorFila ->
                    itemRoute(routesPorFila, navController)
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
@Composable
fun itemRoute(routesPorFila: List<TransportRoute>, navController: NavController) {
    val context = LocalContext.current
    var isloading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit){
        delay(1200)
        isloading = false
    }

    // Para cada par de routes en la lista, crea una fila
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        routesPorFila.forEachIndexed { index, route ->
            // Para cada route en la fila, crea una Box estilizada
            Card(
                modifier = Modifier
                    .weight(1f) // Cada Box ocupa la mitad del ancho de la fila
                    .padding(8.dp)
                ,
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
                        modifier = Modifier
                            .fillMaxSize()
                            .height(280.dp)
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
                    // Contenido de la Box para cada route
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                        ,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(
                            modifier = Modifier
                                .padding(10.dp)
                                .size(120.dp),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 1.dp
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White,
                            )
                        ){
                            // Icono arriba
                            Image(
                                painter = painterResource(id = R.drawable.maps), // Reemplaza con tu imagen desde drawable
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .fillMaxHeight()
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        // Textos
                        Text(
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("Start Location:")
                                }
                                append(" ${DataRepository.getWarehouses()?.find { warehouse -> warehouse.id == route.startLocation.toInt() }?.name}")
                            },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("End Location:")
                                }
                                append(" ${DataRepository.getWarehouses()?.find { warehouse -> warehouse.id == route.endLocation.toInt() }?.name}")
                            },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("Date:")
                                }
                                append(" ${route.date.split('T')[0]}")
                            },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )


                        Spacer(modifier = Modifier.height(8.dp))
                        ElevatedButton(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(5.dp)
                                .height(40.dp),
                            onClick = {
                                if(route.status!=2){
                                    DataRepository.setRoutePlus(route)
                                    navController.navigate("route")
                                }else{
                                    Toast.makeText(context, "Route Finished!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors =  androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                                containerColor = Color(0xff5a79ba)
                            ),
                            elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                                defaultElevation = 5.dp
                            )


                        ) {
                            Text("Open", color = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(imageVector = Icons.Filled.Map, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            }

            // Añadir una Box vacía si es el último elemento único en la fila
            if (index == routesPorFila.size - 1 && routesPorFila.size % 2 == 1) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                ) {}
            }
        }
    }
}