package com.example.dairyledger.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dairyledger.R
import com.example.dairyledger.models.CurrentWeekViewModel
import com.example.dairyledger.models.SettingsViewModel
import com.example.dairyledger.ui.icons.AppIcons
import java.text.DecimalFormat
import java.util.Calendar


@Composable
fun WeekClosingScreen(
    navigator: Navigator,
    currentWeekViewModel: CurrentWeekViewModel,
    settingsViewModel: SettingsViewModel
) {
    val week = currentWeekViewModel.week
    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            ClosingTopBar()
        },
    ) { innerPadding ->
        if (week == null) {
            NoContentPlaceHolder(modifier = Modifier.padding(innerPadding))
        } else {
            WeekClosingContent(
                modifier = Modifier.padding(innerPadding),
                navigator,
                currentWeekViewModel,
                settingsViewModel
            )
        }
    }
}

@Composable
fun WeekClosingContent(
   modifier: Modifier,
   navigator: Navigator,
   currentWeekViewModel: CurrentWeekViewModel,
   settingsViewModel: SettingsViewModel
) {
    val numberFormatter = remember { DecimalFormat("#,##0.00") }
    val dayFormatter = remember { java.text.SimpleDateFormat("EEE, MMM dd", java.util.Locale.getDefault()) }

    val weekEndingLabel: String = currentWeekViewModel.week?.let { week ->
        val calendar = Calendar.getInstance()
        calendar.time = week.startDate
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        val endDate = calendar.time

        stringResource(R.string.week_ending, dayFormatter.format(endDate))
    } ?: ""
    val totalMilkCollected: Double = currentWeekViewModel.weekTotal.toDouble()
    val unitPrice: Double by settingsViewModel.defaultPrice.collectAsState(initial = 0.0)
    val totalAmountCollected: String = numberFormatter.format(unitPrice * totalMilkCollected)
    val onArchiveAndCloseClick: () -> Unit = { currentWeekViewModel.closeWeek() }
    val onDownloadPdfFirstClick: () -> Unit = {}

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        currentWeekViewModel.events.collect { event ->
            when (event) {
                is CurrentWeekViewModel.UiEvent.WeekClosed -> {
                    navigator.goback()
                }
                is CurrentWeekViewModel.UiEvent.Error -> {
                    android.widget.Toast.makeText(
                        context,
                        event.message,
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(12.dp))

        // Section Header Block
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = weekEndingLabel,
                fontSize = 14.sp,
                color = MutedText,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.weekly_summary),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }

        Spacer(Modifier.height(20.dp))

        // Card 1: Total Milk Collected Summary (Primary Highlight Card)
        PrimaryMetricHighlightCard(value = numberFormatter.format(totalMilkCollected))

        Spacer(Modifier.height(14.dp))

        // Card 2: Total Financial Payout Amount Summary
        SecondaryMetricCard(
            title = stringResource(R.string.total_amount),
            value = totalAmountCollected,
            icon = AppIcons.Payments
        )

        Spacer(Modifier.height(20.dp))

        // Core Warning System Notification Notice Panel Box
        ImportantNoticeAlertCard()

        Spacer(Modifier.height(24.dp))

        // Action Execution Button Control Architecture Block
        Button(
            onClick = onArchiveAndCloseClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DairyGreen)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(AppIcons.Archive),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.archive_start_new_week),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onDownloadPdfFirstClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextDark)
        ) {
            Text(
                text = stringResource(R.string.download_pdf_report_first),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(Modifier.height(24.dp))

        // System Readiness Graphic/Visual Panel Section
        SystemReadinessPanel()

        Spacer(Modifier.height(32.dp))
    }
}

// ---------- Private Modular Sub-Components ----------

@Composable
private fun ClosingTopBar() {
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
                    modifier = Modifier.size(26.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.dairy_operations),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DairyGreen
                )
            }
        }
    }
}

@Composable
private fun PrimaryMetricHighlightCard(value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AccentGreen)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(AppIcons.WaterDrop),
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.total_milk_collected),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.9f),
                    letterSpacing = 0.5.sp
                )
            }
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    lineHeight = 42.sp
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.liters),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun SecondaryMetricCard(title: String, value: String, icon: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MetricCardBg)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painter = painterResource(icon), contentDescription = null, tint = MutedText, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MutedText)
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = value,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }
    }
}

@Composable
private fun ImportantNoticeAlertCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = WarningBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, WarningBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Outlined.Warning,
                contentDescription = stringResource(R.string.warning_indicator),
                tint = WarningText,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = stringResource(R.string.important_notice),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = WarningText
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.closing_week_warning),
                    fontSize = 13.sp,
                    color = WarningText,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun SystemReadinessPanel() {
    // Standard box housing background configuration graphic layout matching screen elements
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Color(0xFFB0BEC5)) // Stand-in asset structure value
        ) {
            // Live Status Overlaid Badge positioned cleanly inside bottom left viewport
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(StatusGreen)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.system_ready_for_archival),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextDark
                )
            }
        }
    }
}
