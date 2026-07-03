package com.example.dairyledger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dairyledger.data.DairyRepository
import com.example.dairyledger.data.SettingsRepository
import com.example.dairyledger.models.CollectViewModel
import com.example.dairyledger.models.CurrentWeekViewModel
import com.example.dairyledger.models.FarmerDetailsViewModel
import com.example.dairyledger.models.FarmersViewModel
import com.example.dairyledger.models.HomeViewModel
import com.example.dairyledger.models.ReportsViewModel
import com.example.dairyledger.models.SettingsViewModel
import com.example.dairyledger.models.SettingsViewModelFactory
import com.example.dairyledger.models.ViewModelFactory
import com.example.dairyledger.models.WeeklyArchiveViewModel
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
import android.content.Context
import android.content.res.Configuration
import com.example.dairyledger.models.CollectionArchiveViewModel
import com.example.dairyledger.views.CollectionArchiveScreen
import java.util.Locale

class MainActivity : ComponentActivity() {
    val repository by lazy { (application as DairyLedgerApp).repository }
    val settingsRepository by lazy { (application as DairyLedgerApp).settingsRepository }

    override fun attachBaseContext(newBase: Context) {
        // Force the locale to Arabic
        val locale = Locale("ar")
        Locale.setDefault(locale)

        val configuration = Configuration(newBase.resources.configuration)
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        // This applies the localized configuration to the context
        val context = newBase.createConfigurationContext(configuration)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // No need to set locale here anymore, it's handled in attachBaseContext

        setContent {
            DairyLedgerTheme {
                DairyApp(repository, settingsRepository)
            }
        }
    }
}



@Composable
fun DairyApp(repository: DairyRepository, settingsRepository: SettingsRepository) {
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

    val gotoCollectionArchive = { navigateTab(NavItem.CollectionArchive.defaultRoute) }
    val gotoWeeklyArchive = { navigateTab(NavItem.WeeklyArchive.defaultRoute) }
    val gotoSettings = { navigateTab(NavItem.Settings.defaultRoute) }
    val gotoWeekReport = { weekId: Int ->
        navigateTab(NavItem.Reports.createRoute(weekId))
    }
    val gotoCollectWithType = { type: String ->
        navigateTab(NavItem.Collect.createRoute(type))
    }
    val gotoAddFarmer = { navController.navigate(NavItem.AddFarmer.defaultRoute) }
    val gotoEditFarmer = { farmerId: Int -> navController.navigate(NavItem.AddFarmer.createRoute(farmerId)) }
    val gotoFarmerDetails = { farmerId: Int ->
        navController.navigate(NavItem.FarmerDetails.createRoute(farmerId))
    }
    val gotoWeekClosing = { navController.navigate(NavItem.WeekClosing.route) }
    val goback = { navController.popBackStack() }

    val gotoCollection = { collectionId: Int ->
        navigateTab(NavItem.CollectionEdit.createRoute(collectionId))
    }



    val navigator = Navigator(
        gotoHome,
        gotoCollect,
        gotoReports,
        gotoFarmers,
        gotoWeeklyArchive,
        gotoCollectionArchive,
        gotoSettings,
        gotoWeekReport,
        gotoCollectWithType,
        gotoCollection,
        gotoAddFarmer,
        gotoEditFarmer,
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
            composable(NavItem.Home.route) {
                val navBackStackEntry = remember(it) { navController.getBackStackEntry(NavItem.Home.route) }

                val settingVM: SettingsViewModel = viewModel(
                    viewModelStoreOwner = navBackStackEntry,
                    factory = SettingsViewModelFactory(settingsRepository)
                )
                val vm: HomeViewModel = viewModel(factory = ViewModelFactory(repository))

                HomeScreen(navController, navigator, vm, settingVM)
            }

            composable(NavItem.Collect.route,
                arguments = listOf(navArgument("type") { type = NavType.StringType })
            ) {
                val homeEntry = remember(it) { navController.getBackStackEntry(NavItem.Home.route) }
                val vm: CollectViewModel = viewModel(
                    viewModelStoreOwner = homeEntry,
                    factory = ViewModelFactory(repository)
                )
                val type = it.arguments?.getString("type") ?: "default"
                LaunchedEffect(Unit) { vm.loadCollection() }
                CollectScreen(type, navigator, vm)
            }

            composable(NavItem.Reports.route,
                arguments = listOf(navArgument("weekId") { type = NavType.LongType })
            ) {
                // Fetch the shared SettingsViewModel using the Home route's lifecycle
                val homeEntry = remember(it) { navController.getBackStackEntry(NavItem.Home.route) }
                val settingVM: SettingsViewModel = viewModel(
                    viewModelStoreOwner = homeEntry,
                    factory = SettingsViewModelFactory(settingsRepository)
                )

                val vm: ReportsViewModel = viewModel(factory = ViewModelFactory(repository))
                val weekId = it.arguments?.getLong("weekId") ?: -1
                LaunchedEffect(weekId) { vm.loadWeek(weekId) }
                ReportsScreen(navigator, vm, settingVM)
            }

            composable(NavItem.Farmers.route) {
                // Farmers is the main entry point for the farmers' flow
                val vm: FarmersViewModel = viewModel(factory = ViewModelFactory(repository))
                FarmersScreen(navigator, vm)
            }

            composable(NavItem.CollectionArchive.route) {
                val vm: CollectionArchiveViewModel = viewModel(factory = ViewModelFactory(repository))
                CollectionArchiveScreen(navigator, vm)
            }

            composable(NavItem.WeeklyArchive.route) {
                val vm: WeeklyArchiveViewModel = viewModel(factory = ViewModelFactory(repository))
                WeeklyArchiveScreen(navigator, vm)
            }

            composable(NavItem.Settings.route) {
                val homeEntry = remember(it) { navController.getBackStackEntry(NavItem.Home.route) }
                val settingVM: SettingsViewModel = viewModel(
                    viewModelStoreOwner = homeEntry,
                    factory = SettingsViewModelFactory(settingsRepository)
                )
                SettingsScreen(settingVM)
            }

            composable(NavItem.AddFarmer.route,
                arguments = listOf(navArgument("farmerId") { type = NavType.LongType })
            ) {
                // Fetch the identical FarmersViewModel from the Farmers destination backstack entry
                val farmersEntry = remember(it) { navController.getBackStackEntry(NavItem.Farmers.route) }
                val vm: FarmersViewModel = viewModel(
                    viewModelStoreOwner = farmersEntry,
                    factory = ViewModelFactory(repository)
                )
                val farmerId = it.arguments?.getLong("farmerId") ?: -1
                LaunchedEffect(farmerId) { vm.loadFarmer(farmerId) }
                AddFarmerScreen(navigator, vm)
            }

            composable(NavItem.FarmerDetails.route,
                arguments = listOf(navArgument("farmerId") { type = NavType.LongType })
            ) {
                val homeEntry = remember(it) { navController.getBackStackEntry(NavItem.Home.route) }
                val settingVM: SettingsViewModel = viewModel(
                    viewModelStoreOwner = homeEntry,
                    factory = SettingsViewModelFactory(settingsRepository)
                )

                val detailVM: FarmerDetailsViewModel = viewModel(factory = ViewModelFactory(repository))
                val farmerId = it.arguments?.getLong("farmerId") ?: 0
                LaunchedEffect(farmerId) { detailVM.loadFarmer(farmerId) }

                FarmerDetailsScreen(navigator, detailVM, settingVM)
            }

            composable(NavItem.WeekClosing.route) {
                val vm: CurrentWeekViewModel = viewModel(factory = ViewModelFactory(repository))
                val homeEntry = remember(it) { navController.getBackStackEntry(NavItem.Home.route) }
                val settingVM: SettingsViewModel = viewModel(
                    viewModelStoreOwner = homeEntry,
                    factory = SettingsViewModelFactory(settingsRepository)
                )
                WeekClosingScreen(navigator, vm, settingVM)
            }

            composable(NavItem.CollectionEdit.route,
                arguments = listOf(navArgument("collectionId") { type = NavType.LongType})
            ) {
                val homeEntry = remember(it) { navController.getBackStackEntry(NavItem.Home.route) }
                val vm: CollectViewModel = viewModel(
                    viewModelStoreOwner = homeEntry,
                    factory = ViewModelFactory(repository)
                )
                val type = "default"
                val collectionId = it.arguments?.getLong("collectionId") ?: -1
                LaunchedEffect(Unit) { vm.loadCollection(collectionId) }
                CollectScreen(type, navigator, vm)
            }
        }
    }
}

