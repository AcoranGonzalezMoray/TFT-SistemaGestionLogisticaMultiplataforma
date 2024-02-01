package com.example.qrstockmateapp.screens.Chats.Contact

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.qrstockmateapp.R
import com.example.qrstockmateapp.api.models.User
import com.example.qrstockmateapp.navigation.repository.DataRepository
import com.example.qrstockmateapp.ui.theme.BlueSystem

@Composable
fun ContactScreen(navController: NavController) {
    val employees = DataRepository.getEmployees()!!.filter { user: User -> user.id!=DataRepository.getUser()!!.id  }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn {
            items(employees) { employee ->
                EmployeeItem(employee, navController)
                Divider(modifier = Modifier.padding(start = 16.dp, end = 16.dp))
            }
        }
    }

}

@Composable
fun EmployeeItem(employee: User, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
       Row {
           // Image

           if (employee.url.isNullOrBlank()) {
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
                   data = employee.url,
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
           // Space between image and text
           Spacer(modifier = Modifier.width(16.dp))

           // Employee details
           Column {
               Text(text = employee.name, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
               Text(text = employee.email, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
               Row(
                   verticalAlignment = Alignment.CenterVertically,
                   modifier = Modifier.background(MaterialTheme.colorScheme.background)
               ) {
                   Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(15.dp))
                   Spacer(modifier = Modifier.width(8.dp))
                   Text(text = employee.phone, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
               }
           }
       }

        ElevatedButton(
            onClick = {
                DataRepository.setUserPlus(employee)
                navController.navigate("chat")
            },
            colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                containerColor = BlueSystem
            ),
            elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                defaultElevation = 5.dp
            )
        ){
            Text("Chat +", color = Color.White)
        }
    }
}
