package com.example.dairyledger.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dairyledger.models.CollectViewModel
import java.util.Locale


data class FarmerCollectionState(
    val id: String,
    val name: String,
    val initialLiters: Double,
    val isChecked: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectScreen(type: String = "default", navigator: Navigator, collectViewModel: CollectViewModel) {
    val titleLabel = "$type Collection"
    val dateLabel = "Monday, June 15"
    val initialFarmers = listOf(
        FarmerCollectionState("1", "Ahmed Hassan", 12.5, true),
        FarmerCollectionState("2", "Fatima Zahra", 0.0, false),
        FarmerCollectionState("3", "John Doe", 8.2, false),
        FarmerCollectionState("4", "Ibrahim Malik", 15.0, false)
    )
    val onSaveCollectionClick: (Map<String, Double>) -> Unit = { navigator.gotoHome() }

    var searchQuery by remember { mutableStateOf("") }
    
    // Track the liters input for each farmer dynamically
    val volumesState = remember { 
        mutableStateMapOf<String, String>().apply {
            initialFarmers.forEach { this[it.id] = String.format(Locale.US, "%.1f", it.initialLiters) }
        }
    }

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            CollectionTopBar(
                title = titleLabel,
                subtitle = dateLabel,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ScreenBg)
                .padding(innerPadding)
        ) {
            // Search Bar Section
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                placeholder = { Text("Search Farmer", color = MutedText, fontSize = 15.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = MutedText
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = CardBorder,
                    unfocusedBorderColor = CardBorder
                )
            )

            // Scrollable Farmer List
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val filteredFarmers = initialFarmers.filter {
                    it.name.contains(searchQuery, ignoreCase = true)
                }

                filteredFarmers.forEach { farmer ->
                    val currentVolumeText = volumesState[farmer.id] ?: "0.0"
                    
                    FarmerCollectionCard(
                        farmer = farmer,
                        volumeText = currentVolumeText,
                        onVolumeChange = { newValue -> volumesState[farmer.id] = newValue }
                    )
                }
                
                Spacer(Modifier.height(8.dp))

                // Persistent Action Button at the base of the scrollable list
                Button(
                    onClick = {
                        val finalData = initialFarmers.associate { 
                            it.id to (volumesState[it.id]?.toDoubleOrNull() ?: 0.0) 
                        }
                        onSaveCollectionClick(finalData)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DairyGreen)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Save Collection",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

// ---------- Top Bar Component ----------

@Composable
private fun CollectionTopBar(
    title: String,
    subtitle: String,
) {
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
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = DairyGreen
                    )
                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        color = MutedText
                    )
                }
            }
        }
    }
}

// ---------- Farmer Entry Card Component ----------

@Composable
private fun FarmerCollectionCard(
    farmer: FarmerCollectionState,
    volumeText: String,
    onVolumeChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Status Indicator, Name, Status Action Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (farmer.isChecked) StatusGreen else MutedText)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = farmer.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A1A1A)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Stepper Container Panel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(StepperBg)
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Minus Stepper Button
                IconButton(
                    onClick = {
                        val currentVal = volumeText.toDoubleOrNull() ?: 0.0
                        if (currentVal >= 0.25) {
                            onVolumeChange(String.format(Locale.US, "%.1f", currentVal - 0.25))
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                ) {
                    Icon(imageVector = Icons.Filled.KeyboardArrowDown, contentDescription = "Decrease", tint = StatusGreen)
                }

                // Core Interactive Text Field & Measurement Label
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    BasicTextField(
                        value = volumeText,
                        onValueChange = { input ->
                            // Sanitize keyboard input to match standard decimal structures cleanly
                            if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d*$"))) {
                                onVolumeChange(input)
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
                    Text(
                        text = "LITERS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MutedText,
                        letterSpacing = 0.5.sp
                    )
                }

                // Plus Stepper Button
                IconButton(
                    onClick = {
                        val currentVal = volumeText.toDoubleOrNull() ?: 0.0
                        onVolumeChange(String.format(Locale.US, "%.1f", currentVal + 0.25))
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Increase", tint = StatusGreen)
                }
            }
        }
    }
}
