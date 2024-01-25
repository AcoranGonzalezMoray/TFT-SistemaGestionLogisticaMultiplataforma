package com.example.qrstockmateapp.navigation.view

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.ModeNight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.qrstockmateapp.R
import com.example.qrstockmateapp.api.models.User
import com.example.qrstockmateapp.api.services.RetrofitInstance
import com.example.qrstockmateapp.navigation.logic.Navigation
import com.example.qrstockmateapp.navigation.model.ScreenModel
import com.example.qrstockmateapp.navigation.repository.DataRepository
import com.example.qrstockmateapp.navigation.widget.AnimatedBottomBar
import com.example.qrstockmateapp.ui.theme.BlueSystem
import com.example.qrstockmateapp.ui.theme.isDarkMode
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
            drawerGesturesEnabled = destination.route != "route" &&  destination.route != "routeMinus"
        }

        navController.addOnDestinationChangedListener(listener)

        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        scaffoldState = scaffoldState,
        drawerGesturesEnabled = drawerGesturesEnabled ,
        floatingActionButton = {
            if(DataRepository.getUser()?.role == 1 && currentRoute == "routeManagement"){
                androidx.compose.material3.FloatingActionButton(onClick = {navController.navigate("addRoute") }, containerColor = MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier
                    .shadow(4.dp, shape = RoundedCornerShape(18.dp))
                    .border(0.5.dp, Color(0xff5a79ba), shape = RoundedCornerShape(18.dp)),
                ) {
                    Icon(imageVector = Icons.Default.AddLocationAlt, contentDescription = "Crear nota", tint = Color(0xff5a79ba), modifier = Modifier.size(30.dp))
                }
            }
            if(DataRepository.getUser()?.role == 0 && currentRoute == "home"  ){
                androidx.compose.material3.FloatingActionButton(onClick = { navController.navigate("addWarehouse")}, containerColor = MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier
                    .shadow(4.dp, shape = RoundedCornerShape(18.dp))
                    .border(0.5.dp, Color(0xff5a79ba), shape = RoundedCornerShape(18.dp)),
                )  {
                    Row {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Crear nota", tint = Color(0xff5a79ba), modifier = Modifier.size(15.dp))
                        Icon(imageVector = Icons.Default.Warehouse, contentDescription = "Crear nota", tint = Color(0xff5a79ba), modifier = Modifier.size(30.dp))
                    }
                }
            }
            if(DataRepository.getUser()?.role == 0 && currentRoute == "vehicleManagement"){
                androidx.compose.material3.FloatingActionButton(onClick = {navController.navigate("addVehicle") }, containerColor = MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier
                    .shadow(4.dp, shape = RoundedCornerShape(18.dp))
                    .border(0.5.dp, Color(0xff5a79ba), shape = RoundedCornerShape(18.dp)),
                )  {
                    Row {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Crear nota", tint = Color(0xff5a79ba), modifier = Modifier.size(15.dp))
                        Icon(imageVector = Icons.Default.DirectionsCar, contentDescription = "Crear nota", tint = Color(0xff5a79ba), modifier = Modifier.size(30.dp))
                    }              }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
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
            val excludedRoutes = setOf("addVehicle","updateVehicle","route", "routeMinus", "addWarehouse", "updateWarehouse", "updateUser", "addRoute", "updateRoute")

            if (currentRoute !in excludedRoutes) {
                TopAppBar(
                    backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                    title = {
                        // Colocar la imagen en el centro y el icono a la derecha
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize()
                                    .padding(end = 25.dp)
                                    .fillMaxHeight()
                                    .wrapContentSize(Alignment.Center),
                                contentAlignment = Alignment.Center
                            ) {
                                // Imagen en el centro
                                Image(
                                    painter = painterResource(id = R.drawable.icon_white),
                                    contentDescription = "",
                                    colorFilter = ColorFilter.tint(Color(0xff5a79ba))
                                )
                            }

                            // Icono a la derecha
                            Box(
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .fillMaxHeight()
                                    .wrapContentSize(Alignment.Center),
                                contentAlignment = Alignment.Center
                            ) {
                                androidx.compose.material3.Icon(
                                    imageVector = Icons.Filled.Message,
                                    contentDescription = null,
                                    tint = Color(0xff5a79ba)
                                )
                                Badge(
                                    content = { Text(text = "5", color = Color.White) },
                                    modifier = Modifier.offset(x = 12.dp, y = -8.dp)
                                )
                            }
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
            }




        },

        bottomBar = {
            val excludedRoutes = setOf("addVehicle","updateVehicle", "route", "routeMinus", "addWarehouse", "updateWarehouse", "updateUser", "addRoute", "updateRoute")

            if (currentRoute !in excludedRoutes) {
                AnimatedBottomBar(
                    screens = ScreenModel().screensInHomeFromBottomNav,
                    navController = navController
                )
            }
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
    var changeMode by remember { mutableStateOf(false) }

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
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(10.dp)
        ) {
            if (showDialog) {
                AlertDialog(
                    backgroundColor = MaterialTheme.colorScheme.background,
                    onDismissRequest = {
                        // Handle dismissal if needed
                        showDialog = false
                    },
                    title = {
                        Text(text = "Alert", color = MaterialTheme.colorScheme.primary)
                    },
                    text = {
                        Text(text ="Are you sure you want to delete this account?",  color = MaterialTheme.colorScheme.primary)
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
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
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
            if (changeMode) {
                var selectedOption by remember { mutableStateOf<Option?>(null) }

                AlertDialog(
                    backgroundColor = MaterialTheme.colorScheme.background,
                    onDismissRequest = {
                        // Handle dismissal if needed
                        changeMode = false
                    },
                    title = {
                        Text(text = "Style Settings", color = MaterialTheme.colorScheme.primary)
                    },
                    text = {
                        StyleSelectionBox(selectedOption = selectedOption) {
                            selectedOption = it
                        }
                    },
                    confirmButton = {
                        ElevatedButton(
                            onClick = {
                                //sharedPreferences.edit().clear().apply()

                                changeMode = false
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
                                changeMode = false
                            },
                            colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
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
                    .background(MaterialTheme.colorScheme.secondaryContainer) ,
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Apartment,
                    contentDescription = "",
                    tint =  Color(0xff5a79ba),
                    modifier = Modifier
                        .height(40.dp)
                        .size(48.dp),
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
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 20.dp),
                    verticalArrangement = Arrangement.Bottom

                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {showDialog = true},
                            colors = ButtonDefaults.buttonColors(Color.Red),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(9f)
                                .padding(start = 4.dp) // Agrega espacio a la izquierda del botón
                        ) {
                            Text(text = "Delete Account", color= Color.White)
                        }
                        Spacer(modifier = Modifier.padding(10.dp))
                        ElevatedButton(
                            colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                                containerColor = BlueSystem,
                            )
                            ,onClick = {
                            changeMode = true
                        }) {
                            if(isDarkMode()){
                                Icon(imageVector = Icons.Filled.DarkMode, contentDescription = null, tint = Color.White)
                            }else{
                                Icon(imageVector = Icons.Filled.LightMode, contentDescription = null, tint = Color.Black)
                            }
                        }
                    }
                }
            }else{
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 20.dp),
                    verticalArrangement = Arrangement.Bottom

                ) {
                    Icon(imageVector = Icons.Filled.Settings, contentDescription = null )
                }
            }

        }


    }


}

data class Option(val label: String, val icon: ImageVector)
@Composable
fun StyleSelectionBox(selectedOption: Option?, onOptionSelected: (Option) -> Unit) {
    val options = listOf(
        Option("Dark Mode", Icons.Default.DarkMode),
        Option("Light Mode", Icons.Default.LightMode),
        Option("System Mode", Icons.Default.SystemUpdate),
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

                Icon(imageVector = option.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)

                Text(text = option.label, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}