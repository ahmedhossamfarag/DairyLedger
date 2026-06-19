package com.example.dairyledger.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch


data class CollectionStatus(
    val label: String,
    val isDone: Boolean,
    val liters: String,
    val farmerCount: Int
)

@Composable
fun HomeScreen(navController: NavHostController, navigator: Navigator) {
    val weekLabel: String = "Week 23 (Jun 10 - Jun 16)"
    val morning: CollectionStatus = CollectionStatus("Morning\nCollection", true, "350", 24)
    val evening: CollectionStatus = CollectionStatus("Evening\nCollection", false, "290", 24)
    val weeklyTotalLiters: String = "4,830"
    val unitPrice: String = "$0.45"
    val agentName: String = "Collection Agent"
    val appVersion: String = "App Version 2.4.1 (Stable)"
    val onMorningCollectionClick: () -> Unit = { navigator.gotoCollectWithType("morning") }
    val onEveningCollectionClick: () -> Unit = { navigator.gotoCollectWithType("evening") }
    val onViewWeeklyReportClick: () -> Unit = { navigator.gotoReports() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                agentName = agentName,
                appVersion = appVersion,
                navController = navController,
            )
        }
    ) {
        Scaffold(
            containerColor = ScreenBg,
            topBar = {
                DairyTopBar(
                    onMenuClick = { scope.launch { drawerState.open() } },
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ScreenBg)
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Milk Collection",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = weekLabel,
                    fontSize = 14.sp,
                    color = MutedText
                )

                Spacer(Modifier.height(16.dp))

                // Morning / Evening collection cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CollectionStatusCard(status = morning, modifier = Modifier.weight(1f))
                    CollectionStatusCard(status = evening, modifier = Modifier.weight(1f))
                }

                Spacer(Modifier.height(16.dp))

                WeeklyTotalCard(
                    totalLiters = weeklyTotalLiters,
                )

                Spacer(Modifier.height(16.dp))

                UnitPriceCard(
                    price = unitPrice,
                )

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Daily Collection Entry",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A)
                )

                Spacer(Modifier.height(12.dp))

                PrimaryGreenButton(
                    text = "Morning Collection",
                    onClick = onMorningCollectionClick
                )

                Spacer(Modifier.height(10.dp))

                PrimaryGreenButton(
                    text = "Evening Collection",
                    onClick = onEveningCollectionClick
                )

                Spacer(Modifier.height(10.dp))

                SecondaryButton(
                    text = "View Weekly Report",
                    icon = Icons.Outlined.DateRange,
                    onClick = onViewWeeklyReportClick
                )
            }
        }
    }
}

// ---------- Top bar ----------

@Composable
private fun DairyTopBar(
    onMenuClick: () -> Unit,
) {
    Surface(color = ScreenBg) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Open menu",
                        tint = Color(0xFF2A2A2A)
                    )
                }
                Spacer(Modifier.width(10.dp))
                // Material Icons has no tractor glyph. Swap this for a custom
                // vector asset, e.g. Icon(painterResource(R.drawable.ic_tractor), ...)
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = null,
                    tint = DairyGreen,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Dairy Operations",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DairyGreen
                )
            }
        }
    }
}

// ---------- Collection status card ----------

@Composable
private fun CollectionStatusCard(status: CollectionStatus, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            StatusPill(text = if (status.isDone) "Done" else "Pending", isDone = status.isDone)
            Spacer(Modifier.height(10.dp))
            Text(
                text = status.label,
                fontSize = 13.sp,
                color = MutedText,
                lineHeight = 16.sp
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = status.liters,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Spacer(Modifier.width(2.dp))
                Text(
                    text = "L",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1A1A1A),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = MutedText,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "${status.farmerCount} Farmers",
                    fontSize = 12.sp,
                    color = MutedText
                )
            }
        }
    }
}

@Composable
private fun StatusPill(text: String, isDone: Boolean) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (isDone) DoneGreenBg else PendingGrayBg
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = if (isDone) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                contentDescription = null,
                tint = if (isDone) Color.White else Color(0xFF555555),
                modifier = Modifier.size(13.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDone) Color.White else Color(0xFF555555)
            )
        }
    }
}

// ---------- Weekly total card ----------

@Composable
private fun WeeklyTotalCard(totalLiters: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(DairyGreenDark)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Weekly Total",
                fontSize = 14.sp,
                color = Color(0xFFBFD8BF)
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = totalLiters,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Liters",
                    fontSize = 16.sp,
                    color = Color(0xFFD9E8D9),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
        }
    }
}

// ---------- Unit price card ----------

@Composable
private fun UnitPriceCard(price: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = PriceCardBg,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF4C95D)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color(0xFF5C4500),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Current Unit Price",
                    fontSize = 13.sp,
                    color = MutedText
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = price,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    Text(
                        text = " / Liter",
                        fontSize = 13.sp,
                        color = MutedText
                    )
                }
            }
        }
    }
}

// ---------- Buttons ----------

@Composable
private fun PrimaryGreenButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(containerColor = DairyGreen)
    ) {
        Icon(
            imageVector = Icons.Filled.AddCircle,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(text = text, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.White)
    }
}

@Composable
private fun SecondaryButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFEDEBE5)),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF333333),
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(text = text, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color(0xFF222222))
    }
}

