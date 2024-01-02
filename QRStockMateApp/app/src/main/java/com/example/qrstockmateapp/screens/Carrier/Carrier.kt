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
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import org.apache.poi.hpsf.Date


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CarrierScreen(navController: NavController) {
    // Supongamos que tienes una lista de objetos TransportRouteModel
    val listaRutas = listOf(
        TransportRouteModel(1, "Ruta1", "Inicio1", "Fin1", Date(), "", "1;2;4;4;5,2;4;3;", 1, 1),
        TransportRouteModel(2, "Ruta2", "Inicio2", "Fin2", Date(), "1", "3;2;4;4;5,2;4;3;", 2, 2),
        TransportRouteModel(3, "Ruta3", "Inicio3", "Fin3", Date(), "1", "2;2;4;4;5,2;4;3;", 3, 3),
        TransportRouteModel(4, "Ruta4", "Inicio4", "Fin4", Date(), "1", "4;2;4;4;5,2;4;3;", 4, 4),
        TransportRouteModel(5, "Ruta3", "Inicio3", "Fin3", Date(), "", "2;2;4;4;5,2;4;3;", 3, 3),
        TransportRouteModel(6, "Ruta4", "Inicio4", "Fin4", Date(), "1", "4;2;4;4;5,2;4;3;", 4, 4),
        TransportRouteModel(7, "Ruta3", "Inicio3", "Fin3", Date(), "", "2;2;4;4;5,2;4;3;", 3, 3),
        TransportRouteModel(3, "Ruta3", "Inicio3", "Fin3", Date(), "1", "2;2;4;4;5,2;4;3;", 3, 3),
        TransportRouteModel(4, "Ruta4", "Inicio4", "Fin4", Date(), "", "4;2;4;4;5,2;4;3;", 4, 4),
    )

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
fun itemRoute(rutasPorFila: List<TransportRouteModel>, navController: NavController) {
    val context = LocalContext.current
    // Para cada par de rutas en la lista, crea una fila
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        rutasPorFila.forEachIndexed { index, ruta ->
            // Para cada ruta en la fila, crea una Box estilizada
            Box(
                modifier = Modifier
                    .weight(1f) // Cada Box ocupa la mitad del ancho de la fila
                    .padding(8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clipToBounds()
                    .border(1.dp, color = Color.Black, RoundedCornerShape(16.dp))
            ) {
                if (ruta.arrivalTime.toString()!="") {
                    Badge(
                        modifier = Modifier
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
                            .align(Alignment.TopEnd)
                            .offset(x = (-4).dp, y = (5).dp),
                        contentColor = Color.White,
                        backgroundColor = Color(0xFF006400)
                    ) {
                        Text("Available", style = MaterialTheme.typography.labelSmall, color = Color.White)
                    }
                }
                // Contenido de la Box para cada ruta
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Icono arriba
                    Image(
                        painter = painterResource(id = R.drawable.maps), // Reemplaza con tu imagen desde drawable
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp) // Ajusta el tamaño según tus necesidades
                            .clip(shape = RoundedCornerShape(8.dp))
                            .padding(4.dp)
                    )
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

                    Button(
                        onClick = {
                            if(ruta.arrivalTime.toString()==""){
                                navController.navigate("route")
                            }else{
                                Toast.makeText(context, "Route Finished!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black)
                    ) {
                        Text("Open Route", color = Color.White)
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

data class TransportRouteModel(
    val id: Int,
    val code: String,
    val startLocation: String,
    val endLocation: String,
    val departureTime: Date,
    val arrivalTime:String,
    val palets: String,
    val assignedVehicleId: Int,
    val carrierId: Int
)