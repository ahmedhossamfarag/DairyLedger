package com.example.dairyledger.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------- Design Tokens ----------
//private val DairyGreen = Color(0xFF1B5E20)
private val ActiveGreen = Color(0xFF2E7D32)
//private val ScreenBg = Color(0xFFFAF8F4)
private val CardBorder = Color(0xFFE7E3DA)
private val ControlFieldBg = Color(0xFFEFEBE9)
private val MutedText = Color(0xFF6B6B6B)
private val TextDark = Color(0xFF1A1A1A)
private val LightCardBg = Color(0xFFF5F5F5)

@Composable
fun Settings() {
    val agentName: String = "Collection Agent"
    val agentRoute: String = "Route #42 - North District"
    val onBackClick: () -> Unit = {}
    val onCalendarClick: () -> Unit = {}
    val onSaveConfigClick: (Double, String, Boolean, String) -> Unit = { _, _, _, _ -> }
    val onResetDefaultsClick: () -> Unit = {}

    // Component Interaction States
    var unitPrice by remember { mutableStateOf(0.45) }
    var resetDay by remember { mutableStateOf("Friday Evening") }
    var isBackupEnabled by remember { mutableStateOf(true) }
    var selectedCurrency by remember { mutableStateOf("EGP") }

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            SettingsTopBar(
                onBackClick = onBackClick,
                onCalendarClick = onCalendarClick
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

            // Profile Agent Header Card Banner
            AgentProfileCard(name = agentName, route = agentRoute)

            Spacer(Modifier.height(20.dp))

            // Setting Block 1: Unit Price Stepper Modifier
            UnitPriceCard(
                currentPrice = unitPrice,
                onPriceChange = { unitPrice = it }
            )

            Spacer(Modifier.height(16.dp))

            // Setting Block 2: Reset Day Selector Dropdown Menu Mock
            ResetDayCard(
                selectedDay = resetDay,
                onDayClick = { /* Launch day picker bottom sheet */ }
            )

            Spacer(Modifier.height(16.dp))

            // Setting Block 4: Currency Selection Box
            CurrencyCard(
                currency = selectedCurrency,
                onCurrencyClick = { /* Launch currency dropdown list */ }
            )

            Spacer(Modifier.height(24.dp))

            // Action Execution Buttons Stack
            Button(
                onClick = { onSaveConfigClick(unitPrice, resetDay, isBackupEnabled, selectedCurrency) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = DairyGreen)
            ) {
                Icon(imageVector = Icons.Outlined.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(text = "Save Configuration", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onResetDefaultsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(50),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ActiveGreen)
            ) {
                Icon(imageVector = Icons.Outlined.Refresh, contentDescription = null, tint = ActiveGreen, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(text = "Reset to Defaults", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ---------- Sub-Components Implementation ----------

@Composable
private fun SettingsTopBar(onBackClick: () -> Unit, onCalendarClick: () -> Unit) {
    Surface(color = ScreenBg) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Go back", tint = TextDark)
                }
                Spacer(Modifier.width(4.dp))
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
private fun AgentProfileCard(name: String, route: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LightCardBg)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mocking Image Avatar component matching Settings.png design outline
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            ) {
                // Application network/asset painter element would swap here
                Icon(
                    imageVector = Icons.Default.Person, 
                    contentDescription = null, 
                    tint = Color.White,
                    modifier = Modifier.fillMaxSize().padding(8.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(text = name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(Modifier.height(2.dp))
                Text(text = route, fontSize = 13.sp, color = MutedText)
            }
        }
    }
}

@Composable
private fun UnitPriceCard(currentPrice: Double, onPriceChange: (Double) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Outlined.Edit, contentDescription = null, tint = ActiveGreen, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(text = "Unit Price", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
            }
            
            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Decrement Button Control
                OutlinedIconButton(
                    onClick = { onPriceChange((currentPrice - 0.01).coerceAtLeast(0.0)) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(46.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Decrease price", tint = TextDark)
                }

                // Numeric Presentation Center Field Box Display
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(LightCardBg),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "$", fontSize = 14.sp, color = MutedText, modifier = Modifier.padding(end = 6.dp))
                        Text(
                            text = String.format("%.2f", currentPrice),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    }
                }

                // Increment Button Control
                OutlinedIconButton(
                    onClick = { onPriceChange(currentPrice + 0.01) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(46.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Increase price", tint = TextDark)
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(text = "Current rate per Liter of Grade A milk.", fontSize = 12.sp, color = MutedText)
        }
    }
}

@Composable
private fun ResetDayCard(selectedDay: String, onDayClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Outlined.DateRange, contentDescription = null, tint = ActiveGreen, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(text = "Weekly Reset Day", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
            }

            Spacer(Modifier.height(12.dp))

            // Styled Pseudo-Spinner Composable Selector Trigger Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(LightCardBg)
                    .clickable { onDayClick() }
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = selectedDay, fontSize = 14.sp, color = TextDark, fontWeight = FontWeight.Medium)
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Select options", tint = TextDark)
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(text = "Ledgers will automatically archive on this day.", fontSize = 12.sp, color = MutedText)
        }
    }
}

@Composable
private fun CurrencyCard(currency: String, onCurrencyClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Outlined.ShoppingCart, contentDescription = null, tint = ActiveGreen, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(text = "Currency", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
            }

            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(LightCardBg)
                    .clickable { onCurrencyClick() }
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = currency, fontSize = 14.sp, color = TextDark, fontWeight = FontWeight.Medium)
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = TextDark)
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 744)
@Composable
private fun SettingsScreenPreview() {
    MaterialTheme {
        Settings()
    }
}