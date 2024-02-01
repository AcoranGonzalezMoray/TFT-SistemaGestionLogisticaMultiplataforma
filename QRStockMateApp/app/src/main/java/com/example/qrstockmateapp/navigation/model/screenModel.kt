package com.example.qrstockmateapp.navigation.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.outlined.ManageSearch
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material.icons.filled.WorkHistory
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Warehouse
import androidx.compose.material.icons.outlined.WorkHistory
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.qrstockmateapp.navigation.repository.DataRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

        object Message : HomeScreens("chats", "Chats", Icons.AutoMirrored.Filled.Message,
            Icons.AutoMirrored.Outlined.Message
        )
        object Comunity : HomeScreens("communication", "Communication", Icons.Filled.WorkHistory, Icons.Outlined.WorkHistory)
        object Contact : HomeScreens("contact", "Contact", Icons.Filled.Contacts, Icons.Outlined.Contacts)
    }


    @OptIn(DelicateCoroutinesApi::class)
    val screensInHomeFromBottomNavFun: () -> List<HomeScreens> = {
        var userRole: Int? = DataRepository.getUser()?.role

        // Utilizar una corutina para esperar hasta que userRole sea diferente de null
        GlobalScope.launch(Dispatchers.IO){
            while (userRole == null) {
                delay(100) // Puedes ajustar el tiempo de espera según tus necesidades
                userRole = DataRepository.getUser()?.role
            }
        }


        when (userRole) {
            4 -> listOf( //Carrier
                HomeScreens.Home,
                HomeScreens.Carrier,
                HomeScreens.Search,
                HomeScreens.Profile
            )
            3 -> listOf( //User
                HomeScreens.Home,
                HomeScreens.Search,
                HomeScreens.Profile
            )
            2 -> listOf( // Techni
                HomeScreens.Home,
                HomeScreens.Scan,
                HomeScreens.Search,
                HomeScreens.Profile
            )
            1 -> listOf( // Director or Administrator
                HomeScreens.Home,
                HomeScreens.TransactionHistory,
                HomeScreens.Route,
                HomeScreens.Search,
                HomeScreens.Profile
            )
            0 -> listOf( // Director or Administrator
                HomeScreens.Home,
                HomeScreens.TransactionHistory,
                HomeScreens.Vehicle,
                HomeScreens.Search,
                HomeScreens.Profile
            )
            else -> listOf(
                HomeScreens.Home,
                HomeScreens.TransactionHistory,
                HomeScreens.Scan,
                HomeScreens.Search,
                HomeScreens.Profile
            )
        }
    }

    val screensInHomeFromBottomNav = screensInHomeFromBottomNavFun()

}
