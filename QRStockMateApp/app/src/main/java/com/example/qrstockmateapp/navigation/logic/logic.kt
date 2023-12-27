package com.example.qrstockmateapp.navigation.logic

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.qrstockmateapp.api.models.User
import com.example.qrstockmateapp.navigation.repository.DataRepository
import com.example.qrstockmateapp.screens.Auth.ForgotPassword.ForgotPassword
import com.example.qrstockmateapp.screens.Auth.Login.Login
import com.example.qrstockmateapp.screens.Home.AddWarehouse.AddWarehouseScreen
import com.example.qrstockmateapp.screens.Home.HomeScreen
import com.example.qrstockmateapp.screens.Home.ManageUser.ManageUserScreen
import com.example.qrstockmateapp.screens.Home.OpenWarehouse.OpenWarehouseScreen
import com.example.qrstockmateapp.screens.Home.UpdateUser.UpdateUserScreen
import com.example.qrstockmateapp.screens.Home.UpdateWarehouse.UpdateWarehouseScreen
import com.example.qrstockmateapp.screens.Profile.ProfileScreen
import com.example.qrstockmateapp.screens.ScanQR.AddItem.AddItemScreen
import com.example.qrstockmateapp.screens.ScanQR.ScanScreen
import com.example.qrstockmateapp.screens.Search.ItemDetails.ItemDetailsScreen
import com.example.qrstockmateapp.screens.Search.SearchScreen
import com.example.qrstockmateapp.screens.TransactionHistory.TransactionHistoryScreen


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalAnimationApi::class)
@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
@Composable
fun  Navigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController)
        }
        composable("transactionHistory") {
            TransactionHistoryScreen(navController)
        }
        composable("scan") {
            ScanScreen(navController)
        }
        composable("search") {
            SearchScreen(navController)
        }
        composable("profile") {
            ProfileScreen(navController)
        }


        //fuera del menu
        composable("addWarehouse") {
           AddWarehouseScreen(navController)
        }
        composable("manageUser") {
            ManageUserScreen(navController)
        }
        composable("updateWarehouse"){
            UpdateWarehouseScreen(navController)
        }
        composable("openWarehouse"){
           OpenWarehouseScreen(navController)
        }
        composable("updateUser"){
            UpdateUserScreen(navController)
        }
        composable("itemDetails"){
            ItemDetailsScreen(navController)
        }
        composable("addItem"){
            AddItemScreen(navController)
        }
    }
}