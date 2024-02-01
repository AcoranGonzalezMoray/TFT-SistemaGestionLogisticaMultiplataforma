package com.example.qrstockmateapp.screens.Home.UpdateUser

import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ElevatedButton
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.qrstockmateapp.R
import com.example.qrstockmateapp.api.models.Transaction
import com.example.qrstockmateapp.api.models.userRoleToString
import com.example.qrstockmateapp.api.services.RetrofitInstance
import com.example.qrstockmateapp.navigation.repository.DataRepository
import com.example.qrstockmateapp.ui.theme.BlueSystem
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UpdateUserScreen(navController: NavController) {
    var user = remember { DataRepository.getUserPlus() }
    var selectedOption by remember { mutableStateOf("Select a role") }
    var isloading by remember { mutableStateOf<Boolean>(false) }
    val focusManager = LocalFocusManager.current

    val context = LocalContext.current

    val updateImage:(File)->Unit={file ->
        GlobalScope.launch(Dispatchers.IO){
            try {
                isloading = true
                val userC = user
                val userId = user?.id
                val userIdRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), userId.toString())

                // Crea RequestBody y MultipartBody.Part con el archivo de imagen seleccionado
                val imageRequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                val imagePart = MultipartBody.Part.createFormData("image", file.name, imageRequestBody)

                val imageResponse =  RetrofitInstance.api.updateImageUser(userIdRequestBody, imagePart)
                val cm  =DataRepository.getCompany()
                if(cm!=null){
                    val employeesResponse = RetrofitInstance.api.getEmployees(cm)
                    if (employeesResponse.isSuccessful) {
                        val employeesIO = employeesResponse.body()
                        val me = employeesIO?.find {it.id == DataRepository.getUser()!!.id }
                        if(me!=null)DataRepository.setUser(me)
                    }
                }


                if(imageResponse.isSuccessful){
                    val user = DataRepository.getUser()
                    if(user!=null){
                        val zonedDateTime = ZonedDateTime.now()
                        val formattedDate = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                        val addTransaccion = RetrofitInstance.api.addHistory(
                            Transaction(0,user.id.toString(),user.code, "The image of the ${userC?.name} user has been modified",
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
                        navController.navigate("manageUser")
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
    var roles = remember { mutableListOf(0, 1, 2, 3, 4) }

    LaunchedEffect(Unit){
        if(roles!=null && user!=null)selectedOption= "Role:${ userRoleToString(user.role)}"
    }
    val customTextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        textColor = MaterialTheme.colorScheme.primary,
        backgroundColor = MaterialTheme.colorScheme.outline,
        cursorColor =  BlueSystem,
        focusedBorderColor =  BlueSystem,
        focusedLabelColor = BlueSystem,
        unfocusedBorderColor =  BlueSystem
    )


    val updateUser : () -> Unit = {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                if(user!=null){
                    val response =  RetrofitInstance.api.updateUser(user)
                    val userC = user
                    if (response.isSuccessful) {
                        val wResponse = response.body()
                        val cm  =DataRepository.getCompany()
                        if(cm!=null){
                            val employeesResponse = RetrofitInstance.api.getEmployees(cm)
                            if (employeesResponse.isSuccessful) {
                                val employeesIO = employeesResponse.body()
                                val me = employeesIO?.find {it.id == DataRepository.getUser()!!.id  }
                                if(me!=null)DataRepository.setUser(me)
                            }
                        }
                        val user = DataRepository.getUser()
                        if(user!=null){
                            val zonedDateTime = ZonedDateTime.now()
                            val formattedDate = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                            val addTransaccion = RetrofitInstance.api.addHistory(
                                Transaction(0,user.id.toString(),user.code, "The data of the ${userC?.name} user has been modified",
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
                            navController.navigate("manageUser")
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
                .background(Color.White)
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
        Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        androidx.compose.material3.Icon(Icons.Default.ArrowBack, contentDescription = "Back to Login", tint = BlueSystem)
                    }
                },
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                title = { androidx.compose.material.Text(text = "Update User", color = BlueSystem) }
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    if(user!=null && !user.url.isNullOrBlank()){
                        val painter = rememberImagePainter(
                            data = user.url,
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
                            painter = painterResource(id = R.drawable.user), // Reemplaza con tu lógica para cargar la imagen
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.5f) // La imagen ocupa la mitad de la pantalla
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
                    user?.let {
                        var name by remember { mutableStateOf(it.name) }
                        var email by remember { mutableStateOf(it.email) }
                        var phone by remember { mutableStateOf(it.phone) }


                        TextField(
                            value = name,
                            shape = RoundedCornerShape(8.dp),
                            label = { Text("Name", color = MaterialTheme.colorScheme.outlineVariant) },
                            onValueChange = { name = it },
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
                        TextField(
                            value = email,
                            shape = RoundedCornerShape(8.dp),
                            label = { Text("Email", color = MaterialTheme.colorScheme.outlineVariant) },
                            onValueChange = {email = it },
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
                        TextField(
                            value = phone,
                            shape = RoundedCornerShape(8.dp),
                            label = { Text("Phone", color = MaterialTheme.colorScheme.outlineVariant) },
                            onValueChange = { phone = it },
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
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                                .background(BlueSystem)
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
                                    roles?.forEach { rol ->
                                        DropdownMenuItem(onClick = {
                                            selectedOption= "Role:${ userRoleToString(rol)}"
                                            user.role= rol
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
                                            Text("Role:${ userRoleToString(rol)}", color = MaterialTheme.colorScheme.primary )
                                        }
                                    }
                                }
                                Icon(modifier = Modifier.weight(1f), imageVector = Icons.Filled.ArrowDropDown, contentDescription = null, tint =BlueSystem)
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
                                    navController.navigate("manageUser")
                                },
                                colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                ),
                                elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                                    defaultElevation = 5.dp
                                )
                            ){
                                Text(text = "Cancel",color = BlueSystem)
                            }
                            ElevatedButton(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                onClick = {
                                    user.name =name
                                    user.email = email
                                    user.phone = phone
                                    updateUser()
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

            }
        }
    }
}
