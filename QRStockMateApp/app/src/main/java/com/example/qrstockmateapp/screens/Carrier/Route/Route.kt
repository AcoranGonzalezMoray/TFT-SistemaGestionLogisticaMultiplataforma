package com.example.qrstockmateapp.screens.Carrier.Route

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.LayersClear
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Streetview
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController

import com.example.qrstockmateapp.R
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions


import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Math.atan2
import java.lang.Math.cos
import java.lang.Math.sin
import java.lang.Math.sqrt
import kotlin.math.roundToInt
import kotlin.math.roundToLong


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RouteScreen(navController: NavController) {
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(
            initialValue = BottomSheetValue.Expanded
        )
    )
    val scope = rememberCoroutineScope()
    val startPoint = LatLng(37.419568, -122.086717)
    val endPoint = LatLng(37.423273, -122.080427)
    val launchPoint = LatLng(28.09973, -15.41343)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(launchPoint, 90f)
    }
    var bitmapDescriptor by remember { mutableStateOf<BitmapDescriptor?>(null) }
    var bitmapDescriptorNow by remember { mutableStateOf<BitmapDescriptor?>(null) }

    val context = LocalContext.current
    val availableColors = listOf(
        Color.Red,
        Color.Green,
        Color.Blue,
        Color.Yellow,
        Color.Black
    )

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
                }
            }

        LaunchedEffect(Unit) {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // Puedes mostrar un mensaje indicando que el permiso es necesario aquí
        return Text(text = "Need Location")
    }

    // Ubicación actual del dispositivo
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult?.lastLocation?.let {
                val latLng = LatLng(it.latitude, it.longitude)
                currentLocation = latLng

                // Agregar un mensaje de registro para verificar las actualizaciones de ubicación
                Log.d("LocationUpdates", "Nueva ubicación recibida: $latLng")
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

    // Obtener la ubicación actual al iniciar el Composable
    LaunchedEffect(Unit) {
        // Obtener la última ubicación conocida
        fusedLocationClient.lastLocation
            .addOnSuccessListener { locationResult: Location? ->
                locationResult?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    currentLocation = latLng
                }
            }
        cameraPositionState.animate(
            update = CameraUpdateFactory.newCameraPosition(
                CameraPosition(startPoint, 80f, 0f, 0f)
            ),
            durationMs = 2000
        )
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
                               },
                onFinishRoute = {
                    isRouteStarted = false

                    GlobalScope.launch(Dispatchers.Main) {
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newCameraPosition(
                                CameraPosition(endPoint, 80f, 0f, 0f)
                            ),
                            durationMs = 2000
                        )
                    }
                }
            )
        },
        sheetElevation = 5.dp,
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        scaffoldState = scaffoldState,
        modifier = Modifier.zIndex(30f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 55.dp),
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
                        PointMarker(it, "Ubicación Actual", "Marker en la Ubicación Actual", bitmapDescriptorNow!!, "ubi", false)
                    }
                }

                PointMarker(endPoint, "End Point", "Marker at End Point", bitmapDescriptor!!, "Fin", false)
                PointMarker(startPoint, "Start Point", "Marker at Start Point", bitmapDescriptor!!, "Inicio", true)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center // Alinea el contenido en el centro
            ) {
                if (scaffoldState.bottomSheetState.isExpanded) {
                    Row {
                        Button(
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
                            colors = ButtonDefaults.buttonColors(Color.Black),
                            modifier = Modifier.padding(16.dp)
                        ) {
                            //Text("Focus Location", color = Color.White)
                            Icon(imageVector = Icons.Filled.MyLocation, contentDescription = "", tint=Color.White)

                        }
                        Button(
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
                            colors = ButtonDefaults.buttonColors(Color.Black),
                            modifier = Modifier.padding(16.dp)
                        ) {
                            //Text("Street View", color = Color.White)
                            Icon(imageVector = Icons.Filled.Streetview, contentDescription = "", tint=Color.White)
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
                               colors = ButtonDefaults.buttonColors(Color.Black),
                               modifier = Modifier.padding(8.dp)
                           ) {
                               //Text("Undo", color = Color.White)
                               Icon(imageVector = Icons.Filled.Undo, contentDescription = "", tint=Color.White)

                           }
                           Button(
                               onClick = {
                                   // Acciones cuando el Bottom Sheet no está expandido
                                   designMode = false
                                   if(!userRoutePoints.isEmpty())userRoutePoints = userRoutePoints + endPoint
                               },
                               colors = ButtonDefaults.buttonColors(Color.Black),
                               modifier = Modifier.padding(8.dp)
                           ) {
                               //Text("Finish", color = Color.White)
                               Icon(imageVector = Icons.Filled.Save, contentDescription = "", tint=Color.White)

                           }
                           Button(
                               onClick = {
                                   // Acciones cuando el Bottom Sheet no está expandido
                                         userRoutePoints = emptyList()
                               },
                               colors = ButtonDefaults.buttonColors(Color.Black),
                               modifier = Modifier.padding(8.dp)
                           ) {
                               //Text("Clear", color = Color.White)
                               Icon(imageVector = Icons.Filled.LayersClear, contentDescription = "", tint=Color.White)

                           }
                           // Botón para abrir/cerrar el menú desplegable
                           Button(
                               onClick = { isDropdownMenuExpanded = !isDropdownMenuExpanded },
                               colors = ButtonDefaults.buttonColors(Color.Black ),
                               modifier = Modifier.padding(8.dp)
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
                                   .background(Color.Black)
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
                                               .border(2.dp, Color.White)
                                               .size(20.dp, 20.dp)
                                       )
                                   }
                               }
                           }
                       }
                   }else{
                       Row {
                           Button(
                               onClick = {
                                   // Acciones cuando el Bottom Sheet no está expandido
                                   designMode = true
                               },
                               colors = ButtonDefaults.buttonColors(Color.Black),
                               modifier = Modifier.padding(16.dp)
                           ) {
                               //Text("Design Route",color = Color.White)
                               Icon(imageVector = Icons.Filled.DesignServices, contentDescription = "", tint=Color.White)

                           }
                           Button(
                               onClick = {
                                   // Acciones cuando el Bottom Sheet no está expandido
                                   scope.launch {
                                       scaffoldState.bottomSheetState.expand()
                                   }
                               },
                               colors = ButtonDefaults.buttonColors(Color.Black),
                               modifier = Modifier.padding(16.dp)
                           ) {
                               //Text("Open Options",color = Color.White)
                               Icon(imageVector = Icons.Filled.Tune, contentDescription = "", tint=Color.White)

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
    distance:Double,
    onStartRoute: () -> Unit,
    onFinishRoute: () -> Unit
) {
    val distanceRounded = (distance * 100.0).roundToLong() / 100.0

    // Contenido del Bottom Sheet
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f)
            .background(
                color = Color.Black,
            )
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(
                onClick = {
                    scope.launch {
                        scaffoldState.bottomSheetState.collapse()
                    }
                },
                modifier = Modifier
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "send",
                    tint = Color.White,
                )
            }
            Text("Aprox. Distance: ", color = Color.White)
            Text(text = "${distanceRounded}Km", color = Color.White,fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Añadir una línea de separación gris
        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = "",
            onValueChange = {},
            label = { Text("Texto") },
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        )
        Spacer(modifier = Modifier.height(16.dp))
        if(!isRouteStarted){
            Button(
                onClick = {
                    // AQUI ME DA EL ERROR AL CAMBIARLO
                    onStartRoute.invoke()

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                ,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF006400))
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start Route", color=Color.White)
            }
        }else{
            Button(
                onClick = {
                    onFinishRoute.invoke()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint= Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Finish Route", color=Color.White)
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
