package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: FinancialViewModel,
    onNavigateToCards: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    val context = LocalContext.current

    // Popup input editors state
    var showSalaryDialog by remember { mutableStateOf(false) }
    var tempSalaryValue by remember { mutableStateOf("") }

    var showBudgetDialog by remember { mutableStateOf(false) }
    var tempBudgetValue by remember { mutableStateOf("") }

    var showSavingsDialog by remember { mutableStateOf(false) }
    var tempSavingsValue by remember { mutableStateOf("") }

    var showResetConfirm by remember { mutableStateOf(false) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp) // Offset space for nav bar
        ) {
            // Profile & Title Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الإعدادات",
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
                // Profile Main info card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceCard)
                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = "https://lh3.googleusercontent.com/aida-public/AB6AXuAz63s0htfNtA3ooEYzMeqTHxu6Z5xf17H6D5SZSlJMZmnOsY20AYEOEWrFJoPMvLwXR6wOLtGuZQOqFb2qxEeB44_Vn6RyrbZUcsk8NRBD5OdreVhcmZCmwgeXqWhymT1EwCYwRpnE6fcDV-QIGoVxO0jfbUY8Cetng3lRzr9wiaj7-R6K5Y1BmxcGNzquYX1f7B7GqwPD9UB-9D0if1pr53dxyQV01seEQ7o3eP1-1A0veg4eVJg2fmf5SVKOWqA4gPwj9teNbw",
                        contentDescription = "User Photo",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .border(2.dp, PrimaryPurple, CircleShape)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "محمد السميري",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "mohammad.alsumairi@gmail.com",
                            color = TextSecondary,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    if (settings.isPremium) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Brush.horizontalGradient(listOf(TertiaryAmber, Color(0xFFD43F8D))))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "بريميوم نشط ✨",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Premium upgrade interactive banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                colors = if (settings.isPremium) {
                                    listOf(ContainerLow, ContainerLowest)
                                } else {
                                    listOf(Color(0xFFFFB400).copy(alpha = 0.15f), Color(0xFFFF8400).copy(alpha = 0.25f))
                                }
                            )
                        )
                        .border(
                            1.dp,
                            if (settings.isPremium) Color.White.copy(alpha = 0.1f) else Color(0xFFFFB400).copy(alpha = 0.4f),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable {
                            viewModel.triggerUpgradeSettings()
                            Toast
                                .makeText(
                                    context,
                                    if (settings.isPremium) "تم إلغاء بريميوم مؤقتاً" else "مرحباً بك في ميزات بريميوم الذهبية! 💎",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        }
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (settings.isPremium) "أنت مستخدم مميز ذهبي 👑" else "ترقى للـ Premium الذهبي",
                                color = Color.White,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (settings.isPremium) "تتمتع بالخصم من التقارير الذكية غير المحدودة" else "افتح تقارير ذكاء مالي مخصصة وقراءة غير محدودة",
                                color = Color.White.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = null,
                            tint = if (settings.isPremium) PrimaryPurple else Color(0xFFFFB400),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Section 1: Accounts & budgets
                GroupSettingsCard(title = "الحساب والميزانية") {
                    SettingsRowItem(
                        icon = Icons.Default.Payments,
                        label = "الراتب الشهري",
                        value = "${String.format("%,.0f", settings.monthlySalary)} ريال",
                        onClick = {
                            tempSalaryValue = settings.monthlySalary.toInt().toString()
                            showSalaryDialog = true
                        }
                    )
                    SettingsRowItem(
                        icon = Icons.Default.ShoppingCart,
                        label = "ميزانية المصاريف",
                        value = "${String.format("%,.0f", settings.expensesBudget)} ريال",
                        onClick = {
                            tempBudgetValue = settings.expensesBudget.toInt().toString()
                            showBudgetDialog = true
                        }
                    )
                    SettingsRowItem(
                        icon = Icons.Default.Savings,
                        label = "هدف التوفير",
                        value = "${String.format("%,.0f", settings.savingsGoal)} ريال",
                        onClick = {
                            tempSavingsValue = settings.savingsGoal.toInt().toString()
                            showSavingsDialog = true
                        }
                    )
                }

                // Section 2: Notifications settings
                GroupSettingsCard(title = "التنبيهات والاتصالات") {
                    SettingsSwitchRowItem(
                        icon = Icons.Default.AccountBalance,
                        label = "قراءة رسايل البنك تلقائياً",
                        description = "يقوم التطبيق بقراءة رسايل البنك وتصنيف المصاريف تلقائياً",
                        checked = settings.smsPermissionAllowed,
                        onCheckedChange = { viewModel.updateSmsPermission(it) }
                    )

                    SettingsSwitchRowItem(
                        icon = Icons.Default.NotificationAdd,
                        label = "تنبيهات الميزانية اليومية",
                        description = "تنبيه يومي بحد الصرف المتبقي ونسب الاستهلاك",
                        checked = settings.dailyLimitNotificationEnabled,
                        onCheckedChange = { checked ->
                            viewModel.updateNotificationSettings(
                                smsEnabled = settings.smsNotificationsEnabled,
                                dailyLimitEnabled = checked,
                                cardEnabled = settings.cardNotificationsEnabled
                            )
                        }
                    )

                    SettingsSwitchRowItem(
                        icon = Icons.Default.CreditCard,
                        label = "تذكيرات سداد البطاقات",
                        description = "إشعار قبل تاريخ استحقاق بطاقتك الائتمانية بـ ٣ أيام",
                        checked = settings.cardNotificationsEnabled,
                        onCheckedChange = { checked ->
                            viewModel.updateNotificationSettings(
                                smsEnabled = settings.smsNotificationsEnabled,
                                dailyLimitEnabled = settings.dailyLimitNotificationEnabled,
                                cardEnabled = checked
                            )
                        }
                    )
                }

                // Section 3: Credit Card management link
                GroupSettingsCard(title = "البطاقات والحسابات") {
                    SettingsRowItem(
                        icon = Icons.Default.CreditCard,
                        label = "إدارة بطاقات الائتمان",
                        value = "تفاصيل البطاقات",
                        onClick = { onNavigateToCards() }
                    )
                }

                // Section 4: Advanced developer backup & reset
                GroupSettingsCard(title = "خيارات متقدمة") {
                    SettingsRowItem(
                        icon = Icons.Default.CloudUpload,
                        label = "تصدير البيانات",
                        value = "Excel / PDF",
                        onClick = {
                            Toast.makeText(context, "تم تصدير ملف البيانات بنجاح! 💾", Toast.LENGTH_SHORT).show()
                        }
                    )
                    SettingsRowItem(
                        icon = Icons.Default.CloudDownload,
                        label = "استعادة نسخة احتياطية",
                        value = "السحابة مجاناً",
                        onClick = {
                            Toast.makeText(context, "تمت استعادة البيانات بنجاح! ✅", Toast.LENGTH_SHORT).show()
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { showResetConfirm = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DangerRose.copy(alpha = 0.1f),
                            contentColor = DangerRose
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("حذف كل البيانات وإعادة التهيئة", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // INPUT POPUP EDITORS DIALOGS
        // Salary Edit
        if (showSalaryDialog) {
            EditValueDialog(
                title = "تعديل الراتب الشهري",
                label = "الراتب بالريال",
                initialValue = tempSalaryValue,
                onDismiss = { showSalaryDialog = false },
                onSave = {
                    val d = it.toDoubleOrNull() ?: settings.monthlySalary
                    viewModel.updateMonthlySalary(d)
                    showSalaryDialog = false
                }
            )
        }

        // Budget Edit
        if (showBudgetDialog) {
            EditValueDialog(
                title = "تعديل ميزانية المصاريف",
                label = "الميزانية بالريال",
                initialValue = tempBudgetValue,
                onDismiss = { showBudgetDialog = false },
                onSave = {
                    val d = it.toDoubleOrNull() ?: settings.expensesBudget
                    viewModel.updateExpensesBudget(d)
                    showBudgetDialog = false
                }
            )
        }

        // Savings Edit
        if (showSavingsDialog) {
            EditValueDialog(
                title = "تعديل هدف التوفير",
                label = "الهدف بالريال",
                initialValue = tempSavingsValue,
                onDismiss = { showSavingsDialog = false },
                onSave = {
                    val d = it.toDoubleOrNull() ?: settings.savingsGoal
                    viewModel.updateSavingsGoal(d)
                    showSavingsDialog = false
                }
            )
        }

        // Reset confirm
        if (showResetConfirm) {
            AlertDialog(
                onDismissRequest = { showResetConfirm = false },
                title = { Text("حذف كل البيانات ومسح الذاكرة؟", color = Color.White) },
                text = { Text("سيتم مسح كافة المصاريف المدخلة والبطاقات والعودة لإعدادات التثبيت الأساسية. لا يمكن التراجع عن هذا الإجراء.", color = TextSecondary) },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.resetDatabase()
                            showResetConfirm = false
                            Toast.makeText(context, "تمت إعادة تعيين البيانات بنجاح", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DangerRose)
                    ) {
                        Text("نعم، احذف الكل", color = OnDangerRose, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetConfirm = false }) {
                        Text("إلغاء", color = Color.White)
                    }
                },
                containerColor = SurfaceModal
            )
        }
    }
}

@Composable
fun GroupSettingsCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(start = 4.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceCard)
                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
    }
}

@Composable
fun SettingsRowItem(
    icon: ImageVector,
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 4.dp),
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
                    .clip(RoundedCornerShape(8.dp))
                    .background(ContainerHighest),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = PrimaryPurple)
            }
            Text(
                text = label,
                color = Color.White,
                style = MaterialTheme.typography.labelLarge
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = value,
                color = TextSecondary,
                style = MaterialTheme.typography.labelLarge
            )
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun SettingsSwitchRowItem(
    icon: ImageVector,
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ContainerHighest),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = PrimaryPurple)
            }
            Column(modifier = Modifier.padding(end = 12.dp)) {
                Text(
                    text = label,
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                     text = description,
                     color = TextSecondary,
                     style = MaterialTheme.typography.labelSmall,
                     lineHeight = 16.sp
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = SecondaryGreen
            )
        )
    }
}

@Composable
fun EditValueDialog(
    title: String,
    label: String,
    initialValue: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var txt by remember { mutableStateOf(initialValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, color = Color.White) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = txt,
                    onValueChange = { txt = it.filter { it.isDigit() } },
                    label = { Text(label) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(txt) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
            ) {
                Text("حفظ", color = OnPrimaryPurple)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = Color.White)
            }
        },
        containerColor = SurfaceModal
    )
}
