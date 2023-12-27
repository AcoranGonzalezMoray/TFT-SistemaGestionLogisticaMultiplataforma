package com.example.qrstockmateapp.screens.TransactionHistory

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
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
import com.example.qrstockmateapp.api.models.Warehouse
import com.example.qrstockmateapp.api.models.operationToString
import com.example.qrstockmateapp.api.services.RetrofitInstance
import com.example.qrstockmateapp.navigation.repository.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionHistoryScreen(navController: NavController) {
    var transactionList by remember { mutableStateOf(emptyList<Transaction>()) }
    var searchQuery by remember { mutableStateOf("") };

    val customTextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        cursorColor = Color.Black,
        focusedBorderColor = Color.Black,
        focusedLabelColor = Color.Black,
        unfocusedBorderColor = Color.Black,
        backgroundColor = Color.LightGray
    )

    var filteredItems = if (searchQuery.isEmpty()) {
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
            val response  = RetrofitInstance.api.getHistory()
            if(response.isSuccessful){
                val responseBody = response.body()
                if(responseBody!=null){
                    transactionList = responseBody.filter { it.code == DataRepository.getUser()?.code }
                }
            }
        }
    }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if(DataRepository.getUser()?.role!=3){
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search") },
                colors = customTextFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )
            Button(modifier = Modifier.align(Alignment.CenterHorizontally),onClick = {
                downloadTransactionList(context = context, transactionList = transactionList, fileName = "transactions${LocalDateTime.now()}.xlsx")
            }) {
                Text(text = "Download")
            }
            LazyColumn {
                items(filteredItems) { transaction ->
                    TransactionListItem(transaction = transaction)
                }
            }
        }else{
            Text(
                text = "Permission denied",
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
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "ID: ${transaction.id}",
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "USER: ${transaction.name}",
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Code: ${transaction.code}",
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Description: ${transaction.description}",
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Created: ${transaction.created}",
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Operation: ${operationToString(transaction.operation)}",
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurface
            )
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
    Log.d("URI", "${uri}")
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