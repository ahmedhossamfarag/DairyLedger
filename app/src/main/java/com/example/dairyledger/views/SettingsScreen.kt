package com.example.dairyledger.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dairyledger.models.SettingsViewModel
import com.example.dairyledger.ui.icons.AppIcons
import java.text.DecimalFormat


@Composable
fun SettingsScreen(settingsViewModel: SettingsViewModel) {
    val profileTitle: String = "System Configuration"
    val profileSubtitle: String = "Manage application preferences"
    val onSaveConfigClick: (Double) -> Unit = { unitPrice -> settingsViewModel.setDefaultPrice(unitPrice) }
    val onResetDefaultsClick: () -> Unit = {}

    val context = LocalContext.current // Get the context

    LaunchedEffect(Unit) {
        settingsViewModel.events.collect { event ->
            when (event) {
                is SettingsViewModel.UiEvent.SettingsSaved -> {
                    // Show the toast message here
                    android.widget.Toast.makeText(
                        context,
                        "Configuration saved successfully",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
                is SettingsViewModel.UiEvent.Error -> {
                    // Handle error event
                    android.widget.Toast.makeText(
                        context,
                        event.message,
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    // Component Interaction States
    val defaultPrice by settingsViewModel.defaultPrice.collectAsState(initial = 0.0)
    var unitPrice by remember { mutableStateOf(defaultPrice) }
    var resetDay by remember { mutableStateOf("Friday Evening") }
    var selectedCurrency by remember { mutableStateOf("EGP") }

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            SettingsTopBar()
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
            AgentProfileCard(name = profileTitle, route = profileSubtitle)

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
                onClick = { onSaveConfigClick(unitPrice) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = DairyGreen)
            ) {
                Icon(painter = painterResource(AppIcons.Save), contentDescription = null, modifier = Modifier.size(18.dp))
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
                Icon(painter = painterResource(AppIcons.Reset), contentDescription = null, tint = ActiveGreen, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(text = "Reset to Defaults", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ---------- Sub-Components Implementation ----------

@Composable
private fun SettingsTopBar() {
    Surface(color = ScreenBg) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {}) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "Go back", tint = TextDark)
                }
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Settings",
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
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            ) {
                Icon(
                    painter = painterResource(AppIcons.Agriculture),
                    contentDescription = null, 
                    tint = Color.White,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
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
    val numberFormatter = remember { DecimalFormat("#,##0.00") }

    var textValue by remember(currentPrice) { mutableStateOf(numberFormatter.format(currentPrice)) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painter = painterResource(AppIcons.Payments), contentDescription = null, tint = ActiveGreen, modifier = Modifier.size(20.dp))
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
                    Icon(painter = painterResource(AppIcons.Remove), contentDescription = "Decrease price", tint = TextDark)
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
                        BasicTextField(
                            value = textValue,
                            onValueChange = { input ->
                                // Sanitize keyboard input to match standard decimal structures cleanly
                                if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d*$"))) {
                                    textValue = input
                                    input.toDoubleOrNull()?.let { onPriceChange(it) }
                                }
                            },
                            textStyle = TextStyle(
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1A1A),
                                textAlign = TextAlign.Center
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
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
                Icon(painter = painterResource(AppIcons.CalendarMonth), contentDescription = null, tint = ActiveGreen, modifier = Modifier.size(20.dp))
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
            Text(text = "Users are encouraged to reset the archive on this day.", fontSize = 12.sp, color = MutedText)
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
                Icon(painter = painterResource(AppIcons.Exchange), contentDescription = null, tint = ActiveGreen, modifier = Modifier.size(20.dp))
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
