package com.example.dairyledger.views

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.navigation.NavHostController

/**
 * Represents one navigation tab.
 * route: unique navigation key
 * label: text shown under the icon
 * icon: icon shown in the tab
 */
sealed class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    data object Home : NavItem("home", "Home", Icons.Filled.Home)
    data object Collect : NavItem("collect", "Collect", Icons.Filled.Add)
    data object Reports : NavItem("reports", "Reports", Icons.Filled.DateRange)
    data object Farmers : NavItem("farmers", "Farmers", Icons.Filled.Person)
    data object WeeklyArchive : NavItem("weekly-archive", "Weekly Archive", Icons.Filled.Lock)
    data object Settings : NavItem("settings", "Settings", Icons.Filled.Settings)
    data object WeekReport : NavItem("week-report/{weekId}", "Week Report", Icons.Filled.DateRange)
    data object CollectWithType : NavItem("collect-with-type/{type}", "Collect", Icons.Filled.Add)
    data object AddFarmer : NavItem("add-farmer", "Add Farmer", Icons.Filled.Add)
    data object FarmerDetails : NavItem("farmer-details/{farmerId}", "Farmer Details", Icons.Filled.Person)
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

