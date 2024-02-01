package com.example.qrstockmateapp.screens.Profile

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.qrstockmateapp.R
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

@ExperimentalAnimationApi
@Composable
fun ProfileScreen(navController: NavController) {

    var user = DataRepository.getUser()

    val context = LocalContext.current
    val imageUrl = user?.url
    val userId = user?.id
    val userName = user?.name
    val userCode = user?.code
    val userRole = user?.role
    var userEmail by remember { mutableStateOf(user?.email) }
    var userPhone by remember { mutableStateOf(user?.phone) }

    val placeholderImage = painterResource(id = R.drawable.user)
    val updateInfo:()->Unit={
        if(userEmail!=null && userEmail.toString().isNotBlank()
            && userPhone!=null && userPhone.toString().isNotBlank()){
            GlobalScope.launch(Dispatchers.IO){
                var userMod = user
                userMod?.email = userEmail.toString()
                userMod?.phone = userPhone.toString()
                if(userMod!=null){
                    val response = RetrofitInstance.api.updateUser(userMod)
                    if(response.isSuccessful){
                        DataRepository.setUser(userMod)
                        withContext(Dispatchers.Main){
                            Toast.makeText(context, "satisfactory update", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }else {
            Toast.makeText(context, "You should not leave empty fields", Toast.LENGTH_SHORT).show()
        }

    }

    val customTextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        textColor = MaterialTheme.colorScheme.primary,
        backgroundColor = MaterialTheme.colorScheme.outline,
        cursorColor =  BlueSystem,
        focusedBorderColor =  BlueSystem,
        focusedLabelColor = BlueSystem,
        unfocusedBorderColor =  BlueSystem
    )

    val updateImage:(File)->Unit={ file ->
        GlobalScope.launch(Dispatchers.IO){
            try {

                val userId = user?.id
                val userIdRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), userId.toString())

                // Crea RequestBody y MultipartBody.Part con el archivo de imagen seleccionado
                val imageRequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                val imagePart = MultipartBody.Part.createFormData("image", file.name, imageRequestBody)

                withContext(Dispatchers.Main){
                    Toast.makeText(context, "loading...", Toast.LENGTH_SHORT).show()
                }

                val imageResponse =  RetrofitInstance.api.updateImageUser(userIdRequestBody, imagePart)
                val cm  =DataRepository.getCompany()
                if(cm!=null){
                    val employeesResponse = RetrofitInstance.api.getEmployees(cm)
                    if (employeesResponse.isSuccessful) {
                        val employeesIO = employeesResponse.body()
                        val me = employeesIO?.find {it.id == userId  }
                        if(me!=null)DataRepository.setUser(me)
                    }
                }


                if(imageResponse.isSuccessful){
                    withContext(Dispatchers.Main){
                       navController.navigate("profile")
                    }
                }else{
                    try {
                        val errorBody = imageResponse.errorBody()?.string()
                        Log.e("excepcionUserB", "$errorBody")
                    } catch (e: Exception) {
                        Log.e("excepcionUserB", "Error al obtener el cuerpo del error: $e")
                    }
                }

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


    fun userRoleToString(roleId: Int): String {
        return when (roleId) {
            0 -> "Director"
            1 -> "Administrator"
            2 -> "Inventory Technician"
            3 -> "User"
            4 -> "Carrier"
            else -> "Unknown Role"
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = { navController.navigate("home");DataRepository.setCurrentScreenIndex(0)}) {
                    androidx.compose.material3.Icon(Icons.Default.ArrowBack, contentDescription = "Back to Login", tint = BlueSystem)
                }
            },
            backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
            title = { Text(text = "Profile", color = BlueSystem) }
        )
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState()),
        )
        {
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)) {

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
                        imageVector = Icons.Filled.AddAPhoto,
                        contentDescription = "",
                        tint = Color.White
                    )
                }
            }
            Column(     //Imagen de Perfil de Usuario & Nombre
                modifier = Modifier
                    .fillMaxHeight(0.45f)
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center)
                    .padding(7.dp)
            ) {

                if (imageUrl.isNullOrBlank()) {
                    // Si la URL es nula o vacía, mostrar la imagen por defecto
                    Image(
                        painter = placeholderImage,
                        contentDescription = "Default User Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .size(400.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .shadow(4.dp, CircleShape),
                        contentScale = ContentScale.FillBounds

                    )
                } else {
                    // Si hay una URL válida, cargar la imagen usando Coil
                    val painter = rememberImagePainter(
                        data = imageUrl,
                        builder = {
                            crossfade(true)
                            placeholder(R.drawable.loading)
                        }
                    )
                    Image(
                        painter = painter,
                        contentDescription = "User Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .size(400.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .shadow(4.dp, CircleShape),
                        contentScale = ContentScale.FillBounds
                    )
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            if (userName != null) {
                Text(
                    text = userName,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 15.sp,
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(15.dp)
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
            Spacer(modifier = Modifier.height(30.dp)) // Espacio entre Imagen & Nombre y Info de Usuario
            if (userRole != null) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold,color = MaterialTheme.colorScheme.primary,)) {
                            append("Role: ")
                        }
                        append(userRoleToString(userRole))
                    },
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
            Spacer(modifier = Modifier.height(15.dp)) // Espacio entre Imagen & Nombre y Info de Usuario
            if (userCode != null) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary,)) {
                            append("Warehouse Code: ")
                        }
                        append(userCode)
                    },
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.55f),
                verticalArrangement = Arrangement.Center,
            ) {

                Spacer(modifier = Modifier.height(20.dp))
                if (userEmail != null) {
                    TextField(
                        value = userEmail!!,
                        label = { androidx.compose.material3.Text("Email", color = MaterialTheme.colorScheme.outlineVariant) },
                        colors = customTextFieldColors,
                        onValueChange = {userEmail = it},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .border(
                                width = 0.5.dp,
                                color = BlueSystem,
                                shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                            ),
                        readOnly = false
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                if (userPhone != null) {
                    TextField(
                        value = userPhone!!,
                        shape = RoundedCornerShape(8.dp),
                        label = { androidx.compose.material3.Text("Phone", color = MaterialTheme.colorScheme.outlineVariant) },
                        colors = customTextFieldColors,
                        onValueChange = {userPhone=it},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .border(
                                width = 0.5.dp,
                                color = BlueSystem,
                                shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                            ),
                        readOnly = false
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                ElevatedButton(
                    onClick = {
                        updateInfo()
                    },
                    colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                        containerColor = BlueSystem
                    ),
                    elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = 5.dp
                    )
                ){
                    Text(text = "Update", color=Color.White)
                }
            }
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(75.dp))
        }
    }




}