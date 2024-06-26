package com.example.qrstockmateapp.screens.Search.ItemDetails

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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.qrstockmateapp.R
import com.example.qrstockmateapp.api.models.Item
import com.example.qrstockmateapp.api.models.Transaction
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
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ItemDetailsScreen(navController: NavController) {
    var item = DataRepository.getItem()
    var availableCount by remember { mutableStateOf(item?.stock) }
    val availableState = rememberUpdatedState(availableCount)
    var count by remember { mutableStateOf(0) }
    val countState = rememberUpdatedState(count)
    val context = LocalContext.current
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

                val itemId =item?.id
                val itemIdRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), itemId.toString())

                // Crea RequestBody y MultipartBody.Part con el archivo de imagen seleccionado
                val imageRequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                val imagePart = MultipartBody.Part.createFormData("image", file.name, imageRequestBody)

                withContext(Dispatchers.Main){
                    Toast.makeText(context, "loading...", Toast.LENGTH_SHORT).show()
                }

                val imageResponse =  RetrofitInstance.api.updateImageItem(itemIdRequestBody, imagePart)
                if(imageResponse.isSuccessful){
                    withContext(Dispatchers.Main){
                        navController.navigate("search")
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

    val updateStock : (item:Item) -> Unit = {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                if(item!=null){
                    val response =  RetrofitInstance.api.updateItem(item)

                    if (response.isSuccessful) {
                        val user = DataRepository.getUser()
                        if(user!=null){
                            val zonedDateTime = ZonedDateTime.now()
                            val formattedDate = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                            val addTransaccion = RetrofitInstance.api.addHistory(
                                Transaction(0,user.id.toString(),user.code, "The info of the ${item?.name} item has been modified",
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
                        val wResponse = response.body()
                    }else{
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
            .background(color = MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center)
        ) {
            var name by remember { mutableStateOf(item?.name) }
            Text(text = "Name: "+name.toString(),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
        Box(
            modifier = Modifier
                .zIndex(20f)
                .height(300.dp)
        ) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)) {
                ElevatedButton(
                    modifier = Modifier
                        .padding(top = 8.dp, end = 10.dp)
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
                        .padding(top = 80.dp)
                        .clip(shape = RoundedCornerShape(36.dp))
                        .fillMaxSize()
                )
            }else{
                Image(
                    painter = painterResource(id = R.drawable.item), // Reemplaza con tu lógica para cargar la imagen
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            var location by remember { mutableStateOf(item?.location.toString()) }
            var warehouse by remember { mutableStateOf(item?.warehouseId) }
            var weight by remember { mutableStateOf(item?.weightPerUnit) }
            Text(text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary,)) {
                    append("Location: ")
                }
                append(location)
            },
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.primary,
            )
            Text(text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary,)) {
                    append("Warehouse: ")
                }
                append(DataRepository.getWarehouses()?.find { it.id == warehouse }?.name)
            },
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.primary,
            )
            Text(text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold,color = MaterialTheme.colorScheme.primary,)) {
                    append("Weight per unit: ")
                }
                append(weight.toString()+"  Kg")
            },
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.primary,
            )
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
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary,)) {
                        append("Available: ")
                    }
                    append(availableState.value.toString())
                },
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.primary,
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
                        shape = RoundedCornerShape(8.dp),
                        onValueChange = { newValue ->
                            count = newValue.toIntOrNull() ?: 0
                        },
                        modifier = Modifier
                            .padding(16.dp)
                            .width(60.dp)
                            .height(55.dp).border(
                                width = 0.5.dp,
                                color =  BlueSystem,
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
                    Spacer(modifier = Modifier.width(5.dp))
                    ElevatedButton(
                        onClick = {

                            var newStock = item?.stock?.plus(countState.value)
                            var newItem = item
                            if (newItem != null) {
                                if (newStock != null && newStock>=0) {
                                    newItem.stock = newStock
                                    updateStock(newItem)
                                    availableCount = newStock
                                    count = 0
                                }else{
                                    Toast.makeText(context, "There cannot be a negative stock", Toast.LENGTH_SHORT).show()
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
                        androidx.compose.material.Text("Update", color = Color.White)
                    }
                }
            }

        }

    }

}


