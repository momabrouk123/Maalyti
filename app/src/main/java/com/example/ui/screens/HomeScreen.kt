package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.Transaction
import com.example.ui.theme.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.core.content.ContextCompat
import com.example.ui.viewmodel.FinancialViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    viewModel: FinancialViewModel,
    onNavigateToExpenses: () -> Unit,
    onNavigateToDetail: (Transaction) -> Unit,
    onNavigateToCards: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    val transactions by viewModel.allTransactions.collectAsState()

    // Calculate dynamic parameters based on transactions
    val now = System.currentTimeMillis()
    val dayInMs = 86400000L
    val oneWeekAgo = now - 7 * dayInMs

    // Spends this week
    val spendsThisWeek = transactions
        .filter { it.timestamp >= oneWeekAgo && it.amount < 0 }
        .sumOf { -it.amount }

    // Dynamic calculations for circular ring
    // We assume daily spend limit rate. Average target daily rate: budget / 30.
    val dailyLimit = settings.expensesBudget / 30.0
    val todayStart = now - (now % dayInMs)
    val todaySpends = transactions
        .filter { it.timestamp >= todayStart && it.amount < 0 && it.isCalculatedInBudget }
        .sumOf { -it.amount }

    val remainingDaily = (dailyLimit - todaySpends).coerceAtLeast(0.0)
    val remainingDays = 30 - ((now % (30 * dayInMs)) / dayInMs).toInt().coerceIn(1..30)

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp) // padding for floating bottom nav
        ) {
            // Header Top Bar
            HomeTopBar(isPremium = settings.isPremium)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Hero Card: Daily limit concentric ring gauge
                HeroCardGauge(
                    limit = dailyLimit,
                    spent = todaySpends,
                    remaining = remainingDaily,
                    daysLeft = remainingDays,
                    onNavigateToCards = onNavigateToCards
                )

                // Burn Rate Prediction Amber warning board
                BurnRateCard(limitExceeded = todaySpends > dailyLimit)

                // Banking SMS Sync and Detected Monthly Salary
                BankSmsStatusCard(viewModel = viewModel)

                // Quick Stats Double Card row
                QuickStatsRow(
                    savedThisWeek = 340.0, // Matches spec
                    savedCumulative = 2300.0, // Cumulative progress
                    savingGoal = settings.savingsGoal
                )

                // Transactions Header & List
                TransactionsHeaderAndList(
                    transactions = transactions.take(3),
                    onSeeAll = onNavigateToExpenses,
                    onItemClick = onNavigateToDetail
                )
            }
        }
    }
}

@Composable
fun HomeTopBar(isPremium: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "صباح الخير، محمد 👋",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "الأربعاء، 13 مايو",
                color = TextSecondary,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Active notification bell
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SurfaceCard)
                    .clickable { }
                    .border(1.dp, Color.White.copy(alpha = 0.05f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = PrimaryPurple,
                    modifier = Modifier.size(22.dp)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 10.dp, end = 10.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(DangerRose)
                        .border(1.5.dp, BackgroundDark, CircleShape)
                )
            }

            // Headshot
            AsyncImage(
                model = "https://lh3.googleusercontent.com/aida-public/AB6AXuAz63s0htfNtA3ooEYzMeqTHxu6Z5xf17H6D5SZSlJMZmnOsY20AYEOEWrFJoPMvLwXR6wOLtGuZQOqFb2qxEeB44_Vn6RyrbZUcsk8NRBD5OdreVhcmZCmwgeXqWhymT1EwCYwRpnE6fcDV-QIGoVxO0jfbUY8Cetng3lRzr9wiaj7-R6K5Y1BmxcGNzquYX1f7B7GqwPD9UB-9D0if1pr53dxyQV01seEQ7o3eP1-1A0veg4eVJg2fmf5SVKOWqA4gPwj9teNbw",
                contentDescription = "User Headshot",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, PrimaryPurple.copy(alpha = 0.3f), CircleShape)
            )
        }
    }
}

@Composable
fun HeroCardGauge(
    limit: Double,
    spent: Double,
    remaining: Double,
    daysLeft: Int,
    onNavigateToCards: () -> Unit
) {
    val sweepAngle = if (limit > 0) ((remaining / limit) * 360f).toFloat().coerceIn(0f..360f) else 360f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(SurfaceCard)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
            .clickable { onNavigateToCards() }
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Drawing Circular Indicator
        Box(
            modifier = Modifier.size(180.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 14.dp.toPx()
                // Empty background gauge track
                drawArc(
                    color = LineBorder,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
                // Consumed gauge arc status using Amber warning colors
                drawArc(
                    color = TertiaryAmber,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = String.format("%,.0f", remaining),
                    color = TertiaryAmber,
                    style = MaterialTheme.typography.displayLarge
                )
                Text(
                    text = "ريال متبقي",
                    color = TextSecondary,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        // Horizontal status counters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "منصرف",
                    color = TextSecondary,
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = String.format("%,.0f", spent),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Box(modifier = Modifier.width(1.dp).height(30.dp).background(ContainerHighest))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "متبقي",
                    color = TextSecondary,
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = String.format("%,.0f", limit - spent),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Box(modifier = Modifier.width(1.dp).height(30.dp).background(ContainerHighest))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "أيام",
                    color = TextSecondary,
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = daysLeft.toString(),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}

@Composable
fun BurnRateCard(limitExceeded: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceCard)
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(TertiaryAmber, Color.Transparent)
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Speed,
            contentDescription = null,
            tint = TertiaryAmber,
            modifier = Modifier.size(28.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "توقعات الصرف",
                color = TextSecondary,
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Pulse animated color bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(ContainerHighest)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.85f)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(DangerRose, TertiaryAmber, PrimaryPurple)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (limitExceeded)
                    "بالمعدل ده — ميزانيتك اليومية تخطت الحد المقرر اليوم ⚠️"
                else
                    "بالمعدل ده — فلوسك هتخلص قبل آخر الشهر بـ 11 يوم ⚠️",
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun QuickStatsRow(
    savedThisWeek: Double,
    savedCumulative: Double,
    savingGoal: Double
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Saved this week status
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceCard)
                .border(1.dp, SecondaryGreen.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Savings,
                contentDescription = null,
                tint = SecondaryGreen,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "وفّرت الأسبوع ده",
                color = TextSecondary,
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = String.format("%.0f ريال 🎉", savedThisWeek),
                color = SecondaryGreen,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Saving goals state
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceCard)
                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "هدف التوفير",
                color = TextSecondary,
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = String.format("%,.0f / %,.0f", savedCumulative, savingGoal),
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            val progress = (savedCumulative / savingGoal).coerceIn(0.0..1.0).toFloat()
            LinearProgressIndicator(
                progress = { progress },
                color = PrimaryPurple,
                trackColor = ContainerHighest,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(CircleShape)
            )
        }
    }
}

@Composable
fun TransactionsHeaderAndList(
    transactions: List<Transaction>,
    onSeeAll: () -> Unit,
    onItemClick: (Transaction) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "آخر المصاريف",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "الكل",
                color = PrimaryPurple,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.clickable { onSeeAll() }
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "لا توجد مصاريف حالية",
                        color = TextSecondary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            } else {
                for (t in transactions) {
                    TransactionRow(transaction = t, onClick = { onItemClick(t) })
                }
            }
        }
    }
}

@Composable
fun TransactionRow(
    transaction: Transaction,
    onClick: () -> Unit
) {
    val dateLabel = remember(transaction.timestamp) {
        val sdf = SimpleDateFormat("hh:mm a", Locale("ar"))
        sdf.format(Date(transaction.timestamp))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceCard)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when (transaction.category) {
                            "مطاعم" -> TertiaryAmber.copy(alpha = 0.1f)
                            "مقاضي" -> SecondaryGreen.copy(alpha = 0.1f)
                            else -> PrimaryPurple.copy(alpha = 0.1f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (transaction.category) {
                        "مطاعم" -> Icons.Default.LocalCafe
                        "مقاضي" -> Icons.Default.ShoppingCart
                        "مواصلات" -> Icons.Default.DirectionsCar
                        "تسوق" -> Icons.Default.ShoppingBag
                        else -> Icons.Default.Payments
                    },
                    contentDescription = null,
                    tint = when (transaction.category) {
                        "مطاعم" -> TertiaryAmber
                        "مقاضي" -> SecondaryGreen
                        else -> PrimaryPurple
                    }
                )
            }

            Column {
                Text(
                    text = transaction.merchant,
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (transaction.isSmsSource) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ContainerHighest)
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "SMS 📩",
                                color = TextSecondary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = transaction.category,
                        color = TextSecondary,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = if (transaction.amount >= 0) String.format("+%,.0f ريال", transaction.amount) else String.format("%,.0f ريال", transaction.amount),
                color = if (transaction.amount >= 0) SecondaryGreen else DangerRose,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = dateLabel,
                color = TextSecondary,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun BankSmsStatusCard(viewModel: FinancialViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val settings by viewModel.settings.collectAsState()
    val transactions by viewModel.allTransactions.collectAsState()

    // Find if we have any transaction that is SMS-sourced and is a deposit (positive amount) and looks like salary
    val salaryTransaction = remember(transactions) {
        transactions.firstOrNull { 
            it.isSmsSource && it.amount > 0.0 && 
            (it.merchant.contains("راتب") || it.category == "دخل" || it.merchant.contains("salary") || it.category == "أخرى") 
        }
    }

    val totalSmsImported = remember(transactions) {
        transactions.count { it.isSmsSource }
    }

    var isSyncing by remember { mutableStateOf(false) }
    var syncResultMsg by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceCard)
            .border(1.dp, PrimaryPurple.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PrimaryPurple.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Payments,
                        contentDescription = null,
                        tint = PrimaryPurple,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = "مزامنة رسايل البنك والراتب",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "قراءة ومسح تلقائي للرسائل النصية البنكية لهذا الشهر",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            // Sync Button
            Button(
                onClick = {
                    if (!isSyncing) {
                        isSyncing = true
                        coroutineScope.launch {
                            // Check SMS Permission
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    android.Manifest.permission.READ_SMS
                                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
                            ) {
                                syncResultMsg = "يرجى منح صلاحية قراءة الرسائل لمزامنة البيانات"
                            } else {
                                val results = com.example.data.SmsParser.readAndSyncSms(context, viewModel)
                                val added = results["newTransactionsCount"] as? Int ?: 0
                                syncResultMsg = if (added > 0) {
                                    "تمت المزامنة بنجاح! تم استيراد $added مصاريف جديدة وفلترة المكرر."
                                } else {
                                    "تم فحص الرسائل — بياناتك محدثة بالكامل ولا توجد عناصر جديدة."
                                }
                            }
                            isSyncing = false
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSyncing) ContainerHighest else PrimaryPurple
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(34.dp)
            ) {
                if (isSyncing) {
                    Box(modifier = Modifier.size(16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                    }
                } else {
                    Text("مزامنة الآن", fontSize = 12.sp, color = Color.White)
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.White.copy(alpha = 0.05f))
        )

        // Last Salary stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "آخر راتب تم إيداعه",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (salaryTransaction != null) {
                        String.format("%,.0f ريال", salaryTransaction.amount)
                    } else if (settings.monthlySalary > 0.0) {
                        String.format("%,.0f ريال", settings.monthlySalary)
                    } else {
                        "غير محدد"
                    },
                    color = SecondaryGreen,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(24.dp)
                    .background(Color.White.copy(alpha = 0.05f))
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "العناصر المستوردة للبنك",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$totalSmsImported رسائل SMS",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (syncResultMsg.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(PrimaryPurple.copy(alpha = 0.08f))
                    .padding(10.dp)
            ) {
                Text(
                    text = syncResultMsg,
                    color = PrimaryPurple,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}
