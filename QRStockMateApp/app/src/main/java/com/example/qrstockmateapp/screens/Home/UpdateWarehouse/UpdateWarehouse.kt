package com.example.qrstockmateapp.screens.Home.UpdateWarehouse


import android.annotation.SuppressLint
import android.location.Geocoder
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.qrstockmateapp.R
import com.example.qrstockmateapp.api.models.Transaction
import com.example.qrstockmateapp.api.models.User
import com.example.qrstockmateapp.api.services.RetrofitInstance
import com.example.qrstockmateapp.navigation.repository.DataRepository
import com.example.qrstockmateapp.screens.Carrier.Route.PointMarker
import com.example.qrstockmateapp.screens.Home.AddWarehouse.generateOrganizationString
import com.example.qrstockmateapp.screens.Home.AddWarehouse.modifyText
import com.example.qrstockmateapp.ui.theme.BlueSystem
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@SuppressLint("Recycle")
@OptIn(DelicateCoroutinesApi::class)
@Composable
fun UpdateWarehouseScreen(navController: NavController) {
    val warehouse = remember { DataRepository.getWarehousePlus() }
    var selectedOption by remember { mutableStateOf("Select an existing administrator to associate with the warehouse") }
    var isloading by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var pinLocation by remember { mutableStateOf<LatLng?>(null) }
    val focusManager = LocalFocusManager.current

    val context = LocalContext.current

    var otherInfo by remember { mutableStateOf("") }

    var numberofplants by remember { mutableStateOf(0) }
    var hallwaysPerPlant = remember { mutableStateListOf<Int>() }
    var racksPerHallway = remember { mutableStateMapOf<Int, MutableList<String>>() }


    val geocoder = Geocoder(context, Locale.getDefault())

    val updateImage:(File)->Unit={file ->
        GlobalScope.launch(Dispatchers.IO){
            try {
                isloading = true
                val warehouseId = warehouse?.id
                val warehouseIdRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), warehouseId.toString())

                // Crea RequestBody y MultipartBody.Part con el archivo de imagen seleccionado
                val imageRequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                val imagePart = MultipartBody.Part.createFormData("image", file.name, imageRequestBody)

                val imageResponse =  RetrofitInstance.api.updateImageWarehouse(warehouseIdRequestBody, imagePart)
                if(imageResponse.isSuccessful){
                    val user = DataRepository.getUser()
                    if(user!=null){
                        val zonedDateTime = ZonedDateTime.now()
                        val formattedDate = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                        val addTransaccion = RetrofitInstance.api.addHistory(
                            Transaction(0,user.id.toString(),user.code, "The image of the ${warehouse?.name} warehouse has been modified",
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
                    withContext(Dispatchers.Main){
                        navController.navigate("home")
                    }

                }else{
                    try {
                        val errorBody = imageResponse.errorBody()?.string()
                        Log.e("excepcionUserB", "$errorBody")
                    } catch (e: Exception) {
                        Log.e("excepcionUserB", "Error al obtener el cuerpo del error: $e")
                    }
                }
                isloading = false
            }catch (e: Exception){
                Log.d("ExceptionImage", "$e")
            }
        }
    }

    val pickImageLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            // El usuario seleccionó una imagen, realiza el procesamiento aquí
            if (uri != null) {
                // Haz algo con la URI de la imagen, como cargarla en tu aplicación
                // Luego, envía la imagen a la API
                val imageFile = uri.let { uri ->
                    try {
                        val inputStream = context.contentResolver.openInputStream(uri)
                        val file = createTempFile("image", null, context.cacheDir)
                        file.outputStream().use { output ->
                            inputStream?.copyTo(output)
                        }
                        file
                    } catch (e: Exception) {
                        Log.e("ImageFileException", "Error al obtener el archivo de imagen: $e")
                        null
                    }
                }
                if (imageFile!=null) updateImage(imageFile)

                // Por ejemplo, puedes mostrar el nombre del archivo seleccionado
                Toast.makeText(context, "Selected Image: ${imageFile?.name}", Toast.LENGTH_SHORT).show()
            }
        }





    var isMenuExpanded by remember { mutableStateOf(false) }
    val employees = remember { DataRepository.getEmployees()?.filter { it.role == 1 } ?: emptyList() }



    val customTextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        textColor = MaterialTheme.colorScheme.primary,
        backgroundColor = MaterialTheme.colorScheme.outline,
        cursorColor =  BlueSystem,
        focusedBorderColor =  BlueSystem,
        focusedLabelColor = BlueSystem,
        unfocusedBorderColor =  MaterialTheme.colorScheme.secondaryContainer
    )


    val updateWarehouse : () -> Unit = {
        warehouse?.organization = generateOrganizationString(numberofplants, hallwaysPerPlant, racksPerHallway, otherInfo)
        GlobalScope.launch(Dispatchers.IO) {
            try {
                if(warehouse!=null){
                    if(pinLocation!=null){
                        warehouse.longitude = pinLocation!!.longitude
                        warehouse.latitude = pinLocation!!.latitude
                    }
                    val response =  RetrofitInstance.api.updateWarehouse(warehouse)

                    if (response.isSuccessful) {
                        val wResponse = response.body()
                        val user = DataRepository.getUser()
                        if(user!=null){
                            val zonedDateTime = ZonedDateTime.now()
                            val formattedDate = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                            val addTransaccion = RetrofitInstance.api.addHistory(
                                Transaction(0,user.id.toString(),user.code, "The data of the ${warehouse?.name} warehouse has been modified",
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
                        withContext(Dispatchers.Main){
                            Toast.makeText(context, "Warehouse successfully updated", Toast.LENGTH_SHORT).show()

                            navController.navigate("home")
                        }
                    }else{
                    }
                }
            }catch (e: Exception) {
                Log.d("excepcionWarehouse","$e")
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
                line.startsWith("Other") -> {
                    otherInfo = line.split(":", limit = 2)[1].trim()
                }

            }
        }
    }


    LaunchedEffect(Unit){
        pinLocation = LatLng(warehouse!!.latitude, warehouse.longitude)
        updateOrganizationData(warehouse.organization)
        if(employees!=null && warehouse!=null)selectedOption= "Name: ${employees.find { user: User ->  user.id == warehouse.idAdministrator}?.name}  Role: Administrator Code: ${employees.find { user: User ->  user.id == warehouse.idAdministrator}?.code};${employees.find { user: User ->  user.id == warehouse.idAdministrator}?.id}"
    }



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
           modifier = Modifier.background(MaterialTheme.colorScheme.background)
       ) {
           TopAppBar(
               navigationIcon = {
                   IconButton(onClick = { navController.popBackStack() }) {
                       androidx.compose.material3.Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Login", tint = BlueSystem)
                   }
               },
               backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
               title = { androidx.compose.material.Text(text = "Update Warehouse", color = BlueSystem) }
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
                   if(warehouse!=null && !warehouse.url.isNullOrBlank()){
                       val painter = rememberImagePainter(
                           data = warehouse.url,
                           builder = {
                               crossfade(true)
                               placeholder(R.drawable.loading)
                           }
                       )
                       Image(
                           painter = painter,
                           contentDescription = null,
                           modifier = Modifier
                               .fillMaxSize()
                       )
                   }else{
                       Image(
                           painter = painterResource(id = R.drawable.warehouse), // Reemplaza con tu lógica para cargar la imagen
                           contentDescription = null,
                           modifier = Modifier
                               .fillMaxSize()
                       )
                   }
                   Box(modifier = Modifier.fillMaxWidth()) {
                       ElevatedButton(
                           modifier = Modifier
                               .padding(top = 8.dp)
                               .align(alignment = Alignment.CenterStart),
                           onClick = {
                               pickImageLauncher.launch("image/*")
                           },
                           colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                               containerColor = BlueSystem
                           ),
                           elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                               defaultElevation = 5.dp
                           )
                       ){
                           Icon(
                               imageVector = Icons.Filled.Refresh,
                               contentDescription = "",
                               tint = Color.White
                           )
                       }
                   }
               }

               Column(
                   modifier = Modifier
                       .fillMaxWidth()
                       .padding(top = 16.dp)
               ) {
                   warehouse?.let {
                       var name by remember { mutableStateOf(it.name) }
                       var location by remember { mutableStateOf(it.location) }
                       var organization by remember { mutableStateOf(it.organization) }
                       var administratorId by remember { mutableIntStateOf(it.idAdministrator) }
                       if (showDialog) {
                           ShowDialog(
                               onDismiss = { showDialog = false},
                               onSuccessfully = {
                                   showDialog = false
                                   val geo = geocoder.getFromLocation(it.latitude, it.longitude, 1)?.get(0)
                                   if (geo != null) {
                                       location =  geo.getAddressLine(0)
                                   }
                                   pinLocation = it
                               }
                           )
                       }
                       TextField(
                           value = name,
                           label = { Text("Name", color = MaterialTheme.colorScheme.outlineVariant) },
                           onValueChange = { name = it },
                           shape = RoundedCornerShape(8.dp),
                           colors= customTextFieldColors,
                           keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                           keyboardActions = KeyboardActions(
                               onNext = {
                                   focusManager.moveFocus(FocusDirection.Down)
                               }
                           ),
                           modifier = Modifier
                               .fillMaxWidth()
                               .padding(4.dp)
                               .border(
                                   width = 0.5.dp,
                                   color = BlueSystem,
                                   shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                               )
                       )
                       Spacer(modifier = Modifier.height(10.dp))

                       TextField(
                           value = numberofplants.toString(),
                           onValueChange = {
                               if (it.isEmpty() || it.toIntOrNull() != null) {
                                   numberofplants = it.toIntOrNull() ?: 0
                               }
                           },
                           shape = RoundedCornerShape(8.dp),
                           label = { Text("Number of Plants", color = MaterialTheme.colorScheme.outlineVariant) },
                           colors = customTextFieldColors,
                           keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                           keyboardActions = KeyboardActions(
                               onNext = {
                                   focusManager.moveFocus(FocusDirection.Down)
                               }
                           ),
                           modifier = Modifier
                               .fillMaxWidth()
                               .padding(4.dp)
                               .border(
                                   width = 0.5.dp,
                                   color = BlueSystem,
                                   shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias
                               ),
                       )
                       Spacer(modifier = Modifier.height(10.dp))

                       if (numberofplants != 0) {
                           for (i in 0 until numberofplants) {
                               TextField(
                                   value = hallwaysPerPlant.getOrNull(i)?.toString() ?: "",
                                   onValueChange = { newValue ->
                                       hallwaysPerPlant.getOrNull(i)?.let {
                                           hallwaysPerPlant[i] = newValue.toIntOrNull() ?: 0
                                       } ?: hallwaysPerPlant.add(newValue.toIntOrNull() ?: 0)
                                   },
                                   shape = RoundedCornerShape(8.dp),
                                   label = { Text("Number of hallways on plant Nº${i}", color = MaterialTheme.colorScheme.outlineVariant) },
                                   colors = customTextFieldColors,
                                   keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                   keyboardActions = KeyboardActions(
                                       onNext = {
                                           focusManager.moveFocus(FocusDirection.Down)
                                       }
                                   ),
                                   modifier = Modifier
                                       .fillMaxWidth()
                                       .padding(4.dp)
                                       .border(
                                           width = 0.5.dp,
                                           color = BlueSystem,
                                           shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias
                                       ),
                               )
                               Spacer(modifier = Modifier.height(10.dp))
                               if (hallwaysPerPlant.isNotEmpty() && hallwaysPerPlant.count() != 0) {
                                   for (x in 0 until (hallwaysPerPlant.getOrNull(i) ?: 0)) {
                                       TextField(
                                           value = racksPerHallway[i]?.getOrNull(x).orEmpty(),
                                           onValueChange = { newValue ->
                                               // Modifica el texto para mantener el formato LETRA-LETRA-NUMERO y una longitud máxima de 5
                                               val modifiedText = modifyText(newValue)

                                               // Actualiza el valor en la lista de racksPerHallway
                                               val currentRacksList = racksPerHallway[i]?.toMutableList() ?: mutableListOf()
                                               if (currentRacksList.size <= x) {
                                                   repeat(x - currentRacksList.size + 1) {
                                                       currentRacksList.add("") // Añade elementos vacíos para mantener la longitud
                                                   }
                                               }
                                               currentRacksList[x] = modifiedText // Actualiza el valor en la posición x
                                               racksPerHallway[i] = currentRacksList // Actualiza la lista en el mapa
                                           },
                                           shape = RoundedCornerShape(8.dp),
                                           label = { Text("Racks in hallway ${x + 1} of plant ${i}", color = MaterialTheme.colorScheme.outlineVariant) },
                                           colors = customTextFieldColors,
                                           keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                           keyboardActions = KeyboardActions(
                                               onNext = {
                                                   focusManager.moveFocus(FocusDirection.Down)
                                               }
                                           ),
                                           modifier = Modifier
                                               .fillMaxWidth()
                                               .padding(4.dp)
                                               .border(
                                                   width = 0.5.dp,
                                                   color = BlueSystem,
                                                   shape = RoundedCornerShape(8.dp)
                                               ),
                                       )
                                       Spacer(modifier = Modifier.height(10.dp))
                                   }
                               }
                           }
                       }
                       Spacer(modifier = Modifier.height(10.dp))
                       Row(
                           verticalAlignment = Alignment.CenterVertically,
                           horizontalArrangement = Arrangement.Start
                       ) {
                           Icon(imageVector = Icons.Filled.Info, contentDescription ="info about de hallway", tint = Color.LightGray )
                           Text(text = "When adding racks in hallway, you must specify a range, for example A-C-3 where A-C indicates the rack sections and the last number the height.",color = Color.LightGray)
                       }
                       Spacer(modifier = Modifier.height(10.dp))
                       ElevatedButton(
                           modifier = Modifier
                               .fillMaxWidth(),
                           onClick = {
                               showDialog = true
                           },
                           colors =  androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                               containerColor = BlueSystem
                           ),
                           elevation =  androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                               defaultElevation = 5.dp
                           )
                       ){
                           Icon(imageVector = Icons.Filled.Add, contentDescription = null, tint = Color.White )
                       }
                       Spacer(modifier = Modifier.height(10.dp))
                       if(pinLocation!=null){
                           ElevatedButton(
                               modifier = Modifier
                                   .fillMaxWidth(),
                               onClick = {

                               },
                               colors =  androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                                   containerColor =  MaterialTheme.colorScheme.secondaryContainer
                               ),
                               elevation =  androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                                   defaultElevation = 5.dp
                               )
                           ){
                               geocoder.getFromLocation(pinLocation!!.latitude, pinLocation!!.longitude, 1)?.get(0)
                                   ?.let {
                                       Text(
                                           it.getAddressLine(0), color = BlueSystem)
                                   }
                           }
                           Spacer(modifier = Modifier.height(10.dp))
                       }
                       TextField(
                           value = otherInfo,
                           onValueChange = { otherInfo = it },
                           label = { Text("Other information about the warehouse (optional)", color = MaterialTheme.colorScheme.outlineVariant) },
                           shape = RoundedCornerShape(8.dp),
                           colors = customTextFieldColors,
                           keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                           keyboardActions = KeyboardActions(
                               onNext = {
                                   focusManager.moveFocus(FocusDirection.Down)
                               }
                           ),
                           modifier = Modifier
                               .fillMaxWidth()
                               .padding(4.dp)
                               .border(
                                   width = 0.5.dp,
                                   color = BlueSystem,
                                   shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                               ),
                       )
                       Spacer(modifier = Modifier.height(10.dp))

                       TextField(
                           value = location,
                           label = { Text("Location", color = MaterialTheme.colorScheme.outlineVariant) },
                           shape = RoundedCornerShape(8.dp),
                           onValueChange = { location = it },
                           colors= customTextFieldColors,
                           keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                           keyboardActions = KeyboardActions(
                               onNext = {
                                   focusManager.moveFocus(FocusDirection.Down)
                               }
                           ),
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
                                    employees.forEach { employee ->
                                        DropdownMenuItem(onClick = {
                                            selectedOption= "Name: ${employee.name}  Role: Administrator Code: ${employee.code};${employee.id}"
                                            administratorId = employee.id
                                            isMenuExpanded = false
                                        },
                                            modifier = Modifier
                                                .padding(5.dp)
                                                .border(
                                                    width = 0.5.dp,
                                                    color = BlueSystem,
                                                    shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                                                )
                                        ) {
                                            Text("Name: ${employee.name}  Role: Administrator Code: ${employee.code}", color = MaterialTheme.colorScheme.primary )
                                        }
                                    }
                                }
                                Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null, tint =BlueSystem)
                            }
                       }
                       Row(
                           horizontalArrangement = Arrangement.spacedBy(8.dp)
                       ){
                           ElevatedButton(
                               modifier = Modifier
                                   .weight(1f)
                                   .fillMaxWidth(),
                               onClick = {
                                   navController.navigate("home")
                               },
                               colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                                   containerColor =MaterialTheme.colorScheme.secondaryContainer,
                               ),
                               elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
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
                                   warehouse.name =name
                                   warehouse.location = location
                                   warehouse.organization = organization
                                   warehouse.idAdministrator = administratorId

                                   updateWarehouse()
                               },
                               colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                                   containerColor = BlueSystem
                               ),
                               elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                                   defaultElevation = 5.dp
                               )
                           ){
                               Text(text = "Update", color = Color.White)
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
@Composable
fun ShowDialog(onDismiss: () -> Unit, onSuccessfully: (LatLng) -> Unit) {
    var pinLocation by remember { mutableStateOf<LatLng?>(null) }

    Dialog(
        onDismissRequest = {
            pinLocation?.let { onSuccessfully(it) }
            onDismiss()
        },
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        ),
        content = {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically, // Alineación vertical
                    horizontalArrangement = Arrangement.SpaceAround
                ){
                    ElevatedButton(
                        modifier = Modifier
                            .padding(top = 8.dp)
                        ,
                        onClick = {
                            onDismiss.invoke()
                        },
                        colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        ),
                        elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                            defaultElevation = 5.dp
                        )
                    ){
                        Text(text ="Cancel", color = BlueSystem)
                    }
                    ElevatedButton(
                        modifier = Modifier
                            .padding(top = 8.dp)
                        ,
                        onClick = {
                            onSuccessfully.invoke(pinLocation!!)
                        },
                        colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                            containerColor = BlueSystem
                        ),
                        elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                            defaultElevation = 5.dp
                        )
                    ){
                        Text(text ="Continue", color = Color.White)
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    GoogleMap(
                        modifier = Modifier
                            .fillMaxSize(),
                        onMapClick = { clickedLatLng ->
                            // Al hacer clic en el mapa, actualiza la ubicación del pin
                            pinLocation = null
                            pinLocation = clickedLatLng
                        }
                    ) {
                        pinLocation?.let { location ->
                            PointMarker(location, "Pin Location", "Description", BitmapDescriptorFactory.defaultMarker(), "tag", false)
                        }
                    }
                }
            }
        }
    )
}


