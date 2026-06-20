package com.example.dairyledger.views

import android.icu.text.DecimalFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dairyledger.R
import com.example.dairyledger.models.ReportsViewModel
import com.example.dairyledger.models.SettingsViewModel
import com.example.dairyledger.pdf.PdfReportExporter
import com.example.dairyledger.ui.icons.AppIcons
import kotlinx.coroutines.launch
import java.util.Calendar


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
    navigator: Navigator,
    reportsViewModel: ReportsViewModel,
    settingsViewModel: SettingsViewModel
) {
    val week = reportsViewModel.week

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            ReportsTopBar()
        },
    ) { innerPadding ->
        if (week == null) {
            NoContentPlaceHolder(modifier = Modifier.padding(innerPadding))
        } else {
            ReportsContent(
                modifier = Modifier.padding(innerPadding),
                navigator,
                reportsViewModel,
                settingsViewModel
            )
        }
    }

}

@Composable
fun ReportsContent(
    modifier: Modifier = Modifier,
    navigator: Navigator,
    reportsViewModel: ReportsViewModel,
    settingsViewModel: SettingsViewModel
) {
    val numberFormatter = remember { DecimalFormat("#,##0.00") }
    val dayFormatter = remember { java.text.SimpleDateFormat("MMM dd", java.util.Locale.ENGLISH) }

    val weekTitle = stringResource(R.string.week_number, reportsViewModel.week?.id ?: 0)
    val dateRange: String = reportsViewModel.week?.let { week ->
        val calendar = Calendar.getInstance()
        calendar.time = week.startDate
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        val endDate = calendar.time

        stringResource(R.string.week_date_range, week.id, dayFormatter.format(week.startDate), dayFormatter.format(endDate))
    } ?: ""
    val totalMilk: Double = reportsViewModel.farmerWeekTotal.sumOf { it.total.toDouble() }
    val unitPrice: Double by settingsViewModel.defaultPrice.collectAsState(initial = 0.0)
    val totalRevenue: String = numberFormatter.format(unitPrice * totalMilk)
    val reportData: List<ReportRowItem> = reportsViewModel.farmerWeekTotal.map { farmerWeekTotal ->
        ReportRowItem(
            id = farmerWeekTotal.farmerId.toString(),
            name = farmerWeekTotal.farmerName,
            initials = farmerWeekTotal.farmerName.first().toString(),
            liters = numberFormatter.format(farmerWeekTotal.total),
            pricePerL = numberFormatter.format(unitPrice),
            total = numberFormatter.format(farmerWeekTotal.total * unitPrice)
        )
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val weekReportTitle = stringResource(R.string.week_report)
    val onExportPdfClick: () -> Unit = {
        scope.launch {
            PdfReportExporter.exportAndOpen(context, reportData, title = weekReportTitle)
        }
    }
    val onCloseWeek: () -> Unit = { navigator.gotoWeekClosing() }
    val isCurrentWeek = reportsViewModel.weekId == -1L

    Column(
            modifier = modifier
                .fillMaxSize()
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
                        text = stringResource(R.string.current_week),
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
                    title = stringResource(R.string.total_milk),
                    value = numberFormatter.format( totalMilk),
                    suffix = stringResource(R.string.ltr),
                    valueColor = DairyGreen,
                    modifier = Modifier.weight(1f)
                )
                SummaryMetricCard(
                    title = stringResource(R.string.revenue),
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
                    Icon(painter = painterResource(AppIcons.Pdf), contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(text = stringResource(R.string.export_pdf), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(32.dp))

            if (isCurrentWeek) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onCloseWeek,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MutedText)
                    ) {
                        Icon(painter = painterResource(AppIcons.Archive), contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(text = stringResource(R.string.close_week), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
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
                    painter = painterResource(AppIcons.Agriculture),
                    contentDescription = null,
                    tint = DairyGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.weekly_summary),
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
                Text(stringResource(R.string.farmer_name_upper), fontSize = 11.sp, color = MutedText, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(2f))
                Text(stringResource(R.string.liters_upper), fontSize = 11.sp, color = MutedText, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                Text(stringResource(R.string.price_per_liter_upper), fontSize = 11.sp, color = MutedText, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                Text(stringResource(R.string.total_upper), fontSize = 11.sp, color = MutedText, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.End, modifier = Modifier.weight(1.2f))
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
                    Text(text = row.name, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextDark)
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
                    Text(text = stringResource(R.string.report_total), fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = MutedText)
                    Text(text = stringResource(R.string.total_milk_ltr, totalMilk), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DairyGreen)
                }
                Spacer(Modifier.width(24.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = stringResource(R.string.total_payout), fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = MutedText)
                    Text(text = totalRevenue, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                }
            }
        }
    }
}
