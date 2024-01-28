package com.example.qrstockmateapp.screens.Chats

import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.rememberDismissState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.qrstockmateapp.MainActivity
import com.example.qrstockmateapp.R
import com.example.qrstockmateapp.api.models.User
import com.example.qrstockmateapp.api.services.RetrofitInstance
import com.example.qrstockmateapp.navigation.repository.DataRepository
import com.example.qrstockmateapp.ui.theme.BlueSystem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun ChatsScreen(navController: NavController, sharedPreferences: SharedPreferences) {
    var employees by remember{ mutableStateOf(DataRepository.getEmployees()!!.filter { user: User -> user.id!= DataRepository.getUser()!!.id  }) }



    val getMessages: () -> Unit = {
        GlobalScope.launch(Dispatchers.IO) {
            val response = RetrofitInstance.api.getMessageByCode(DataRepository.getUser()!!.code)
            if (response.isSuccessful) {
                var messages = response.body()

                if (messages != null && messages.isNotEmpty()) {
                    // Obtener todos los IDs de contactos involucrados en los mensajes
                    messages = messages.filter { message ->
                        (message.receiverContactId == DataRepository.getUser()?.id) || (message.senderContactId == DataRepository.getUser()?.id)
                    }
                    val allContactIds = messages.flatMap { listOf(it.senderContactId, it.receiverContactId) }.distinct()
                    Log.d("ContactIDs", "Todos los IDs de contactos: $allContactIds")
                    Log.d("EmployeesBeforeFilter", "Empleados antes del filtro: $employees")

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
                employees = DataRepository.getEmployees()!!.filter { user: User -> user.id!= DataRepository.getUser()!!.id  }
                getMessages()
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
        LazyColumn {
            items(employees) { employee ->
                EmployeeItem(employee, navController, onDelete = {user ->
                    deleteMessages(user)
                })
                Divider(modifier = Modifier.padding(start = 16.dp, end = 16.dp))
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun EmployeeItem(employee: User, navController: NavController, onDelete:(user: User)->Unit) {
    val context = LocalContext.current
    val totalDismissDistance = 300 // Ajusta según el ancho del card u otro criterio
    val dismissThreshold = 0.95f

    val dismissState = rememberDismissState()
    SwipeToDismiss(
        state = dismissState,
        dismissThresholds = { direction ->
            FractionalThreshold(dismissThreshold)
        },
        dismissContent = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
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
                        Image(
                            painter = painterResource(id = R.drawable.user), // Replace with your actual image resource
                            contentDescription = null,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .shadow(4.dp, CircleShape)
                        )

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
                }
            )
            if (dismissState.isAnimationRunning) {
                DisposableEffect(Unit) {
                    onDispose {
                        when (dismissState.targetValue) {
                            DismissValue.DismissedToStart -> {
                                Toast.makeText(context, "Deleting conversation", Toast.LENGTH_SHORT).show()
                                onDelete(employee)
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
}
