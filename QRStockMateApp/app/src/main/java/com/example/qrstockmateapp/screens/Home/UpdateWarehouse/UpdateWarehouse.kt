package com.example.qrstockmateapp.screens.Home.UpdateWarehouse


import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.qrstockmateapp.R
import com.example.qrstockmateapp.api.models.Transaction
import com.example.qrstockmateapp.api.models.User
import com.example.qrstockmateapp.api.services.RetrofitInstance
import com.example.qrstockmateapp.navigation.repository.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UpdateWarehouseScreen(navController: NavController) {
    var warehouse = remember { DataRepository.getWarehousePlus() }
    var selectedOption by remember { mutableStateOf("Selected an existing administrator to associate with the warehouse") }
    var isloading by remember { mutableStateOf<Boolean>(false) }

    val context = LocalContext.current

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
                Log.d("ExceptionImage", "${e}")
            }
        }
    }

    val pickImageLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            // El usuario seleccionó una imagen, realiza el procesamiento aquí
            if (uri != null) {
                // Haz algo con la URI de la imagen, como cargarla en tu aplicación
                // Luego, envía la imagen a la API
                val imageFile = uri?.let { uri ->
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
    var employees = remember { DataRepository.getEmployees()?.filter { it.role == 1 } ?: emptyList() }

    LaunchedEffect(Unit){
        if(employees!=null && warehouse!=null)selectedOption= "Name: ${employees.find { user: User ->  user.id == warehouse.idAdministrator}?.name}  Role: Administrator Code: ${employees.find { user: User ->  user.id == warehouse.idAdministrator}?.code};${employees.find { user: User ->  user.id == warehouse.idAdministrator}?.id}"
    }

    val customTextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        cursorColor = Color.Black,
        focusedBorderColor = Color.Black,
        focusedLabelColor = Color.Black,
        unfocusedBorderColor = Color.Black,
        backgroundColor = Color.LightGray
    )


    val updateWarehouse : () -> Unit = {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                if(warehouse!=null){
                    Log.d("excepcionWarehouseCambio","${warehouse}")
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
                            navController.navigate("home")
                        }
                    }else{
                    }
                }
            }catch (e: Exception) {
                Log.d("excepcionWarehouse","${e}")
            }
        }

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
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
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
                            .fillMaxWidth()
                            .fillMaxHeight(0.5f)
                    )
                }else{
                    Image(
                        painter = painterResource(id = R.drawable.warehouse), // Reemplaza con tu lógica para cargar la imagen
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.5f) // La imagen ocupa la mitad de la pantalla
                    )
                }
                Box(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .align(alignment = Alignment.CenterStart),
                        onClick = { pickImageLauncher.launch("image/*") }
                    ) {
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
                    var administratorId by remember { mutableStateOf(it.idAdministrator) }

                    TextField(
                        value = name,
                        label = { Text("Name") },
                        onValueChange = { name = it },
                        colors= customTextFieldColors,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    )
                    TextField(
                        value = location,
                        label = { Text("Location") },
                        onValueChange = { location = it },
                        colors= customTextFieldColors,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    )
                    TextField(
                        value = organization,
                        label = { Text("Organization") },
                        onValueChange = { organization = it },
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
                            employees?.forEach { employee ->
                                DropdownMenuItem(onClick = {
                                    selectedOption= "Name: ${employee.name}  Role: Administrator Code: ${employee.code};${employee.id}"
                                    administratorId = employee.id
                                    isMenuExpanded = false
                                }) {
                                    Text("Name: ${employee.name}  Role: Administrator Code: ${employee.code}" )
                                }
                            }
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ){
                        Button(colors = ButtonDefaults.buttonColors(Color.Red),
                            onClick = { navController.navigate("home")},
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()) {
                            Text(text = "Cancel",color = Color.White)
                        }
                        Button(colors = ButtonDefaults.buttonColors(Color.Black),modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                            onClick = {
                                warehouse.name =name
                                warehouse.location = location
                                warehouse.organization = organization
                                warehouse.idAdministrator = administratorId

                                updateWarehouse()
                            } ) {
                            Text(text = "Update", color = Color.White)
                        }
                    }
                }
            }

        }
    }
}
