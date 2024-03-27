package com.example.qrstockmateapp.screens.Home.ManageUser


import android.util.Log
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.qrstockmateapp.R
import com.example.qrstockmateapp.api.models.Transaction
import com.example.qrstockmateapp.api.models.User
import com.example.qrstockmateapp.api.models.userRoleToString
import com.example.qrstockmateapp.api.services.RetrofitInstance
import com.example.qrstockmateapp.navigation.repository.DataRepository
import com.example.qrstockmateapp.ui.theme.BlueSystem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ManageUserScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var employees by remember { mutableStateOf(emptyList<User>()) }
    var isloading by remember { mutableStateOf(false) }

    var users = DataRepository.getEmployees()
    employees = users ?: emptyList()

    val loadEmployees:()->Unit = {
        GlobalScope.launch(Dispatchers.IO) {
            isloading = true
            val company = DataRepository.getCompany()
            if(company!=null){
                val employeesResponse = RetrofitInstance.api.getEmployees(company)
                if (employeesResponse.isSuccessful) {
                    val employeesIO = employeesResponse.body()
                    if(employeesIO!=null){
                        DataRepository.setEmployees(employeesIO)
                        users = DataRepository.getEmployees()
                        employees = users ?: emptyList()
                    }
                } else Log.d("compnayError", "error")
            }
            delay(1100)
            isloading = false
        }
    }

    LaunchedEffect(Unit){
        loadEmployees()
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isloading,
        onRefresh = loadEmployees
    )

    val customTextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        textColor = MaterialTheme.colorScheme.primary,
        backgroundColor = MaterialTheme.colorScheme.outline,
        cursorColor =  BlueSystem,
        focusedBorderColor =  BlueSystem,
        focusedLabelColor = BlueSystem,
        unfocusedBorderColor =  BlueSystem
    )
    // Filtrar la lista de empleados en función de la consulta de búsqueda
    val filteredEmployees = if (searchQuery.isEmpty()) {
        employees
    } else {
        employees.filter { user ->
            user.name.contains(searchQuery, ignoreCase = true) ||
                    user.email.contains(searchQuery, ignoreCase = true) ||
                    user.phone.contains(searchQuery, ignoreCase = true) ||
                    userRoleToString(user.role).contains(searchQuery, ignoreCase = true)
        }
    }
    Box(Modifier.pullRefresh(pullRefreshState)) {
        PullRefreshIndicator(
            refreshing = isloading,
            state = pullRefreshState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(1f),
            backgroundColor =  MaterialTheme.colorScheme.background,
            contentColor = BlueSystem
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search", color = MaterialTheme.colorScheme.outlineVariant)  },
                colors = customTextFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )


            if(filteredEmployees.isNotEmpty()){
                // Lista de usuarios filtrada
                LazyColumn {
                    items(filteredEmployees) { employee ->
                        UserListItem(user = employee, navController,loadEmployees)
                    }
                    item {
                        Spacer(modifier = Modifier.fillMaxWidth().height(48.dp))
                    }
                }
            }else {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment =  Alignment.CenterVertically
                ) {
                    Text(
                        text = "There are no employees available ir in the search made",
                        color = MaterialTheme.colorScheme.outlineVariant,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

        }
    }

}

@Composable
fun UserListItem(user: User, navController: NavController,loadEmployees: () -> Unit) {
    //Función Comprobar Inactividad
    fun checkDisabled(): Boolean {
        var result = false
        if (user != null){
            result = user.email.contains(":")
        }
        return result
    }

    var disabled by remember { mutableStateOf(checkDisabled()) }

    //Función Hacer Inactivo
    val disableUser:()->Unit = {
        GlobalScope.launch(Dispatchers.IO) {
            if(user!=null){
                user.email = "inactivo:" + user.email
                val updateUser = RetrofitInstance.api.updateUser(user)
                var me = DataRepository.getUser()
                if(me!=null){
                    val zonedDateTime = ZonedDateTime.now()
                    val formattedDate = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    val addTransaccion = RetrofitInstance.api.addHistory(
                        Transaction(0,me.name,me.code, "The user with name ${user.name} and ID ${user.id} has been suspended",
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
                if (updateUser.isSuccessful) {
                    loadEmployees()
                    disabled = true
                } else Log.d("compnayError", "error")
            }
        }
    }

    //Función Hacer Activo
    val enableUser:()->Unit = {
        GlobalScope.launch(Dispatchers.IO) {
            if(user!=null){
                user.email = user.email.split(':')[1]
                val updateUser = RetrofitInstance.api.updateUser(user)
                var me = DataRepository.getUser()
                if(me!=null){
                    val zonedDateTime = ZonedDateTime.now()
                    val formattedDate = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    val addTransaccion = RetrofitInstance.api.addHistory(
                        Transaction(0,me.name,me.code, "The user with name ${user.name} and ID ${user.id} has been reactivated",
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
                if (updateUser.isSuccessful) {
                    loadEmployees()
                    disabled = false
                } else Log.d("compnayError", "error")
            }
        }
    }
    var isloading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit){
        checkDisabled()
        delay(1200)
        isloading = false
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        shape = RoundedCornerShape(16.dp),

        ) {
        if (isloading){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .height(190.dp)
                    .background(Color.White.copy(alpha = 0.8f)) // Ajusta el nivel de opacidad aquí
            ) {
                // Muestra el indicador de carga lineal con efecto de cristal
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .align(Alignment.Center),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.9f), // Ajusta el nivel de opacidad aquí
                    trackColor = BlueSystem.copy(alpha = 0.1f), // Ajusta el nivel de opacidad aquí
                )
            }
        }else{
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                // Mostrar la imagen del usuario (assumiendo que `url` es una URL de imagen)
                val imageUrl = user.url
                val placeholderImage = painterResource(id = R.drawable.user)

                // Utiliza un Card para aplicar una sombra suave a la imagen del usuario
                Card(
                    modifier = Modifier
                        .size(120.dp)
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 10.dp
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White,
                    )
                ){
                    if (imageUrl.isNullOrBlank()) {
                        // Si la URL es nula o vacía, mostrar la imagen por defecto
                        Image(
                            painter = placeholderImage,
                            contentDescription = "Default User Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
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
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Column que contiene la información del usuario
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(text = "Name: ${user.name}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text(text = "Email: ${user.email}", color = MaterialTheme.colorScheme.primary)
                    Text(text = "Phone: ${user.phone}", color = MaterialTheme.colorScheme.primary)
                    Text(text = "Role: ${userRoleToString(user.role)}", color = MaterialTheme.colorScheme.primary)
                    ElevatedButton(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = {
                            DataRepository.setUserPlus(user)
                            navController.navigate("updateUser")
                        },
                        colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                            containerColor =  BlueSystem
                        ),
                        elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                            defaultElevation = 5.dp
                        )
                    ){
                        Icon(
                            imageVector = Icons.Filled.Create,
                            contentDescription = "",
                            tint = Color.White
                        )
                    }
                    if (disabled) {      //Desactivado
                        ElevatedButton(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = {
                                enableUser()
                            },
                            colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                                containerColor =  BlueSystem
                            ),
                            elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                                defaultElevation = 5.dp
                            )
                        ){
                            Text("Enable", color = Color.White)
                            Spacer(modifier = Modifier.padding(3.dp))
                            Icon(imageVector = Icons.Filled.ToggleOn, contentDescription = "Enable", tint = Color.Green )
                        }
                    } else { //Activado
                        ElevatedButton(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = {
                                disableUser()
                            },
                            colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                                containerColor = BlueSystem
                            ),
                            elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                                defaultElevation = 5.dp
                            )
                        ){
                            Text("Disable", color = Color.White)
                            Spacer(modifier = Modifier.padding(3.dp))
                            Icon(imageVector = Icons.Filled.ToggleOff, contentDescription = "Disable", tint = Color.Red )
                        }
                    }
                }
            }
        }
    }
}