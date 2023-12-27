package com.example.qrstockmateapp.screens.Search

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.qrstockmateapp.R
import com.example.qrstockmateapp.api.models.Item
import com.example.qrstockmateapp.api.models.User
import com.example.qrstockmateapp.api.models.Warehouse
import com.example.qrstockmateapp.api.models.userRoleToString
import com.example.qrstockmateapp.api.services.RetrofitInstance
import com.example.qrstockmateapp.navigation.repository.DataRepository
import com.example.qrstockmateapp.screens.Home.ManageUser.UserListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
enum class SortOrder {
    ASCENDING,
    DESCENDING
}
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchScreen(navController: NavController) {
    val context = LocalContext.current;
    val listaAlmacenes = DataRepository.getWarehouses();
    var listaItems by remember { mutableStateOf( mutableListOf<Item>()) };
    var searchQuery by remember { mutableStateOf("") };

    var sortOrder by remember { mutableStateOf(SortOrder.ASCENDING) } // Puedes definir un enum SortOrder con ASCENDING y DESCENDING

    var filteredItems = if (searchQuery.isEmpty()) {
        listaItems
    } else {
        listaItems.filter { item->
            item.name.contains(searchQuery, ignoreCase = true) ||
                    item.location.contains(searchQuery, ignoreCase = true) ||
                    item.stock.toString().contains(searchQuery, ignoreCase = true)
        }
    }

    LaunchedEffect(Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            if (listaAlmacenes != null) {
                for(warehouse in listaAlmacenes) {
                    if (warehouse != null) {
                        try {
                            val itemResponse = RetrofitInstance.api.getItems(warehouse.id);
                            if (itemResponse.isSuccessful) {
                                val item = itemResponse.body()
                                if (item != null) listaItems.addAll(item.toMutableList());

                            } else {
                                Log.d("ItemsNotSuccessful", "NO")
                            }

                        } catch (e: Exception) {
                            Log.d("ExceptionItems", "${e.message}")
                        }
                    }
                }
                Log.d("Lista de Items", "${listaItems}")
            } else {
                Log.d("La lista es NULL", "NULL")
            }
        }
    }

    val customTextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        cursorColor = Color.Black,
        focusedBorderColor = Color.Black,
        focusedLabelColor = Color.Black,
        unfocusedBorderColor = Color.Black,
        backgroundColor = Color.LightGray
    )

    val sortedItems = sortItems(filteredItems, sortOrder)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search") },
            colors = customTextFieldColors,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )
        Button( colors = ButtonDefaults.buttonColors(Color.Black),
            onClick = {
            // Cambiar el orden de la lista al hacer clic en el botón
            sortOrder = if (sortOrder == SortOrder.ASCENDING) SortOrder.DESCENDING else SortOrder.ASCENDING
            filteredItems = filteredItems.sortedBy { it.stock }  // Cambia esto a tu criterio de ordenación
            if (sortOrder == SortOrder.DESCENDING) {
                filteredItems = filteredItems.reversed()
            }
        }) {
            Text(color= Color.White,text = if (sortOrder == SortOrder.ASCENDING) "Sort Ascending" else "Sort Descending")
        }
        if(filteredItems.isNotEmpty()){
            ItemList(items = sortedItems, navController = navController)

        }else{
            Box {
                Text(text = "There are no items in this company")
            }
        }
    }

}


@Composable
fun ItemList(items: List<Item>,navController: NavController) {
    LazyColumn {
        items(items) { item ->
            Item(item = item, navController = navController)
            Spacer(modifier = Modifier.height(8.dp)) // Agrega un espacio entre elementos de la lista
        }
    }
    Spacer(modifier = Modifier.height(60.dp))
}

@Composable
fun Item(item: Item,navController: NavController) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
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
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .shadow(
                        elevation = 5.dp,
                    )
                    .padding(16.dp)
            ){
                if (imageUrl.isNullOrBlank()) {
                    // Si la URL es nula o vacía, mostrar la imagen por defecto
                    Image(
                        painter = placeholderImage,
                        contentDescription = "Default User Image",
                        modifier = Modifier.fillMaxSize(),
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
                Text(
                    text = "Name: ${item.name}",
                    fontWeight = FontWeight.Bold
                )
                Text(text = "Warehouse: ${DataRepository.getWarehouses()?.find{ it.id == item.warehouseId}?.name}")
                Text(text = "Stock: ${item.stock}", fontWeight = FontWeight.Bold)
                Button(
                    onClick = {
                       if(DataRepository.getUser()?.role !=3){
                           DataRepository.setItem(item)
                           navController.navigate("itemDetails")
                       }else{
                           Toast.makeText(context, "permission denied", Toast.LENGTH_SHORT).show()
                       }
                    },
                    colors = ButtonDefaults.buttonColors(Color.Black)
                ) {
                    Text("Open", color = Color.White)
                }
            }
        }
    }
}


fun sortItems(items: List<Item>, sortOrder: SortOrder): List<Item> {
    return when (sortOrder) {
        SortOrder.ASCENDING -> items.sortedBy { it.stock }
        SortOrder.DESCENDING -> items.sortedByDescending { it.stock }
    }
}