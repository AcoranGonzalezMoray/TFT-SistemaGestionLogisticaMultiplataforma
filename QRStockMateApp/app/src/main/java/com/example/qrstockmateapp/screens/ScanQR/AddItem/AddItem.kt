package com.example.qrstockmateapp.screens.ScanQR.AddItem

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.qrstockmateapp.R
import com.example.qrstockmateapp.api.models.Item
import com.example.qrstockmateapp.api.models.Transaction
import com.example.qrstockmateapp.api.models.Warehouse
import com.example.qrstockmateapp.api.services.RetrofitInstance
import com.example.qrstockmateapp.navigation.repository.DataRepository
import com.example.qrstockmateapp.ui.theme.BlueSystem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

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
        textColor = MaterialTheme.colorScheme.primary,
        backgroundColor = MaterialTheme.colorScheme.outline,
        cursorColor =  BlueSystem,
        focusedBorderColor =  BlueSystem,
        focusedLabelColor = BlueSystem,
        unfocusedBorderColor =  BlueSystem
    )


    var selectedOption by remember { mutableStateOf("Select a warehouse to add your product") }
    var isMenuExpanded by remember { mutableStateOf(false) }

    var warehouses by remember { mutableStateOf(emptyList<Warehouse>()) }

    var warehouseLocal by remember { mutableStateOf<Warehouse?>(null) }



    var numberofplants by remember { mutableStateOf(0) }
    var hallwaysPerPlant = remember { mutableStateListOf<Int>() }
    var racksPerHallway = remember { mutableStateMapOf<Int, MutableList<String>>() }


    var plant by remember { mutableStateOf(0) }
    var hallway by remember { mutableStateOf(0) }
    var racks by remember { mutableStateOf("") }

    var location by remember { mutableStateOf(item?.location.toString()) }
    var name by remember { mutableStateOf(item?.name.toString()) }
    var weight by remember { mutableStateOf(item?.weightPerUnit.toString()) }


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
    fun updateOrganizationData(organizationString: String) {
        val lines = organizationString.lines() // Dividimos la cadena en líneas

        // Extraemos el número de plantas de la primera línea
        val numberOfPlants = lines.first().split(":").last().trim().toInt()
        numberofplants = numberOfPlants

        // Limpiamos las listas de pasillos y estanterías antes de actualizarlas
        hallwaysPerPlant.clear()
        racksPerHallway.clear()

        // Iteramos sobre las líneas restantes para actualizar los pasillos y las estanterías
        var currentPlantIndex = -1
        var currentHallwayIndex = -1
        lines.drop(2).forEach { line ->
            when {
                line.startsWith("Plant") -> {
                    currentPlantIndex++
                    hallwaysPerPlant.add(0)
                    currentHallwayIndex = -1
                }
                line.startsWith("\tHallway") -> {
                    currentHallwayIndex++
                    hallwaysPerPlant[currentPlantIndex]++
                }
                line.startsWith("\t\tRacks Size") -> {
                    val rackString = line.split(":").last().trim()
                    val currentRacksList = racksPerHallway.getOrDefault(currentPlantIndex, mutableListOf()) // Obtener la lista de estantes actual o crear una nueva si no existe
                    currentRacksList.add(rackString) // Agregar el rack obtenido de la línea actual
                    racksPerHallway[currentPlantIndex] = currentRacksList // Asignar la lista de estantes al mapa
                }

            }
        }
    }


    val addItem : (item:Item) -> Unit = {
        item?.location = "Plant: ${plant}, HallWay: ${hallway}, Rack: ${racks}"
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
                            DataRepository.setCurrentScreenIndex(0)
                            navController.navigate("home")
                        }
                    }else{
                        try {
                            withContext(Dispatchers.Main) {
                                val errorBody = itemResponse.errorBody()?.string()
                                Toast.makeText(context, errorBody, Toast.LENGTH_SHORT).show()
                            }
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
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center)
        ) {
            Text(text = "Name: $name",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary,
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
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.background),
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.5f)
                )
            }else{
                Image(
                    painter = painterResource(id = R.drawable.item), // Reemplaza con tu lógica para cargar la imagen
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
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
                label = { Text("Name: ", color = MaterialTheme.colorScheme.outlineVariant) },
                onValueChange = { name = it },
                colors= customTextFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .border(
                        width = 0.5.dp,
                        color = BlueSystem,
                        shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                    )
            )

            if(warehouseLocal != null){
                TextField(
                    value = plant.toString(),
                    label = { Text("Plant: (0 - ${numberofplants-1})", color = MaterialTheme.colorScheme.outlineVariant) },
                    onValueChange = {
                        val newValue = it.toIntOrNull() ?: 0
                        if (newValue in 0..numberofplants-1) { // Establecer el rango de 0 a 5
                            plant = newValue
                        }
                    },
                    colors= customTextFieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .border(
                            width = 0.5.dp,
                            color = BlueSystem,
                            shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                        )
                )
                TextField(
                    value = hallway.toString(),
                    label = { Text("Hallway: (0 - ${hallwaysPerPlant.get(plant)-1})", color = MaterialTheme.colorScheme.outlineVariant) },
                    onValueChange = {
                        val newValue = it.toIntOrNull()?:0
                        if (newValue in 0..hallwaysPerPlant.get(plant)-1) { // Establecer el rango de 0 a 5
                            hallway =newValue
                        }
                    },
                    colors= customTextFieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .border(
                            width = 0.5.dp,
                            color = BlueSystem,
                            shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                        )
                )
                TextField(
                    value = racks,
                    label = { Text("Rack: (${racksPerHallway[plant]?.get(hallway)})", color = MaterialTheme.colorScheme.outlineVariant) },
                    onValueChange = {
                        if (it.length <= 2) { // Verificar la longitud máxima
                            racks = it.uppercase()
                        }
                    },
                    colors= customTextFieldColors,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .border(
                            width = 0.5.dp,
                            color = BlueSystem,
                            shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                        ) ,
                )
            }

            TextField(
                value = weight,
                label = { Text("Weight Per Unit (Kg) : ", color = MaterialTheme.colorScheme.outlineVariant) },
                onValueChange = { weight = it},
                colors= customTextFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .border(
                        width = 0.5.dp,
                        color = BlueSystem,
                        shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                    )
            )
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)

                .background(Color(0xfff5f6f7))
                .border(
                    width = 0.5.dp,
                    color = BlueSystem,
                    shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                )
            ) {
                Row(
                    modifier = Modifier.background(MaterialTheme.colorScheme.outline),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedOption,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .weight(9f)
                            .background(MaterialTheme.colorScheme.outline)
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
                        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        warehouses.forEach { warehouse ->
                            DropdownMenuItem(onClick = {
                                selectedOption = "Name : ${warehouse.name} with Id :${warehouse.id}"
                                warehouseLocal = warehouse
                                updateOrganizationData(warehouse.organization)
                                isMenuExpanded = false
                            }, modifier = Modifier
                                .padding(5.dp)
                                .background(MaterialTheme.colorScheme.outline)
                                .border(
                                    width = 0.5.dp,
                                    color = BlueSystem,
                                    shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                                )) {
                                Text("Name : ${warehouse.name} Location: ${warehouse.location}", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                    Icon(modifier = Modifier.weight(1f), imageVector = Icons.Filled.ArrowDropDown, contentDescription = null, tint =BlueSystem)

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
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        ElevatedButton(
                            onClick = {
                                count++
                            },
                            colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                                containerColor = BlueSystem
                            ),
                            elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                                defaultElevation = 5.dp
                            )
                        ){
                            androidx.compose.material.Text("+", color= Color.White)
                        }
                        ElevatedButton(
                            onClick = {
                                count--
                            },
                            colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                                containerColor =  BlueSystem
                            ),
                            elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                                defaultElevation = 5.dp
                            )
                        ){
                            androidx.compose.material.Text("-", color= Color.White)
                        }
                    }
                    TextField(
                        value = countState.value.toString(),
                        colors = customTextFieldColors,
                        onValueChange = { newValue ->
                            count = newValue.toIntOrNull() ?: 0
                        },
                        modifier = Modifier
                            .padding(16.dp)
                            .width(60.dp)
                            .height(55.dp)
                            .border(
                                width = 0.5.dp,
                                color = BlueSystem,
                                shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                            )

                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    ElevatedButton(
                        onClick = {
                            navController.popBackStack()
                        },
                        colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                            defaultElevation = 5.dp
                        )
                    ){
                        Text(text = "Cancel", color=BlueSystem)
                    }
                    Spacer(modifier = Modifier.width(15.dp))
                    ElevatedButton(
                        onClick = {
                            var newStock = item?.stock?.plus(countState.value)
                            var newItem = item
                            if (newItem != null) {
                                if(selectedOption != "Select a warehouse to add your product"){
                                    if (newStock != null && newStock>=0) {
                                        newItem.stock = newStock
                                        newItem.warehouseId = selectedOption.split(':')[2].toInt()
                                        newItem.location =  location
                                        newItem.name = name
                                        newItem.weightPerUnit = weight.toDouble()
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
                        },
                        colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                            containerColor = BlueSystem
                        ),
                        elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                            defaultElevation = 5.dp
                        )
                    ){
                        androidx.compose.material.Text("Add", color = Color.White)
                    }
                }
            }

        }

    }

}