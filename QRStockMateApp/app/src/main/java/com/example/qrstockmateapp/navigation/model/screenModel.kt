package com.example.qrstockmateapp.navigation.model

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.automirrored.outlined.ManageSearch
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ManageSearch
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material.icons.filled.WorkHistory
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Message
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Warehouse
import androidx.compose.material.icons.outlined.WorkHistory
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import com.example.qrstockmateapp.R
import com.example.qrstockmateapp.navigation.repository.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ScreenModel {

    sealed class HomeScreens(
        val route: String,
        val title: String,
        val icon: ImageVector,
        val iconO: ImageVector
    ) {
        object Home : HomeScreens("home", "Home", Icons.Filled.Warehouse, Icons.Outlined.Warehouse )
        object TransactionHistory : HomeScreens("transactionHistory", "History", Icons.AutoMirrored.Filled.ManageSearch, Icons.AutoMirrored.Outlined.ManageSearch)
        object Scan : HomeScreens("scan", "Scan QR", Icons.Filled.QrCodeScanner, Icons.Outlined.QrCodeScanner)
        object Carrier : HomeScreens("carrier", "Carrier", Icons.Filled.LocalShipping, Icons.Outlined.LocalShipping)
        object Route: HomeScreens("routeManagement", "Route", Icons.Filled.Map, Icons.Outlined.Map)
        object Vehicle : HomeScreens("vehicleManagement", "Vehicle", Icons.Filled.DirectionsCar, Icons.Outlined.DirectionsCar)
        object Search : HomeScreens("search", "Search", Icons.Filled.Search, Icons.Outlined.Search)
        object Profile : HomeScreens("profile", "Profile", Icons.Filled.Badge, Icons.Outlined.Badge)

        object Message : HomeScreens("chats", "Chats", Icons.Filled.Message, Icons.Outlined.Message)
        object Comunity : HomeScreens("communication", "Communication", Icons.Filled.WorkHistory, Icons.Outlined.WorkHistory)
        object Contact : HomeScreens("contact", "Contact", Icons.Filled.Contacts, Icons.Outlined.Contacts)
    }


    val screensInHomeFromBottomNavFun: () -> List<ScreenModel.HomeScreens> = {
        var userRole: Int? = DataRepository.getUser()?.role

        // Utilizar una corutina para esperar hasta que userRole sea diferente de null
        GlobalScope.launch(Dispatchers.IO){
            while (userRole == null) {
                delay(100) // Puedes ajustar el tiempo de espera según tus necesidades
                userRole = DataRepository.getUser()?.role
            }
        }

        Log.d("role", "$userRole")

        when (userRole) {
            4 -> listOf( //Carrier
                ScreenModel.HomeScreens.Home,
                ScreenModel.HomeScreens.Carrier,
                ScreenModel.HomeScreens.Search,
                ScreenModel.HomeScreens.Profile
            )
            3 -> listOf( //User
                ScreenModel.HomeScreens.Home,
                ScreenModel.HomeScreens.Search,
                ScreenModel.HomeScreens.Profile
            )
            2 -> listOf( // Techni
                ScreenModel.HomeScreens.Home,
                ScreenModel.HomeScreens.Scan,
                ScreenModel.HomeScreens.Search,
                ScreenModel.HomeScreens.Profile
            )
            1 -> listOf( // Director or Administrator
                ScreenModel.HomeScreens.Home,
                ScreenModel.HomeScreens.TransactionHistory,
                ScreenModel.HomeScreens.Route,
                ScreenModel.HomeScreens.Search,
                ScreenModel.HomeScreens.Profile
            )
            0 -> listOf( // Director or Administrator
                ScreenModel.HomeScreens.Home,
                ScreenModel.HomeScreens.TransactionHistory,
                ScreenModel.HomeScreens.Vehicle,
                ScreenModel.HomeScreens.Search,
                ScreenModel.HomeScreens.Profile
            )
            else -> listOf(
                ScreenModel.HomeScreens.Home,
                ScreenModel.HomeScreens.TransactionHistory,
                ScreenModel.HomeScreens.Scan,
                ScreenModel.HomeScreens.Search,
                ScreenModel.HomeScreens.Profile
            )
        }
    }

    val screensInHomeFromBottomNav = screensInHomeFromBottomNavFun()

}
