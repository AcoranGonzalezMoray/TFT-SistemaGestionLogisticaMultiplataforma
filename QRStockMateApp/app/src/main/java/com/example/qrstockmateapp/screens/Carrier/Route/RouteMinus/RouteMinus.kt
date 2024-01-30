package com.example.qrstockmateapp.screens.Carrier.Route.RouteMinus


import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Directions
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
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
import com.example.qrstockmateapp.ui.theme.isDarkMode
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions


import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
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
import java.io.IOException
import java.lang.Math.atan2
import java.lang.Math.cos
import java.lang.Math.sin
import java.lang.Math.sqrt
import java.util.Locale
import kotlin.math.roundToLong



@OptIn(ExperimentalMaterialApi::class, DelicateCoroutinesApi::class)
@Composable
fun RouteMinusScreen(navController: NavController,) {
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(
            initialValue = BottomSheetValue.Collapsed
        )
    )
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    var mapWeight by remember { mutableStateOf(135.dp) }


    var route = DataRepository.getRoutePlus()


    var start = DataRepository.getWarehouses()?.find { warehouse -> warehouse.id == route!!.startLocation.toInt()}
    var end  = DataRepository.getWarehouses()?.find { warehouse -> warehouse.id == route!!.endLocation.toInt()}
    Log.d("SERA?", start!!.latitude.toString())
    val startPoint =  LatLng(start!!.latitude, start.longitude)
    val endPoint = LatLng(end!!.latitude, end.longitude)
    val launchPoint = LatLng(81.444125, 163.066796)



    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(launchPoint, 90f)
    }
    var bitmapDescriptor by remember { mutableStateOf<BitmapDescriptor?>(null) }
    var bitmapDescriptorNow by remember { mutableStateOf<BitmapDescriptor?>(null) }



    ////////////////////////////////////////////
    // Estado para almacenar los puntos de la ruta trazada por el usuario
    var userRoutePoints by remember { mutableStateOf(listOf<LatLng>()) }
    // Estado para almacenar la ubicación actual
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var change by remember { mutableStateOf<Boolean>(true) }
    var IN by remember { mutableStateOf<Boolean>(true) }

    userRoutePoints = convertStringToLatLngList(route!!.route)

    val initLocation = DataRepository.getVehicles()!!.filter { vehicle -> vehicle.id == route!!.assignedVehicleId}[0]
    currentLocation = LatLng(initLocation.location.split(";")[0].toDouble(), initLocation.location.split(";")[1].toDouble())


    val updateLocation: (id: Int) -> Unit = { vehicleId ->
        coroutineScope.launch(Dispatchers.IO) {
            while (IN) {
                try {
                    delay(500)
                    change = false

                    val response = RetrofitInstance.api.getLocationVehicle(vehicleId)
                    val responseRoute = RetrofitInstance.api.getTransportRoute(route.id)
                    if (response.isSuccessful && responseRoute.isSuccessful) {
                        val res = response.body()?.location
                        val status = responseRoute.body()?.status
                        Log.d("Cargando", "${res}")
                        if (res != null) {
                            val locationParts = res.split(";")
                            if (locationParts.size == 2) {
                                if(status == 2){
                                    withContext(Dispatchers.Main){
                                        navController.popBackStack()
                                        Toast.makeText(context, "Route Finished!", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                currentLocation = LatLng(locationParts[0].toDouble(), locationParts[1].toDouble())
                                change = true
                            } else {
                                Log.e("Error", "Respuesta del servidor en un formato inesperado: $res")
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("Error", "Error al procesar la respuesta: $e")
                }
            }
        }
    }


    var geo by remember { mutableStateOf<Address?>(null) }

    var getLocation:() -> Unit = {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                if(currentLocation!=null){
                    geo = Geocoder(context, Locale.getDefault()).getFromLocation(currentLocation!!.latitude, currentLocation!!.longitude, 1)?.get(0)
                }
                Log.d("ACTUALIACION", "${geo}")
            }catch (e: IOException) {
                // Manejar excepciones de geocodificación (pueden ocurrir por problemas de red o límites de uso)
                Log.d("ACTUALIACION", "${e}")
                e.printStackTrace()
            }
        }
    }

    ///////////////////////////////////////////
    LaunchedEffect(context) {
        getLocation()
        coroutineScope.launch {
            updateLocation(initLocation.id)
        }

        val drawable = ContextCompat.getDrawable(context, R.drawable.warehouse)
        val drawableNow = ContextCompat.getDrawable(context, R.drawable.carrierbl)
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

    DisposableEffect(Unit) {
        onDispose {
            coroutineScope.cancel()
        }
    }


    val darkMapStyle = MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_dark)

    BottomSheetScaffold(
        sheetContent = {
            // Contenido del Bottom Sheet
            BottomSheetContent("${geo?.thoroughfare}, ${geo?.subAdminArea}", onReload = { getLocation() },
                com.example.qrstockmateapp.screens.Carrier.Route.haversine(
                    userRoutePoints
                ), route)
        },
        sheetPeekHeight = 150.dp, // Establecer la altura mínima del BottomSheet
        sheetElevation = 5.dp,
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        scaffoldState = scaffoldState,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = mapWeight),
                properties = if(isDarkMode())MapProperties(mapStyleOptions = darkMapStyle ) else MapProperties(),
                cameraPositionState = cameraPositionState
            ) {
                if (userRoutePoints.size >= 2) {
                    Polyline(
                        points = userRoutePoints,
                        color = Color(0xff5a79ba),
                        width = 10f,
                        onClick = {
                            // Manejar clic en la línea si es necesario
                        }
                    )
                }
                // Marcador para la ubicación actual
                currentLocation?.let {
                    if(change){
                        Log.d("CAMBIA", "CAMBIA")
                        PointMarker(it, "Ubicación Actual", "Marker en la Ubicación Actual", bitmapDescriptorNow!!, "ubi", false)
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

                Row {
                    FloatingActionButton(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier
                            .padding(16.dp)
                            .shadow(4.dp, shape = RoundedCornerShape(18.dp))
                            .border(
                                0.5.dp,
                                Color(0xff5a79ba),
                                shape = RoundedCornerShape(18.dp)
                            ),
                        onClick = {
                            IN = false
                            navController.popBackStack()
                                  },

                        ) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "", tint=Color(0xff5a79ba))
                    }

                    FloatingActionButton(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
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
                            }
                        },
                    ){
                        Icon(imageVector = Icons.Filled.MyLocation, contentDescription = "", tint=Color(0xff5a79ba))
                    }
                    FloatingActionButton(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
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
                        },
                    ){
                        Icon(imageVector = Icons.Filled.Streetview, contentDescription = "", tint=Color(0xff5a79ba))
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
    address: String = "",
    onReload: () -> Unit,
    distance: Double,
    route: TransportRoute?
) {
    val distanceRounded = (distance * 100.0).roundToLong() / 100.0
    val person = DataRepository.getEmployees()!!.filter{ employee -> employee.id == route!!.carrierId }.firstOrNull()
    val vehicle = DataRepository.getVehicles()!!.filter{ vehicle -> vehicle.id == route!!.assignedVehicleId}.firstOrNull()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f)
            .background(
                MaterialTheme.colorScheme.secondaryContainer,
            )
            .border(
                BorderStroke(1.dp, Color(0xff5a79ba)),
                shape = RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp)
            )
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            androidx.compose.material.Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) ) {
                        append("${distanceRounded} Km")
                    }
                },
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .clickable {
                        onReload()
                    },
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background,
                )
            ){
                Row(
                    modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = Icons.Filled.Directions, contentDescription = null, tint = MaterialTheme.colorScheme.primary )
                    androidx.compose.material.Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(address.replace("null", "-"))
                            }
                        },
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
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
                    color = MaterialTheme.colorScheme.primary,
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
                    color = MaterialTheme.colorScheme.onTertiary,
                    modifier = Modifier.padding(bottom = 1.dp)
                )
                Icon(imageVector = Icons.Filled.PersonPin, contentDescription = null, tint = MaterialTheme.colorScheme.primary )
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

                    if (person!!.url.isNullOrBlank()) {
                        Image(
                            painter = painterResource(id = R.drawable.user),
                            contentDescription = "Default User Image",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)  // Esta línea hará que la imagen sea circular
                        )
                    }else{
                        val painter = rememberImagePainter(
                            data = person!!.url,
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
                    color = MaterialTheme.colorScheme.primary,
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
                    color = MaterialTheme.colorScheme.onTertiary,
                    modifier = Modifier.padding(bottom = 1.dp)
                )
                Icon(imageVector = Icons.Filled.DirectionsCar, contentDescription = null, tint = MaterialTheme.colorScheme.primary )
            }
        }


        Spacer(modifier = Modifier.height(16.dp))
        // Añadir una línea de separación gris
        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.fillMaxWidth())
    }
}

fun convertStringToLatLngList(input: String): List<LatLng> {
    val regex = Regex("""\(([-+]?\d+\.\d+),([-+]?\d+\.\d+)\)""")
    val matches = regex.findAll(input)
    return matches.map { matchResult ->
        val (lat, lng) = matchResult.destructured
        LatLng(lat.toDouble(), lng.toDouble())
    }.toList()
}
