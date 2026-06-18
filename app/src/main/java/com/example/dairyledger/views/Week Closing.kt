package com.example.dairyledger.views

import androidx.compose.foundation.background
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------- Design Tokens ----------
private val AccentGreen = Color(0xFF2E7D32)        // Metric card green background
private val StatusGreen = Color(0xFF4CAF50)        // Status active dot indicator
private val MetricCardBg = Color(0xFFEAEAEA)       // Light gray card background
private val CardBorder = Color(0xFFE7E3DA)         // Subtle layout divider outlines
private val TextDark = Color(0xFF1A1A1A)           // Dominant header text
private val MutedText = Color(0xFF6B6B6B)          // Subtitles and descriptive text

// Warning Notice Alert Tones
private val WarningBg = Color(0xFFFEECEB)
private val WarningBorder = Color(0xFFF5B1AA)
private val WarningText = Color(0xFFC62828)

@Composable
fun WeekClosingScreen() {
    val weekEndingLabel: String = "Week Ending Oct 27, 2023"
    val totalMilkCollected: String = "4,830"
    val totalAmountCollected: String = "$2,173.50"
    val onArchiveAndCloseClick: () -> Unit = {}
    val onDownloadPdfFirstClick: () -> Unit = {}

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            ClosingTopBar()
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
                    text = "Weekly Summary",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }

            Spacer(Modifier.height(20.dp))

            // Card 1: Total Milk Collected Summary (Primary Highlight Card)
            PrimaryMetricHighlightCard(value = totalMilkCollected)

            Spacer(Modifier.height(14.dp))

            // Card 2: Total Financial Payout Amount Summary
            SecondaryMetricCard(
                title = "Total Amount",
                value = totalAmountCollected,
                icon = Icons.Outlined.ShoppingCart
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
                    Icon(imageVector = Icons.Outlined.Lock, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(text = "Archive & Start New Week", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
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
                Text(text = "Download PDF Report First", fontSize = 15.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(24.dp))

            // System Readiness Graphic/Visual Panel Section
            SystemReadinessPanel()

            Spacer(Modifier.height(32.dp))
        }
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
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = null,
                    tint = DairyGreen,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Dairy Operations",
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
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "TOTAL MILK COLLECTED",
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
                    text = "Liters",
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
private fun SecondaryMetricCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MetricCardBg)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = MutedText, modifier = Modifier.size(18.dp))
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
                contentDescription = "Warning Indicator",
                tint = WarningText,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = "Important Notice",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = WarningText
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Closing the week will archive all current records and clear the dashboard for Monday's new collections. This action cannot be undone.",
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
                    text = "System Ready for Archival",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextDark
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 944)
@Composable
private fun WeekClosingScreenPreview() {
    MaterialTheme {
        WeekClosingScreen()
    }
}