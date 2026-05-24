package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.*
import com.example.ui.viewmodel.FinancialViewModel

@Composable
fun OnboardingPermissionsScreen(
    viewModel: FinancialViewModel,
    onNext: () -> Unit
) {
    // Force RTL local view
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF1A1040), BackgroundDark),
                        center = Offset(1000f, 0f)
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "تخطي",
                    color = TextSecondary,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.clickable { onNext() }
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(PrimaryPurple))
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ContainerHighest))
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ContainerHighest))
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Illustration
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .drawBehind {
                        drawCircle(
                            color = PrimaryPurple.copy(alpha = 0.05f),
                            radius = size.width / 1.5f
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = "https://lh3.googleusercontent.com/aida-public/AB6AXuAO3x_xqhK0pC5qv9hYFSztRl3VAzLcL1q6Mk1sx7DMrQaqEpa_W0JYW-m0XRj0CbMt9BYGrcED7ygsiIchRVol5g9u3CD0zfRLiRC6lMepqE4vE0mvCcMuiqWuzfnGD6bpoTwtmdAzv9XYHek0oiYhFN-4JmBq7gazNB20NapNwgUpiCC_D8n88zeGD79b2JrL7TnEWucenloPbeGOEaPsjhdeHC3OnYfjEThe_beHRjzhdBdRlRrnObaS_SUiAzj14gBrTfxs4Q",
                    contentDescription = "Fintech Illustration",
                    modifier = Modifier.size(200.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Text content
            Text(
                text = "خليني أقرا رسايل البنك عشانك",
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "مش محتاج تسجل أي حاجة، التطبيق بيقرا المصاريف لوحده من رسايل البنك",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // SMS Example Animated Preview Cards
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // SMS Received card (RTL left-aligned)
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .align(Alignment.Start)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceCard.copy(alpha = 0.8f))
                        .border(1.dp, PrimaryPurple.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ContainerHighest),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = PrimaryPurple,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Al Rajhi Bank",
                            color = PrimaryPurple,
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            text = "Purchase: 218.50 SAR\nAt: CARREFOUR\nCard: *1234",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Connector Pulse Arrow
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = SecondaryGreen,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Parsed expense card (RTL right-aligned)
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .align(Alignment.End)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceCard.copy(alpha = 0.8f))
                        .border(1.dp, SecondaryGreen.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SecondaryGreen.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = SecondaryGreen,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "كارفور",
                                color = Color.White,
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(
                                text = "تم التصنيف تلقائياً",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "٢١٨.٥٠",
                            color = SecondaryGreen,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = "ر.س",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // CTA Button
            Button(
                onClick = {
                    viewModel.updateSmsPermission(true)
                    onNext()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(30.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF6C63FF), Color(0xFFA855F7))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "السماح بقراءة الرسايل",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer Privacy Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "خصوصيتك محمية — بياناتك على موبايلك بس",
                    color = TextSecondary,
                    style = MaterialTheme.typography.labelSmall
                )
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(14.dp)
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingBudgetScreen(
    viewModel: FinancialViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val salaryInput by viewModel.tempSalary.collectAsState()
    val budgetInput by viewModel.tempBudget.collectAsState()

    val salaryVal = salaryInput.replace(",", "").toDoubleOrNull() ?: 0.0
    val budgetVal = budgetInput.replace(",", "").toDoubleOrNull() ?: 0.0
    val savingsVal = if (salaryVal - budgetVal > 0) salaryVal - budgetVal else 0.0
    val targetSavingsString = String.format("%,.0f", savingsVal)

    // Calculate ratio
    val expensesRatio = if (salaryVal > 0) (budgetVal / salaryVal).coerceIn(0.0..1.0) else 0.6
    val savingsRatio = 1.0 - expensesRatio

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Step status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.width(32.dp).height(6.dp).clip(CircleShape).background(PrimaryPurple.copy(alpha = 0.4f)))
                    Box(modifier = Modifier.width(32.dp).height(6.dp).clip(CircleShape).background(PrimaryPurple))
                    Box(modifier = Modifier.width(32.dp).height(6.dp).clip(CircleShape).background(ContainerHighest))
                }
                Text(
                    text = "الخطوة ٢ من ٣",
                    color = TextSecondary,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Heading
            Text(
                text = "حدد ميزانيتك الشهرية",
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "لنقوم بتنظيم خطتك المالية بدقة بناءً على دخلك ومصاريفك.",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Inputs
            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                // Salary Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceCard)
                        .border(1.dp, PrimaryPurple.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "الراتب الشهري",
                        color = TextSecondary,
                        style = MaterialTheme.typography.labelLarge
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.weight(1f)
                        ) {
                            TextField(
                                value = salaryInput,
                                onValueChange = { input ->
                                    val formatted = input.filter { it.isDigit() || it == '.' }
                                    viewModel.tempSalary.value = formatted
                                },
                                textStyle = MaterialTheme.typography.displayMedium.copy(
                                    color = PrimaryPurple,
                                    fontWeight = FontWeight.Bold
                                ),
                                placeholder = {
                                    Text(
                                        "10,000",
                                        style = MaterialTheme.typography.displayMedium,
                                        color = ContainerHighest
                                    )
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(0.7f)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ريال",
                                color = PrimaryPurple,
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            tint = PrimaryPurple.copy(alpha = 0.4f),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Expenses Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceCard)
                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "ميزانية المصاريف",
                        color = TextSecondary,
                        style = MaterialTheme.typography.labelLarge
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.weight(1f)
                        ) {
                            TextField(
                                value = budgetInput,
                                onValueChange = { input ->
                                    val formatted = input.filter { it.isDigit() || it == '.' }
                                    viewModel.tempBudget.value = formatted
                                },
                                textStyle = MaterialTheme.typography.displayMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                ),
                                placeholder = {
                                    Text(
                                        "6,000",
                                        style = MaterialTheme.typography.displayMedium,
                                        color = ContainerHighest
                                    )
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(0.7f)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ريال",
                                color = Color.White,
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = TextSecondary.copy(alpha = 0.4f),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Visualization Target Settings Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(ContainerLow, ContainerLowest)
                        )
                    )
                    .border(1.dp, SecondaryGreen.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Stars,
                        contentDescription = null,
                        tint = SecondaryGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "🎯 هتوفر $targetSavingsString ريال كل شهر",
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                // Split Stacked Progress Bar
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .clip(CircleShape)
                            .background(ContainerHighest)
                    ) {
                        // Expenses ratio block
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(expensesRatio.toFloat().coerceAtLeast(0.01f))
                                .background(PrimaryPurple.copy(alpha = 0.8f))
                        )
                        // Savings ratio block
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(savingsRatio.toFloat().coerceAtLeast(0.01f))
                                .background(SecondaryGreen)
                        )
                    }

                    // Legends
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(SecondaryGreen))
                            Text(
                                text = "توفير (${(savingsRatio * 100).toInt()}%)",
                                color = TextSecondary,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(PrimaryPurple))
                            Text(
                                text = "مصاريف (${(expensesRatio * 100).toInt()}%)",
                                color = TextSecondary,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(40.dp))

            // Navigation Buttons
            Button(
                onClick = { onNext() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "التالي",
                        color = BackgroundDark,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = null,
                        tint = BackgroundDark,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "رجوع للخطوة السابقة",
                color = TextSecondary,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onBack() }
                    .padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingCardScreen(
    viewModel: FinancialViewModel,
    onBack: () -> Unit,
    onFinish: () -> Unit
) {
    val cardName by viewModel.tempCardName.collectAsState()
    val cardDate by viewModel.tempCardDate.collectAsState()
    val cardDigits by viewModel.tempCardDigits.collectAsState()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Step Progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.width(32.dp).height(6.dp).clip(CircleShape).background(PrimaryPurple))
                    Box(modifier = Modifier.width(32.dp).height(6.dp).clip(CircleShape).background(PrimaryPurple))
                    Box(modifier = Modifier.width(32.dp).height(6.dp).clip(CircleShape).background(PrimaryPurple))
                }
                Text(
                    text = "الخطوة ٣ من ٣",
                    color = TextSecondary,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Heading
            Text(
                text = "عندك كريدت كارد؟",
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "هنبعتلك تنبيه قبل ما ينزل عليك غرامة.",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Animated Visual Card Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.58f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF1E1440), Color(0xFF2D1B69))
                        )
                    )
                    .border(1.dp, PrimaryPurple.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                // Background decorative circles
                Box(
                    modifier = Modifier
                        .offset(x = (-40).dp, y = (-40).dp)
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(PrimaryPurple.copy(alpha = 0.1f))
                )

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Contactless,
                            contentDescription = null,
                            tint = PrimaryPurple,
                            modifier = Modifier.size(36.dp)
                        )
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(PrimaryPurple.copy(alpha = 0.2f))
                                .border(1.dp, PrimaryPurple.copy(alpha = 0.3f), CircleShape)
                                .padding(horizontal = 12.dp, java.lang.Double.min(4.0, 4.0).dp)
                        ) {
                            Text(
                                "أساسي",
                                color = PrimaryPurple,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }

                    Text(
                        text = "•••• •••• •••• " + cardDigits.ifEmpty { "0000" },
                        color = Color.White,
                        style = MaterialTheme.typography.headlineLarge,
                        letterSpacing = 2.sp,
                        textAlign = TextAlign.Left,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "اسم البطاقة",
                                color = TextSecondary.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                text = cardName.ifEmpty { "البنك المفضل" },
                                color = Color.White,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "تاريخ السداد",
                                color = TextSecondary.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                text = if (cardDate.isNotEmpty()) "يوم $cardDate" else "--",
                                color = Color.White,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Inputs Form
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Card Name
                Column {
                    Text(
                        text = "اسم البطاقة",
                        color = TextSecondary,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                    )
                    OutlinedTextField(
                        value = cardName,
                        onValueChange = { viewModel.tempCardName.value = it },
                        placeholder = { Text("مثلاً: البنك الأهلي - فيزا", color = ContainerHighest) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceCard,
                            unfocusedContainerColor = SurfaceCard,
                            focusedBorderColor = PrimaryPurple,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Due Day
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "تاريخ السداد",
                            color = TextSecondary,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                        )
                        OutlinedTextField(
                            value = cardDate,
                            onValueChange = { input ->
                                val digits = input.filter { it.isDigit() }
                                val day = digits.toIntOrNull() ?: 0
                                if (day in 0..31) {
                                    viewModel.tempCardDate.value = digits
                                }
                            },
                            placeholder = { Text("يوم (1-31)", color = ContainerHighest) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = SurfaceCard,
                                unfocusedContainerColor = SurfaceCard,
                                focusedBorderColor = PrimaryPurple,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Last 4 Digits
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "آخر 4 أرقام",
                            color = TextSecondary,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                        )
                        OutlinedTextField(
                            value = cardDigits,
                            onValueChange = { input ->
                                val formatted = input.filter { it.isDigit() }
                                if (formatted.length <= 4) {
                                    viewModel.tempCardDigits.value = formatted
                                }
                            },
                            placeholder = { Text("1234", color = ContainerHighest) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = SurfaceCard,
                                unfocusedContainerColor = SurfaceCard,
                                focusedBorderColor = PrimaryPurple,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Info Dialog Card Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE5A93C).copy(alpha = 0.1f))
                    .border(1.dp, Color(0xFFE5A93C).copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFE5A93C),
                    modifier = Modifier.size(24.dp)
                )
                Column {
                    Text(
                        text = "إشعار ذكي",
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = "هنفكرك تدفع قبل التاريخ بـ ٣ أيام عشان تتجنب الفوائد.",
                        color = TextSecondary,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(40.dp))

            // Action CTAs
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        viewModel.completeOnboarding()
                        onFinish()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "إضافة البطاقة",
                            color = BackgroundDark,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.AddCard,
                            contentDescription = null,
                            tint = BackgroundDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                TextButton(
                    onClick = {
                        viewModel.completeOnboarding()
                        onFinish()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = "مش دلوقتي — ابدأ",
                        color = TextSecondary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
