package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.Transaction
import com.example.ui.theme.*
import com.example.ui.viewmodel.FinancialViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(
    viewModel: FinancialViewModel,
    onNavigateToDetail: (Transaction) -> Unit
) {
    val transactions by viewModel.allTransactions.collectAsState()
    val settings by viewModel.settings.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("الكل") }

    // Manual insert bottom-sheet toggle
    var showAddDialog by remember { mutableStateOf(false) }

    val categoriesList = listOf("الكل", "مقاضي", "مطاعم", "مواصلات", "تسوق", "فواتير", "صحة")

    // Filtered lists
    val filteredTransactions = remember(transactions, searchQuery, selectedCategoryFilter) {
        transactions.filter {
            val matchesQuery = it.merchant.contains(searchQuery, ignoreCase = true) ||
                    it.category.contains(searchQuery, ignoreCase = true)
            val matchesFilter = selectedCategoryFilter == "الكل" || it.category == selectedCategoryFilter
            matchesQuery && matchesFilter
        }
    }

    // Grouping by Date
    val groupedMap = remember(filteredTransactions) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        filteredTransactions.groupBy {
            val dateStr = sdf.format(Date(it.timestamp))
            dateStr
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = PrimaryPurple,
                    contentColor = BackgroundDark,
                    shape = CircleShape,
                    modifier = Modifier.padding(bottom = 50.dp) // Offset above bottom nav
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Transaction")
                }
            },
            containerColor = BackgroundDark
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
            ) {
                // Header Top
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "المصاريف",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { }
                    )
                }

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("ابحث عن مصروف...", color = ContainerHighest) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ContainerLow,
                        unfocusedContainerColor = ContainerLow,
                        focusedBorderColor = PrimaryPurple,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.05f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                // Chips LazyRow
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    items(categoriesList) { cat ->
                        val isActive = selectedCategoryFilter == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isActive) PrimaryPurple else ContainerLow)
                                .clickable { selectedCategoryFilter = cat }
                                .padding(horizontal = 20.dp, vertical = 8.dp)
                                .border(
                                    1.dp,
                                    if (isActive) PrimaryPurple else Color.White.copy(alpha = 0.05f),
                                    RoundedCornerShape(20.dp)
                                )
                        ) {
                            Text(
                                text = cat,
                                color = if (isActive) OnPrimaryPurple else TextSecondary,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }

                // LazyColumn transactions map
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    if (groupedMap.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "لا توجد عمليات تطابق البحث",
                                    color = TextSecondary,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    } else {
                        groupedMap.forEach { (dateKey, itemsGroup) ->
                            item {
                                val headerTitle = getGroupHeaderLabel(dateKey)
                                val sum = itemsGroup.sumOf { it.amount }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = headerTitle,
                                        color = TextSecondary,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                    Text(
                                        text = String.format("%,.0f ريال", -sum),
                                        color = SecondaryGreen,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }

                            items(itemsGroup) { trans ->
                                SwipeableTransactionRow(
                                    transaction = trans,
                                    onEdit = { onNavigateToDetail(trans) },
                                    onDelete = { viewModel.deleteTransaction(trans) }
                                )
                            }
                        }
                    }
                }
            }

            // Quick Add Dialog
            if (showAddDialog) {
                AddTransactionDialog(
                    onDismiss = { showAddDialog = false },
                    onSave = { name, category, amount ->
                        viewModel.addTransaction(name, category, -amount)
                        showAddDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun SwipeableTransactionRow(
    transaction: Transaction,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    // Standard rows have click action that opens the edit detail, and custom side action buttons to quickly edit/delete!
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCard)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .clickable { onEdit() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ContainerHighest),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (transaction.category) {
                        "مطاعم" -> Icons.Default.LocalCafe
                        "مقاضي" -> Icons.Default.ShoppingCart
                        "مواصلات" -> Icons.Default.DirectionsCar
                        "تسوق" -> Icons.Default.ShoppingBag
                        "صحة" -> Icons.Default.HealthAndSafety
                        else -> Icons.Default.Payments
                    },
                    tint = PrimaryPurple,
                    contentDescription = null
                )
            }

            Column {
                Text(
                    text = transaction.merchant,
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = transaction.category + " • " + getShortTimeFormat(transaction.timestamp),
                    color = TextSecondary,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format("%.0f-", -transaction.amount),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                if (transaction.isSmsSource) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(SecondaryGreen.copy(alpha = 0.1f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            "SMS",
                            color = SecondaryGreen,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Quick Delete Button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = DangerRose.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// Dialog for quick addition
@Composable
fun AddTransactionDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, category: String, amount: Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("مقاضي") }
    var amountText by remember { mutableStateOf("") }

    val categories = listOf("مقاضي", "مطاعم", "مواصلات", "تسوق", "فواتير", "صحة", "أخرى")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "إضافة مصروف جديد",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("التاجر") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Amount
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("المبلغ (ريال)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Category selector
                Text(
                    "التصنيف",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        val isSelected = category == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PrimaryPurple else ContainerLow)
                                .clickable { category = cat }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                                .border(
                                    1.dp,
                                    if (isSelected) PrimaryPurple else Color.White.copy(alpha = 0.05f),
                                    RoundedCornerShape(8.dp)
                                )
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) OnPrimaryPurple else TextSecondary,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (name.isNotEmpty() && amount > 0) {
                        onSave(name, category, amount)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
            ) {
                Text("إضافة", color = OnPrimaryPurple, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = TextSecondary)
            }
        },
        containerColor = SurfaceModal
    )
}

// Screen 6 Details Composable
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExpenseDetailScreen(
    viewModel: FinancialViewModel,
    transactionId: Int,
    onBack: () -> Unit
) {
    val transaction by viewModel.editingTransaction.collectAsState()

    // Load inside editingTransaction container on launch
    LaunchedEffect(transactionId) {
        val t = viewModel.getTransactionById(transactionId)
        viewModel.setEditingTransaction(t)
    }

    val current = transaction ?: return

    val categories = listOf("مقاضي", "مطاعم", "مواصلات", "صحة", "فواتير", "تسوق")

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Detail Header Top bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    IconButton(
                        onClick = { onBack() },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SurfaceCard)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Back",
                            tint = PrimaryPurple,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        text = "تفاصيل المصروف",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineLarge
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = {
                            viewModel.deleteTransaction(current)
                            onBack()
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SurfaceCard)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Amount Hero Card Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(DangerRose.copy(alpha = 0.1f))
                        .border(1.dp, DangerRose.copy(alpha = 0.2f), CircleShape)
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = DangerRose,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "مصروفات",
                            color = DangerRose,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = String.format("%,.2f", -current.amount),
                        color = Color.White,
                        style = MaterialTheme.typography.displayLarge
                    )
                    Text(
                        text = "ريال سعودي",
                        color = PrimaryPurple.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Primary Bento Card Details Info Grid
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Merchant Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceCard)
                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ContainerHighest),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Store,
                            contentDescription = null,
                            tint = PrimaryPurple,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                "التاجر",
                                color = TextSecondary,
                                style = MaterialTheme.typography.labelSmall
                            )
                            if (current.isSmsSource) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(SecondaryGreen)
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        "SMS",
                                        color = OnSecondaryGreen,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Merchant TextField Edit directly updates editing state
                        BasicTextField(
                            value = current.merchant,
                            onValueChange = { viewModel.setEditingTransaction(current.copy(merchant = it)) },
                            textStyle = MaterialTheme.typography.headlineMedium.copy(color = Color.White),
                            singleLine = true
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = PrimaryPurple,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Date row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceCard)
                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ContainerHighest),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = PrimaryPurple,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            "التاريخ والوقت",
                            color = TextSecondary,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = getFullTimeFormat(current.timestamp),
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                // Payment scheme Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceCard)
                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ContainerHighest),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = null,
                            tint = TertiaryAmber,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            "وسيلة الدفع",
                            color = TextSecondary,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = current.paymentMethod,
                                color = Color.White,
                                style = MaterialTheme.typography.labelLarge
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.White.copy(alpha = 0.05f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Contactless,
                                        contentDescription = null,
                                        tint = Color(0xFF00A9E0),
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Text(
                                        "SAMSUNG PAY",
                                        color = TextSecondary,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Category select matrix
            Text(
                "التصنيف",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                maxItemsInEachRow = 3,
                modifier = Modifier.fillMaxWidth()
            ) {
                categories.forEach { cat ->
                    val isSelected = current.category == cat
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(72.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) PrimaryPurple else SurfaceCard)
                            .border(
                                1.dp,
                                if (isSelected) PrimaryPurple else Color.White.copy(alpha = 0.05f),
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { viewModel.updateEditingCategory(cat) }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = when (cat) {
                                    "مطاعم" -> Icons.Default.LocalCafe
                                    "مقاضي" -> Icons.Default.ShoppingCart
                                    "مواصلات" -> Icons.Default.DirectionsCar
                                    "تسوق" -> Icons.Default.ShoppingBag
                                    "صحة" -> Icons.Default.HealthAndSafety
                                    else -> Icons.Default.Payments
                                },
                                contentDescription = null,
                                tint = if (isSelected) OnPrimaryPurple else TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = cat,
                                color = if (isSelected) OnPrimaryPurple else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Calc in budget Switch row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceCard)
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
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
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SecondaryGreen.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = null,
                            tint = SecondaryGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "احسب في ميزانيتك؟",
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            text = "سيتم خصم المبلغ من الميزانية الشهرية المحددة",
                            color = TextSecondary,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                Switch(
                    checked = current.isCalculatedInBudget,
                    onCheckedChange = { viewModel.updateEditingBudgetToggle(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = SecondaryGreen
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Button(
                onClick = {
                    viewModel.saveEditingTransaction()
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryPurple
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "حفظ التغييرات",
                        color = OnPrimaryPurple,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = OnPrimaryPurple,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// Format Helpers
fun getGroupHeaderLabel(dateKey: String): String {
    return try {
        val sdfFile = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = sdfFile.parse(dateKey) ?: return dateKey

        val sdfToday = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val todayStr = sdfToday.format(Date())
        val yesterdayStr = sdfToday.format(Date(System.currentTimeMillis() - 86400000L))

        when (dateKey) {
            todayStr -> "النهارده — " + SimpleDateFormat("EEEE d MMMM", Locale("ar")).format(date)
            yesterdayStr -> "أمس — " + SimpleDateFormat("EEEE d MMMM", Locale("ar")).format(date)
            else -> SimpleDateFormat("EEEE d MMMM yyyy", Locale("ar")).format(date)
        }
    } catch (e: Exception) {
        dateKey
    }
}

fun getShortTimeFormat(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm م", Locale("ar"))
    val text = sdf.format(Date(timestamp))
    // simple RTL formatting for AM/PM in custom text
    return text.replace("AM", "ص").replace("PM", "م")
}

fun getFullTimeFormat(timestamp: Long): String {
    val date = Date(timestamp)
    val sdfDate = SimpleDateFormat("d MMMM yyyy", Locale("ar"))
    val sdfTime = SimpleDateFormat("h:mm م", Locale("ar"))
    return sdfDate.format(date) + " — " + sdfTime.format(date)
}
