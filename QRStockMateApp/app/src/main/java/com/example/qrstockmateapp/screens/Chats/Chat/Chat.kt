package com.example.qrstockmateapp.screens.Chats.Chat

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhoneEnabled
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.outlined.MicNone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.qrstockmateapp.MainActivity
import com.example.qrstockmateapp.R
import com.example.qrstockmateapp.api.models.Message
import com.example.qrstockmateapp.api.services.RetrofitInstance
import com.example.qrstockmateapp.navigation.repository.DataRepository
import com.example.qrstockmateapp.ui.theme.BlueSystem
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@Throws(IOException::class)
private fun createImageFile(context: Context): File {
    // Crea un archivo de imagen único
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_TEMP" + timeStamp + "_"
    val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
    return File.createTempFile(imageFileName, ".jpg", storageDir)
}
@OptIn(DelicateCoroutinesApi::class)
@Composable
fun ChatScreen(navController: NavController, sharedPreferences: SharedPreferences) {

    val context = LocalContext.current

    var newMessage by remember { mutableStateOf(TextFieldValue()) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val lazyListState = rememberLazyListState()
    val updatedLazyListState = rememberUpdatedState(lazyListState)

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.audio))
    val progress by animateLottieCompositionAsState(composition = composition, iterations = LottieConstants.IterateForever, speed = 1f)


    val options = listOf(
        OptionSize("Small",  12, 0),
        OptionSize("Medium",  17, 1),
        OptionSize("Large",  24, 2)
    )
    var selectedOption by remember { mutableStateOf<OptionSize?>(options[sharedPreferences.getInt(
        MainActivity.FONT_SIZE_CHAT, 0)]) }

    // Variable para rastrear si el botón de grabación está presionado
    var isRecording by remember { mutableStateOf(false) }
    var mediaRecorder: MediaRecorder? = null
    var isloadScreen by remember {
        mutableStateOf(true)
    }
    var isloading by remember{ mutableStateOf(false) }

    var current by remember{ mutableStateOf(true) }
    var messages by remember{ mutableStateOf(emptyList<Message>()) }


    val goBottom:()->Unit = {
        MainScope().launch {
            if (messages.isNotEmpty()){
                updatedLazyListState.value.scrollToItem(messages.size)
            }
        }
    }

    val getMessages: () -> Unit = {
        GlobalScope.launch(Dispatchers.IO) {
            while (current && DataRepository.getUser()!=null){
                val response = RetrofitInstance.api.getMessageByCode(DataRepository.getUser()!!.code)
                if (response.isSuccessful) {
                    val newMessage = response.body()
                    if (newMessage != null) {
                        val newMessagesList = newMessage.filter { message ->
                            // Filtrar mensajes enviados por el usuario actual
                            (message.senderContactId == DataRepository.getUser()?.id && message.receiverContactId == DataRepository.getUserPlus()?.id) ||

                                    // Filtrar mensajes enviados al usuario actual
                                    (message.senderContactId == DataRepository.getUserPlus()?.id && message.receiverContactId == DataRepository.getUser()?.id)
                        }

                        // Verificar si hay nuevos mensajes
                        val hasNewMessages = newMessagesList.any { newMessage ->
                            !messages.any { oldMessage ->
                                newMessage.id == oldMessage.id
                            }
                        }

                        if (hasNewMessages) {
                            // Añadir los nuevos mensajes al final de la lista
                            messages = newMessagesList
                            goBottom()
                        }
                    }
                }
                delay(500) // Espera 500 milisegundos antes de realizar la siguiente solicitud
            }

        }
    }


    val REQUEST_RECORD_AUDIO_PERMISSION = 200 // Puedes elegir cualquier número entero


    // Función para iniciar la grabación
    val startRecording: () -> Unit = {
        current = false
        goBottom()
        // Verificar y solicitar permisos de grabación de audio
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO_PERMISSION
            )
        }else{

            try {
                // Crea una instancia de MediaRecorder
                mediaRecorder = MediaRecorder()

                // Configura la fuente de audio y el formato de salida
                mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
                mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
                mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                mediaRecorder?.setAudioChannels(1)

                val bitDepth = 16
                val sampleRate = 44100
                val  bitRate = sampleRate * bitDepth

                mediaRecorder?.setAudioEncodingBitRate(bitRate)
                mediaRecorder?.setAudioSamplingRate(sampleRate)

                // Especifica la ruta del archivo de salida
                // Reemplaza "output.3gp" con el nombre y formato deseado
                mediaRecorder?.setOutputFile(context.externalCacheDir?.absolutePath + "/output.aac")


                // Prepara y comienza la grabación
                mediaRecorder?.prepare()
                mediaRecorder?.start()

                isRecording = true


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    }

    // Función para detener la grabación y enviar el audio
    val stopRecording: () -> Unit = {
        try {
            // Detiene y libera los recursos de MediaRecorder
            mediaRecorder?.stop()
            mediaRecorder?.reset()
            mediaRecorder?.release()
            mediaRecorder = null

            isRecording = false

            // Obtiene la ruta del archivo de audio
            val audioFile = File(context.externalCacheDir?.absolutePath, "output.aac")

            // Crea un objeto RequestBody a partir del archivo de audio
            val audioRequestBody = audioFile.asRequestBody("audio/aac".toMediaTypeOrNull())

            // Convierte el objeto RequestBody a MultipartBody.Part
            val audioPart = MultipartBody.Part.createFormData("file", audioFile.name, audioRequestBody)

            // Crea un objeto Message a ser enviado como parte del formulario
            val zonedDateTime = ZonedDateTime.now()
            val formattedDate = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

            // Convierte el objeto Message a RequestBody
            val codeRequestBody = DataRepository.getUser()!!.code.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val senderContactIdRequestBody = DataRepository.getUser()!!.id.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val receiverContactIdRequestBody = DataRepository.getUserPlus()!!.id.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val contentRequestBody = "ghghgh".toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val formattedDateRequestBody = formattedDate.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val typeRequestBody = 1.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

            GlobalScope.launch(Dispatchers.IO) {
                isloading = true
                // Realiza la llamada a la función de Retrofit
                val response = RetrofitInstance.api.uploadFile(
                    audioPart,
                    codeRequestBody,
                    senderContactIdRequestBody,
                    receiverContactIdRequestBody,
                    contentRequestBody,
                    formattedDateRequestBody,
                    typeRequestBody
                )


                if (response.isSuccessful){
                    withContext(Dispatchers.Main){
                        Toast.makeText(context, "Sent audio", Toast.LENGTH_SHORT).show()
                    }
                }
                isloading = false

            }

            current = true
            getMessages()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    val uploadFile: (Uri) -> Unit = {
        try {


            // Obtiene la ruta del archivo PDF a partir de la Uri
            val pdfFile = File(context.cacheDir, "output.pdf")

            // Copia el contenido de la Uri al archivo PDF
            context.contentResolver.openInputStream(it)?.use { input ->
                pdfFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            val pdfRequestBody = pdfFile.asRequestBody("application/pdf".toMediaTypeOrNull())

            val pdfPart = MultipartBody.Part.createFormData("file", pdfFile.name, pdfRequestBody)

            val zonedDateTime = ZonedDateTime.now()
            val formattedDate = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

            // Convierte el objeto Message a RequestBody
            val codeRequestBody = DataRepository.getUser()!!.code.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val senderContactIdRequestBody = DataRepository.getUser()!!.id.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val receiverContactIdRequestBody = DataRepository.getUserPlus()!!.id.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val contentRequestBody = "ghghgh".toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val formattedDateRequestBody = formattedDate.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val typeRequestBody = 2.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

            GlobalScope.launch(Dispatchers.IO) {
                isloading = true
                // Realiza la llamada a la función de Retrofit
                val response = RetrofitInstance.api.uploadFile(
                    pdfPart,
                    codeRequestBody,
                    senderContactIdRequestBody,
                    receiverContactIdRequestBody,
                    contentRequestBody,
                    formattedDateRequestBody,
                    typeRequestBody
                )


                if (response.isSuccessful){
                    withContext(Dispatchers.Main){
                        Toast.makeText(context, "Sent file", Toast.LENGTH_SHORT).show()
                    }
                }
                isloading = false

            }

            current = true
            getMessages()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val uploadImage: (Uri) -> Unit = { imageUri ->
        try {
            val imageFile = createImageFile(context)
            context.contentResolver.openInputStream(imageUri)?.use { input ->
                imageFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            val imageRequestBody = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("file", imageFile.name, imageRequestBody)

            val zonedDateTime = ZonedDateTime.now()
            val formattedDate = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

            val codeRequestBody = DataRepository.getUser()!!.code.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val senderContactIdRequestBody = DataRepository.getUser()!!.id.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val receiverContactIdRequestBody = DataRepository.getUserPlus()!!.id.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val contentRequestBody = "ghghgh".toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val formattedDateRequestBody = formattedDate.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val typeRequestBody = 3.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

            GlobalScope.launch(Dispatchers.IO) {
                isloading = true

                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", imageFile)
                val imagePartSecure = MultipartBody.Part.createFormData("file", imageFile.name, imageRequestBody)

                val response = RetrofitInstance.api.uploadFile(
                    imagePartSecure,
                    codeRequestBody,
                    senderContactIdRequestBody,
                    receiverContactIdRequestBody,
                    contentRequestBody,
                    formattedDateRequestBody,
                    typeRequestBody
                )

                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Sent image", Toast.LENGTH_SHORT).show()
                    }
                }
                isloading = false
            }

            current = true
            getMessages()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    val getContent = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        // Handle the selected file URI here
        uri?.let {
            // You can perform further actions with the selected file URI
            // For example, you can use it to upload the file
            uploadFile(it)
        }
    }


    val currentImageUri = remember { mutableStateOf<Uri?>(null) }

    val takePicture = rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success) {
            // The picture was taken successfully, you can perform further actions
            // For example, you can use the captured image URI to upload the file
            val imageUri = currentImageUri.value
            imageUri?.let {
                uploadImage(it)
            }
        }
    }

    val openCamera: () -> Unit = {
        try {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_RECORD_AUDIO_PERMISSION
                )
            }else{
                // Intenta lanzar la actividad de la cámara
                val imageFile = createImageFile(context)
                currentImageUri.value = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", imageFile)

                try {
                    takePicture.launch(currentImageUri.value)
                } catch (e: ActivityNotFoundException) {
                    // Maneja la excepción si la actividad de la cámara no está disponible
                    Toast.makeText(context, "Error: La cámara no está disponible", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }





    val postMessage:(message: String)->Unit = {
        if(it.isNotBlank()){
            val zonedDateTime = ZonedDateTime.now()
            val formattedDate = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            val m = Message(
                id = 0,
                code = DataRepository.getUser()!!.code,
                senderContactId = DataRepository.getUser()!!.id,
                receiverContactId = DataRepository.getUserPlus()!!.id,
                content = it,
                sentDate = formattedDate,
                type = 0
            )
            GlobalScope.launch(Dispatchers.IO) {
                val postResponse = RetrofitInstance.api.sendMessage(m)
                if (postResponse.isSuccessful){
                }
                goBottom()
            }
        }
    }

    LaunchedEffect(Unit){
        delay(1000)
        isloadScreen = false
        goBottom()
        getMessages()
    }

    var showDialog by remember { mutableStateOf(false) }

    if (isloadScreen){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (showDialog) {
                AlertDialog(
                    backgroundColor = MaterialTheme.colorScheme.background,
                    onDismissRequest = {
                        // Handle dismissal if needed
                        showDialog = false
                    },
                    title = {
                        androidx.compose.material.Text(
                            text = "Font Size Settings",
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    text = {
                        FontSelectionBox(selectedOption = selectedOption) {
                            selectedOption = it
                        }
                    },
                    confirmButton = {
                        ElevatedButton(
                            onClick = {
                                showDialog = false
                                sharedPreferences.edit().putInt(MainActivity.FONT_SIZE_CHAT, selectedOption!!.pos).apply()
                            },
                            colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                                containerColor = Color(0xff5a79ba)
                            ),
                            elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
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
                            colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            ),
                            elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                                defaultElevation = 5.dp
                            )
                        ){
                            androidx.compose.material.Text("Cancel", color = Color(0xff5a79ba))
                        }
                    }
                )
            }
            TopAppBar(
                navigationIcon = {
                    // Puedes personalizar el ícono de navegación según tus necesidades
                    IconButton(onClick = { navController.navigate("chats"); current = false;DataRepository.setCurrentScreenIndex(0) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = BlueSystem)
                    }
                },
                title = {
                    // Puedes agregar más elementos como la imagen de perfil y el nombre del usuario
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (DataRepository.getUserPlus()?.url.isNullOrBlank()) {
                            // Si la URL es nula o vacía, mostrar la imagen por defecto
                            Image(
                                painter = painterResource(id = R.drawable.user), // Reemplazar con tu recurso de imagen
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )
                        } else {

                            val painter = rememberImagePainter(
                                data = DataRepository.getUserPlus()?.url,
                                builder = {
                                    crossfade(true)
                                    placeholder(R.drawable.loading)
                                }
                            )
                            Image(
                                painter = painter, // Reemplazar con tu recurso de imagen
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.FillBounds
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = DataRepository.getUserPlus()!!.name, // Reemplazar con el nombre del usuario
                            style = MaterialTheme.typography.titleSmall,
                            color = BlueSystem
                        )
                    }
                },
                actions = {

                    IconButton(onClick = {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CALL_PHONE
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                context as Activity,
                                arrayOf(Manifest.permission.CALL_PHONE),
                                REQUEST_RECORD_AUDIO_PERMISSION
                            )
                        }else{
                            val callIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:600622680"))
                            context.startActivity(callIntent)
                        }

                    }) {
                        Icon(Icons.Default.PhoneEnabled, contentDescription = "Settings", tint = BlueSystem)
                    }
                    // Puedes agregar más acciones según tus necesidades
                    IconButton(onClick = { showDialog = true}) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = BlueSystem)
                    }
                },
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                elevation = 4.dp
            )


            LazyColumn(
                state = updatedLazyListState.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f)
                    .padding(horizontal = 10.dp)
            ) {
                items(messages) { message ->
                    MessageItem(message, selectedOption)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Input section
                Card(
                    modifier = Modifier
                        .weight(8f)
                        .padding(start = 12.dp, bottom = 12.dp, top = 5.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 10.dp
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                ) {
                    Row(
                        modifier = Modifier.padding(5.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if(!isRecording){
                            if(!isloading){
                                BasicTextField(
                                    value = newMessage,
                                    onValueChange = {
                                        newMessage = it
                                    },
                                    textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.primary),
                                    singleLine = false,
                                    cursorBrush = SolidColor(BlueSystem), // Cambiamos el color del cursor a rojo
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Send,
                                        keyboardType = KeyboardType.Text
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onSend = {
                                            //keyboardController?.hide()
                                            if (newMessage.text.isNotEmpty()) {
                                                postMessage(newMessage.text)
                                                newMessage = TextFieldValue()
                                            }

                                        }
                                    ),
                                    modifier = Modifier
                                        .weight(7f)
                                        .padding(end = 8.dp)
                                )

                                Row(
                                    modifier = Modifier.weight(3f),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(onClick = { getContent.launch("application/pdf")}) {
                                        Icon(imageVector = Icons.Filled.AttachFile, contentDescription = null, tint =  MaterialTheme.colorScheme.outlineVariant )
                                    }
                                    IconButton(onClick = { openCamera() }) {
                                        Icon(imageVector = Icons.Filled.CameraAlt, contentDescription = null, tint = MaterialTheme.colorScheme.outlineVariant  )
                                    }
                                }
                            }else{
                                LinearProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth() ,
                                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.9f), // Ajusta el nivel de opacidad aquí
                                    trackColor = BlueSystem.copy(alpha = 0.1f), // Ajusta el nivel de opacidad aquí
                                )
                                IconButton(onClick = { /*TODO*/ }) {
                                    Icon(imageVector = Icons.Filled.CameraAlt, contentDescription = null, tint = MaterialTheme.colorScheme.secondaryContainer )
                                }
                            }
                        }else{
                            LottieAnimation(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(0.8f),
                                composition = composition,
                                progress = { progress }
                            )
                        }
                    }
                }
                Card(
                    modifier = Modifier
                        .weight(2f)
                        .padding(start = 12.dp, bottom = 12.dp, top = 5.dp, end = 12.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 10.dp
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                ) {
                    Row(
                        modifier = Modifier.padding(5.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (newMessage.text.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    //keyboardController?.hide()
                                    if (newMessage.text.isNotEmpty()) {
                                        if (newMessage.text.isNotEmpty()) {
                                            postMessage(newMessage.text)
                                            newMessage = TextFieldValue()
                                        }
                                    }
                                }
                            ) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = BlueSystem)
                            }
                        }else{
                            IconButton(
                                onClick = {
                                    if(!isRecording) startRecording()
                                    else stopRecording()
                                }
                            ) {
                                if(!isRecording) Icon(imageVector = Icons.Outlined.MicNone, contentDescription = "Send", tint = BlueSystem)
                                else  Icon(imageVector = Icons.Filled.Mic, contentDescription = "Send", tint = BlueSystem)
                            }
                        }
                    }

                }
            }


        }
    }

}

@Composable
fun MessageItem(message: Message, selectedOption: OptionSize?) {
    val isSentByUser = message.senderContactId == DataRepository.getUser()!!.id // Replace with the actual user's contact ID
    val horaYMinuto = obtenerHoraYMinuto(message.sentDate)

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.audioprogess))
    val progress by animateLottieCompositionAsState(composition = composition, iterations = LottieConstants.IterateForever, speed = 1f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = if (isSentByUser) Arrangement.End else Arrangement.Start
    ) {
        // Message bubble
        if (!isSentByUser){
            if (DataRepository.getUserPlus()?.url.isNullOrBlank()) {
                Image(
                    painter = painterResource(id = R.drawable.user),
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                )
            } else {

                val painter = rememberImagePainter(
                    data = DataRepository.getUserPlus()?.url,
                    builder = {
                        crossfade(true)
                        placeholder(R.drawable.loading)
                    }
                )
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.FillBounds
                )
            }


        }

        if(message.type == 0){ //Texto
           Card(
               elevation = CardDefaults.cardElevation(
                   defaultElevation = 5.dp
               ),
               colors = CardDefaults.cardColors(
                   containerColor = if (isSentByUser) BlueSystem else Color.DarkGray,
               ),
               shape = RoundedCornerShape(8.dp),
               modifier = Modifier
                   .padding(8.dp)
           ) {
               Column(
                   modifier = Modifier
                       .padding(8.dp)
               ) {
                   Text(
                       text = message.content,
                       style = MaterialTheme.typography.labelLarge.copy(fontSize = selectedOption!!.size.sp),
                       color = Color.White
                   )
                   Row(
                       modifier = Modifier.padding(1.dp),
                       horizontalArrangement = Arrangement.End
                   ) {
                       if(horaYMinuto!=null) {
                           val (hora, minuto) = horaYMinuto
                           Text(text = "${hora}:${minuto}", color = Color.LightGray,style = MaterialTheme.typography.bodySmall)
                       }else{
                           Text(text = "00:00", color = Color.LightGray,style = MaterialTheme.typography.bodySmall)
                       }
                   }
               }
           }
        }
        if(message.type == 1){ //Audio
           Card(
               elevation = CardDefaults.cardElevation(
                   defaultElevation = 5.dp
               ),
               colors = CardDefaults.cardColors(
                   containerColor = if (isSentByUser) BlueSystem else Color.DarkGray,
               ),
               shape = RoundedCornerShape(8.dp),
               modifier = Modifier
                   .height(95.dp)
                   .padding(8.dp)
           ) {
               Column(
                   modifier = Modifier
                       .padding(8.dp)
               ) {
                   val player = AudioPlayer(message.content)
                   var isPlaying by remember {
                       mutableStateOf(false)
                   }
                   Row(
                       horizontalArrangement = Arrangement.Center,
                       verticalAlignment = Alignment.CenterVertically
                   ){

                       if(!isPlaying){
                           Divider(modifier = Modifier.width(260.dp), thickness = 2.dp, color = Color.LightGray)
                           IconButton(onClick = {
                               player.seekTo(0)

                               player.addListener(object : Player.Listener {
                                   override fun onPlaybackStateChanged(state: Int) {
                                       super.onPlaybackStateChanged(state)
                                       isPlaying = state == Player.STATE_READY
                                   }
                               })

                               player.play()
                           }) {
                               Icon(imageVector = Icons.Filled.PlayCircle, contentDescription = null)
                           }
                       }else{

                           LottieAnimation(
                               modifier = Modifier
                                   .width(260.dp)
                                   .padding(end = 8.dp),
                               composition = composition,
                               progress = { progress }
                           )

                           IconButton(onClick = {
                               player.pause()
                               isPlaying = false
                           }) {
                               Icon(imageVector = Icons.Filled.StopCircle, contentDescription = null)
                           }
                       }
                   }
                   Row(
                       modifier = Modifier.padding(1.dp),
                       horizontalArrangement = Arrangement.End
                   ) {
                       if(horaYMinuto!=null) {
                           val (hora, minuto) = horaYMinuto
                           Text(text = "${hora}:${minuto}", color = Color.LightGray,style = MaterialTheme.typography.bodySmall)
                       }else{
                           Text(text = "00:00", color = Color.LightGray,style = MaterialTheme.typography.bodySmall)
                       }
                   }
               }
           }
        }
        if (message.type == 2){ //File
            val context = LocalContext.current

            Card(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 5.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSentByUser) BlueSystem else Color.DarkGray,
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .height(200.dp)
                    .clickable { downloadAndOpenPdf(context, message.content) }
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,

                ) {
                    Row {
                        Image(
                            painter = painterResource(id = R.drawable.pdf_icon),
                            contentDescription = null,
                            modifier = Modifier.height(150.dp)

                        )
                    }
                    Row(
                        modifier = Modifier.padding(1.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if(horaYMinuto!=null) {
                            val (hora, minuto) = horaYMinuto
                            Text(text = "${hora}:${minuto}", color = Color.LightGray,style = MaterialTheme.typography.bodySmall)
                            Text(text = "   ${getNamePDF(message.content)}", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)

                        }else{
                            Text(text = "00:00", color = Color.LightGray,style = MaterialTheme.typography.bodySmall)
                            Text(text = "   ${getNamePDF(message.content)}", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)

                        }
                    }
                }

            }
        }
        if (message.type == 3){ //File
            val context = LocalContext.current


            Card(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 5.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSentByUser) BlueSystem else Color.DarkGray,
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .height(150.dp)
                    .width(160.dp)
                    .clickable {
                        downloadAndOpenImage(
                            context = context,
                            imageUrl = message.content
                        )
                    }
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(8.dp)

                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.image_icon),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth().height(100.dp)

                        )
                    }
                    Row(
                        modifier = Modifier.padding(1.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if(horaYMinuto!=null) {
                            val (hora, minuto) = horaYMinuto
                            Text(text = "${hora}:${minuto}", color = Color.LightGray,style = MaterialTheme.typography.bodySmall)
                        }else{
                            Text(text = "00:00", color = Color.LightGray,style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))

        // Sender image
        if (isSentByUser){
            if (DataRepository.getUser()?.url.isNullOrBlank()) {
                Image(
                    painter = painterResource(id = R.drawable.user),
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                )
            } else {

                val painter = rememberImagePainter(
                    data = DataRepository.getUser()?.url,
                    builder = {
                        crossfade(true)
                        placeholder(R.drawable.loading)
                    }
                )
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.FillBounds
                )
            }
        }
    }
}

fun getNamePDF(url: String): String {
    // Crear un objeto Uri para facilitar el manejo de la URL
    val uri = Uri.parse(url)

    // Obtener el último segmento de la URL, que será el nombre del archivo
    val decodedFilename = uri.lastPathSegment ?: ""

    // Decodificar el nombre del archivo
    val decodedName = Uri.decode(decodedFilename)

    // Dividir el nombre del archivo usando el carácter '_' como separador y obtener el último elemento
    val name = decodedName.split("_").lastOrNull() ?: ""

    return name
}



// Function to download and open PDF
@OptIn(DelicateCoroutinesApi::class)
fun downloadAndOpenPdf(context: Context, pdfUrl: String) {
    GlobalScope.launch(Dispatchers.IO) {
        try {
            // Download the PDF file
            val url = URL(pdfUrl)
            val connection = url.openConnection()
            connection.connect()

            // Create a temporary file to save the PDF with a fixed name
            val fileName = "temp_pdf.pdf" // Nombre fijo del archivo
            val tempDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val tempFile = File(tempDir, fileName)

            // Save the PDF to the temporary file
            val input = connection.getInputStream()
            val output = FileOutputStream(tempFile)
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } != -1) {
                output.write(buffer, 0, bytesRead)
            }
            output.close()
            input.close()

            // Open the PDF viewer with the downloaded file
            openPdfViewer(context, Uri.fromFile(tempFile))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}


// Function to open PDF viewer with FileProvider
fun openPdfViewer(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        val contentUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", File(uri.path!!))
        setDataAndType(contentUri, "application/pdf")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(intent)
}


@OptIn(DelicateCoroutinesApi::class)
fun downloadAndOpenImage(context: Context, imageUrl: String) {
    GlobalScope.launch(Dispatchers.IO) {
        try {
            // Download the image file
            val url = URL(imageUrl)
            val connection = url.openConnection()
            connection.connect()

            // Create a temporary file to save the image with a fixed name
            val fileName = "temp_image.jpg" // Nombre fijo del archivo
            val tempDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val tempFile = File(tempDir, fileName)

            // Save the image to the temporary file
            val input = connection.getInputStream()
            val output = FileOutputStream(tempFile)
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } != -1) {
                output.write(buffer, 0, bytesRead)
            }
            output.close()
            input.close()

            // Open the image viewer with the downloaded file
            openImageViewer(context, Uri.fromFile(tempFile))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

// Function to open an image viewer with FileProvider
fun openImageViewer(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "image/*")
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        val contentUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", File(uri.path!!))
        setDataAndType(contentUri, "image/*")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(intent)
}


fun obtenerHoraYMinuto(fechaString: String): Pair<String, String>? {
    ///2024-02-01T16:47:40.5631196
    return try {
        val hora = fechaString.split("T")[1].split(".")[0].split(":")[0]
        val minuto = fechaString.split("T")[1].split(".")[0].split(":")[1]

        Pair(hora, minuto)
    } catch (e: Exception) {
        // Manejar el caso en que la cadena de fecha no se pueda analizar correctamente
        null
    }
}


@Composable
fun AudioPlayer(url: String): ExoPlayer {
    val context = LocalContext.current
    val player = remember { ExoPlayer.Builder(context).build() }

    DisposableEffect(player) {
        player.setMediaItem(MediaItem.fromUri(Uri.parse(url)))
        player.prepare()
        onDispose {
            player.release()
        }
    }
    PlayerView(context).apply {
        this.player = player
    }

    return player
}


data class OptionSize(val label: String, val size: Int, val pos: Int)
@Composable
fun FontSelectionBox(selectedOption: OptionSize?, onOptionSelected: (OptionSize) -> Unit) {
    val options = listOf(
        OptionSize("Small",  12, 0),
        OptionSize("Medium",  17, 1),
        OptionSize("Large",  24, 2)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .selectable(
                        selected = (selectedOption == option),
                        onClick = {
                            onOptionSelected(option)
                        }
                    )
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (selectedOption == option),
                    colors = RadioButtonDefaults.colors(selectedColor = BlueSystem, unselectedColor = MaterialTheme.colorScheme.primary),
                    onClick = {
                        onOptionSelected(option)
                    }
                )

                androidx.compose.material.Text(
                    text = option.label,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}