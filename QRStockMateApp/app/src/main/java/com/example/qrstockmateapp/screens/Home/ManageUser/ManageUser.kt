package com.example.qrstockmateapp.screens.Home.ManageUser


import android.util.Log
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.qrstockmateapp.R
import com.example.qrstockmateapp.api.models.User
import com.example.qrstockmateapp.api.models.userRoleToString
import com.example.qrstockmateapp.api.services.RetrofitInstance
import com.example.qrstockmateapp.navigation.repository.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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
                    Log.d("EMPLOYEE", "SI")
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
        cursorColor = Color.Black,
        focusedBorderColor = Color.Black,
        focusedLabelColor = Color.Black,
        unfocusedBorderColor = Color.Black,
        backgroundColor = Color.LightGray
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
            backgroundColor =  Color.White,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search") },
                colors = customTextFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )


            // Lista de usuarios filtrada
            LazyColumn {
                items(filteredEmployees) { employee ->
                    UserListItem(user = employee, navController,loadEmployees)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

}

@Composable
fun UserListItem(user: User, navController: NavController,loadEmployees: () -> Unit) {
    //Función Comprobar Inactividad
    fun checkDisabled(): Boolean {
        Log.d("DISABLE", "${user}")
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
                if (updateUser.isSuccessful) {
                    Log.d("Updated User", "User was Disabled")
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
                if (updateUser.isSuccessful) {
                    Log.d("Updated User", "User was Enabled")
                    loadEmployees()
                    disabled = false
                } else Log.d("compnayError", "error")
            }
        }
    }

    LaunchedEffect(Unit){
        checkDisabled()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
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
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .shadow(
                        elevation = 5.dp,
                    )
                    .padding(16.dp)
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
                Text(text = "Name: ${user.name}", fontWeight = FontWeight.Bold)
                Text(text = "Email: ${user.email}")
                Text(text = "Phone: ${user.phone}")
                Text(text = "Role: ${userRoleToString(user.role)}")
                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(Color.Yellow),onClick = {
                        DataRepository.setUserPlus(user)
                        navController.navigate("updateUser")
                    }, ) {
                    Icon(
                        imageVector = Icons.Filled.Create,
                        contentDescription = "",
                        tint = Color.White
                    )
                }
                if (disabled) {      //Desactivado
                    Button(
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(Color.Green),onClick = {
                            enableUser()
                        }, ) {
                        Text("Enable", color = Color.White)
                    }
                } else {        //Activado
                    Button(
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(Color.Red),onClick = {
                            disableUser()
                        }, ) {
                        Text("Disable", color = Color.White)
                    }
                }
            }
        }
    }
}