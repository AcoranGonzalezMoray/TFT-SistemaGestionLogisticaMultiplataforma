package com.example.qrstockmateapp.navigation.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.SharedPreferences
import android.util.Log
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.qrstockmateapp.MainActivity.Companion.KEY_DARK_THEME
import com.example.qrstockmateapp.MainActivity.Companion.NEW_MESSAGES
import com.example.qrstockmateapp.R
import com.example.qrstockmateapp.api.services.RetrofitInstance
import com.example.qrstockmateapp.navigation.logic.Navigation
import com.example.qrstockmateapp.navigation.model.ScreenModel
import com.example.qrstockmateapp.navigation.repository.DataRepository
import com.example.qrstockmateapp.navigation.widget.AnimatedBottomBar
import com.example.qrstockmateapp.navigation.widget.AnimatedOutBottomBar
import com.example.qrstockmateapp.ui.theme.BlueSystem
import com.example.qrstockmateapp.ui.theme.isDarkMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun BottomNavigationScreen(navControllerLogin: NavController,sharedPreferences: SharedPreferences) {
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    val scope = rememberCoroutineScope()
    var drawerGesturesEnabled by remember { mutableStateOf(true) }
    val user = DataRepository.getUser()
    val chat = setOf("chats", "chat", "contact", "communication")

    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()


    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var new by remember {
        mutableStateOf(DataRepository.getNewMessages()?.minus(
            sharedPreferences.getInt(
                NEW_MESSAGES, 0)
        ))
    }
    LaunchedEffect(Unit) {
        val excludedRoutes = setOf("chat","route","routeMinus", "addWarehouse")

        // Lanzar una corrutina en el alcance de la pantalla
            coroutineScope.launch(Dispatchers.IO) {
                while (user!=null){
                    if(navBackStackEntry?.destination?.route !in excludedRoutes){
                        try {
                            val response = RetrofitInstance.api.getNewMessages("${user.code};${user.id}")
                            if (response.isSuccessful) {
                                val messages = response.body()
                                if (messages != null) {
                                    DataRepository.setNewMessages(messages)
                                }else{
                                    DataRepository.setNewMessages(0)
                                }
                            } else {
                                DataRepository.setNewMessages(0)
                            }
                        } catch (e: Exception) {
                            // Manejar cualquier excepción que pueda ocurrir durante la solicitud
                            e.printStackTrace()
                        }
                        new = DataRepository.getNewMessages()?.minus(
                            sharedPreferences.getInt(
                                NEW_MESSAGES, 0)
                        )
                    }

                    // Esperar 2 segundos antes de realizar la próxima solicitud
                    delay(2000)
                }
            }
    }

    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            drawerGesturesEnabled = destination.route != "route" &&  destination.route != "routeMinus" &&  destination.route != "chat"
        }

        navController.addOnDestinationChangedListener(listener)

        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }


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
            val excludedRoutes = setOf("profile","chat","addVehicle","updateVehicle","route", "routeMinus", "addWarehouse", "updateWarehouse", "updateUser", "addRoute", "updateRoute")

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
                                    painter = painterResource(id = R.drawable.app_icon_removed),
                                    contentDescription = "",
                                )
                            }

                            // Icono a la derecha
                           Box(

                               modifier = Modifier
                                   .padding(end = 16.dp)
                                   .fillMaxHeight()
                                   .wrapContentSize(Alignment.Center)
                                   .clickable {
                                       DataRepository.setSplash("chats")
                                       navController.navigate("splashScreen")
                                       DataRepository.setCurrentScreenIndex(0)
                                   },
                               contentAlignment = Alignment.Center
                           ) {
                               if(currentRoute !in chat){
                                   Image(
                                       painter = painterResource(id = R.drawable.message_icon),
                                       modifier = Modifier.size(25.dp),
                                       contentDescription = "",
                                   )
                                   if(new !=null && new!=0){
                                       Badge(
                                           content = { Text(text = "${if(new!! >0)new else 0}", color = Color.White) },
                                           modifier = Modifier.offset(x = 12.dp, y = (-8).dp)
                                       )
                                   }
                               }else{
                                   Icon(imageVector = Icons.Filled.Close, contentDescription = null, tint = BlueSystem, modifier = Modifier.clickable {
                                       navController.navigate("home")
                                   })
                               }
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
            val excludedRoutes = setOf("profile", "itemDetails", "chat","splashScreen","addVehicle","updateVehicle", "route", "routeMinus", "addWarehouse", "updateWarehouse", "updateUser", "addRoute", "updateRoute")
            val screens = listOf( //Chat
                ScreenModel.HomeScreens.Message,
                ScreenModel.HomeScreens.Comunity,
                ScreenModel.HomeScreens.Contact
            )
            if (currentRoute !in excludedRoutes) {
                if(currentRoute !in chat){
                    AnimatedBottomBar(
                        screens = ScreenModel().screensInHomeFromBottomNav,
                        navController = navController
                    )
                }else{
                    AnimatedBottomBar(
                        screens = screens,
                        navController = navController
                    )
                }
            }else {
               if(currentRoute != "chat" && currentRoute!="route" && currentRoute !="routeMinus"){
                   AnimatedOutBottomBar(
                       screens = ScreenModel().screensInHomeFromBottomNav,
                       navController = navController
                   )
               }
            }
        },
    ) {
        Navigation(navController = navController, sharedPreferences)
    }


}


@OptIn(DelicateCoroutinesApi::class)
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
    val context = LocalContext.current as Activity

    val deleteAccount:()->Unit = {
        GlobalScope.launch(Dispatchers.IO) {
            val user =DataRepository.getUser()
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
                val options = listOf(
                    Option("Dark Mode", Icons.Default.DarkMode, 0),
                    Option("Light Mode", Icons.Default.LightMode, 1),
                    Option("System Mode", Icons.Default.SystemUpdate, 2),
                )
                var selectedOption by remember { mutableStateOf<Option?>(options[sharedPreferences.getInt(KEY_DARK_THEME, 2)]) }

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
                                changeMode = false
                                sharedPreferences.edit().putInt(KEY_DARK_THEME, selectedOption!!.mode).apply()
                                DataRepository.setCurrentScreenIndex(0)
                                GlobalScope.launch(Dispatchers.Main) {
                                    context.recreate()
                                }
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
                if (DataRepository.getUser()?.url.isNullOrBlank()) {
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
                        data = DataRepository.getUser()!!.url,
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
                Spacer(modifier = Modifier.width(30.dp))
                Text("Company: ${DataRepository.getCompany()?.name} \nCode: ${DataRepository.getUser()?.code}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color=  Color(0xff5a79ba))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        item.forEach { item ->
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
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
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
                                Icon(imageVector = Icons.Filled.LightMode, contentDescription = null, tint = Color.White)
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
                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .weight(9f))
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
                            Icon(imageVector = Icons.Filled.LightMode, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            }

        }


    }


}

data class Option(val label: String, val icon: ImageVector, val mode: Int)
@Composable
fun StyleSelectionBox(selectedOption: Option?, onOptionSelected: (Option) -> Unit) {
    val options = listOf(
        Option("Dark Mode", Icons.Default.DarkMode, 0),
        Option("Light Mode", Icons.Default.LightMode, 1),
        Option("System Mode", Icons.Default.SystemUpdate, 2),
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