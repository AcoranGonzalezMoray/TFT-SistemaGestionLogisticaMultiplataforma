package com.example.qrstockmateapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.qrstockmateapp.R


@Composable
fun errorApiScreen(navController: NavController, type:Int = 0){

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.error_server))
    val progress by animateLottieCompositionAsState(composition = composition, iterations = LottieConstants.IterateForever, speed = 0.5f)



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .zIndex(50f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        LottieAnimation(
            modifier = Modifier.size(400.dp),
            composition = composition,
            progress = { progress }
        )

        Text(text = "\"Sorry, we are currently experiencing technical issues, and the functionality you're trying to access is not available at the moment. Please try again later. Thank you for your understanding.\"",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(40.dp)
        )
    }







}