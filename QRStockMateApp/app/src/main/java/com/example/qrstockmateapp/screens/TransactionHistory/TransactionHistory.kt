package com.example.qrstockmateapp.screens.TransactionHistory

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.example.qrstockmateapp.R
import com.example.qrstockmateapp.api.models.Transaction
import com.example.qrstockmateapp.api.models.operationToString
import com.example.qrstockmateapp.api.services.RetrofitInstance
import com.example.qrstockmateapp.navigation.repository.DataRepository
import com.example.qrstockmateapp.screens.Search.SortOrder
import com.example.qrstockmateapp.ui.theme.BlueSystem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionHistoryScreen(navController: NavController) {
    var transactionList by remember { mutableStateOf(emptyList<Transaction>()) }
    var searchQuery by remember { mutableStateOf("") };
    var sortOrder by remember { mutableStateOf(SortOrder.ASCENDING) } // Puedes definir un enum SortOrder con ASCENDING y DESCENDING
    var filteredItems by remember { mutableStateOf<List<Transaction>>(emptyList()) }

    val customTextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        textColor = MaterialTheme.colorScheme.primary,
        backgroundColor = MaterialTheme.colorScheme.outline,
        cursorColor =  BlueSystem,
        focusedBorderColor =  BlueSystem,
        focusedLabelColor = BlueSystem,
        unfocusedBorderColor =  BlueSystem
    )

    filteredItems = if (searchQuery.isEmpty()) {
        transactionList
    } else {
        transactionList.filter { item->
            item.name.contains(searchQuery, ignoreCase = true) ||
                    item.id.toString().contains(searchQuery, ignoreCase = true) ||
                    item.code.contains(searchQuery, ignoreCase = true)||
                    item.created.contains(searchQuery, ignoreCase = true)||
                    item.name.contains(searchQuery, ignoreCase = true)||
                    item.description.contains(searchQuery, ignoreCase = true)||
                    item.operation.toString().contains(searchQuery, ignoreCase = true)
        }
    }

    LaunchedEffect(Unit){
        GlobalScope.launch(Dispatchers.IO) {
            val response  = RetrofitInstance.api.getHistory(DataRepository.getUser()!!.code)
            if(response.isSuccessful){
                val responseBody = response.body()
                if(responseBody!=null){
                    transactionList = responseBody
                }
            }
        }
    }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        if(DataRepository.getUser()?.role!=3 && DataRepository.getUser()?.role!=4){
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search", color = MaterialTheme.colorScheme.outlineVariant) },
                colors = customTextFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                ElevatedButton(
                    modifier = Modifier.weight(0.5f).padding(5.dp),
                    onClick = {
                        sortOrder = if (sortOrder == SortOrder.ASCENDING) SortOrder.DESCENDING else SortOrder.ASCENDING
                        filteredItems = when (sortOrder) {
                            SortOrder.ASCENDING -> {
                                filteredItems.sortedBy { it.id }
                            }
                            SortOrder.DESCENDING -> {
                                filteredItems.sortedByDescending { it.id }
                            }
                        }


                    },
                    colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                        containerColor = Color(0xff5a79ba)
                    ),
                    elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = 5.dp
                    )
                ){
                    Text(
                        color = Color.White,
                        text = if (sortOrder == SortOrder.ASCENDING) "Sort Ascending" else "Sort Descending"
                    )
                    Icon(imageVector = Icons.Filled.SwapVert, contentDescription = "sort", tint=Color.White)
                }
                ElevatedButton(
                    modifier = Modifier.weight(0.5f).padding(5.dp),
                    onClick = {
                        downloadTransactionList(context = context, transactionList = transactionList, fileName = "transactions${LocalDateTime.now()}.xlsx")
                    },
                    colors = androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                        containerColor = Color(0xff5a79ba)
                    ),
                    elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = 5.dp
                    )
                ){
                    Text(text = "Download", color = Color.White)
                    Icon(imageVector = Icons.Filled.Download, contentDescription ="download", tint = Color.White )
                }
            }

           if (filteredItems.isNotEmpty()){
               LazyColumn {
                   items(filteredItems) { transaction ->
                       TransactionListItem(transaction = transaction)
                   }
                   item {
                       Spacer(modifier = Modifier
                           .fillMaxWidth()
                           .height(60.dp))
                   }
               }
           }else {
               Row(
                   modifier = Modifier.fillMaxSize(),
                   horizontalArrangement = Arrangement.Center,
                   verticalAlignment =  Alignment.CenterVertically
               ) {
                   Text(
                       text = "There are no transactions available in this company",
                       color = MaterialTheme.colorScheme.outlineVariant,
                       style = TextStyle(
                           fontWeight = FontWeight.Bold
                       )
                   )
               }
           }
        }else{
            Text(
                text = "Permission denied",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}


@Composable
fun TransactionListItem(transaction: Transaction) {
    var isloading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit){
        delay(1200)
        isloading = false
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp
    ) {
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
           Column(
               modifier = Modifier
                   .fillMaxSize()
                   .background(color = MaterialTheme.colorScheme.secondaryContainer)
                   .padding(16.dp)
           ) {
               Text(
                   text = "ID: ${transaction.id}",
                   style = MaterialTheme.typography.bodyMedium,
                   color = MaterialTheme.colorScheme.primary
               )
               Spacer(modifier = Modifier.height(8.dp))
               Text(
                   text = "USER: ${transaction.name}",
                   style = MaterialTheme.typography.bodyMedium,
                   color = MaterialTheme.colorScheme.primary
               )
               Spacer(modifier = Modifier.height(8.dp))
               Text(
                   text = "Code: ${transaction.code}",
                   style = MaterialTheme.typography.bodyMedium,
                   color = MaterialTheme.colorScheme.primary
               )
               Spacer(modifier = Modifier.height(8.dp))
               Text(
                   text = "Description: ${transaction.description}",
                   style = MaterialTheme.typography.bodyMedium,
                   color = MaterialTheme.colorScheme.primary
               )
               Spacer(modifier = Modifier.height(8.dp))
               Text(
                   text = "Created: ${transaction.created}",
                   style = MaterialTheme.typography.bodyMedium,
                   color = MaterialTheme.colorScheme.primary
               )
               Spacer(modifier = Modifier.height(8.dp))
               Text(
                   text = "Operation: ${operationToString(transaction.operation)}",
                   style = MaterialTheme.typography.bodyMedium,
                   color = MaterialTheme.colorScheme.primary
               )
           }
       }
    }
}

fun downloadTransactionList(context: Context, transactionList: List<Transaction>, fileName: String) {
    try {
        if (isExternalStorageWritable()) {
            val externalDir = context.getExternalFilesDir(null)
            val file = File(externalDir, fileName)

            // Crea un libro de trabajo de Excel
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Transaction Data")

            // Crea el encabezado de la hoja de cálculo
            val headerRow: Row = sheet.createRow(0)
            val headers = arrayOf("ID", "User", "Code", "Description", "Created", "Operation")
            for ((index, header) in headers.withIndex()) {
                val cell: Cell = headerRow.createCell(index)
                cell.setCellValue(header)
            }
            // Llena la hoja de cálculo con datos de transacciones
            for ((rowIndex, transaction) in transactionList.withIndex()) {
                val row: Row = sheet.createRow(rowIndex + 1)

                row.createCell(0).setCellValue("${transaction.id}")
                row.createCell(1).setCellValue(transaction.name)
                row.createCell(2).setCellValue(transaction.code)
                row.createCell(3).setCellValue(transaction.description)
                row.createCell(4).setCellValue(transaction.created) // Convierte LocalDateTime a String
                row.createCell(5).setCellValue(operationToString(transaction.operation))
            }

            // Guarda el libro de trabajo en el archivo
            FileOutputStream(file).use { fos ->
                workbook.write(fos)
            }

            workbook.close()

            showNotification(context, "Download Complete", "File saved in $externalDir", file)
        } else {
            // Maneja el caso en el que no se puede escribir en el almacenamiento externo
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun isExternalStorageWritable(): Boolean {
    val state = Environment.getExternalStorageState()
    return Environment.MEDIA_MOUNTED == state
}

private fun showNotification(context: Context, title: String, message: String, file: File) {
    val notificationManager = ContextCompat.getSystemService(
        context,
        NotificationManager::class.java
    ) as NotificationManager?

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Crear un canal de notificación para versiones de Android 8.0 y superiores
        val channel = NotificationChannel(
            "default",
            "Downloads",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager?.createNotificationChannel(channel)
    }

    val notificationIntent = Intent(Intent.ACTION_VIEW)
    val uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
    notificationIntent.setDataAndType(uri, "text/plain")
    notificationIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        notificationIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )


    val notification = NotificationCompat.Builder(context, "default")
        .setContentTitle(title)
        .setContentText(message)
        .setSmallIcon(R.drawable.icon)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()

    notificationManager?.notify(1, notification)
}