package com.example.dairyledger.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


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
fun WeeklyArchiveScreen(navigator: Navigator) {
    val featuredArchive: FeaturedArchive = FeaturedArchive("WEEK 22", "Jun 03 - Jun 09, 2024", "4,720", 42)
    val recentArchives: List<WeeklyArchiveItem> = listOf(
        WeeklyArchiveItem(1, "Week 21", "May 27 - Jun 02", "4,680"),
        WeeklyArchiveItem(2, "Week 20", "May 20 - May 26", "4,595"),
        WeeklyArchiveItem(3, "Week 19", "May 13 - May 19", "4,810"),
        WeeklyArchiveItem(4, "Week 18", "May 06 - May 12", "4,420")
    )
    val onViewReportClick: () -> Unit = { navigator.gotoReports() }
    val onCardClick: (Int) -> Unit = { id -> navigator.gotoWeekReport(id) }
    val onLoadOlderClick: () -> Unit = {}

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            ArchiveTopBar()
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                        text = "Weekly Archive",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Historical milk collection records",
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
                Text(text = "Load Older Archives", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(Modifier.height(32.dp))
        }
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
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = null,
                    tint = DairyGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(10.dp))
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
            Text(text = "TOTAL YIELD", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedText, letterSpacing = 0.5.sp)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(text = data.totalYield, fontSize = 36.sp, fontWeight = FontWeight.Bold, color = DoneGreenBg)
                    Spacer(Modifier.width(4.dp))
                    Text(text = "L", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = DoneGreenBg, modifier = Modifier.padding(bottom = 6.dp))
                }
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowUp,
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
                Text(text = "${data.activeFarmers} Active Farmers", fontSize = 13.sp, color = MutedText)
                TextButton(
                    onClick = onViewReportClick,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "View Report", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DoneGreenBg)
                        Spacer(Modifier.width(4.dp))
                        Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = null, tint = DoneGreenBg, modifier = Modifier.size(14.dp))
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
                Icon(imageVector = Icons.Outlined.CheckCircle, contentDescription = null, tint = MutedText, modifier = Modifier.size(20.dp))
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
                    Text(text = "L", fontSize = 12.sp, color = TextDark, modifier = Modifier.padding(bottom = 2.dp))
                }
            }
        }
    }
}
