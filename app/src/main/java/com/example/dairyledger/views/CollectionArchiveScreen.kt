package com.example.dairyledger.views

import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dairyledger.R
import com.example.dairyledger.data.CollectionType
import com.example.dairyledger.models.CollectionArchiveViewModel
import com.example.dairyledger.ui.icons.AppIcons

data class CollectionArchiveItem(
    val id: Long,
    val collectionLabel: String,
    val dateRange: String,
    val yield: String,
)

@Composable
fun CollectionArchiveScreen(
    navigator: Navigator,
    collectionArchiveViewModel: CollectionArchiveViewModel
) {
    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            ArchiveTopBar()
        },
    ) { innerPadding ->
        if (collectionArchiveViewModel.collections.isEmpty()) {
            NoContentPlaceHolder(modifier = Modifier.padding(innerPadding))
        } else {
            CollectionArchiveContent(
                modifier = Modifier.padding(innerPadding),
                navigator,
                collectionArchiveViewModel
            )
        }
    }
}

@Composable
fun CollectionArchiveContent(
    modifier: Modifier,
    navigator: Navigator,
    collectionArchiveViewModel: CollectionArchiveViewModel
) {
    val numberFormatter = remember { java.text.DecimalFormat("#,##0.00") }
    val dayFormatter = remember { java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault()) }

    val collections = collectionArchiveViewModel.collections

    val recentArchives = collections
        .groupBy { it.collection.type }
        .flatMap { (type, items) ->
            items.mapIndexed { index, item ->
                CollectionArchiveItem(
                    id = item.collection.id,
                    collectionLabel =
                        if (item.collection.type == CollectionType.MORNING)
                            stringResource(R.string.morning_collection_indexed, index + 1)
                        else
                            stringResource(R.string.evening_collection_indexed, index + 1),
                    dateRange = dayFormatter.format(item.collection.timestamp),
                    yield = numberFormatter.format(item.totalYield)
                )
            }
        }
    val onCardClick: (Long) -> Unit = { id -> navigator.gotoCollection(id.toInt()) }

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
                    text = stringResource(R.string.collection_archive),
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

        // Main Core Archive Stack
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            recentArchives.forEach { archive ->
                CollectionArchiveRowCard(item = archive, onCardClick = onCardClick)
            }
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

// ---------- Intermediate Standard Card Component ----------

@Composable
private fun CollectionArchiveRowCard(item: CollectionArchiveItem, onCardClick: (Long) -> Unit) {
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
                .clickable { onCardClick(item.id) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ArchiveIconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(painter = painterResource(AppIcons.WaterDrop), contentDescription = null, tint = MutedText, modifier = Modifier.size(20.dp))
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = item.collectionLabel, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
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
