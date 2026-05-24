package com.example.data

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

// ---------------- Entities ----------------

@Entity(tableName = "budget_settings")
data class BudgetSettings(
    @PrimaryKey val id: Int = 1,
    val monthlySalary: Double = 10000.0,
    val expensesBudget: Double = 6000.0,
    val savingsGoal: Double = 4000.0,
    val isPremium: Boolean = true,
    val smsPermissionAllowed: Boolean = true,
    val smsNotificationsEnabled: Boolean = true,
    val dailyLimitNotificationEnabled: Boolean = true,
    val cardNotificationsEnabled: Boolean = false,
    val language: String = "العربية",
    val currency: String = "ريال سعودي (SAR)"
)

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val merchant: String,
    val category: String, // "مقاضي" (Grocery), "مطاعم" (Food), "مواصلات" (Transport), "فواتير" (Bills), "صحة" (Health), "تسوق" (Shopping), "أخرى" (Other)
    val amount: Double,   // Negative for expenses
    val timestamp: Long,
    val isSmsSource: Boolean = false,
    val paymentMethod: String = "Visa Cashback (6025)",
    val isCalculatedInBudget: Boolean = true,
    val note: String = "",
    val hasReceipt: Boolean = false
)

@Entity(tableName = "credit_cards")
data class CreditCard(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val last4Digits: String,
    val paymentDay: Int, // 1 to 31
    val amountDue: Double,
    val cashbackEarned: Double = 43.0,
    val expectedLateFee: Double = 150.0,
    val isPrimary: Boolean = true
)

@Entity(tableName = "alert_logs")
data class AlertLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val message: String,
    val timeLabel: String, // "اليوم", "منذ 3 أيام", etc.
    val type: String // "danger", "warning", "info"
)

// ---------------- DAOs ----------------

@Dao
interface BudgetSettingsDao {
    @Query("SELECT * FROM budget_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<BudgetSettings?>

    @Query("SELECT * FROM budget_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): BudgetSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSettings(settings: BudgetSettings)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactionsFlow(): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction): Long

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Int): Transaction?
}

@Dao
interface CreditCardDao {
    @Query("SELECT * FROM credit_cards ORDER BY isPrimary DESC, id ASC")
    fun getAllCardsFlow(): Flow<List<CreditCard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: CreditCard): Long

    @Update
    suspend fun updateCard(card: CreditCard)

    @Delete
    suspend fun deleteCard(card: CreditCard)
}

@Dao
interface AlertLogDao {
    @Query("SELECT * FROM alert_logs ORDER BY id DESC")
    fun getAllAlertsFlow(): Flow<List<AlertLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertLog): Long
}

// ---------------- App Database ----------------

@Database(
    entities = [BudgetSettings::class, Transaction::class, CreditCard::class, AlertLog::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract val budgetSettingsDao: BudgetSettingsDao
    abstract val transactionDao: TransactionDao
    abstract val creditCardDao: CreditCardDao
    abstract val alertLogDao: AlertLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "maalyti_database"
                )
                .addCallback(DatabaseCallback(context))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(val context: Context) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Populate default data on separate thread
                CoroutineScope(Dispatchers.IO).launch {
                    val appDb = getDatabase(context)
                    populateDefaultData(appDb)
                }
            }
        }

        private suspend fun populateDefaultData(db: AppDatabase) {
            // 1. Initial settings
            db.budgetSettingsDao.updateSettings(
                BudgetSettings(
                    id = 1,
                    monthlySalary = 10000.0,
                    expensesBudget = 6000.0,
                    savingsGoal = 4000.0,
                    isPremium = true,
                    smsPermissionAllowed = true
                )
            )

            // Current time constants
            val now = System.currentTimeMillis()
            val hourMs = 3600000L
            val dayMs = 86400000L

            // 2. Initial transactions (from Screen 4 and Screen 5)
            // Screen 4:
            // kf -85, 2 hours ago
            db.transactionDao.insertTransaction(
                Transaction(
                    merchant = "كارفور ماركت",
                    category = "مقاضي",
                    amount = -85.0,
                    timestamp = now - 2 * hourMs,
                    isSmsSource = true,
                    paymentMethod = "Al Rajhi Bank *1234"
                )
            )
            // starbucks -24, today 09:15 am
            db.transactionDao.insertTransaction(
                Transaction(
                    merchant = "ستاربكس",
                    category = "مطاعم",
                    amount = -24.0,
                    timestamp = (now - dayMs) + 12 * hourMs, // mock time
                    isSmsSource = true,
                    paymentMethod = "Al Rajhi Bank *1234"
                )
            )
            // uber -42, yesterday
            db.transactionDao.insertTransaction(
                Transaction(
                    merchant = "أوبر",
                    category = "مواصلات",
                    amount = -42.0,
                    timestamp = now - 1 * dayMs,
                    isSmsSource = true,
                    paymentMethod = "Al Rajhi Bank *1234"
                )
            )

            // Screen 5 additional entries:
            // غداء - قصر الكبابجي -450 today 02:30 pm
            db.transactionDao.insertTransaction(
                Transaction(
                    merchant = "غداء - قصر الكبابجي",
                    category = "مطاعم",
                    amount = -450.0,
                    timestamp = now - hourMs,
                    isSmsSource = true,
                    paymentMethod = "Visa Cashback (6025)"
                )
            )
            // ملابس - زارا -1000 today 12:15 pm
            db.transactionDao.insertTransaction(
                Transaction(
                    merchant = "ملابس - زارا",
                    category = "تسوق",
                    amount = -1000.0,
                    timestamp = now - 4 * hourMs,
                    isSmsSource = false,
                    paymentMethod = "Visa Cashback (6025)"
                )
            )
            // رحلة أوبر -120 yesterday 08:45 pm
            db.transactionDao.insertTransaction(
                Transaction(
                    merchant = "رحلة أوبر",
                    category = "مواصلات",
                    amount = -120.0,
                    timestamp = now - 1 * dayMs - 2 * hourMs,
                    isSmsSource = true,
                    paymentMethod = "Al Rajhi Bank *1234"
                )
            )
            // قهوة - ستاربكس -100 yesterday 09:00 am
            db.transactionDao.insertTransaction(
                Transaction(
                    merchant = "قهوة - ستاربكس",
                    category = "مطاعم",
                    amount = -100.0,
                    timestamp = now - 1 * dayMs - 12 * hourMs,
                    isSmsSource = false,
                    paymentMethod = "Visa Cashback (6025)"
                )
            )

            // 3. Initial Credit Cards (Screen 3 and Screen 7)
            db.creditCardDao.insertCard(
                CreditCard(
                    name = "Visa Cashback",
                    last4Digits = "8842",
                    paymentDay = 13,
                    amountDue = 1850.0,
                    cashbackEarned = 43.0,
                    expectedLateFee = 150.0,
                    isPrimary = true
                )
            )

            // 4. Initial Alert Logs (Screen 7)
            db.alertLogDao.insertAlert(
                AlertLog(
                    title = "تنبيه عاجل",
                    message = "موعد السداد يقترب! تجنب رسوم التأخير البالغة 150 ريال.",
                    timeLabel = "اليوم",
                    type = "danger"
                )
            )
            db.alertLogDao.insertAlert(
                AlertLog(
                    title = "تذكير ثاني",
                    message = "تبقّى 6 أيام على نهاية فترة السماح لسداد بطاقة Visa.",
                    timeLabel = "منذ 3 أيام",
                    type = "warning"
                )
            )
            db.alertLogDao.insertAlert(
                AlertLog(
                    title = "تذكير أول",
                    message = "صدر كشف الحساب الجديد لشهر أكتوبر.",
                    timeLabel = "منذ 7 أيام",
                    type = "info"
                )
            )
        }
    }
}
