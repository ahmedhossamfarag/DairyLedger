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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



data class ShiftLogItem(
    val dayLabel: String,
    val morningAmount: String,
    val eveningAmount: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerDetailsScreen() {

    val farmerName: String = "Ahmed Hassan"
    val phone: String = "0123456789"
    val idNumber: String = "#9822"
    val statusLabel: String = "Active"
    val morningTotal: String = "78"
    val eveningTotal: String = "74"
    val cumulativeTotal: String = "152"
    val unitPrice: String = "$0.45 / L"
    val totalAmountDue: String = "$68.40"
    val weeklyShiftLogs: List<ShiftLogItem> = listOf(
        ShiftLogItem("Mon", "12.5 L", "11.0 L"),
        ShiftLogItem("Tue", "13.0 L", "10.5 L"),
        ShiftLogItem("Wed", "11.2 L", "12.0 L"),
        ShiftLogItem("Thu", "14.1 L", "11.8 L"),
        ShiftLogItem("Fri", "13.2 L", "14.1 L"),
        ShiftLogItem("Sat", "14.0 L", "14.6 L")
    )
    val onBackClick: () -> Unit = {}

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
                status = statusLabel
            )

            Spacer(Modifier.height(16.dp))

            // Dual Shift Metrics Panel Row Split
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ShiftTotalDisplayCard(
                    title = "Morning Total",
                    value = morningTotal,
                    isPrimaryHighlight = true,
                    modifier = Modifier.weight(1f)
                )
                ShiftTotalDisplayCard(
                    title = "Evening Total",
                    value = eveningTotal,
                    isPrimaryHighlight = false,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Large Full-Width Production Aggregation Display Card
            CumulativeProductionCard(totalVolume = cumulativeTotal)

            Spacer(Modifier.height(16.dp))

            // Financial Ledger Overview Panel Card Container Block
            FinancialSummaryCard(
                unitPrice = unitPrice,
                totalDue = totalAmountDue,
            )

            Spacer(Modifier.height(16.dp))

            // NEW REPLACEMENT ELEMENT: 2-Column Morning vs Evening Weekly Collection Table Block
            WeeklyCollectionsTableCard(logs = weeklyShiftLogs)

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
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = DairyGreen)
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
private fun FarmerIdentityProfileCard(name: String, phone: String, idNumber: String, status: String) {
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
                    Text("ID NUMBER", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = MutedText)
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
                    Text("STATUS", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = MutedText)
                    Spacer(Modifier.height(2.dp))
                    Text(status, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DairyGreen)
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
                    imageVector = if (isPrimaryHighlight) Icons.Outlined.Star else Icons.Outlined.Close,
                    contentDescription = null,
                    tint = if (isPrimaryHighlight) LightGreenBg else DairyGreen,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "This Week",
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
                    text = "Liters",
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
                text = "CUMULATIVE WEEKLY PRODUCTION",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MutedText,
                letterSpacing = 0.5.sp
            )
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = totalVolume, fontSize = 38.sp, fontWeight = FontWeight.Black, color = DairyGreen)
                Spacer(Modifier.width(6.dp))
                Text(text = "Liters Total", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TextDark, modifier = Modifier.padding(bottom = 4.dp))
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
                Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null, tint = DairyGreen, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(8.dp))
                Text(text = "Financial Summary", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
            }

            Spacer(Modifier.height(16.dp))

            // Sub row info breakdown matrices
            FinancialSummaryRow(label = "Unit Price (Standard Grade)", value = unitPrice)

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = CardBorder, thickness = 1.dp)
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Total Amount", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text(text = "Due", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
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
                text = "Weekly Shift Collections",
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
                Text(text = "Day", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MutedText, modifier = Modifier.padding(start = 12.dp).weight(0.8f))
                Text(text = "Morning Shift", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DairyGreen, textAlign = TextAlign.Center, modifier = Modifier.weight(1.2f))
                Text(text = "Evening Shift", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MutedText, textAlign = TextAlign.Center, modifier = Modifier.weight(1.2f))
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
                    Text(text = item.morningAmount, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark, textAlign = TextAlign.Center, modifier = Modifier.weight(1.2f))
                    Text(text = item.eveningAmount, fontSize = 13.sp, color = MutedText, textAlign = TextAlign.Center, modifier = Modifier.weight(1.2f))
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
                Text(text = "Premium Quality Batch", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PremiumAlertText)
                Text(text = "Fat content averaged 4.2% this week.", fontSize = 12.sp, color = PremiumAlertText.copy(alpha = 0.85f))
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1400)
@Composable
private fun FarmerDetailsScreenPreview() {
    MaterialTheme {
        FarmerDetailsScreen()
    }
}