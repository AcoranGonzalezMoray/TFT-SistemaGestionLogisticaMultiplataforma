package com.example.qrstockmateapp.screens.ScanQR.AddItem

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.qrstockmateapp.R
import com.example.qrstockmateapp.api.models.Item
import com.example.qrstockmateapp.api.models.Transaction
import com.example.qrstockmateapp.api.models.User
import com.example.qrstockmateapp.api.models.Warehouse
import com.example.qrstockmateapp.api.services.RetrofitInstance
import com.example.qrstockmateapp.navigation.repository.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddItemScreen(navController: NavController) {
    var item = DataRepository.getItem()
    var availableCount by remember { mutableStateOf(item?.stock) }
    val availableState = rememberUpdatedState(availableCount)
    var count by remember { mutableStateOf(0) }
    var countState = rememberUpdatedState(count)
    val context = LocalContext.current
    val customTextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        cursorColor = Color.Black,
        focusedBorderColor = Color.Black,
        focusedLabelColor = Color.Black,
        unfocusedBorderColor = Color.Black,
        backgroundColor = Color.LightGray
    )

    var selectedOption by remember { mutableStateOf("Select a warehouse to add your product") }
    var isMenuExpanded by remember { mutableStateOf(false) }

    var warehouses by remember { mutableStateOf(emptyList<Warehouse>()) }

    var location by remember { mutableStateOf(item?.location.toString()) }
    var name by remember { mutableStateOf(item?.name.toString()) }

    LaunchedEffect(Unit) {
        val companyResponse = RetrofitInstance.api.getCompanyByUser(DataRepository.getUser()!!)
        if (companyResponse.isSuccessful) {
            val company = companyResponse.body()
            if (company != null) {
                DataRepository.setCompany(company)
                val warehouseResponse = RetrofitInstance.api.getWarehouse(company)

                if (warehouseResponse.isSuccessful) {
                    val warehousesIO = warehouseResponse.body()
                    if (warehousesIO != null) {
                        DataRepository.setWarehouses(warehousesIO)
                        warehouses = warehousesIO
                    }
                }
            }
        }
    }

    val addItem : (item:Item) -> Unit = {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                if (item != null) {
                    val itemResponse = RetrofitInstance.api.addItem(item.warehouseId, item);
                    if(itemResponse.isSuccessful){
                        val user = DataRepository.getUser()
                        if(user!=null){
                            val zonedDateTime = ZonedDateTime.now()
                            val formattedDate = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                            val addTransaccion = RetrofitInstance.api.addHistory(
                                Transaction(0,user.id.toString(),user.code, "An item,${item.name} , has been added to the warehouse with id ${item.warehouseId}",
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
                        withContext(Dispatchers.Main) {
                            navController.navigate("home")
                        }
                    }else{
                        try {
                            val errorBody = itemResponse.errorBody()?.string()
                            Log.d("ErrorBody", "$errorBody")
                        } catch (e: Exception) {
                            Log.e("excepcionUserB", "Error al obtener el cuerpo del error: $e")
                        }
                    }
                }
            }catch (e: Exception) {
                Log.d("excepcionItem","${e}")
            }
        }

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center)
        ) {
            var name by remember { mutableStateOf(item?.name) }
            Text(text =  "Name: "+name.toString(),
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            if(item!=null && !item.url.isNullOrBlank()){
                val painter = rememberImagePainter(
                    data = item.url,
                    builder = {
                        crossfade(true)
                        placeholder(R.drawable.loading)
                    }
                )
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.5f)
                )
            }else{
                Image(
                    painter = painterResource(id = R.drawable.item), // Reemplaza con tu lógica para cargar la imagen
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.5f) // La imagen ocupa la mitad de la pantalla
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {

            TextField(
                value = name,
                label = { Text("Name: ") },
                onValueChange = { name = it },
                colors= customTextFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            )

            TextField(
                value = location,
                label = { Text("Location: ") },
                onValueChange = { location = it },
                colors= customTextFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            )
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = selectedOption,
                    modifier = Modifier
                        .background(Color.LightGray)
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
                    warehouses.forEach { warehouse ->
                        DropdownMenuItem(onClick = {
                            selectedOption = "Name : ${warehouse.name} with Id :${warehouse.id}"
                            isMenuExpanded = false
                        }) {
                            Text("Name : ${warehouse.name} with Id : ${warehouse.id}")
                        }
                    }
                }
            }
        }


        Row(    //Aqui va el COLUMN y el Botón de REMOVE
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column( //Aquí va el ROW y el texto de stock disponible
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center)
            ) {
                Text(text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Available: ")
                    }
                    append(availableState.value.toString())
                },
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Button(
                            onClick = {
                                count++
                            },
                            colors = ButtonDefaults.buttonColors(Color.Cyan)
                        ) {
                            androidx.compose.material.Text("+")
                        }
                        Button(
                            onClick = {
                                count--
                            },
                            colors = ButtonDefaults.buttonColors(Color.Cyan)
                        ) {
                            androidx.compose.material.Text("-")
                        }
                    }
                    TextField(
                        value = countState.value.toString(),
                        onValueChange = { newValue ->
                            count = newValue.toIntOrNull() ?: 0
                        },
                        modifier = Modifier
                            .padding(16.dp)
                            .width(60.dp)
                            .height(55.dp)

                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Button(onClick = {
                        navController.popBackStack()
                    }, colors = ButtonDefaults.buttonColors(Color.Red)) {
                        Text(text = "Cancel", color=Color.White)
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    Button(
                        onClick = {

                            var newStock = item?.stock?.plus(countState.value)
                            Log.d("New Stock", "${newStock}")
                            var newItem = item
                            if (newItem != null) {
                                if(selectedOption != "Select a warehouse to add your product"){
                                    if (newStock != null && newStock>=0) {
                                        newItem.stock = newStock
                                        newItem.warehouseId = selectedOption.split(':')[2].toInt()
                                        newItem.location =  location
                                        newItem.name = name
                                        addItem(newItem)
                                        availableCount = newStock
                                        count = 0
                                    }else{
                                        Toast.makeText(context, "There cannot be a negative stock", Toast.LENGTH_SHORT).show()
                                    }
                                }else{
                                    Toast.makeText(context, "You must assign this item to a warehouse", Toast.LENGTH_SHORT).show()
                                }

                            }
                        },colors = ButtonDefaults.buttonColors(Color.Black)
                    ) {
                        androidx.compose.material.Text("Add", color = Color.White)
                    }
                }
            }

        }

    }

}