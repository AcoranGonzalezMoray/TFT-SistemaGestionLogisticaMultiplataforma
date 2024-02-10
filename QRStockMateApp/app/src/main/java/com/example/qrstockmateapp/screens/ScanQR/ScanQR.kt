package com.example.qrstockmateapp.screens.ScanQR

import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.qrstockmateapp.R
import com.example.qrstockmateapp.api.models.Item
import com.example.qrstockmateapp.navigation.repository.DataRepository
import com.example.qrstockmateapp.qr.BarcodeAnalyser
import java.util.concurrent.Executors

@Composable
@ExperimentalGetImage
fun ScanScreen(navController: NavController) {
    val user = DataRepository.getUser()

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.qrscan))
    val progress by animateLottieCompositionAsState(composition = composition, iterations = LottieConstants.IterateForever)

    // Contenedor principal
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Vista de la cámara
        AndroidView({ context ->
            val cameraExecutor = Executors.newSingleThreadExecutor()
            val previewView = PreviewView(context).also {
                it.scaleType = PreviewView.ScaleType.FILL_CENTER
            }
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                val imageCapture = ImageCapture.Builder().build()

                val imageAnalyzer = ImageAnalysis.Builder()
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor) { image ->
                            BarcodeAnalyser { qrCodeValue ->
                                if(user?.role!=3){
                                    addItem(qrCodeValue)
                                    navController.navigate("addItem")
                                    Toast.makeText(context, "QR Code found: ${qrCodeValue.split(';')[1]}", Toast.LENGTH_SHORT).show()
                                }else{
                                    Toast.makeText(context, "you do not have permission to scan", Toast.LENGTH_SHORT).show()
                                }
                            }.analyze(image)
                        }
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    // Unbind use cases before rebinding
                    cameraProvider.unbindAll()

                    // Bind use cases to camera
                    cameraProvider.bindToLifecycle(
                        context as ComponentActivity, cameraSelector, preview, imageCapture, imageAnalyzer)

                } catch(exc: Exception) {
                    Log.e("DEBUG", "Use case binding failed", exc)
                }
            }, ContextCompat.getMainExecutor(context))
            previewView
        },
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
        )

        // Cuadro de guía visual

        Box(
            modifier = Modifier
                .size(400.dp),
            contentAlignment = Alignment.Center
        ) {
            // Puedes personalizar el cuadro de guía visual según tus necesidades
            LottieAnimation(
                modifier = Modifier.size(400.dp),
                composition = composition,
                progress = { progress }
            )

        }
    }
}

fun addItem(qrCodeValue: String) {
    if (qrCodeValue != null) {
        val parts = qrCodeValue.split(';')
        val productId = parts[0].toInt()
        val name = parts[1]
        var warehouseId = parts[2].toInt()
        var location = parts[3]
        var stock = parts[4].toInt()
        var imageUrl = ""
        if (parts[5] != "null") imageUrl = parts[5]
        var weightPerUnit = parts[6].toDouble()
        DataRepository.setItem(Item(productId, name, warehouseId, location, stock, imageUrl, weightPerUnit))
    }
}