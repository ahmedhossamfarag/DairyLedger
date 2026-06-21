package com.example.dairyledger.views

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dairyledger.R
import com.example.dairyledger.data.Farmer
import com.example.dairyledger.models.FarmersViewModel
import com.example.dairyledger.ui.icons.AppIcons


@Composable
fun AddFarmerScreen(navigator: Navigator, farmersViewModel: FarmersViewModel) {
    val routeLabel = stringResource(R.string.register_new_producer)
    val onCancelClick: () -> Unit = { navigator.gotoFarmers() }
    val onSaveFarmerClick: (name: String, phone: String, notes: String) -> Unit = {
        name, phone, notes -> farmersViewModel.addFarmer(
        Farmer(
            -1,
            name,
            phone,
            notes
        )
    )
    }

    val context = LocalContext.current // Get the context

    LaunchedEffect(Unit) {
        farmersViewModel.events.collect { event ->
            when (event) {
                is FarmersViewModel.UiEvent.FarmerAdded -> {
                    navigator.gotoFarmers()
                }
                is FarmersViewModel.UiEvent.NameLengthError -> {
                    android.widget.Toast.makeText(
                        context,
                        context.getString(R.string.name_is_too_short),
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                }
                is FarmersViewModel.UiEvent.Error -> {
                    android.widget.Toast.makeText(
                        context,
                        event.message,
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    var farmerName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var additionalNotes by remember { mutableStateOf("") }

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
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
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = DairyGreen
                        )
                    }
                }
            }
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

            // Screen Subheading layout
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(AppIcons.AddPerson),
                    contentDescription = null,
                    tint = TextDark,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.add_new_farmer),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = routeLabel,
                fontSize = 14.sp,
                color = MutedText
            )

            Spacer(Modifier.height(24.dp))

            // Avatar Dashed Image Uploader Frame
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally)
                    .border(width = 1.5.dp, color = MutedText, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = stringResource(R.string.upload_avatar_pic),
                    tint = MutedText,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            // Form inputs block
            FormInputField(
                label = stringResource(R.string.farmer_name),
                value = farmerName,
                onValueChange = { farmerName = it },
                placeholder = stringResource(R.string.enter_full_name),
                leadingIcon = AppIcons.Person
            )

            Spacer(Modifier.height(16.dp))

            FormInputField(
                label = stringResource(R.string.phone_number),
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                placeholder = stringResource(R.string.phone_placeholder),
                leadingIcon = AppIcons.Phone
            )

            Spacer(Modifier.height(20.dp))

            FormInputField(
                label = stringResource(R.string.additional_notes),
                value = additionalNotes,
                onValueChange = { additionalNotes = it },
                placeholder = stringResource(R.string.additional_notes_placeholder),
                leadingIcon = AppIcons.Description,
                singleLine = false,
                modifier = Modifier.height(100.dp)
            )

            Spacer(Modifier.height(32.dp))

            // Action Execution Button Row Block
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onCancelClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MutedText),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextDark)
                ) {
                    Text(text = stringResource(R.string.cancel), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }

                Button(
                    onClick = { onSaveFarmerClick(farmerName, phoneNumber, additionalNotes) },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = DairyGreen)
                ) {
                    Icon(
                        painter = painterResource(AppIcons.Save),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(text = stringResource(R.string.save_farmer), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun FormInputField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: Int,
    singleLine: Boolean = true,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextDark,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.fillMaxWidth(),
            placeholder = { Text(text = placeholder, color = MutedText, fontSize = 15.sp) },
            leadingIcon = { Icon(painter = painterResource(leadingIcon), contentDescription = null, tint = MutedText) },
            singleLine = singleLine,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = CardBorder,
                unfocusedBorderColor = CardBorder
            )
        )
    }
}
