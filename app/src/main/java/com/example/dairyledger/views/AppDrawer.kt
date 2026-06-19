package com.example.dairyledger.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController



/**
 * Sidebar / nav-drawer content shown for the Collection Agent profile.
 * Pass this into ModalNavigationDrawer's `drawerContent` slot (see HomeScreen.kt).
 */
@Composable
fun AppDrawerContent(
    modifier: Modifier = Modifier,
    agentName: String = "Dairy Ledger",
    appVersion: String = "App Version 1.1.0 (Stable)",
    navController: NavHostController,
) {
    ModalDrawerSheet(
        modifier = modifier,
        drawerContainerColor = ScreenBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(28.dp))

            // Avatar
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(AvatarBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = agentName,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DairyGreen
            )

            Spacer(Modifier.height(16.dp))

            HorizontalDivider(color = DividerColor, thickness = 1.dp)

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Activity",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = SectionLabelColor
            )

            Spacer(Modifier.height(8.dp))

            drawerNavItems.forEach {
                DrawerRow(
                    it.label,
                    it.icon
                ) {
                    navController.navigate(it.route) {
                        // Avoid building up a large stack as the user taps tabs.
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Text(
                text = appVersion,
                fontSize = 12.sp,
                color = SecondaryText,
                modifier = Modifier.padding(bottom = 20.dp)
            )
        }
    }
}

@Composable
private fun DrawerRow(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color.Transparent,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF3A3A3A),
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(14.dp))
            Text(
                text = label,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1A1A)
            )
        }
    }
}

