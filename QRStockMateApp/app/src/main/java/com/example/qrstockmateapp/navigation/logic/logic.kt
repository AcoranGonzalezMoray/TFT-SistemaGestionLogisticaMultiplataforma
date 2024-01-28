package com.example.qrstockmateapp.navigation.logic

import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.qrstockmateapp.screens.Carrier.CarrierScreen
import com.example.qrstockmateapp.screens.Carrier.Route.RouteMinus.RouteMinusScreen
import com.example.qrstockmateapp.screens.Carrier.Route.RouteScreen
import com.example.qrstockmateapp.screens.Carrier.RouteManagement.AddRoute.AddRouteScreen
import com.example.qrstockmateapp.screens.Carrier.RouteManagement.RouteManagementScreen
import com.example.qrstockmateapp.screens.Carrier.RouteManagement.UpdateRoute.UpdateRouteScreen
import com.example.qrstockmateapp.screens.Carrier.VehicleManagement.AddVehicle.AddVehicleScreen
import com.example.qrstockmateapp.screens.Carrier.VehicleManagement.UpdateVehicle.UpdateVehicleScreen
import com.example.qrstockmateapp.screens.Carrier.VehicleManagement.VehicleManagementScreen
import com.example.qrstockmateapp.screens.Chats.Chat.ChatScreen
import com.example.qrstockmateapp.screens.Chats.ChatsScreen
import com.example.qrstockmateapp.screens.Chats.Contact.ContactScreen
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
import com.example.qrstockmateapp.ui.theme.splashScreen


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalAnimationApi::class)
@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
@Composable
fun  Navigation(navController: NavHostController, sharedPreferences: SharedPreferences) {
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
        composable("carrier"){
            CarrierScreen(navController)
        }
        composable("routeManagement"){
            RouteManagementScreen(navController)
        }
        composable("vehicleManagement"){
            VehicleManagementScreen(navController)
        }
        composable("search") {
            SearchScreen(navController)
        }
        composable("profile") {
            ProfileScreen(navController)
        }



        //fuera del menu
        composable("chat"){
            ChatScreen(navController)
        }
        composable("chats"){
            ChatsScreen(navController, sharedPreferences = sharedPreferences)
        }
        composable("contact"){
            ContactScreen(navController)
        }
        composable("splashScreen"){
            splashScreen(navController, 1)
        }
        composable("updateVehicle"){
            UpdateVehicleScreen(navController)
        }
        composable("addVehicle"){
            AddVehicleScreen(navController)
        }
        composable("addRoute"){
            AddRouteScreen(navController)
        }
        composable("route"){
            RouteScreen(navController)
        }
        composable("routeMinus"){
            RouteMinusScreen(navController)
        }
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
        composable("updateRoute"){
            UpdateRouteScreen(navController)
        }
    }
}