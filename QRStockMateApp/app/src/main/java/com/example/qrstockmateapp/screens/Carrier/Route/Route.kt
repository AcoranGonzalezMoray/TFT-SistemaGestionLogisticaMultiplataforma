package com.example.qrstockmateapp.screens.Carrier.Route

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LayersClear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Maximize
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Streetview
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import coil.compose.rememberImagePainter

import com.example.qrstockmateapp.R
import com.example.qrstockmateapp.api.models.TransportRoute
import com.example.qrstockmateapp.api.models.Warehouse
import com.example.qrstockmateapp.api.services.RetrofitInstance
import com.example.qrstockmateapp.navigation.repository.DataRepository
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng


import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Math.atan2
import java.lang.Math.cos
import java.lang.Math.sin
import java.lang.Math.sqrt
import kotlin.math.roundToLong


@OptIn(ExperimentalMaterialApi::class, DelicateCoroutinesApi::class)
@Composable
fun RouteScreen(navController: NavController,) {
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(
            initialValue = BottomSheetValue.Collapsed
        )
    )

    val context = LocalContext.current
    var mapWeight by remember { mutableStateOf(400.dp) }

    LaunchedEffect(scaffoldState.bottomSheetState) {
        snapshotFlow { scaffoldState.bottomSheetState.currentValue }
            .distinctUntilChanged()
            .collect { newState ->
                when (newState) {
                    BottomSheetValue.Collapsed -> {
                        mapWeight = 135.dp
                        println("BottomSheet colapsado")
                    }
                    BottomSheetValue.Expanded -> {
                        mapWeight = 400.dp
                        println("BottomSheet expandido")
                    }
                    else -> {
                        // Otros estados del BottomSheet
                    }
                }
            }
    }
    // Ubicación actual del dispositivo
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    var route = DataRepository.getRoutePlus()
    val scope = rememberCoroutineScope()

    var start = DataRepository.getWarehouses()?.find { warehouse -> warehouse.id == route!!.startLocation.toInt()}
    var end  = DataRepository.getWarehouses()?.find { warehouse -> warehouse.id == route!!.endLocation.toInt()}
    Log.d("SERA?", start!!.latitude.toString())
    val startPoint =  LatLng(start!!.latitude, start.longitude)
    val endPoint = LatLng(end!!.latitude, end.longitude)
    val launchPoint = LatLng(81.444125, 163.066796)

    //val launchPoint = LatLng(start.latitude, start.longitude)
    //81.444125, 163.066796


    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(launchPoint, 90f)
    }
    var bitmapDescriptor by remember { mutableStateOf<BitmapDescriptor?>(null) }
    var bitmapDescriptorNow by remember { mutableStateOf<BitmapDescriptor?>(null) }

    val availableColors = listOf(
        Color.Red,
        Color.Green,
        Color.Blue,
        Color.Yellow,
        Color.Black
    )
    val coroutineScope = rememberCoroutineScope()

    var selectedColor by remember { mutableStateOf(availableColors.first()) }
    var isDropdownMenuExpanded by remember { mutableStateOf(false) }
    ////////////////////////////////////////////
    // Estado para almacenar los puntos de la ruta trazada por el usuario
    var userRoutePoints by remember { mutableStateOf(listOf<LatLng>()) }
    var designMode by remember { mutableStateOf(false) }

    // Evento de clic en el mapa para agregar puntos a la ruta
    val onMapClick: (LatLng) -> Unit = { clickedLatLng ->
        if(designMode){
            // Añadir el punto clicado a la lista de puntos de la ruta
            if (userRoutePoints.isEmpty()) {
                userRoutePoints = listOf(startPoint, clickedLatLng)
            } else {
                // Añadir el punto clicado a la lista de puntos de la ruta
                userRoutePoints = userRoutePoints + clickedLatLng
            }

            // Dibujar la línea de la ruta con los puntos existentes
            GlobalScope.launch(Dispatchers.Main) {
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLng(clickedLatLng),
                    durationMs = 1000
                )
            }
        }
    }

    // Estado para almacenar la ubicación actual
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var change by remember { mutableStateOf<Boolean>(false) }


    var isRouteStarted by remember { mutableStateOf(false) }

    // Configuración de seguimiento de ubicación
    val locationRequest = LocationRequest.create()
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        .setInterval(5000) // Intervalo de actualización de ubicación en milisegundos

    // Estado para almacenar el resultado de la solicitud de permisos
    var locationPermissionGranted by remember { mutableStateOf(false) }

    // Función para verificar si se tiene el permiso necesario
    fun checkLocationPermission() {
        locationPermissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Comprobar permisos al inicio del Composable
    DisposableEffect(Unit) {
        checkLocationPermission()
        onDispose { /* No es necesario hacer nada al finalizar */ }
    }

    // Solicitar permisos si no están otorgados
    if (!locationPermissionGranted) {
        var launcher =
            rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    // Permiso concedido, reiniciar el Composable
                    locationPermissionGranted = true
                }else{
                    Toast.makeText(context, "Need Location", Toast.LENGTH_SHORT).show()
                }
            }

        LaunchedEffect(Unit) {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }


    }

    val updateLocation: (latLng: LatLng) -> Unit = { latLng ->

        coroutineScope.launch(Dispatchers.IO) {
            change = false
            try {
                // Realizar la solicitud de actualización de ubicación
                val locationResponse = RetrofitInstance.api.updateLocationVehicle(route!!.assignedVehicleId, "${latLng.latitude};${latLng.longitude}")
                if(locationResponse.isSuccessful){
                    Log.d("LocationUpdates", "Ubicación actualizada con éxito: $latLng")
                }

                change = true
            } catch (e: Exception) {
                // Manejar errores aquí
                Log.e("LocationUpdates", "Error al actualizar la ubicación: $e")
            }
            change = true
        }
    }

    val saveRoute: () -> Unit = {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                route!!.route = userRoutePoints.toString()
                // Realizar la solicitud de actualización de ubicación
                val locationResponse = RetrofitInstance.api.putTransportRoutes(route)
                if(locationResponse.isSuccessful){
                    Log.d("InitRoute", "Ubicación actualizada")
                }
                change = true
            } catch (e: Exception) {
                // Manejar errores aquí
                Log.e("InitRoute", "Error al actualizar la ubicación: $e")
            }
        }
    }

    val startRoute: () -> Unit = {

        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Realizar la solicitud de actualización de ubicación
                val locationResponse = RetrofitInstance.api.initRoute(route!!.id)
                if(locationResponse.isSuccessful){
                    Log.d("InitRoute", "Ubicación actualizada")
                }
                change = true
            } catch (e: Exception) {
                // Manejar errores aquí
                Log.e("InitRoute", "Error al actualizar la ubicación: $e")
            }
        }
    }


    val finishRoute: () -> Unit = {

        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Realizar la solicitud de actualización de ubicación
                val locationResponse = RetrofitInstance.api.finishRoute(route!!.id)
                if(locationResponse.isSuccessful){
                    Log.d("InitRoute", "Ubicación actualizada")
                    withContext(Dispatchers.Main){
                        Toast.makeText(context, "Finalized route", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }

                }
            } catch (e: Exception) {
                // Manejar errores aquí
                Log.e("InitRoute", "Error al actualizar la ubicación: $e")
            }
        }
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult?.lastLocation?.let {
                val latLng = LatLng(it.latitude, it.longitude)
                currentLocation = latLng

                if(isRouteStarted){
                    // Agregar un mensaje de registro para verificar las actualizaciones de ubicación
                    Log.d("LocationUpdates", "Nueva ubicación recibida: $latLng")
                    coroutineScope.launch {
                        updateLocation(latLng)
                    }

                }
            }
        }
    }

    DisposableEffect(locationCallback) {
        // Solicita actualizaciones de ubicación
        if (locationPermissionGranted) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            Log.d("LocationUpdates", "Solicitadas actualizaciones de ubicación")
        }
        onDispose {
            // Elimina las actualizaciones de ubicación cuando se desecha el composable
            fusedLocationClient.removeLocationUpdates(locationCallback)
            Log.d("LocationUpdates", "Eliminadas actualizaciones de ubicación")
        }
    }


    if (fusedLocationClient != null && fusedLocationClient.lastLocation != null) {
        // Obtener la última ubicación conocida
        Log.d("ERROR", "Entra")
        fusedLocationClient.lastLocation
            .addOnSuccessListener { locationResult: Location? ->
                locationResult?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    currentLocation = latLng
                }
            }
    }
    ///////////////////////////////////////////
    LaunchedEffect(context) {
        val drawable = ContextCompat.getDrawable(context, R.drawable.warehouse)
        val drawableNow = ContextCompat.getDrawable(context, R.drawable.carrier)
        if (drawable != null && drawableNow!=null) {
            // Define el tamaño deseado en píxeles (por ejemplo, 50x50)
            val targetSize = 150
            val scaledDrawable = Bitmap.createScaledBitmap(
                drawable.toBitmap(),
                targetSize,
                targetSize,
                false
            )
            val scaledDrawableNow = Bitmap.createScaledBitmap(
                drawableNow.toBitmap(),
                targetSize,
                targetSize,
                false
            )
            bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(scaledDrawable)
            bitmapDescriptorNow = BitmapDescriptorFactory.fromBitmap(scaledDrawableNow)
        }

        Log.d("CameraAnimation", "Animación de cámara iniciada")
        cameraPositionState.animate(
            update = CameraUpdateFactory.newCameraPosition(
                CameraPosition(startPoint, 80f, 0f, 0f)
            ),
            durationMs = 2000
        )
        Log.d("CameraAnimation", "Animación de cámara completada")

    }
    if(route!!.route!=""){
        Log.d("QUE COJONES FUNCIONA", "FUNCIONA")
        userRoutePoints =
            com.example.qrstockmateapp.screens.Carrier.Route.RouteMinus.convertStringToLatLngList(
                route!!.route
            )
    }else{
        Log.d("QUE COJONES", "COJONES")
    }

    DisposableEffect(Unit) {
        onDispose {
            coroutineScope.cancel()
        }
    }

    BottomSheetScaffold(
        sheetContent = {
            // Contenido del Bottom Sheet
            BottomSheetContent(scope, scaffoldState,isRouteStarted, haversine(userRoutePoints),
                onStartRoute = {
                    isRouteStarted = true

                    GlobalScope.launch(Dispatchers.Main) {
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newCameraPosition(
                                CameraPosition(LatLng(currentLocation!!.latitude, currentLocation!!.longitude), 15f, 50f, 0f)
                            ),
                            durationMs = 2000
                        )
                    }
                    startRoute()
                               },
                onFinishRoute = {
                    isRouteStarted = false

                    //GlobalScope.launch(Dispatchers.Main) {
                        //cameraPositionState.animate(
                            //update = CameraUpdateFactory.newCameraPosition(
                                //CameraPosition(endPoint, 80f, 0f, 0f)
                            //),
                            //durationMs = 2000
                       // )
                    //}
                    finishRoute()
                }, route, start, end
            )
        },
        sheetPeekHeight = 150.dp, // Establecer la altura mínima del BottomSheet
        sheetElevation = 5.dp,
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        scaffoldState = scaffoldState,
        ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = mapWeight),
                cameraPositionState = cameraPositionState,
                onMapClick = onMapClick
            ) {
                if (userRoutePoints.size >= 2) {
                    Polyline(
                        points = userRoutePoints,
                        color = selectedColor,
                        width = 10f,
                        onClick = {
                            // Manejar clic en la línea si es necesario
                        }
                    )
                }
                if (isRouteStarted) {
                    // Marcador para la ubicación actual
                    currentLocation?.let {
                        if(change){
                            PointMarker(it, "Ubicación Actual", "Marker en la Ubicación Actual", bitmapDescriptorNow!!, "ubi", false)
                        }
                    }
                }

                PointMarker(endPoint, "End Point", end.name, bitmapDescriptor!!, "Fin", false)
                PointMarker(startPoint, "Start Point", start.name, bitmapDescriptor!!, "Inicio", false)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center // Alinea el contenido en el centro
            ) {
                if (scaffoldState.bottomSheetState.isExpanded) {
                    Row {
                        FloatingActionButton(
                            containerColor = Color.White,
                            modifier = Modifier
                                .padding(16.dp)
                                .shadow(4.dp, shape = RoundedCornerShape(18.dp))
                                .border(
                                    0.5.dp,
                                    Color(0xff5a79ba),
                                    shape = RoundedCornerShape(18.dp)
                                ),
                            onClick = {
                                // Acciones cuando el Bottom Sheet está expandido
                                GlobalScope.launch(Dispatchers.Main) {
                                    if(isRouteStarted) {
                                        cameraPositionState.animate(
                                            update = CameraUpdateFactory.newCameraPosition(
                                                CameraPosition(
                                                    LatLng(
                                                        currentLocation!!.latitude,
                                                        currentLocation!!.longitude
                                                    ), 80f, 0f, 0f
                                                )
                                            ),
                                            durationMs = 2000
                                        )
                                    }else{
                                        Toast.makeText(context, "You need to start the route", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                        ){
                            Icon(imageVector = Icons.Filled.MyLocation, contentDescription = "", tint=Color(0xff5a79ba))
                        }
                        FloatingActionButton(
                            containerColor = Color.White,
                            modifier = Modifier
                                .padding(16.dp)
                                .shadow(4.dp, shape = RoundedCornerShape(18.dp))
                                .border(
                                    0.5.dp,
                                    Color(0xff5a79ba),
                                    shape = RoundedCornerShape(18.dp)
                                ),
                            onClick = {
                                if(isRouteStarted) {
                                    // Acciones cuando el Bottom Sheet está expandido
                                    GlobalScope.launch(Dispatchers.Main) {
                                        cameraPositionState.animate(
                                            update = CameraUpdateFactory.newCameraPosition(
                                                CameraPosition(
                                                    LatLng(
                                                        currentLocation!!.latitude,
                                                        currentLocation!!.longitude
                                                    ), 15f, 50f, 0f
                                                )
                                            ),
                                            durationMs = 2000
                                        )
                                    }
                                }else{
                                    Toast.makeText(context, "You need to start the route", Toast.LENGTH_SHORT).show()
                                }
                            },
                        ){
                            Icon(imageVector = Icons.Filled.Streetview, contentDescription = "", tint=Color(0xff5a79ba))
                        }
                    }
                } else {
                   if(designMode){
                       Row {
                           Button(
                               onClick = {
                                   // Acciones cuando el Bottom Sheet no está expandido
                                   userRoutePoints =  userRoutePoints.dropLast(1)
                               },
                               colors = ButtonDefaults.buttonColors(Color.White),
                               modifier = Modifier.padding(8.dp)
                           ) {
                               //Text("Undo", color = Color.White)
                               Icon(imageVector = Icons.Filled.Undo, contentDescription = "", tint=Color(0xff5a79ba))

                           }
                           Button(
                               onClick = {
                                   // Acciones cuando el Bottom Sheet no está expandido
                                   designMode = false
                                   if(!userRoutePoints.isEmpty())userRoutePoints = userRoutePoints + endPoint
                                   saveRoute()
                               },
                               colors = ButtonDefaults.buttonColors(Color.White),
                               modifier = Modifier.padding(8.dp)
                           ) {
                               //Text("Finish", color = Color.White)
                               Icon(imageVector = Icons.Filled.Save, contentDescription = "", tint=Color(0xff5a79ba))

                           }
                           Button(
                               onClick = {
                                   // Acciones cuando el Bottom Sheet no está expandido
                                         userRoutePoints = emptyList()
                               },
                               colors = ButtonDefaults.buttonColors(Color.White),
                               modifier = Modifier.padding(8.dp)
                           ) {
                               //Text("Clear", color = Color.White)
                               Icon(imageVector = Icons.Filled.LayersClear, contentDescription = "", tint=Color(0xff5a79ba))

                           }
                           // Botón para abrir/cerrar el menú desplegable
                           Button(
                               onClick = { isDropdownMenuExpanded = !isDropdownMenuExpanded },
                               colors = ButtonDefaults.buttonColors(Color.White),
                               modifier = Modifier
                                   .padding(top = 12.dp, start = 8.dp)
                                   .height(40.dp)
                           ) {
                               Box(
                                   modifier = Modifier
                                       .background(selectedColor)
                                       .border(2.dp, Color.White)
                                       .size(20.dp, 20.dp)
                               )
                           }

                           // Menú desplegable de colores
                           DropdownMenu(
                               expanded = isDropdownMenuExpanded,
                               modifier = Modifier
                                   .background(Color.White)
                                   .width(50.dp),
                               onDismissRequest = { isDropdownMenuExpanded = false }
                           ) {
                               availableColors.forEach { color ->
                                   DropdownMenuItem(onClick = {
                                       selectedColor = color
                                       isDropdownMenuExpanded = false
                                   }) {
                                       Box(
                                           modifier = Modifier
                                               .background(color)
                                               .border(2.dp, Color(0xff5a79ba))
                                               .size(20.dp, 20.dp)
                                       )
                                   }
                               }
                           }
                       }
                   }else{
                       Row {
                           FloatingActionButton(
                               containerColor = Color.White,
                               modifier = Modifier
                                   .padding(16.dp)
                                   .shadow(4.dp, shape = RoundedCornerShape(18.dp))
                                   .border(
                                       0.5.dp,
                                       Color(0xff5a79ba),
                                       shape = RoundedCornerShape(18.dp)
                                   ),
                               onClick = { navController.popBackStack() },

                               ) {
                               Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "", tint=Color(0xff5a79ba))
                           }
                           FloatingActionButton(
                               containerColor = Color.White,
                               modifier = Modifier
                                   .padding(16.dp)
                                   .shadow(4.dp, shape = RoundedCornerShape(18.dp))
                                   .border(
                                       0.5.dp,
                                       Color(0xff5a79ba),
                                       shape = RoundedCornerShape(18.dp)
                                   ),
                               onClick = {  designMode = true },

                           ) {
                               Icon(imageVector = Icons.Filled.DesignServices, contentDescription = "", tint=Color(0xff5a79ba))
                           }
                           FloatingActionButton(
                               containerColor = Color.White,
                               modifier = Modifier
                                   .padding(16.dp)
                                   .shadow(4.dp, shape = RoundedCornerShape(18.dp))
                                   .border(
                                       0.5.dp,
                                       Color(0xff5a79ba),
                                       shape = RoundedCornerShape(18.dp)
                                   ),
                               onClick = {   // Acciones cuando el Bottom Sheet no está expandido
                                   scope.launch {
                                       scaffoldState.bottomSheetState.expand()
                                   }
                                         },
                           ) {
                               Icon(imageVector = Icons.Filled.Tune, contentDescription = "", tint=Color(0xff5a79ba))
                           }

                       }
                   }
                }
            }
        }
    }

}




@Composable
fun PointMarker(position: LatLng, title: String, snippet: String?, icon:  BitmapDescriptor, tag:String, click:Boolean){
    val markerState = rememberMarkerState(null, position)
    Marker(
        state = markerState,
        title = title,
        snippet = snippet,
        icon = icon,
        tag = tag
    )

   if(click)markerState.showInfoWindow()

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun BottomSheetContent(
    scope: CoroutineScope,
    scaffoldState: BottomSheetScaffoldState,
    isRouteStarted: Boolean,
    distance: Double,
    onStartRoute: () -> Unit,
    onFinishRoute: () -> Unit,
    route: TransportRoute?,
    start: Warehouse,
    end: Warehouse
) {
    val distanceRounded = (distance * 100.0).roundToLong() / 100.0
    val person = DataRepository.getEmployees()!!.filter{ employee -> employee.id == route!!.carrierId }.firstOrNull()
    val vehicle = DataRepository.getVehicles()!!.filter{ vehicle -> vehicle.id == route!!.assignedVehicleId}.firstOrNull()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f)
            .background(
                color = Color.White,
            )
            .border(
                BorderStroke(1.dp, Color(0xff5a79ba)),
                shape = RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp)
            )
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            androidx.compose.material.Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("${distanceRounded} Km")
                    }
                },
                fontSize = 9.sp,
            )
            androidx.compose.material.Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("${(distanceRounded/80) * 60} Min")
                    }
                },
                fontSize = 9.sp,
                color = Color(0xff5a79ba)
            )
        }
        Row(modifier = Modifier.fillMaxWidth(),  verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Maximize,
                contentDescription = null,
                tint = Color.DarkGray,
                modifier = Modifier
                    .fillMaxWidth()  // Ajusta el valor según tus necesidades
                    .height(35.dp) // Puedes ajustar también la altura si es necesario
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Columna izquierda con el nombre de usuario
            Column(
                modifier = Modifier.weight(1f),  // Proporción relativa
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                androidx.compose.material.Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("")
                        }
                        append(" ${person?.name?.toUpperCase()}")
                    },
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 1.dp)
                )
                androidx.compose.material.Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("")
                        }
                        append(" ${person?.phone?.toUpperCase()}")
                    },
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(bottom = 1.dp)
                )
                Icon(imageVector = Icons.Filled.PersonPin, contentDescription = null )
            }

            // Columna central con la imagen circular
            Column(
                modifier = Modifier.weight(1f),  // Proporción relativa
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)  // Ajusta el tamaño según tus preferencias
                        .background(color = Color(0xff5a79ba), shape = CircleShape)
                ) {


                    if (DataRepository.getUser()!!.url.isNullOrBlank()) {
                        Image(
                            painter = painterResource(id = R.drawable.user),
                            contentDescription = "Default User Image",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)  // Esta línea hará que la imagen sea circular
                        )
                    }else{
                        val painter = rememberImagePainter(
                            data = DataRepository.getUser()!!.url,
                            builder = {
                                crossfade(true)
                                placeholder(R.drawable.loading)
                            }
                        )
                        Image(
                            painter = painter,
                            contentDescription = "User Image",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }





                    // Badge sobre la imagen
                    Box(
                        modifier = Modifier
                            .width(100.dp)  // Ajusta el ancho del badge
                            .height(24.dp) // Ajusta el alto del badge
                            .background(color = Color.White)
                            .border(BorderStroke(1.dp, Color.Black))
                            .padding(2.dp)
                            .align(Alignment.BottomCenter)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(10.dp)  // Ajusta el ancho del badge
                                .height(24.dp)
                                .background(color = Color.Black)
                                .padding(end = 6.dp)
                        ) {
                            Text(text ="L", color = Color.White,                            fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.Center))
                        }
                        // Número de matrícula
                        Text(
                            text = "${vehicle?.licensePlate?.toUpperCase()}",  // Puedes cambiar este número según tus necesidades
                            color = Color.Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(start = 8.dp)
                        )
                    }
                }

            }

            // Columna derecha con el texto a la derecha
            Column(
                modifier = Modifier.weight(1f),  // Proporción relativa
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                androidx.compose.material.Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("")
                        }
                        append(" ${vehicle!!.make?.toUpperCase()}")
                    },
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 1.dp)
                )
                androidx.compose.material.Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("")
                        }
                        append(" ${DataRepository.getVehicles()!!.filter{ vehicle -> vehicle.id == route!!.assignedVehicleId}.firstOrNull()?.model?.toUpperCase()}")
                    },
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(bottom = 1.dp)
                )
                Icon(imageVector = Icons.Filled.DirectionsCar, contentDescription = null )
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        // Añadir una línea de separación gris
        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.fillMaxWidth())

        // Muestra los detalles del almacén dentro de un Card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly

        ){
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(5.dp),  // Proporción relativa
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                androidx.compose.material3.Card(
                    modifier = Modifier
                        .padding(1.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 10.dp
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White,
                    ),
                    shape = RoundedCornerShape(16.dp),

                    ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(1.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Filled.LocationOn, contentDescription = null, tint = Color.Green )
                        Spacer(modifier = Modifier.width(3.dp))
                        Column {
                            // Nombre del almacén
                            androidx.compose.material.Text(
                                buildAnnotatedString {
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("Start:")
                                    }
                                    append(" ${start.name}")
                                },
                                fontSize = 12.sp,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )

                            // Ubicación del almacén
                            androidx.compose.material.Text(
                                buildAnnotatedString {
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("Location:")
                                    }
                                    append(" ${start.location}")
                                },
                                fontSize = 12.sp,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )

                            // Organización del almacén
                            androidx.compose.material.Text(
                                buildAnnotatedString {
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("Organization:")
                                    }
                                    append(" ${start.organization}")
                                },
                                fontSize = 12.sp,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )

                            // Administrador
                            androidx.compose.material.Text(
                                buildAnnotatedString {
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("Administrator:")
                                    }
                                    append(
                                        " ${
                                            DataRepository.getEmployees()
                                                ?.find { user -> user.id == start.idAdministrator }?.name
                                        }"
                                    )
                                },
                                fontSize = 12.sp,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1f),  // Proporción relativa
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                androidx.compose.material3.Card(
                    modifier = Modifier
                        .padding(1.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 10.dp
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White,
                    ),
                    shape = RoundedCornerShape(16.dp),

                    ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(1.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Filled.LocationOn, contentDescription = null, tint = Color.Red )
                        Spacer(modifier = Modifier.width(3.dp))
                        Column {
                            // Nombre del almacén
                            androidx.compose.material.Text(
                                buildAnnotatedString {
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("End:")
                                    }
                                    append(" ${end.name}")
                                },
                                fontSize = 12.sp,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )

                            // Ubicación del almacén
                            androidx.compose.material.Text(
                                buildAnnotatedString {
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("Location:")
                                    }
                                    append(" ${end.location}")
                                },
                                fontSize = 12.sp,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )

                            // Organización del almacén
                            androidx.compose.material.Text(
                                buildAnnotatedString {
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("Organization:")
                                    }
                                    append(" ${end.organization}")
                                },
                                fontSize = 12.sp,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )

                            // Administrador
                            androidx.compose.material.Text(
                                buildAnnotatedString {
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("Administrator:")
                                    }
                                    append(
                                        " ${
                                            DataRepository.getEmployees()
                                                ?.find { user -> user.id == end.idAdministrator }?.name
                                        }"
                                    )
                                },
                                fontSize = 12.sp,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        if(!isRouteStarted){
            ElevatedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                onClick = {
                    onStartRoute.invoke()
                },
                colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                    containerColor = Color(0xff5a79ba)
                ),
                elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                    defaultElevation = 5.dp
                )
            )
            {
                Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start Route", color=Color.White)
            }
        }else{
            ElevatedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                onClick = {
                    onFinishRoute.invoke()
                },
                colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                    containerColor = Color.White
                ),
                elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                    defaultElevation = 5.dp
                )
            )
            {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint= Color(0xff5a79ba))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Finish Route", color=Color(0xff5a79ba))
            }
        }
    }
}


//La fórmula del haversine es una fórmula trigonométrica que calcula la distancia entre dos puntos en una esfera (como la Tierra).
fun haversine(points: List<LatLng>): Double {
    val R = 6371 // Radio de la Tierra en kilómetros
    var totalDistance = 0.0

    for (i in 0 until points.size - 1) {
        val lat1 = points[i].latitude
        val lon1 = points[i].longitude
        val lat2 = points[i + 1].latitude
        val lon2 = points[i + 1].longitude

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        totalDistance += R * c
    }

    return totalDistance
}
