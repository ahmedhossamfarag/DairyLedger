package com.example.dairyledger.views

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings

/**
 * Represents one navigation tab.
 * route: unique navigation key
 * label: text shown under the icon
 * icon: icon shown in the tab
 */
sealed class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val defaultRoute: String = route,
    val baseRoute: String = route.substringBefore("/"),
) {
    data object Home : NavItem("home", "Home", Icons.Filled.Home)
    data object Collect : NavItem("collect/{type}", "Collect", Icons.Filled.Add, "collect/default") {
        fun createRoute(type: String) = "collect/$type"
    }
    data object Reports : NavItem("reports/{weekId}", "Reports", Icons.Filled.DateRange, "reports/-1") {
        fun createRoute(weekId: Int) = "reports/$weekId"
    }
    data object Farmers : NavItem("farmers", "Farmers", Icons.Filled.Person)
    data object WeeklyArchive : NavItem("weekly-archive", "Weekly Archive", Icons.Filled.Lock)
    data object Settings : NavItem("settings", "Settings", Icons.Filled.Settings)
    data object AddFarmer : NavItem("add-farmer", "Add Farmer", Icons.Filled.Add)


    data object FarmerDetails : NavItem("farmer-details/{farmerId}", "Farmer Details", Icons.Filled.Person) {
        fun createRoute(farmerId: Int) = "farmer-details/$farmerId"
    }
    data object WeekClosing : NavItem("week-closing", "Week Closing", Icons.Filled.Lock)
}

val bottomNavItems = listOf(
    NavItem.Home,
    NavItem.Collect,
    NavItem.Reports,
    NavItem.Farmers
)


val drawerNavItems = listOf(
    NavItem.WeeklyArchive,
    NavItem.Settings,
)


class Navigator(
    val gotoHome: () -> Unit,
    val gotoCollect: () -> Unit,
    val gotoReports: () -> Unit,
    val gotoFarmers: () -> Unit,
    val gotoWeeklyArchive: () -> Unit,
    val gotoSettings: () -> Unit,
    val gotoWeekReport: (weekId: Int) -> Unit,
    val gotoCollectWithType: (type: String) -> Unit,
    val gotoAddFarmer: () -> Unit,
    val gotoFarmerDetails: (farmerId: Int) -> Unit,
    val gotoWeekClosing: () -> Unit,
    val goback: () -> Boolean,
)
