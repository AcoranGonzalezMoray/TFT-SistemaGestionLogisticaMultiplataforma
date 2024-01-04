package com.example.qrstockmateapp.screens.Carrier.RouteManagement

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.qrstockmateapp.R
import com.example.qrstockmateapp.api.models.TransportRoute
import com.example.qrstockmateapp.api.services.RetrofitInstance
import com.example.qrstockmateapp.navigation.repository.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.poi.hpsf.Date
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteManagementScreen(navController: NavController) {
    var transportRoutes by remember { mutableStateOf(emptyList<TransportRoute>()) }

    var isDatePickerVisible by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<Triple<Int, Int, Int>?>(Triple(0,0,0)) }

    val filteredItems = if (selectedDate == Triple(0, 0, 0)) {
        transportRoutes
    } else {
        transportRoutes.filter { item ->
            val (year, month, day) = selectedDate!!
            val (itemYear, itemMonth,itemDay ) = item.date.split("T")[0].split("-").map { it.toInt() }

            day == itemDay && month == itemMonth && year == itemYear
        }
    }

    LaunchedEffect(Unit){
        GlobalScope.launch(Dispatchers.IO) {

            val response= RetrofitInstance.api.getTransportRoutes(DataRepository.getUser()!!.code)
            if (response.isSuccessful) {
                val transporRoutesResponse = response.body()
                if(transporRoutesResponse!=null){
                   transportRoutes = transporRoutesResponse
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
        }
    }
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
                }
            )
        }
        LazyColumn {
            items(filteredItems) { route ->
                TransportRouteItem(route = route, navController = navController)
            }
        }
    }
}

@Composable
fun TransportRouteItem(route: TransportRoute, navController: NavController) {
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                Text(text = "Inicio: ${route.startLocation}", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Fin: ${route.endLocation}", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Carrier: ${DataRepository.getEmployees()?.filter { employee -> employee.id == 33 }
                    ?.get(0)?.name}", fontSize = 16.sp)
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
