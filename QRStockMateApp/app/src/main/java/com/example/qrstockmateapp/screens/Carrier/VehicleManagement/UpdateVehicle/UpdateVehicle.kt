package com.example.qrstockmateapp.screens.Carrier.VehicleManagement.UpdateVehicle

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.qrstockmateapp.R
import com.example.qrstockmateapp.api.models.Transaction
import com.example.qrstockmateapp.api.services.RetrofitInstance
import com.example.qrstockmateapp.navigation.repository.DataRepository
import com.example.qrstockmateapp.ui.theme.BlueSystem
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun UpdateVehicleScreen(navController: NavController){
    val vehicle = remember { DataRepository.getVehiclePlus() }
    val context = LocalContext.current
    val jsonString = context.resources.openRawResource(R.raw.models).bufferedReader().use { it.readText() }
    val jsonObject = remember { JSONObject(jsonString) }

    val makes = jsonObject.keys().asSequence().toList()
    var selectedMake by remember { mutableStateOf(vehicle?.make ?: "Select an existing car make") }
    var selectedMakeIndex by remember { mutableStateOf(0) }

    var isMenuExpandedMake by remember { mutableStateOf(false) }

    var selectedModel by remember { mutableStateOf(vehicle?.model ?: "Select an existing model to associate with the car make") }
    var isMenuExpandedModel by remember { mutableStateOf(false) }
    val isloading by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    val updateVehicle : () -> Unit = {

        if (vehicle != null && vehicle.code.isNotBlank() &&
            vehicle.make.isNotBlank() &&
            vehicle.model.isNotBlank() &&
            vehicle.color.isNotBlank() &&
            vehicle.licensePlate.isNotBlank()) {
            GlobalScope.launch(Dispatchers.IO) {
                try {


                    val response =  RetrofitInstance.api.updateVehicle(vehicle)

                    if (response.isSuccessful) {
                        response.body()
                        val user = DataRepository.getUser()
                        if(user!=null){
                            val zonedDateTime = ZonedDateTime.now()
                            val formattedDate = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                            val addTransaccion = RetrofitInstance.api.addHistory(
                                Transaction(0,user.id.toString(),user.code, "The data of the ${vehicle.licensePlate} vehicle has been modified",
                                    formattedDate , 2)
                            )
                            if(addTransaccion.isSuccessful){
                            }else{
                                try {
                                    val errorBody = addTransaccion.errorBody()?.string()
                                    Log.d("Transaccion", errorBody ?: "Error body is null")
                                } catch (e: Exception) {
                                    Log.e("Transaccion", "Error al obtener el cuerpo del error: $e")
                                }
                            }
                        }
                        withContext(Dispatchers.Main){
                            Toast.makeText(context, "Vehicle successfully updated", Toast.LENGTH_SHORT).show()

                            navController.popBackStack()
                        }
                    }

                }catch (e: Exception) {
                    Log.d("excepcionWarehouse","$e")
                }
            }
        }else {
            Toast.makeText(context, "You should not leave empty fields", Toast.LENGTH_SHORT).show()

        }



    }
    val customTextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        textColor = MaterialTheme.colorScheme.primary,
        backgroundColor = MaterialTheme.colorScheme.outline,
        cursorColor =  BlueSystem,
        focusedBorderColor =  BlueSystem,
        focusedLabelColor = BlueSystem,
        unfocusedBorderColor =  BlueSystem
    )


    if (isloading){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray)
        ) {
            // Muestra el círculo de carga
            CircularProgressIndicator(
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.Center),
                color = Color.Black,
                backgroundColor = Color.White
            )
        }
    }else {
        Column {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Login",
                            tint = BlueSystem
                        )
                    }
                },
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                title = { Text(text = "Update Vehicle", color = BlueSystem) }
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .height(300.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.carrierbl), // Reemplaza con tu lógica para cargar la imagen
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    vehicle?.let {
                        var make by remember { mutableStateOf(it.make) }
                        var model by remember { mutableStateOf(it.model) }
                        var year by remember { mutableStateOf(it.year.toString()) }
                        var color by remember { mutableStateOf(it.color) }
                        var licensePlate by remember { mutableStateOf(it.licensePlate) }
                        var maxLoad by remember { mutableStateOf(it.maxLoad.toString()) }

                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .background(Color(0xfff5f6f7))
                            .border(
                                width = 0.5.dp,
                                color = BlueSystem,
                                shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                            )
                        ) {
                            Row(
                                modifier = Modifier.background(color = MaterialTheme.colorScheme.outline),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                androidx.compose.material3.Text(
                                    text = selectedMake,
                                    modifier = Modifier
                                        .weight(9f)
                                        .background(color = MaterialTheme.colorScheme.outline)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            isMenuExpandedMake = true
                                        }
                                        .padding(16.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )

                                DropdownMenu(
                                    expanded = isMenuExpandedMake,
                                    onDismissRequest = { isMenuExpandedMake = false },
                                    modifier = Modifier.fillMaxWidth().background(color = MaterialTheme.colorScheme.secondaryContainer)
                                ) {
                                    makes.forEachIndexed{index, mke ->
                                        DropdownMenuItem(onClick = {
                                            selectedMake= mke
                                            selectedMakeIndex = index
                                            isMenuExpandedMake = false
                                            val makeObject = jsonObject.getJSONObject(selectedMake)
                                            val maxLoadKg = makeObject.getInt("peso_maximo_soportado_kg")
                                            maxLoad = maxLoadKg.toString()
                                            make = selectedMake
                                        }, modifier = Modifier
                                            .padding(5.dp)
                                            .border(
                                                width = 0.5.dp,
                                                color = BlueSystem,
                                                shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                                            )) {
                                            androidx.compose.material3.Text( mke, color = MaterialTheme.colorScheme.primary,)
                                        }
                                    }
                                }
                                Icon(modifier = Modifier.weight(1f), imageVector = Icons.Filled.ArrowDropDown, contentDescription = null, tint =BlueSystem)
                            }
                        }

                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .background(Color(0xfff5f6f7))
                            .border(
                                width = 0.5.dp,
                                color = BlueSystem,
                                shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                            )
                        ) {
                            Row(
                                modifier = Modifier.background(color = MaterialTheme.colorScheme.outline),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                androidx.compose.material3.Text(
                                    text = selectedModel,
                                    modifier = Modifier
                                        .weight(9f)
                                        .background(color = MaterialTheme.colorScheme.outline)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            isMenuExpandedModel = true
                                        }
                                        .padding(16.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )

                                DropdownMenu(
                                    expanded = isMenuExpandedModel,
                                    onDismissRequest = { isMenuExpandedModel = false },
                                    modifier = Modifier.fillMaxWidth().background(color = MaterialTheme.colorScheme.secondaryContainer)
                                ) {
                                    val selectedMake = makes[selectedMakeIndex]
                                    val makeObject = jsonObject.getJSONObject(selectedMake)
                                    val models = makeObject.getJSONArray("modelos")
                                    for (i in 0 until models.length()) {
                                        val localModel = models.getString(i)
                                        DropdownMenuItem(onClick = {
                                            selectedModel= localModel
                                            model = localModel
                                            isMenuExpandedModel = false
                                        }, modifier = Modifier
                                            .padding(5.dp)
                                            .border(
                                                width = 0.5.dp,
                                                color = BlueSystem,
                                                shape = RoundedCornerShape(8.dp) // Ajusta el radio según tus preferencias

                                            )) {
                                            androidx.compose.material3.Text( localModel, color = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                                Icon(modifier = Modifier.weight(1f), imageVector = Icons.Filled.ArrowDropDown, contentDescription = null, tint =BlueSystem)
                            }
                        }

                        TextField(
                            value = year,
                            label = { Text("Year", color = MaterialTheme.colorScheme.outlineVariant) },
                            onValueChange = { year = it },
                            colors = customTextFieldColors ,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    focusManager.moveFocus(FocusDirection.Down)
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                                .border(
                                    width = 0.5.dp,
                                    color = BlueSystem,
                                    shape = RoundedCornerShape(8.dp)
                                )
                        )

                        TextField(
                            value = color,
                            label = { Text("Color", color = MaterialTheme.colorScheme.outlineVariant) },
                            onValueChange = { color = it },
                            colors = customTextFieldColors ,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    focusManager.moveFocus(FocusDirection.Down)
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                                .border(
                                    width = 0.5.dp,
                                    color = BlueSystem,
                                    shape = RoundedCornerShape(8.dp)
                                )
                        )

                        TextField(
                            value = licensePlate,
                            label = { Text("License Plate", color = MaterialTheme.colorScheme.outlineVariant) },
                            onValueChange = { licensePlate = it },
                            colors = customTextFieldColors ,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    focusManager.moveFocus(FocusDirection.Down)
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                                .border(
                                    width = 0.5.dp,
                                    color = BlueSystem,
                                    shape = RoundedCornerShape(8.dp)
                                )
                        )

                        TextField(
                            value = maxLoad,
                            label = { Text("Max Load", color = MaterialTheme.colorScheme.outlineVariant) },
                            onValueChange = { maxLoad = it },
                            colors = customTextFieldColors ,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    focusManager.moveFocus(FocusDirection.Down)
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                                .border(
                                    width = 0.5.dp,
                                    color = BlueSystem,
                                    shape = RoundedCornerShape(8.dp)
                                )
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ){
                            ElevatedButton(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                onClick = {
                                    navController.popBackStack()
                                },
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                ),
                                elevation = ButtonDefaults.elevatedButtonElevation(
                                    defaultElevation = 5.dp
                                )
                            ){
                                androidx.compose.material3.Text(text = "Cancel", color = BlueSystem)
                            }

                            ElevatedButton(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                onClick = {
                                    vehicle.licensePlate = licensePlate
                                    vehicle.make = make
                                    vehicle.maxLoad = maxLoad.toDouble()
                                    vehicle.model = model
                                    vehicle.year = year.toInt()
                                    vehicle.color = color
                                    updateVehicle()
                                },
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = BlueSystem
                                ),
                                elevation = ButtonDefaults.elevatedButtonElevation(
                                    defaultElevation = 5.dp
                                )
                            ){
                                androidx.compose.material3.Text(text = "Update", color = Color.White)
                            }
                        }
                    }

                }
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp))
            }

        }

    }
}