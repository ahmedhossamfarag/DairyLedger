package com.example.dairyledger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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

    val gotoHome = { navController.navigate(NavItem.Home.route) }
    val gotoCollect = { navController.navigate(NavItem.Collect.route) }
    val gotoReports = { navController.navigate(NavItem.Reports.route) }
    val gotoFarmers = { navController.navigate(NavItem.Farmers.route) }
    val gotoWeeklyArchive = { navController.navigate(NavItem.WeeklyArchive.route) }
    val gotoSettings = { navController.navigate(NavItem.Settings.route) }
    val gotoWeekReport = { weekId: Int -> navController.navigate("week-report/$weekId") }
    val gotoCollectWithType = { type: String -> navController.navigate("collect-with-type/$type") }
    val gotoAddFarmer = { navController.navigate(NavItem.AddFarmer.route) }
    val gotoFarmerDetails = { farmerId: Int -> navController.navigate("farmer-details/$farmerId") }


    Scaffold(
        bottomBar = { DairyBottomBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavItem.Home.route) { HomeScreen(navController) }
            composable(NavItem.Collect.route) { CollectScreen() }
            composable(NavItem.Reports.route) { ReportsScreen() }
            composable(NavItem.Farmers.route) { FarmersScreen() }
            composable(NavItem.WeeklyArchive.route) { WeeklyArchiveScreen() }
            composable(NavItem.Settings.route) { SettingsScreen() }
            composable(NavItem.WeekReport.route,
                arguments = listOf(navArgument("weekId") { type = NavType.IntType })
            ) { ReportsScreen() }
            composable(NavItem.CollectWithType.route,
                arguments = listOf(navArgument("type") { type = NavType.StringType })
            ) { CollectScreen() }
            composable(NavItem.AddFarmer.route) { AddFarmerScreen() }
            composable(NavItem.FarmerDetails.route,
                    arguments = listOf(navArgument("farmerId") { type = NavType.IntType })
                ) { FarmerDetailsScreen() }
            composable(NavItem.WeekClosing.route) { WeekClosingScreen() }
        }
    }
}

