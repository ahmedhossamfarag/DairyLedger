package com.example.dairyledger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dairyledger.ui.theme.DairyLedgerTheme
import com.example.dairyledger.views.AddFarmerScreen
import com.example.dairyledger.views.CollectScreen
import com.example.dairyledger.views.DairyBottomBar
import com.example.dairyledger.views.FarmerDetailsScreen
import com.example.dairyledger.views.FarmersScreen
import com.example.dairyledger.views.HomeScreen
import com.example.dairyledger.views.NavItem
import com.example.dairyledger.views.Navigator
import com.example.dairyledger.views.ReportsScreen
import com.example.dairyledger.views.SettingsScreen
import com.example.dairyledger.views.WeekClosingScreen
import com.example.dairyledger.views.WeeklyArchiveScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DairyLedgerTheme {
                DairyApp()
            }
        }
    }
}



@Composable
fun DairyApp() {
    val navController = rememberNavController()

    val navigateTab = { route: String ->
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
        }
    }

    val gotoHome = { navigateTab(NavItem.Home.defaultRoute) }
    val gotoCollect = { navigateTab(NavItem.Collect.defaultRoute) }
    val gotoReports = { navigateTab(NavItem.Reports.defaultRoute) }
    val gotoFarmers = { navigateTab(NavItem.Farmers.defaultRoute) }

    val gotoWeeklyArchive = { navigateTab(NavItem.WeeklyArchive.defaultRoute) }
    val gotoSettings = { navigateTab(NavItem.Settings.defaultRoute) }
    val gotoWeekReport = { weekId: Int ->
        navigateTab(NavItem.Reports.createRoute(weekId))
    }
    val gotoCollectWithType = { type: String ->
        navigateTab(NavItem.Collect.createRoute(type))
    }
    val gotoAddFarmer = { navController.navigate(NavItem.AddFarmer.route) }
    val gotoFarmerDetails = { farmerId: Int ->
        navController.navigate(NavItem.FarmerDetails.createRoute(farmerId))
    }
    val gotoWeekClosing = { navController.navigate(NavItem.WeekClosing.route) }
    val goback = { navController.popBackStack() }


    val navigator = Navigator(
        gotoHome,
        gotoCollect,
        gotoReports,
        gotoFarmers,
        gotoWeeklyArchive,
        gotoSettings,
        gotoWeekReport,
        gotoCollectWithType,
        gotoAddFarmer,
        gotoFarmerDetails,
        gotoWeekClosing,
        goback
    )

    Scaffold(
        bottomBar = { DairyBottomBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavItem.Home.route) { HomeScreen(navController, navigator) }
            composable(NavItem.Collect.route,
                arguments = listOf(navArgument("type") { type = NavType.StringType })
            ) {
                val type = it.arguments?.getString("type") ?: "default"
                CollectScreen(type, navigator)
            }
            composable(NavItem.Reports.route,
                arguments = listOf(navArgument("weekId") { type = NavType.IntType })
            ) {
                val weekId = it.arguments?.getInt("weekId") ?: -1
                ReportsScreen(weekId)
            }
            composable(NavItem.Farmers.route) { FarmersScreen(navigator) }
            composable(NavItem.WeeklyArchive.route) { WeeklyArchiveScreen(navigator) }
            composable(NavItem.Settings.route) { SettingsScreen() }
            composable(NavItem.AddFarmer.route) { AddFarmerScreen(navigator) }


            composable(NavItem.FarmerDetails.route,
                arguments = listOf(navArgument("farmerId") { type = NavType.IntType })
                ) {
                val farmerId = it.arguments?.getInt("farmerId") ?: 0
                FarmerDetailsScreen(farmerId, navigator)
            }
            composable(NavItem.WeekClosing.route) { WeekClosingScreen(navigator) }
        }
    }
}

