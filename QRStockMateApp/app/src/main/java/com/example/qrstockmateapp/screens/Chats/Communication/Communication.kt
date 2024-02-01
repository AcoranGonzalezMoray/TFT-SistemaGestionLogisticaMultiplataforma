package com.example.qrstockmateapp.screens.Chats.Communication

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.qrstockmateapp.api.models.Communication
import com.example.qrstockmateapp.api.services.RetrofitInstance
import com.example.qrstockmateapp.navigation.repository.DataRepository
import com.example.qrstockmateapp.ui.theme.BlueSystem
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


@OptIn(DelicateCoroutinesApi::class)
@Composable
fun CommunicatioScreen(navController: NavController){
    var communicationList  by remember{ mutableStateOf(emptyList<Communication>()) }

    var newMessage by remember { mutableStateOf(TextFieldValue()) }

    var selectedColor by remember { mutableStateOf(Color.Green) }
    var isDropdownExpanded by remember { mutableStateOf(false) }


    val lazyListState = rememberLazyListState()
    val updatedLazyListState = rememberUpdatedState(lazyListState)

    val currentContext = LocalContext.current

    val loadCommunication: ()->Unit = {
        GlobalScope.launch(Dispatchers.IO) {
            val response = RetrofitInstance.api.getCommunicationsByCode(DataRepository.getUser()!!.code)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    communicationList = body
                }
            }
        }
    }

    val postCommunication: ()->Unit = {
        if(newMessage.text.isNotBlank()){
            val zonedDateTime = ZonedDateTime.now()
            val color = when(selectedColor){
                Color.Green -> {0}
                Color.Yellow -> {1}
                else -> {2}
            }
            val formattedDate = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            val communication = Communication(
                id= 0,
                code= DataRepository.getUser()!!.code,
                content= "${color};"+newMessage.text,
                sentDate= formattedDate
            )
            GlobalScope.launch(Dispatchers.IO) {
                val response = RetrofitInstance.api.postCommunication(communication)
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main){
                        Toast.makeText(currentContext, "Sent communication", Toast.LENGTH_SHORT).show()
                    }
                    newMessage = TextFieldValue()
                    loadCommunication()
                }
            }
        }else {
            Toast.makeText(currentContext, "You should not leave empty fields", Toast.LENGTH_SHORT).show()

        }
    }

    val goBottom:()->Unit = {
        MainScope().launch {
            if (communicationList.isNotEmpty()) {
                updatedLazyListState.value.scrollToItem(communicationList.size)
            }
        }
    }

    LaunchedEffect(Unit){
        loadCommunication()
        goBottom()
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        if(DataRepository.getUser()!!.role==0){
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier
                        .padding(12.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 10.dp
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                ) {
                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        modifier = Modifier.background(color = MaterialTheme.colorScheme.secondaryContainer),
                        onDismissRequest = {
                            isDropdownExpanded = false
                        }
                    ) {
                        DropdownMenuItem(onClick = { selectedColor = Color.Green }) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Low", color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.padding(5.dp))
                                Box(
                                    modifier = Modifier
                                        .background(Color.Green, CircleShape)
                                        .size(12.dp)
                                )
                            }
                        }
                        DropdownMenuItem(onClick = { selectedColor = Color.Yellow }) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Text("Medium", color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.padding(5.dp))
                                Box(
                                    modifier = Modifier
                                        .background(Color.Yellow, CircleShape)
                                        .size(12.dp)
                                )
                            }
                        }
                        DropdownMenuItem(onClick = { selectedColor = Color.Red }) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("High", color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.padding(5.dp))
                                Box(
                                    modifier = Modifier
                                        .background(Color.Red, CircleShape)
                                        .size(12.dp)
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(selectedColor, CircleShape)
                            .size(24.dp)
                            .clickable {
                                isDropdownExpanded = true
                            }
                    )
                }

                Card(
                    modifier = Modifier
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
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

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

                                }
                            ),
                            modifier = Modifier
                                .weight(9f)
                                .padding(end = 8.dp)
                        )
                        IconButton(onClick = {postCommunication()}, modifier = Modifier.weight(1f)) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = BlueSystem)
                        }
                    }

                }
            }
        }

       if(communicationList.isNotEmpty()){
           LazyColumn(
               state = updatedLazyListState.value,
               modifier = Modifier
                   .background(MaterialTheme.colorScheme.background)
           ) {

               items(communicationList) { communication ->
                   CommunicationCard(communication = communication)
               }
               item{
                   Spacer(modifier = Modifier.padding(bottom = 80.dp))
               }
           }
       }else {
           Card(
               modifier = Modifier.padding(12.dp),
               elevation = CardDefaults.cardElevation(
                   defaultElevation = 5.dp
               ),
               colors = CardDefaults.cardColors(
                   containerColor = MaterialTheme.colorScheme.secondaryContainer,
               ),
           ) {
               Text(
                   text = "The company has not sent any communications yet.",
                   color = MaterialTheme.colorScheme.primary,
                   modifier = Modifier
                       .padding(16.dp)
               )
           }
       }
    }
}


@Composable
fun CommunicationCard(communication: Communication) {
    val color = when(communication.content.split(";")[0].toInt()){
         0 ->{Color.Green}
         1-> {Color.Yellow}
        else -> {Color.Red}
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { /* Acción al hacer clic en la tarjeta */ },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
        ) {
            // Contenido de la tarjeta
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = DataRepository.getCompany()!!.name, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .background(color, CircleShape)
                        .size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = communication.content.split(";")[1])
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Sent on: ${communication.sentDate.split("T")[0].replace("-", "/")} - ${communication.sentDate.split("T")[1].split(".")[0]}")
        }
    }
}