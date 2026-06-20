package com.example.dairyledger.views

import androidx.annotation.StringRes
import com.example.dairyledger.R
import com.example.dairyledger.ui.icons.AppIcons

/**
 * Represents one navigation tab.
 * route: unique navigation key
 * label: text shown under the icon
 * icon: icon shown in the tab
 */
sealed class NavItem(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: Int,
    val defaultRoute: String = route,
    val baseRoute: String = route.substringBefore("/"),
) {
    data object Home : NavItem("home", R.string.home, AppIcons.Home)
    data object Collect : NavItem("collect/{type}", R.string.collect, AppIcons.AddCircle, "collect/default") {
        fun createRoute(type: String) = "collect/$type"
    }
    data object Reports : NavItem("reports/{weekId}", R.string.reports, AppIcons.BarChart, "reports/-1") {
        fun createRoute(weekId: Int) = "reports/$weekId"
    }
    data object Farmers : NavItem("farmers", R.string.farmers, AppIcons.People)
    data object WeeklyArchive : NavItem("weekly-archive", R.string.weekly_archive, AppIcons.Inventory)
    data object Settings : NavItem("settings", R.string.settings, AppIcons.Settings)
    data object AddFarmer : NavItem("add-farmer", R.string.add_farmer, AppIcons.AddPerson)


    data object FarmerDetails : NavItem("farmer-details/{farmerId}", R.string.farmer_details, AppIcons.Description) {
        fun createRoute(farmerId: Int) = "farmer-details/$farmerId"
    }
    data object WeekClosing : NavItem("week-closing", R.string.week_closing, AppIcons.Lock)
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
