package com.example.qrstockmateapp.navigation.view

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ElevatedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.qrstockmateapp.R
import com.example.qrstockmateapp.api.models.User
import com.example.qrstockmateapp.api.services.RetrofitInstance
import com.example.qrstockmateapp.navigation.logic.Navigation
import com.example.qrstockmateapp.navigation.model.ScreenModel
import com.example.qrstockmateapp.navigation.repository.DataRepository
import com.example.qrstockmateapp.navigation.widget.BottomBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun BottomNavigationScreen(navControllerLogin: NavController,sharedPreferences: SharedPreferences) {
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    val scope = rememberCoroutineScope()
    var drawerGesturesEnabled by remember { mutableStateOf(true) }

    val navController = rememberNavController()
    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            drawerGesturesEnabled = destination.route != "route"
        }

        navController.addOnDestinationChangedListener(listener)

        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }
    Scaffold(
        scaffoldState = scaffoldState,
        drawerGesturesEnabled = drawerGesturesEnabled ,

        drawerContent = {
            Drawer(

                item = ScreenModel().screensInHomeFromBottomNav,
                sharedPreferences,
                navController = navController,
                navControllerLogin = navControllerLogin,
                scope = scope,
                scaffoldState = scaffoldState

            )
        },
        topBar = {
            TopAppBar(
                backgroundColor = Color.White,
                title = {
                    // Colocar la imagen en el centro
                    Box(
                        modifier = Modifier.fillMaxSize().padding(10.dp).padding(end = 66.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.icon_white),
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(Color(0xff5a79ba)) // Invertir colores de la imagen
                        )
                    }
                },
                navigationIcon = {
                    // Ícono de menú para abrir el cajón de navegación
                    IconButton(onClick = {
                        scope.launch {
                            scaffoldState.drawerState.open()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "",
                            tint = Color(0xff5a79ba)
                        )
                    }
                }
            )



        },

        bottomBar = {
            BottomBar(
                screens = ScreenModel().screensInHomeFromBottomNav,
                navController = navController,

                )

        },
    ) {
        Navigation(navController = navController)
    }


}


@Composable
fun Drawer(
    item: List<ScreenModel.HomeScreens>,
    sharedPreferences: SharedPreferences,
    navController: NavController,
    navControllerLogin: NavController,
    scaffoldState: ScaffoldState,
    scope: CoroutineScope
) {
    var showDialog by remember { mutableStateOf(false) }

    val deleteAccount:()->Unit = {
        GlobalScope.launch(Dispatchers.IO) {
            var user =DataRepository.getUser()
            if(user!=null){
                try {
                    val response = RetrofitInstance.api.deleteAccount(user)
                    if (response.isSuccessful){
                        withContext(Dispatchers.Main){
                            navControllerLogin.navigate("login")
                        }
                    }else{
                        try {
                            val errorBody = response.errorBody()?.string()
                            Log.d("excepcionUserB", errorBody ?: "Error body is null")
                        } catch (e: Exception) {
                            Log.e("excepcionUserB", "Error al obtener el cuerpo del error: $e")
                        }
                    }
                }catch (e: Exception) {
                    Log.e("excepcionUserB", "Error al obtener el cuerpo del error: $e")
                }
            }

        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(10.dp)
        ) {
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = {
                        // Handle dismissal if needed
                        showDialog = false
                    },
                    title = {
                        Text(text = "Alert")
                    },
                    text = {
                        Text(text ="Are you sure you want to delete this account?")
                    },
                    confirmButton = {
                        ElevatedButton(
                            onClick = {
                                sharedPreferences.edit().clear().apply()
                                deleteAccount()
                                showDialog = false
                            },
                            colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                                containerColor = Color(0xff5a79ba)
                            ),
                            elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                                defaultElevation = 5.dp
                            )
                        ){
                            Text("Confirm", color = Color.White)
                        }
                    },
                    dismissButton = {
                        ElevatedButton(
                            onClick = {
                                showDialog = false
                            },
                            colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                                containerColor = Color.White
                            ),
                            elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                                defaultElevation = 5.dp
                            )
                        ){
                            Text("Cancel", color =  Color(0xff5a79ba))
                        }
                    }
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White) ,
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Apartment,
                    contentDescription = "",
                    tint =  Color(0xff5a79ba),
                    modifier = Modifier.height(40.dp).size(48.dp),
                )
                Spacer(modifier = Modifier.width(7.dp))
                Text("Company: ${DataRepository.getCompany()?.name} \nCode: ${DataRepository.getUser()?.code}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color=  Color(0xff5a79ba))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        item.forEach() { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = {
                        navController.navigate(item.route)
                        // Closenav drawer
                        scope.launch {
                            scaffoldState.drawerState.close()
                        }
                        // Close drawer


                    })
                    .height(45.dp)

                    .padding(start = 10.dp)
            ) {
                Icon(imageVector = item.icon, contentDescription = "", tint = Color(0xff5a79ba))
                Spacer(modifier = Modifier.width(7.dp))
                Text(
                    text = item.title,
                    fontSize = 18.sp,
                    color =  Color(0xff5a79ba)
                )
            }


        }

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if(DataRepository.getUser()?.role==0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = {
                            navController.navigate("addWarehouse")
                            scope.launch { scaffoldState.drawerState.close() }

                        })
                        .height(45.dp)

                        .padding(start = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AddBusiness,
                        contentDescription = "",
                        tint =  Color(0xff5a79ba)
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    Text(
                        text = "Add New Warehouse",
                        fontSize = 18.sp,
                        color = Color(0xff5a79ba)
                    )

                }
            }
            if(DataRepository.getUser()?.role==0 || DataRepository.getUser()?.role==1) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = {
                            navController.navigate("manageUser")
                            scope.launch { scaffoldState.drawerState.close() }

                        })
                        .height(45.dp)

                        .padding(start = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ManageAccounts,
                        contentDescription = "",
                        tint =  Color(0xff5a79ba)
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    Text(
                        text = "Manage User",
                        fontSize = 18.sp,
                        color =  Color(0xff5a79ba)
                    )

                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = {
                        DataRepository.LogOut()
                        sharedPreferences.edit {
                            remove("TOKEN_KEY")
                            remove("USER_KEY")
                        }
                        navControllerLogin.navigate("login")
                        scope.launch { scaffoldState.drawerState.close() }

                    })
                    .height(45.dp)

                    .padding(start = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ExitToApp,
                    contentDescription = "",
                    tint = Color.Red
                )
                Spacer(modifier = Modifier.width(7.dp))
                Text(
                    text = "Log Out",
                    fontSize = 18.sp,
                    color = Color.Red
                )

            }
            if(DataRepository.getUser()?.role==0) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(top = 16.dp,start=16.dp, end=16.dp, bottom = 60.dp),
                    verticalArrangement = Arrangement.Bottom

                ) {
                    Button(
                        onClick = {showDialog = true},
                        colors = ButtonDefaults.buttonColors(Color.Red),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp) // Agrega espacio a la izquierda del botón
                    ) {
                        Text(text = "Delete Account", color= Color.White)
                    }
                }
            }
        }


    }


}
