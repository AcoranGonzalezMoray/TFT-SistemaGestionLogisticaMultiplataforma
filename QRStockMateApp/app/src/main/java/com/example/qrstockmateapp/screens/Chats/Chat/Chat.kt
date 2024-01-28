package com.example.qrstockmateapp.screens.Chats.Chat

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhoneEnabled
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.outlined.MicNone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.qrstockmateapp.R
import com.example.qrstockmateapp.api.models.Message
import com.example.qrstockmateapp.api.services.RetrofitInstance
import com.example.qrstockmateapp.navigation.repository.DataRepository
import com.example.qrstockmateapp.ui.theme.BlueSystem
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
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
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavController) {

    var newMessage by remember { mutableStateOf(TextFieldValue()) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val lazyListState = rememberLazyListState()
    val updatedLazyListState = rememberUpdatedState(lazyListState)

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.audio))
    val progress by animateLottieCompositionAsState(composition = composition, iterations = LottieConstants.IterateForever, speed = 1f)


    // Variable para rastrear si el botón de grabación está presionado
    var isRecording by remember { mutableStateOf(false) }
    var mediaRecorder: MediaRecorder? = null

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
            while (current){
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
                Log.d("sigue", "${messages}")
                delay(500) // Espera 500 milisegundos antes de realizar la siguiente solicitud
            }

        }
    }
    val context = LocalContext.current

    val REQUEST_RECORD_AUDIO_PERMISSION = 200 // Puedes elegir cualquier número entero


    // Función para iniciar la grabación
    val startRecording: () -> Unit = {
        current = false

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
                mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
                mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

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
            val audioPart = MultipartBody.Part.createFormData("audio", audioFile.name, audioRequestBody)

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
                val response = RetrofitInstance.api.uploadAudio(
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
    val postMessage:(message: String)->Unit = {
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
                Log.d("OK", "MESSAGE")
            }
            goBottom()
        }
    }

    LaunchedEffect(Unit){
        goBottom()
        getMessages()
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            navigationIcon = {
                // Puedes personalizar el ícono de navegación según tus necesidades
                IconButton(onClick = { navController.navigate("chats"); current = false }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = BlueSystem)
                }
            },
            title = {
                // Puedes agregar más elementos como la imagen de perfil y el nombre del usuario
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.user), // Reemplazar con tu recurso de imagen
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = DataRepository.getUserPlus()!!.name, // Reemplazar con el nombre del usuario
                        style = MaterialTheme.typography.headlineSmall,
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
                IconButton(onClick = { /* Handle settings */ }) {
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
                .padding(10.dp)
        ) {
            items(messages) { message ->
                MessageItem(message)
            }
        }

        // Input section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
        ) {
            Row(
                modifier = Modifier.padding(5.dp),
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
                                    if (newMessage.text.isNotEmpty()) {
                                        postMessage(newMessage.text)
                                        newMessage = TextFieldValue()
                                    }
                                    keyboardController?.hide()
                                }
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        )
                    }else{
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth() ,
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.9f), // Ajusta el nivel de opacidad aquí
                            trackColor = BlueSystem.copy(alpha = 0.1f), // Ajusta el nivel de opacidad aquí
                        )
                    }
                }else{
                    LottieAnimation(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        composition = composition,
                        progress = { progress }
                    )
                }
                if (newMessage.text.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            if (newMessage.text.isNotEmpty()) {
                                if (newMessage.text.isNotEmpty()) {
                                    postMessage(newMessage.text)
                                    newMessage = TextFieldValue()
                                }
                                keyboardController?.hide()
                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = BlueSystem)
                    }
                }else{
                    IconButton(
                        onClick = {
                            if(!isRecording) startRecording()
                            else stopRecording()
                        }
                    ) {
                        if(isRecording==false) Icon(imageVector = Icons.Outlined.MicNone, contentDescription = "Send", tint = BlueSystem)
                        else  Icon(imageVector = Icons.Filled.Mic, contentDescription = "Send", tint = BlueSystem)
                    }
                }
            }
        }


    }
}

@Composable
fun MessageItem(message: Message) {
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
            Image(
                painter = painterResource(id = R.drawable.user),
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
            )
        }

       if(message.type == 0){
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
                       style = MaterialTheme.typography.labelLarge,
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
       }else {
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
                                       Log.d("AudioPlayer", "Playback state changed: $state, isPlaying: $isPlaying")
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

        Spacer(modifier = Modifier.width(8.dp))

        // Sender image
        if (isSentByUser){
            Image(
                painter = painterResource(id = R.drawable.user),
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
            )
        }
    }
}

fun obtenerHoraYMinuto(fechaString: String): Pair<Int, Int>? {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS")

    try {
        val fecha = LocalDateTime.parse(fechaString, formatter)
        val hora = fecha.hour
        val minuto = fecha.minute

        return Pair(hora, minuto)
    } catch (e: Exception) {
        // Manejar el caso en que la cadena de fecha no se pueda analizar correctamente
        return null
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

