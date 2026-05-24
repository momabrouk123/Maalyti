package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.data.SmsParser
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.FinancialViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: FinancialViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Request SMS Permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS), 101)
        } else {
            // Already permitted, trigger sync
            lifecycleScope.launch {
                // Short delay to ensure database is created/populated
                delay(1200)
                SmsParser.readAndSyncSms(this@MainActivity, viewModel)
            }
        }

        setContent {
            MyApplicationTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = BackgroundDark
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            AppMainRouter(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            lifecycleScope.launch {
                delay(500)
                SmsParser.readAndSyncSms(this@MainActivity, viewModel)
            }
        }
    }
}

@Composable
fun AppMainRouter(viewModel: FinancialViewModel) {
    val settings by viewModel.settings.collectAsState()

    // Determine onboarding state manually or let them skip
    var isTutorialFinished by remember { mutableStateOf(true) } // Pre-loaded default true to let user explore immediately

    // Steps: 1 = Permissions, 2 = Budget, 3 = Cards
    var onboardingStepState by remember { mutableStateOf(1) }

    // Selected navigation screen destination
    // "home", "expenses", "cards", "reports", "settings", "detail/{id}"
    var currentScreenDestination by remember { mutableStateOf("home") }
    var selectedDetailId by remember { mutableStateOf(-1) }

    if (!isTutorialFinished) {
        when (onboardingStepState) {
            1 -> OnboardingPermissionsScreen(
                viewModel = viewModel,
                onNext = { onboardingStepState = 2 }
            )
            2 -> OnboardingBudgetScreen(
                viewModel = viewModel,
                onBack = { onboardingStepState = 1 },
                onNext = { onboardingStepState = 3 }
            )
            3 -> OnboardingCardScreen(
                viewModel = viewModel,
                onBack = { onboardingStepState = 2 },
                onFinish = {
                    isTutorialFinished = true
                }
            )
        }
    } else {
        // Main bottom nav container
        Box(modifier = Modifier.fillMaxSize()) {
            // Screen switching controller
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundDark)
            ) {
                when {
                    currentScreenDestination == "home" -> HomeScreen(
                        viewModel = viewModel,
                        onNavigateToExpenses = { currentScreenDestination = "expenses" },
                        onNavigateToDetail = {
                            selectedDetailId = it.id
                            currentScreenDestination = "detail"
                        },
                        onNavigateToCards = { currentScreenDestination = "cards" }
                    )
                    currentScreenDestination == "expenses" -> ExpensesScreen(
                        viewModel = viewModel,
                        onNavigateToDetail = {
                            selectedDetailId = it.id
                            currentScreenDestination = "detail"
                        }
                    )
                    currentScreenDestination == "cards" -> CardsScreen(
                        viewModel = viewModel
                    )
                    currentScreenDestination == "reports" -> ReportsScreen(
                        viewModel = viewModel
                    )
                    currentScreenDestination == "settings" -> SettingsScreen(
                        viewModel = viewModel,
                        onNavigateToCards = { currentScreenDestination = "cards" }
                    )
                    currentScreenDestination == "detail" -> ExpenseDetailScreen(
                        viewModel = viewModel,
                        transactionId = selectedDetailId,
                        onBack = { currentScreenDestination = "expenses" }
                    )
                }
            }

            // Bottom Cristal Navigation Bar (Hides if editing details)
            if (currentScreenDestination != "detail") {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                        .fillMaxWidth()
                        .height(72.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(SurfaceCard.copy(alpha = 0.95f))
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BottomNavTabItem(
                            icon = Icons.Default.Home,
                            label = "الرئيسية",
                            isSelected = currentScreenDestination == "home",
                            onClick = { currentScreenDestination = "home" }
                        )

                        BottomNavTabItem(
                            icon = Icons.Default.Payments,
                            label = "المصاريف",
                            isSelected = currentScreenDestination == "expenses",
                            onClick = { currentScreenDestination = "expenses" }
                        )

                        BottomNavTabItem(
                            icon = Icons.Default.CreditCard,
                            label = "البطاقات",
                            isSelected = currentScreenDestination == "cards",
                            onClick = { currentScreenDestination = "cards" }
                        )

                        BottomNavTabItem(
                            icon = Icons.Default.Analytics,
                            label = "التقارير",
                            isSelected = currentScreenDestination == "reports",
                            onClick = { currentScreenDestination = "reports" }
                        )

                        BottomNavTabItem(
                            icon = Icons.Default.Settings,
                            label = "الإعدادات",
                            isSelected = currentScreenDestination == "settings",
                            onClick = { currentScreenDestination = "settings" }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavTabItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 4.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) PrimaryPurple else TextSecondary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (isSelected) PrimaryPurple else TextSecondary,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
