package com.example.qrstockmateapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.qrstockmateapp.R
import com.example.qrstockmateapp.navigation.repository.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext


@Composable
fun splashScreen(navController: NavController, type:Int = 0){

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(if(type==0)R.raw.splash_fast else R.raw.message))
    val progress by animateLottieCompositionAsState(composition = composition, isPlaying = true, speed = if(type==0)4f else 1f)
    val route = DataRepository.getSplash()

    LaunchedEffect(true){
        delay(2500)
        if (route != null) {
            withContext(Dispatchers.Main){
                navController.navigate(route)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.secondaryContainer).zIndex(50f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        LottieAnimation(
            modifier = Modifier.size(400.dp),
            composition = composition,
            progress = { progress }
        )


    }







}