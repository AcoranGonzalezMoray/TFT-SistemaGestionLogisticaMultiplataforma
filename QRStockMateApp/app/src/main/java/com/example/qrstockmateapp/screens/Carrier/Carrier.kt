package com.example.qrstockmateapp.screens.Carrier


import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Badge
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.qrstockmateapp.R
import com.example.qrstockmateapp.api.models.TransportRoute
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import org.apache.poi.hpsf.Date


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CarrierScreen(navController: NavController) {
    // Supongamos que tienes una lista de objetos TransportRouteModel
    val listaRutas = listOf(
        TransportRoute(1, "Ruta1", "Inicio1", "Fin1", Date().toString(), "", "1;2;4;4;5,2;4;3;", 1, 1,Date().toString(), 0),
        TransportRoute(2, "Ruta2", "Inicio2", "Fin2", Date().toString(), "1", "3;2;4;4;5,2;4;3;", 2, 2,Date().toString(), 0),
        TransportRoute(3, "Ruta3", "Inicio3", "Fin3", Date().toString(), "1", "2;2;4;4;5,2;4;3;", 3, 3,Date().toString(), 0),
        TransportRoute(4, "Ruta4", "Inicio4", "Fin4", Date().toString(), "1", "4;2;4;4;5,2;4;3;", 4, 4,Date().toString(), 0),
        TransportRoute(5, "Ruta3", "Inicio3", "Fin3", Date().toString(), "", "2;2;4;4;5,2;4;3;", 3, 3,Date().toString(), 0),
        TransportRoute(6, "Ruta4", "Inicio4", "Fin4", Date().toString(), "1", "4;2;4;4;5,2;4;3;", 4, 4,Date().toString(), 0),
        TransportRoute(7, "Ruta3", "Inicio3", "Fin3", Date().toString(), "", "2;2;4;4;5,2;4;3;", 3, 3,Date().toString(), 0),
        TransportRoute(3, "Ruta3", "Inicio3", "Fin3", Date().toString(), "1", "2;2;4;4;5,2;4;3;", 3, 3,Date().toString(), 0),
        TransportRoute(4, "Ruta4", "Inicio4", "Fin4", Date().toString(), "", "4;2;4;4;5,2;4;3;", 4, 4,Date().toString(), 0))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(listaRutas.chunked(2)) { rutasPorFila ->
                itemRoute(rutasPorFila, navController)
            }
        }
    }
}
@Composable
fun itemRoute(rutasPorFila: List<TransportRoute>, navController: NavController) {
    val context = LocalContext.current
    // Para cada par de rutas en la lista, crea una fila
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        rutasPorFila.forEachIndexed { index, ruta ->
            // Para cada ruta en la fila, crea una Box estilizada
            Card(
                modifier = Modifier
                    .weight(1f) // Cada Box ocupa la mitad del ancho de la fila
                    .padding(8.dp)
                ,
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White,
                ),
                shape = RoundedCornerShape(16.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                ){
                    if (ruta.arrivalTime.toString()!="") {
                        Badge(
                            modifier = Modifier
                                .height(20.dp)
                                .width(80.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = (-4).dp, y = (5).dp),
                            contentColor = Color.White,
                            backgroundColor = Color.Red
                        ) {
                            Text("Finished", style = MaterialTheme.typography.labelSmall, color = Color.White)
                        }
                    }else{
                        Badge(
                            modifier = Modifier
                                .height(20.dp)
                                .width(80.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = (-4).dp, y = (5).dp),
                            contentColor = Color.White,
                            backgroundColor = Color(0xFF006400)
                        ) {
                            Text("Available", style = MaterialTheme.typography.labelSmall, color = Color.White)
                        }

                    }
                }
                // Contenido de la Box para cada ruta
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        ,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier
                            .padding(10.dp)
                            .size(120.dp),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 1.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White,
                        )
                    ){
                        // Icono arriba
                        Image(
                            painter = painterResource(id = R.drawable.maps), // Reemplaza con tu imagen desde drawable
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .fillMaxHeight()
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    // Textos
                    Text(
                        text = "ID: ${ruta.id}",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Código: ${ruta.code}",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Inicio: ${ruta.startLocation}",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Fin: ${ruta.endLocation}",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ElevatedButton(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(5.dp)
                            .height(40.dp),
                        onClick = {
                            if(ruta.arrivalTime.toString()==""){
                                navController.navigate("route")
                            }else{
                                Toast.makeText(context, "Route Finished!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors =  androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                            containerColor = Color(0xff5a79ba)
                        ),
                        elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                            defaultElevation = 5.dp
                        )


                    ) {
                        Text("Open", color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(imageVector = Icons.Filled.Map, contentDescription = null, tint = Color.White)
                    }
                }
            }

            // Añadir una Box vacía si es el último elemento único en la fila
            if (index == rutasPorFila.size - 1 && rutasPorFila.size % 2 == 1) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                ) {}
            }
        }
    }
}