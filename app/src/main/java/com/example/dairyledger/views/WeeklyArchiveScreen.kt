package com.example.dairyledger.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dairyledger.R
import com.example.dairyledger.data.WeekTotal
import com.example.dairyledger.models.WeeklyArchiveViewModel
import com.example.dairyledger.ui.icons.AppIcons
import java.util.Calendar


data class FeaturedArchive(
    val weekLabel: String,
    val dateRange: String,
    val totalYield: String,
    val activeFarmers: Int
)

data class WeeklyArchiveItem(
    val id: Int,
    val weekLabel: String,
    val dateRange: String,
    val yield: String,
)

@Composable
fun WeeklyArchiveScreen(navigator: Navigator, weekArchiveViewModel: WeeklyArchiveViewModel) {
    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            ArchiveTopBar()
        },
    ) { innerPadding ->
        if (weekArchiveViewModel.weeks.isEmpty()) {
            NoContentPlaceHolder(modifier = Modifier.padding(innerPadding))
        } else {
            WeeklyArchiveContent(
                modifier = Modifier.padding(innerPadding),
                navigator,
                weekArchiveViewModel
            )
        }
    }
}

@Composable
fun WeeklyArchiveContent(
    modifier: Modifier,
    navigator: Navigator,
    weekArchiveViewModel: WeeklyArchiveViewModel
) {
    val numberFormatter = remember { java.text.DecimalFormat("#,##0.00") }
    val dayFormatter = remember { java.text.SimpleDateFormat("MMM dd", java.util.Locale.ENGLISH) }

    val weeks = weekArchiveViewModel.weeks
    val activeFarmers = weekArchiveViewModel.activeFarmersCount
    val context = LocalContext.current

    val localizedDateRange: (WeekTotal) -> String = { week ->
        val calendar = Calendar.getInstance()
        calendar.time = week.startDate
        val startPart = dayFormatter.format(week.startDate)
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        val endPart = dayFormatter.format(calendar.time)
        context.getString(R.string.date_range, startPart, endPart)
    }

    val featuredArchive: FeaturedArchive =  weeks.first().let {
        FeaturedArchive(
            stringResource(R.string.week_upper, it.weekId),
            localizedDateRange(it),
            numberFormatter.format(it.total),
            activeFarmers
        )
    }
    val recentArchives: List<WeeklyArchiveItem> = weeks.drop(1).map {
        WeeklyArchiveItem(
            id = it.weekId.toInt(),
            weekLabel = stringResource(R.string.week_upper, it.weekId),
            dateRange = localizedDateRange(it),
            yield = numberFormatter.format(it.total)
        )
    }
    val onViewReportClick: () -> Unit = { navigator.gotoReports() }
    val onCardClick: (Int) -> Unit = { id -> navigator.gotoWeekReport(id) }
    val onLoadOlderClick: () -> Unit = { weekArchiveViewModel.loadMore() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(12.dp))

        // Header Row Section with Filter Action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    text = stringResource(R.string.weekly_archive),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.historical_milk_records),
                    fontSize = 14.sp,
                    color = MutedText
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Highlighted Featured Week Panel
        FeaturedCard(data = featuredArchive, onViewReportClick = onViewReportClick)

        Spacer(Modifier.height(20.dp))

        // Main Core Archive Stack
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            recentArchives.forEach { archive ->
                WeeklyArchiveRowCard(item = archive, onCardClick = onCardClick)
            }
        }

        Spacer(Modifier.height(24.dp))

        // Paginated Load More Button Action Anchor
        Button(
            onClick = onLoadOlderClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 40.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = DairyGreen)
        ) {
            Text(text = stringResource(R.string.load_older_archives), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(Modifier.height(32.dp))
    }
}

// ---------- Top Bar Component ----------

@Composable
private fun ArchiveTopBar() {
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
                    text = stringResource(R.string.dairy_operations),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DairyGreen
                )
            }
        }
    }
}

// ---------- Featured Archive Card ----------

@Composable
private fun FeaturedCard(data: FeaturedArchive, onViewReportClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFEBE4)), // slightly darker warm backdrop tint
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = DoneGreenBg
                ) {
                    Text(
                        text = data.weekLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Text(text = data.dateRange, fontSize = 13.sp, color = MutedText)
            }

            Spacer(Modifier.height(14.dp))
            Text(text = stringResource(R.string.total_yield), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedText, letterSpacing = 0.5.sp)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(text = data.totalYield, fontSize = 36.sp, fontWeight = FontWeight.Bold, color = DoneGreenBg)
                    Spacer(Modifier.width(4.dp))
                    Text(text = stringResource(R.string.liters_unit_short), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = DoneGreenBg, modifier = Modifier.padding(bottom = 6.dp))
                }
                Icon(
                    painter = painterResource(AppIcons.WaterDrop),
                    contentDescription = null,
                    tint = DoneGreenBg,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = CardBorder, thickness = 1.dp)
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.active_farmers_count, data.activeFarmers), fontSize = 13.sp, color = MutedText)
                TextButton(
                    onClick = onViewReportClick,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = stringResource(R.string.view_report), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DoneGreenBg)
                        Spacer(Modifier.width(4.dp))
                        Icon(painter = painterResource(AppIcons.ArrowForward), contentDescription = null, tint = DoneGreenBg, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

// ---------- Intermediate Standard Card Component ----------

@Composable
private fun WeeklyArchiveRowCard(item: WeeklyArchiveItem, onCardClick: (Int) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
                .clickable{ onCardClick(item.id) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ArchiveIconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(painter = painterResource(AppIcons.Inventory), contentDescription = null, tint = MutedText, modifier = Modifier.size(20.dp))
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = item.weekLabel, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text(text = item.dateRange, fontSize = 12.sp, color = MutedText)
                }
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(text = item.yield, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(Modifier.width(2.dp))
                    Text(text = stringResource(R.string.liters_unit_short), fontSize = 12.sp, color = TextDark, modifier = Modifier.padding(bottom = 2.dp))
                }
            }
        }
    }
}
