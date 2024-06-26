package com.example.qrstockmateapp.screens.Home.OpenWarehouse

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.qrstockmateapp.R
import com.example.qrstockmateapp.api.models.Item
import com.example.qrstockmateapp.api.services.RetrofitInstance
import com.example.qrstockmateapp.navigation.repository.DataRepository
import com.example.qrstockmateapp.screens.Search.SortOrder
import com.example.qrstockmateapp.screens.Search.sortItems
import com.example.qrstockmateapp.ui.theme.BlueSystem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun OpenWarehouseScreen(navController: NavController){

    val context = LocalContext.current
    var items by remember { mutableStateOf(emptyList<Item>()) }
    var searchQuery by remember { mutableStateOf("") }
    var sortOrder by remember { mutableStateOf(SortOrder.ASCENDING) } // Puedes definir un enum SortOrder con ASCENDING y DESCENDING

    var filteredItems = if (searchQuery.isEmpty()) {
        items
    } else {
        items.filter { item->
            item.name.contains(searchQuery, ignoreCase = true) ||
                    item.location.contains(searchQuery, ignoreCase = true) ||
                    item.stock.toString().contains(searchQuery, ignoreCase = true)
        }
    }


    LaunchedEffect(Unit){
        GlobalScope.launch(Dispatchers.IO){
            try{
                val warehouse = DataRepository.getWarehousePlus()
                if(warehouse!=null){
                    val itemResponse = RetrofitInstance.api.getItems(warehouse.id);

                    if(itemResponse.isSuccessful){
                        val itemIO = itemResponse.body()
                        if(itemIO!=null) items=itemIO
                    }else{
                        try {
                            val errorBody = itemResponse.errorBody()?.string()
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "$errorBody", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Log.e("excepcionUserB", "Error al obtener el cuerpo del error: $e")
                        }
                    }
                }
            }catch (e: Exception){
                Log.d("ExceptionItems", "${e}")
            }


        }
    }
    val sortedItems = sortItems(filteredItems, sortOrder)
    val customTextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        textColor = MaterialTheme.colorScheme.primary,
        backgroundColor = MaterialTheme.colorScheme.outline,
        cursorColor =  BlueSystem,
        focusedBorderColor =  BlueSystem,
        focusedLabelColor = BlueSystem,
        unfocusedBorderColor =  BlueSystem
    )

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { androidx.compose.material.Text("Search", color = MaterialTheme.colorScheme.outlineVariant)  },
            colors = customTextFieldColors,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )


        ElevatedButton(
            modifier = Modifier,
            onClick = {
                // Cambiar el orden de la lista al hacer clic en el botón
                sortOrder = if (sortOrder == SortOrder.ASCENDING) SortOrder.DESCENDING else SortOrder.ASCENDING
                filteredItems = filteredItems.sortedBy { it.stock }  // Cambia esto a tu criterio de ordenación
                if (sortOrder == SortOrder.DESCENDING) {
                    filteredItems = filteredItems.reversed()
                }
            },
            colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                containerColor = Color(0xff5a79ba)
            ),
            elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                defaultElevation = 5.dp
            )
        ){
            androidx.compose.material.Text(
                color = Color.White,
                text = if (sortOrder == SortOrder.ASCENDING) "Sort Ascending" else "Sort Descending"
            )
            Icon(imageVector = Icons.Filled.SwapVert, contentDescription = "sort", tint=Color.White)
        }

        if(sortedItems.isNotEmpty()){
            ItemList(items = sortedItems  , navController = navController)

        }else {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment =  Alignment.CenterVertically
            ) {
                androidx.compose.material.Text(
                    text = "There are no items available in this company \n or in the search made",
                    color = MaterialTheme.colorScheme.outlineVariant,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }



}

@Composable
fun ItemList(items: List<Item>,navController: NavController) {
    LazyColumn {
        items(items) { item ->
           Item(item,navController)
            Spacer(modifier = Modifier.height(8.dp)) // Agrega un espacio entre elementos de la lista
        }
        item {
            Spacer(modifier = Modifier.fillMaxWidth().height(48.dp))
        }
    }
}

@Composable
fun Item(item: Item,navController: NavController) {
    val context = LocalContext.current
    var isloading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit){
        delay(1200)
        isloading = false
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        shape = RoundedCornerShape(16.dp)
    ){
        if (isloading){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .height(200.dp)
                    .background(Color.White.copy(alpha = 0.8f)) // Ajusta el nivel de opacidad aquí
            ) {
                // Muestra el indicador de carga lineal con efecto de cristal
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .align(Alignment.Center),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.9f), // Ajusta el nivel de opacidad aquí
                    trackColor = Color(0xff5a79ba).copy(alpha = 0.1f), // Ajusta el nivel de opacidad aquí
                )
            }
        }else{
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                // Mostrar la imagen del usuario (assumiendo que `url` es una URL de imagen)
                val imageUrl = item.url
                val placeholderImage = painterResource(id = R.drawable.item)

                // Utiliza un Card para aplicar una sombra suave a la imagen del usuario
                Card(
                    modifier = Modifier
                        .size(120.dp)
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 10.dp
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.background,
                    ),
                    shape = RoundedCornerShape(16.dp),
                ){
                    if (imageUrl.isNullOrBlank()) {
                        // Si la URL es nula o vacía, mostrar la imagen por defecto
                        Image(
                            painter = placeholderImage,
                            contentDescription = "Default User Image",
                            modifier = Modifier.fillMaxSize(),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Si hay una URL válida, cargar la imagen usando Coil
                        val painter = rememberImagePainter(
                            data = imageUrl,
                            builder = {
                                crossfade(true)
                                placeholder(R.drawable.item)
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
                    // Nombre (en negrita)
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold,color = MaterialTheme.colorScheme.primary)) {
                                append("Name:")
                            }
                            append(" ${item.name}")
                        },color = MaterialTheme.colorScheme.primary
                    )

                    // Ubicación
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)) {
                                append("Location:")
                            }
                            append(" ${item.location}")
                        },
                        color = MaterialTheme.colorScheme.primary
                    )
                    // Stock (en negrita)
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold,color = MaterialTheme.colorScheme.primary)) {
                                append("Stock:")
                            }
                            append(" ${item.stock}")
                        },color = MaterialTheme.colorScheme.primary
                    )


                    ElevatedButton(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = {
                            if(DataRepository.getUser()?.role == 1 || DataRepository.getUser()?.role == 2 || DataRepository.getUser()?.role == 0 ){
                                DataRepository.setItem(item)
                                navController.navigate("itemDetails")
                            }else{
                                Toast.makeText(context, "permission denied", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                            containerColor = Color(0xff5a79ba)
                        ),
                        elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                            defaultElevation = 5.dp
                        )
                    ){
                        Text(text = "Open", color = Color.White)
                    }

                }
            }
        }
    }
}

