package com.example.dairyledger.views

import androidx.compose.foundation.background
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dairyledger.R
import com.example.dairyledger.models.FarmersViewModel
import com.example.dairyledger.ui.icons.AppIcons

data class FarmerListItem(
    val id: Long,
    val name: String,
    val phone: String,
    val initials: String,
    val active: Boolean = true
)

@Composable
fun FarmersScreen(navigator: Navigator, farmersViewModel: FarmersViewModel) {
    val farmersCount: Int = farmersViewModel.farmers.size
    val farmersList: List<FarmerListItem> = farmersViewModel.farmers.map { FarmerListItem(
        id = it.id,
        name = it.name,
        phone = it.phone,
        initials = it.name.take(2).uppercase(),
        active = it.active
    ) }
    val onFarmerCardClick: (Int) -> Unit = { farmerId -> navigator.gotoFarmerDetails(farmerId)}
    val onAddFarmerClick: () -> Unit = { navigator.gotoAddFarmer() }


    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            FarmersTopBar()
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddFarmerClick,
                containerColor = DairyGreen,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    painter = painterResource(AppIcons.AddPerson),
                    contentDescription = stringResource(R.string.add_new_farmer),
                    modifier = Modifier.size(32.dp)
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Filter Pills Row
            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DairyGreen
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(AppIcons.People),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.all_farmers_count, farmersCount),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Section Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.active_collections),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextDark
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.sort_by_name),
                        fontSize = 14.sp,
                        color = ValueGreen,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        tint = ValueGreen
                    )
                }
            }

            if (farmersList.isEmpty()){
                NoContentPlaceHolder()
            } else {
                // Farmers List Block
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    farmersList.forEach { farmer ->
                        FarmerCardRow(farmer = farmer, onFarmerCardClick = onFarmerCardClick)
                    }
                    Spacer(Modifier.height(80.dp)) // Extra layout padding for FAB overlap
                }
            }
        }
    }
}

@Composable
private fun FarmersTopBar() {
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
                    text = stringResource(R.string.farmers),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = DairyGreen
                )
            }
        }
    }
}

@Composable
private fun FarmerCardRow(farmer: FarmerListItem, onFarmerCardClick: (Int) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onFarmerCardClick(farmer.id.toInt()) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile circular thumbnail / Initials backup
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(if (farmer.active) ActiveFarmerAvatarBG else InactiveFarmerAvatarBG),
                contentAlignment = Alignment.Center
            ) {
                // Production note: Replace template Text with async Image painters when assets exist
                Text(
                    text = farmer.initials,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(Modifier.width(14.dp))

            // Information block
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = farmer.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Phone,
                        contentDescription = null,
                        tint = MutedText,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = farmer.phone,
                        fontSize = 13.sp,
                        color = MutedText
                    )
                }
            }

            // Statistics block
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = stringResource(R.string.status),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MutedText,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = if (farmer.active) stringResource(R.string.active_upper) else stringResource(R.string.inactive_upper),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (farmer.active) ValueGreen else InactiveBrown
                )
            }
        }
    }
}
