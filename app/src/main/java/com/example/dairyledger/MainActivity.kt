package com.example.dairyledger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.dairyledger.ui.theme.DairyLedgerTheme

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

/**
 * Represents one bottom navigation tab.
 * route: unique navigation key
 * label: text shown under the icon
 * icon: icon shown in the tab
 */
sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    data object Home : BottomNavItem("home", "Home", Icons.Filled.Home)
    data object Collect : BottomNavItem("collect", "Collect", Icons.Filled.Add)
    data object Reports : BottomNavItem("reports", "Reports", Icons.Filled.DateRange)
    data object Farmers : BottomNavItem("farmers", "Farmers", Icons.Filled.Person)
}

private val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Collect,
    BottomNavItem.Reports,
    BottomNavItem.Farmers
)

@Composable
fun DairyApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { DairyBottomBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) { HomeScreen() }
            composable(BottomNavItem.Collect.route) { CollectScreen() }
            composable(BottomNavItem.Reports.route) { ReportsScreen() }
            composable(BottomNavItem.Farmers.route) { FarmersScreen() }
        }
    }
}

@Composable
fun DairyBottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        bottomNavItems.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(item.route) {
                            // Avoid building up a large stack as the user taps tabs.
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

// ---- Placeholder screens (replace with real implementations) ----

@Composable
fun HomeScreen() {
    PlaceholderScreen(title = "Home — Milk Collection Dashboard")
}

@Composable
fun CollectScreen() {
    PlaceholderScreen(title = "Collect — Daily Collection Entry")
}

@Composable
fun ReportsScreen() {
    PlaceholderScreen(title = "Reports — Weekly Report")
}

@Composable
fun FarmersScreen() {
    PlaceholderScreen(title = "Farmers — Farmer List")
}

@Composable
private fun PlaceholderScreen(title: String) {
    Surface {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )
    }
}