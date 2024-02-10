package com.example.qrstockmateapp.screens.Chats

import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Badge
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.qrstockmateapp.MainActivity
import com.example.qrstockmateapp.R
import com.example.qrstockmateapp.api.models.User
import com.example.qrstockmateapp.api.services.RetrofitInstance
import com.example.qrstockmateapp.navigation.repository.DataRepository
import com.example.qrstockmateapp.ui.theme.BlueSystem
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun ChatsScreen(navController: NavController, sharedPreferences: SharedPreferences) {
    var employees by remember{ mutableStateOf(emptyList<User>()) }



    val getMessages: () -> Unit = {
        GlobalScope.launch(Dispatchers.IO) {
            val response = RetrofitInstance.api.getMessageByCode(DataRepository.getUser()!!.code)
            if (response.isSuccessful) {
                var messages = response.body()

                if (!messages.isNullOrEmpty()) {
                    employees = DataRepository.getEmployees()!!.filter { user: User -> user.id!= DataRepository.getUser()!!.id }
                    // Obtener todos los IDs de contactos involucrados en los mensajes
                    messages = messages.filter { message ->
                        (message.receiverContactId == DataRepository.getUser()?.id) || (message.senderContactId == DataRepository.getUser()?.id)
                    }
                    val allContactIds = messages.flatMap { listOf(it.senderContactId, it.receiverContactId) }.distinct()
                    // Filtrar empleados basándose en si han participado en conversaciones contigo
                    employees = employees.filter { employee ->
                        employee.id in allContactIds
                    }
                    DataRepository.setNewMessages(messages.size )
                    sharedPreferences.edit().putInt(MainActivity.NEW_MESSAGES, messages.size ).apply()
                }else {
                    sharedPreferences.edit().putInt(MainActivity.NEW_MESSAGES, 0).apply()
                    DataRepository.setNewMessages(0)
                    employees = emptyList()
                }
            }
        }
    }

    val deleteMessages: (user: User) -> Unit = {
        GlobalScope.launch(Dispatchers.IO) {
            val response = RetrofitInstance.api.deleteConversation("${it.id};${DataRepository.getUser()!!.id}")
            if (response.isSuccessful) {
                //getMessages()
            }
        }
    }

    LaunchedEffect(Unit){
        getMessages()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if(employees.isNotEmpty()){
            LazyColumn {
                items(employees) { employee ->
                    EmployeeItem(employee, navController, onDelete = {user ->
                        deleteMessages(user)
                    })
                }
            }
        }else {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment =  Alignment.CenterVertically
            ) {
                Text(
                    text = "Does not have any active chat at this time",
                    color = MaterialTheme.colorScheme.outlineVariant,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EmployeeItem(employee: User, navController: NavController, onDelete:(user: User)->Unit) {
    val context = LocalContext.current
    val dismissThreshold = 0.95f
    var visible by remember {
        mutableStateOf(true)
    }
    val dismissState = rememberDismissState()

    val new = DataRepository.getListNewMessage()?.filter { m -> m == employee.id  }
    Log.d("NUEVOS", "${DataRepository.getListNewMessage().toString()}")
    if(visible){
        SwipeToDismiss(
            state = dismissState,
            dismissThresholds = {
                FractionalThreshold(dismissThreshold)
            },
            dismissContent = {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 10.dp
                    )
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row {
                            // Image
                            if (employee.url.isNullOrBlank()) {
                                // Si la URL es nula o vacía, mostrar la imagen por defecto
                                Image(
                                    painter = painterResource(id = R.drawable.user), // Replace with your actual image resource
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                        .shadow(4.dp, CircleShape)
                                )
                            } else {

                                val painter = rememberImagePainter(
                                    data = employee.url,
                                    builder = {
                                        crossfade(true)
                                        placeholder(R.drawable.loading)
                                    }
                                )
                                Image(
                                    painter = painter, // Replace with your actual image resource
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                        .shadow(4.dp, CircleShape),
                                    contentScale = ContentScale.FillBounds
                                )
                            }

                            // Space between image and text
                            Spacer(modifier = Modifier.width(16.dp))

                            // Employee details
                            Column {
                                Text(text = employee.name, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                                Text(text = employee.email, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer)
                                ) {
                                    Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = employee.phone, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }

                        ElevatedButton(
                            onClick = {
                                DataRepository.setUserPlus(employee)
                                navController.navigate("chat")
                            },
                            colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                                containerColor = BlueSystem
                            ),
                            elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                                defaultElevation = 5.dp
                            )
                        ){
                            Icon(imageVector = Icons.Filled.NearMe, contentDescription = null, tint =  Color.White )
                            if(new!!.isNotEmpty()){
                                    Badge(
                                        content = { Text(text = "${new.size}", color = Color.White) },
                                        modifier = Modifier
                                            .padding(horizontal = 1.dp)
                                    )
                            }
                        }
                    }
                }
            },
            directions = setOf(DismissDirection.EndToStart), // Solo permite swipe de derecha a izquierda
            background = {
                val color by animateColorAsState(
                    when (dismissState.targetValue) {
                        DismissValue.Default -> MaterialTheme.colorScheme.background
                        DismissValue.DismissedToEnd -> Color.Green
                        DismissValue.DismissedToStart -> Color.Red
                    }, label = ""
                )
                if (dismissState.isAnimationRunning) {
                    DisposableEffect(Unit) {
                        onDispose {
                            when (dismissState.targetValue) {
                                DismissValue.DismissedToStart -> {
                                    Toast.makeText(context, "Deleting conversation", Toast.LENGTH_SHORT).show()
                                    onDelete(employee)
                                    visible = false
                                }
                                else -> {
                                    return@onDispose
                                }
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = color)
                        .padding(16.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }
        )
        Divider(modifier = Modifier.padding(start = 16.dp, end = 16.dp))
    }
}
