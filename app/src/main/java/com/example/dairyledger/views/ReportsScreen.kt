package com.example.dairyledger.views

import android.icu.text.DecimalFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dairyledger.models.ReportsViewModel
import com.example.dairyledger.models.SettingsViewModel
import java.util.Locale


data class ReportRowItem(
    val id: String,
    val name: String,
    val initials: String,
    val liters: String,
    val pricePerL: String,
    val total: String,
    val avatarBgColor: Color = Color(0xFFB57C1E)
)

@Composable
fun ReportsScreen(
    reportsViewModel: ReportsViewModel,
    settingsViewModel: SettingsViewModel
) {
    val numberFormatter = remember { DecimalFormat("#,##0.00") }

    val weekTitle: String = "Week ${reportsViewModel.weekId}"
    val dateRange: String = "June 10 - June 16, 2024"
    val totalMilk: Double = 4830.0
    val unitPrice: Double by settingsViewModel.defaultPrice.collectAsState(initial = 0.0)
    val totalRevenue: String = numberFormatter.format(unitPrice * totalMilk)
    val reportData: List<ReportRowItem> = listOf(
        ReportRowItem("#8821", "Rajesh Meena", "RM", "420.5", "$0.45", "$189.22", Color(0xFF2E7D32)),
        ReportRowItem("#8822", "Anita Sharma", "AS", "390.0", "$0.45", "$175.50", Color(0xFFB0BEC5)),
        ReportRowItem("#8823", "Suresh Kumar", "SK", "512.2", "$0.45", "$230.49", Color(0xFFFFB300)),
        ReportRowItem("#8824", "Dinesh Patel", "DP", "315.0", "$0.45", "$141.75", Color(0xFF1B5E20)),
        ReportRowItem("#8825", "Priya Pawar", "PP", "488.3", "$0.45", "$219.73", Color(0xFFCFD8DC))
    )
    val onExportPdfClick: () -> Unit = {}
    val isCurrentWeek = true

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            ReportsTopBar()
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                Text(
                    text = weekTitle,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Text(
                    text = dateRange,
                    fontSize = 14.sp,
                    color = MutedText
                )
            }

            // Filter Chips Scrollable Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isCurrentWeek) Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp)) // 1. Clips the background and click ripple to a capsule shape
                        .background(if (isCurrentWeek) DairyGreen else FilterChipBg) // 2. Sets adaptive background color
                        .padding(horizontal = 16.dp, vertical = 8.dp), // 4. Mimics Button's contentPadding
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Current Week",
                        color = if (isCurrentWeek) Color.White else MutedText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Summary Metric Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryMetricCard(
                    title = "TOTAL MILK",
                    value = numberFormatter.format( totalMilk),
                    suffix = "Ltr",
                    valueColor = DairyGreen,
                    modifier = Modifier.weight(1f)
                )
                SummaryMetricCard(
                    title = "REVENUE",
                    value = totalRevenue,
                    suffix = "",
                    valueColor = RevenueBrown,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Detailed Data Table Card
            DetailedReportTable(reportData = reportData, totalMilk = numberFormatter.format(totalMilk), totalRevenue = totalRevenue)

            Spacer(Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onExportPdfClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DairyGreen)
                ) {
                    Icon(imageVector = Icons.Outlined.Email, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(text = "Export PDF", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ---------- Sub-Components ----------

@Composable
private fun ReportsTopBar() {
    Surface(color = ScreenBg) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = null,
                    tint = DairyGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Weekly Summary",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DairyGreen
                )
            }
        }
    }
}

@Composable
private fun SummaryMetricCard(title: String, value: String, suffix: String, valueColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MutedText, letterSpacing = 0.5.sp)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = value, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = valueColor)
                if (suffix.isNotEmpty()) {
                    Spacer(Modifier.width(4.dp))
                    Text(text = suffix, fontSize = 14.sp, color = TextDark, modifier = Modifier.padding(bottom = 6.dp))
                }
            }
        }
    }
}

@Composable
private fun DetailedReportTable(reportData: List<ReportRowItem>, totalMilk: String, totalRevenue: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column {
            // Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF7F7F7))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("FARMER NAME", fontSize = 11.sp, color = MutedText, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(2f))
                Text("LITERS", fontSize = 11.sp, color = MutedText, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                Text("PRICE/L", fontSize = 11.sp, color = MutedText, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                Text("TOTAL", fontSize = 11.sp, color = MutedText, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.End, modifier = Modifier.weight(1.2f))
            }
            HorizontalDivider(color = CardBorder, thickness = 1.dp)

            // Table Rows
            reportData.forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Name and Avatar column
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(2f)) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(row.avatarBgColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = row.initials, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(text = row.name, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextDark)
                            Text(text = "ID: ${row.id}", fontSize = 11.sp, color = MutedText)
                        }
                    }
                    Text(text = row.liters, fontSize = 13.sp, color = TextDark, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                    Text(text = row.pricePerL, fontSize = 13.sp, color = MutedText, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                    Text(text = row.total, fontSize = 13.sp, color = DairyGreen, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.End, modifier = Modifier.weight(1.2f))
                }
                HorizontalDivider(color = CardBorder, thickness = 1.dp)
            }

            // Table Footer (Totals)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFCFCFC))
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "REPORT TOTAL", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = MutedText)
                    Text(text = "$totalMilk Ltr", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DairyGreen)
                }
                Spacer(Modifier.width(24.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "TOTAL PAYOUT", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = MutedText)
                    Text(text = totalRevenue, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                }
            }
        }
    }
}
