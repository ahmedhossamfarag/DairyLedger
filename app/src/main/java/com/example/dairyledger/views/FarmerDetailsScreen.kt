package com.example.dairyledger.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dairyledger.R
import com.example.dairyledger.data.CollectionType
import com.example.dairyledger.data.FarmerCollectionDetail
import com.example.dairyledger.models.FarmerDetailsViewModel
import com.example.dairyledger.models.SettingsViewModel
import com.example.dairyledger.ui.icons.AppIcons
import java.text.DecimalFormat


data class ShiftLogItem(
    val dayLabel: String,
    val morningAmount: String,
    val eveningAmount: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerDetailsScreen(
    navigator: Navigator,
    farmerDetailsViewModel: FarmerDetailsViewModel,
    settingsViewModel: SettingsViewModel
) {
    val farmer = farmerDetailsViewModel.farmer

    if (farmer == null) {
        Scaffold(
            containerColor = ScreenBg,
            topBar = {
                FarmerDetailsTopBar(
                    title = stringResource(R.string.loading),
                    onBackClick = { navigator.goback() },
                )
            },
        ) { innerPadding ->
            NoContentPlaceHolder(modifier = Modifier.padding(innerPadding))
        }
    } else {
        FarmerDetailsScreenContent(navigator, farmerDetailsViewModel, settingsViewModel)
    }

}
@Composable
fun FarmerDetailsScreenContent(
    navigator: Navigator,
    farmerDetailsViewModel: FarmerDetailsViewModel,
    settingsViewModel: SettingsViewModel
) {
    val numberFormatter = remember { DecimalFormat("#,##0.00") }

    val farmer = farmerDetailsViewModel.farmer!!
    val farmerCollections = farmerDetailsViewModel.collectionDetails
    val farmerName: String = farmer.name
    val phone: String = farmer.phone
    val idNumber: String = farmerDetailsViewModel.farmerId.toString()
    val statusLabel: String = if (farmer.active) stringResource(R.string.active) else stringResource(R.string.inactive)
    val morningTotal: String = numberFormatter.format(
        farmerCollections
        .filter { it.type == CollectionType.MORNING }
        .sumOf { it.value.toDouble() }
    )
    val eveningTotal: String = numberFormatter.format(
        farmerCollections
        .filter { it.type == CollectionType.EVENING }
        .sumOf { it.value.toDouble() }
    )
    val cumulativeTotal: Double = farmerCollections.sumOf { it.value.toDouble() }
    val unitPrice: Double by settingsViewModel.defaultPrice.collectAsState(initial = 0.0)
    val totalAmountDue: String = numberFormatter.format (cumulativeTotal * unitPrice)
    val daysOfWeek = stringArrayResource(R.array.days_of_week_short).toList()
    val literShortTemplate = stringResource(R.string.amount_liters_short)
    val weeklyShiftLogs: List<ShiftLogItem> = remember(farmerCollections, daysOfWeek, literShortTemplate) {
        createShiftListOf(farmerCollections, numberFormatter, daysOfWeek, literShortTemplate)
    }
    val onBackClick: () -> Unit = { navigator.goback() }

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            FarmerDetailsTopBar(
                title = farmerName,
                onBackClick = onBackClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            // Main Core Information Identity Card
            FarmerIdentityProfileCard(
                name = farmerName,
                phone = phone,
                idNumber = idNumber,
                active = farmer.active,
                status = statusLabel
            )

            Spacer(Modifier.height(16.dp))

            // Dual Shift Metrics Panel Row Split
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ShiftTotalDisplayCard(
                    title = stringResource(R.string.morning_total),
                    value = morningTotal,
                    isPrimaryHighlight = true,
                    modifier = Modifier.weight(1f)
                )
                ShiftTotalDisplayCard(
                    title = stringResource(R.string.evening_total),
                    value = eveningTotal,
                    isPrimaryHighlight = false,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Large Full-Width Production Aggregation Display Card
            CumulativeProductionCard(totalVolume = numberFormatter.format(cumulativeTotal))

            Spacer(Modifier.height(16.dp))

            // Financial Ledger Overview Panel Card Container Block
            FinancialSummaryCard(
                unitPrice = numberFormatter.format(unitPrice),
                totalDue = totalAmountDue,
            )

            Spacer(Modifier.height(16.dp))

            // NEW REPLACEMENT ELEMENT: 2-Column Morning vs Evening Weekly Collection Table Block
            WeeklyCollectionsTableCard(logs = weeklyShiftLogs)

            Spacer(Modifier.height(16.dp))

            ToggleActiveButtons(active = farmer.active, farmerDetailsViewModel)

            Spacer(Modifier.height(16.dp))

            // Bottom Batch Premium Alert Status Accent Strip
            PremiumQualityBatchAlertCard()

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ---------- Sub-Components Layout Breakdown ----------

@Composable
private fun FarmerDetailsTopBar(
    title: String,
    onBackClick: () -> Unit,
) {
    Surface(color = ScreenBg) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(painter = painterResource(AppIcons.ArrowBack), contentDescription = stringResource(R.string.back), tint = DairyGreen)
                }
                Spacer(Modifier.width(4.dp))
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DairyGreen
                )
            }
        }
    }
}

@Composable
private fun FarmerIdentityProfileCard(name: String, phone: String, idNumber: String, active: Boolean, status: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Circle Avatar Graphic Placeholder Matching standard specs from Farmer Details_2.png
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFCE93D8)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(44.dp))
                // Simulated mini verification dot badge indicator accent layer
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(DairyGreen)
                        .align(Alignment.BottomEnd)
                        .border(1.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                }
            }

            Spacer(Modifier.height(14.dp))

            Text(text = name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = MutedText, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text(text = phone, fontSize = 14.sp, color = MutedText)
            }

            Spacer(Modifier.height(20.dp))

            // Sub Badge Information Row Split Panels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(LightCardBg, RoundedCornerShape(8.dp))
                        .padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(stringResource(R.string.id_number), fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = MutedText)
                    Spacer(Modifier.height(2.dp))
                    Text(idNumber, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DairyGreen)
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(LightCardBg, RoundedCornerShape(8.dp))
                        .padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(stringResource(R.string.status), fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = MutedText)
                    Spacer(Modifier.height(2.dp))
                    Text(status, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (active) DairyGreen else InactiveBrown)
                }
            }
        }
    }
}

@Composable
private fun ShiftTotalDisplayCard(title: String, value: String, isPrimaryHighlight: Boolean, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPrimaryHighlight) DairyGreen else Color.White
        ),
        border = if (isPrimaryHighlight) null else BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(if (isPrimaryHighlight) AppIcons.LightMode else AppIcons.DarkMode),
                    contentDescription = null,
                    tint = if (isPrimaryHighlight) LightGreenBg else DairyGreen,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.this_week),
                    fontSize = 12.sp,
                    color = if (isPrimaryHighlight) LightGreenBg else MutedText
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = if (isPrimaryHighlight) Color.White.copy(alpha = 0.9f) else TextDark
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPrimaryHighlight) Color.White else TextDark
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.liters),
                    fontSize = 14.sp,
                    color = if (isPrimaryHighlight) LightGreenBg else TextDark,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun CumulativeProductionCard(totalVolume: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = stringResource(R.string.cumulative_weekly_production),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MutedText,
                letterSpacing = 0.5.sp
            )
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = totalVolume, fontSize = 38.sp, fontWeight = FontWeight.Black, color = DairyGreen)
                Spacer(Modifier.width(6.dp))
                Text(text = stringResource(R.string.liters_total), fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TextDark, modifier = Modifier.padding(bottom = 4.dp))
            }
        }
    }
}

@Composable
private fun FinancialSummaryCard(unitPrice: String, totalDue: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painter = painterResource(AppIcons.Wallet), contentDescription = null, tint = DairyGreen, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(8.dp))
                Text(text = stringResource(R.string.financial_summary), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
            }

            Spacer(Modifier.height(16.dp))

            // Sub row info breakdown matrices
            FinancialSummaryRow(label = stringResource(R.string.unit_price_standard_grade), value = unitPrice)

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = CardBorder, thickness = 1.dp)
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = stringResource(R.string.total_amount), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text(text = stringResource(R.string.due), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                }
                Text(text = totalDue, fontSize = 36.sp, fontWeight = FontWeight.Black, color = DairyGreen)
            }
        }
    }
}

@Composable
private fun FinancialSummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightCardBg, RoundedCornerShape(8.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, color = MutedText, modifier = Modifier.weight(1f))
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark, textAlign = TextAlign.End)
    }
}

@Composable
private fun WeeklyCollectionsTableCard(logs: List<ShiftLogItem>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.weekly_shift_collections),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(Modifier.height(12.dp))

            // Two-column dynamic headers configuration [Morning, Evening]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LightCardBg, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.day), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MutedText, modifier = Modifier.padding(start = 12.dp).weight(0.8f))
                Text(text = stringResource(R.string.evening_shift), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark, textAlign = TextAlign.Center, modifier = Modifier.weight(1.2f))
                Text(text = stringResource(R.string.morning_shift), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark, textAlign = TextAlign.Center, modifier = Modifier.weight(1.2f))
            }

            // Structured body log rows mapping entries sequentially
            logs.forEach { item ->
                HorizontalDivider(color = CardBorder.copy(alpha = 0.5f), thickness = 1.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = item.dayLabel, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextDark, modifier = Modifier.padding(start = 12.dp).weight(0.8f))
                    Text(text = item.eveningAmount, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SlateNavy, textAlign = TextAlign.Center, modifier = Modifier.weight(1.2f))
                    Text(text = item.morningAmount, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SiennaBrown, textAlign = TextAlign.Center, modifier = Modifier.weight(1.2f))
                }
            }
        }
    }
}

@Composable
private fun PremiumQualityBatchAlertCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PremiumPremiumAlertBg)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = PremiumAlertText, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(text = stringResource(R.string.premium_quality_batch), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PremiumAlertText)
                Text(text = stringResource(R.string.premium_quality_batch_message), fontSize = 12.sp, color = PremiumAlertText.copy(alpha = 0.85f))
            }
        }
    }
}


@Composable
private fun ToggleActiveButtons(active: Boolean, farmerDetailsViewModel: FarmerDetailsViewModel){
    Button(
        onClick = {
            if (active) {
                farmerDetailsViewModel.setInactive()
            } else {
                farmerDetailsViewModel.setActive()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = if (active) MutedText else DairyGreen)
    ) {
        Icon(
            imageVector = if (active) Icons.Filled.Close else Icons.Filled.Check,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = if (active) stringResource(R.string.deactivate) else stringResource(R.string.activate),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
private fun createShiftListOf(
    farmerCollections: List<FarmerCollectionDetail>,
    numberFormatter: DecimalFormat,
    daysOfWeek: List<String>,
    literShortTemplate: String,
) : List<ShiftLogItem> {
    // 2. Divide the collections into morning and evening
    val morningCollections = farmerCollections.filter { it.type == CollectionType.MORNING }
    val eveningCollections = farmerCollections.filter { it.type == CollectionType.EVENING }

    // 3. Process the collections list
    val weeklyShiftLogs: List<ShiftLogItem> = daysOfWeek.mapIndexed { i, day ->
            ShiftLogItem(
                dayLabel = day,
                // Format to 1 decimal place or use your numberFormatter
                morningAmount =
                    if (i < morningCollections.size)
                        literShortTemplate.format(numberFormatter.format(morningCollections[i].value.toDouble()))
                    else "",
                eveningAmount =
                    if (i < eveningCollections.size)
                        literShortTemplate.format(numberFormatter.format(eveningCollections[i].value.toDouble()))
                    else ""
            )
        }

    return weeklyShiftLogs
}
