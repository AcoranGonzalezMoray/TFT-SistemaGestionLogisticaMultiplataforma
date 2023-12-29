package com.example.qrstockmateapp.screens.Carrier.Route

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.unit.dp
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

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


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
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(startPoint, 80f)
    }
    var bitmapDescriptor by remember { mutableStateOf<BitmapDescriptor?>(null) }
    var bitmapDescriptorNow by remember { mutableStateOf<BitmapDescriptor?>(null) }

    val context = LocalContext.current

    ////////////////////////////////////////////
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
            BottomSheetContent(scope, scaffoldState,isRouteStarted,
                onStartRoute = {
                    isRouteStarted = true
                    // Ajusta los valores de latitud y longitud según tus necesidades
                    val newCameraPosition = CameraPosition.builder()
                        .target(LatLng(currentLocation!!.latitude, currentLocation!!.longitude))
                        .zoom(15f) // Ajusta el nivel de zoom según tus necesidades
                        .tilt(50f) // Ajusta la inclinación según tus necesidades
                        .build()

                    cameraPositionState.position = newCameraPosition
                               },
                onFinishRoute = {
                    isRouteStarted=false
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
                cameraPositionState = cameraPositionState
            ) {
                if (isRouteStarted) {
                    Polyline(
                        points = listOf(startPoint, endPoint),
                        color = androidx.compose.ui.graphics.Color.Blue,
                        width = 5f,
                        onClick = {
                            // Maneja el clic en la línea si es necesario
                        }
                    )
                    // Marcador para la ubicación actual
                    currentLocation?.let {
                        Marker(
                            state = MarkerState(position = it),
                            title = "Ubicación Actual",
                            snippet = "Marker en la Ubicación Actual",
                            icon = bitmapDescriptorNow
                        )
                    }
                }
                Marker(
                    state = MarkerState(position = startPoint),
                    title = "Start Point",
                    snippet = "Marker at Start Point",
                    icon = bitmapDescriptor,
                    tag = "Inicio"
                )

                Marker(
                    state = MarkerState(position = endPoint),
                    title = "End Point",
                    snippet = "Marker at End Point",
                    icon = bitmapDescriptor,
                    tag = "Fin"
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center // Alinea el contenido en el centro
            ) {
                if (scaffoldState.bottomSheetState.isExpanded) {
                    Button(
                        onClick = {
                            // Acciones cuando el Bottom Sheet está expandido
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                                LatLng(currentLocation!!.latitude, currentLocation!!.longitude),
                                80f
                            )
                        },
                        colors = ButtonDefaults.buttonColors(Color.Black),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Focus on Current Location", color = Color.White)
                    }
                } else {
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
                        Text("Open Options",color = Color.White)
                    }
                }
            }
        }
    }

}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun BottomSheetContent(
    scope: CoroutineScope,
    scaffoldState: BottomSheetScaffoldState,
    isRouteStarted: Boolean,
    onStartRoute: () -> Unit,
    onFinishRoute: () -> Unit
) {
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
            Text("Contenido del Bottom Sheet", color = Color.White)
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