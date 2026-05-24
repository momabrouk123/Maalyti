package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.Transaction
import com.example.ui.theme.*
import com.example.ui.viewmodel.FinancialViewModel

@Composable
fun ReportsScreen(
    viewModel: FinancialViewModel
) {
    val settings by viewModel.settings.collectAsState()
    val transactions by viewModel.allTransactions.collectAsState()

    var activeTab by remember { mutableStateOf("الشهر") }
    val tabsList = listOf("الشهر", "الأسبوع", "النهارده", "مخصص")

    // Dynamic calculations or fallback to spec figures
    val totalExpenseBudget = settings.expensesBudget
    val actualMonthSpends = transactions.filter { it.amount < 0 }.sumOf { -it.amount }

    // If transactions has zero entries, fallback to spec (3240 is 54% of 6000)
    val spendPercentage = if (totalExpenseBudget > 0) {
        if (transactions.isNotEmpty()) ((actualMonthSpends / totalExpenseBudget) * 100).toInt() else 54
    } else 54

    val finalSpentAmount = if (transactions.isNotEmpty()) actualMonthSpends else 3240.0
    val finalRemainingAmount = (totalExpenseBudget - finalSpentAmount).coerceAtLeast(0.0)

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp) // padding above nav bar
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = "https://lh3.googleusercontent.com/aida-public/AB6AXuCv_X8QXSqD0AH-1L9ZGIGXbJnMnwMs8J73X3033mc4BLjB7CPKNgbD_FkvDcVqZRpppUHt-HkF569Heqlml7nPiSmdtlcsigN3YkTCDmjrK8qhjrYyAd0L4leb2COcaJ_x4toHAdzC8LlgBKksdIbsL_chGu8OVvn_pQ301SFt2M1NZ9IpkPJc4HLNb9HXw_kaNw0jVXgFaHK9rhozHyagPdmfixML9S4NU88fNGcTQuXv2o9Rkw7PJJZ_o8n6bQobUvozTyasyw",
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, PrimaryPurple.copy(alpha = 0.3f), CircleShape)
                )

                Text(
                     text = "التقارير",
                     color = Color.White,
                     style = MaterialTheme.typography.headlineLarge
                )

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SurfaceCard)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = PrimaryPurple,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Scrollable time tabs
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(tabsList) { tab ->
                        val isActive = activeTab == tab
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isActive) PrimaryPurple else SurfaceCard)
                                .clickable { activeTab = tab }
                                .padding(horizontal = 24.dp, vertical = 10.dp)
                                .border(
                                    1.dp,
                                    if (isActive) PrimaryPurple else Color.White.copy(alpha = 0.05f),
                                    RoundedCornerShape(20.dp)
                                )
                        ) {
                            Text(
                                text = tab,
                                color = if (isActive) OnPrimaryPurple else TextSecondary,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }

                // Summary consumption card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceCard)
                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "التقرير الشهري",
                            color = TextSecondary,
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            text = "مايو 2026",
                            color = TextSecondary,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = String.format("%,.0f ريال", finalSpentAmount),
                            color = DangerRose,
                            style = MaterialTheme.typography.displayLarge
                        )
                        Text(
                            text = String.format("من ميزانية %,.0f ريال", totalExpenseBudget),
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        val progress = (finalSpentAmount / totalExpenseBudget).coerceIn(0.0..1.0).toFloat()
                        LinearProgressIndicator(
                            progress = { progress },
                            color = PrimaryPurple,
                            trackColor = ContainerHighest,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(CircleShape)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "$spendPercentage% استهلاك",
                                color = PrimaryPurple,
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                text = String.format("%,.0f ريال متبقي", finalRemainingAmount),
                                color = TextSecondary,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }

                // Financial AI Smart insights
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // bulb insight
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceCard)
                            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = PrimaryPurple,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "صرفت 27% من ميزانيتك على الأكل الشهر ده 😅، جرب تقلل طلبات التوصيل الأسبوع الجاي.",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 24.sp
                        )
                    }

                    // down insight
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceCard)
                            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = SecondaryGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "مصاريف المواصلات قلت بنسبة 12% مقارنة بالشهر اللي فات. استمر!",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 24.sp
                        )
                    }
                }

                // Category list breakdown distribution
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "توزيع المصاريف",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    // Hardcoded fallback or dynamic breakdown categories
                    val breakdown = listOf(
                        CategoryReport(name = "سوبر ماركت", amount = 1120.0, percent = 35, color = PrimaryPurple),
                        CategoryReport(name = "مطاعم وكافيهات", amount = 875.0, percent = 27, color = TertiaryAmber),
                        CategoryReport(name = "مواصلات", amount = 450.0, percent = 14, color = SecondaryGreen),
                        CategoryReport(name = "أخرى", amount = 795.0, percent = 24, color = TextSecondary)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        for (item in breakdown) {
                            CategoryProgressRow(item = item)
                        }
                    }
                }

                // Top merchants list
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "أعلى المتاجر",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(SurfaceCard)
                            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp)),
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        val merchants = listOf(
                            MerchantReport(rank = 1, name = "أسواق بنده", ops = 8, amount = 540.0),
                            MerchantReport(rank = 2, name = "جاهز", ops = 5, amount = 420.0),
                            MerchantReport(rank = 3, name = "ستاربكس", ops = 12, amount = 310.0),
                            MerchantReport(rank = 4, name = "أمازون", ops = 3, amount = 290.0),
                            MerchantReport(rank = 5, name = "أوبر", ops = 9, amount = 215.0)
                        )

                        for (m in merchants) {
                            MerchantReportRow(item = m)
                        }
                    }
                }
            }
        }
    }
}

data class CategoryReport(
    val name: String,
    val amount: Double,
    val percent: Int,
    val color: Color
)

@Composable
fun CategoryProgressRow(item: CategoryReport) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(item.color))
                Text(
                    text = item.name,
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Text(
                text = String.format("%,.0f ريال (%d%%)", item.amount, item.percent),
                color = Color.White,
                style = MaterialTheme.typography.labelLarge
            )
        }

        LinearProgressIndicator(
            progress = { item.percent / 100f },
            color = item.color,
            trackColor = ContainerHighest,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
        )
    }
}

data class MerchantReport(
    val rank: Int,
    val name: String,
    val ops: Int,
    val amount: Double
)

@Composable
fun MerchantReportRow(item: MerchantReport) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceCard)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ContainerHighest),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.rank.toString(),
                    color = PrimaryPurple,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column {
                Text(
                    text = item.name,
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = "${item.ops} عمليات",
                    color = TextSecondary,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        Text(
            text = String.format("%,.0f ريال", item.amount),
            color = PrimaryPurple,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}
