package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FinancialViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FinancialRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = FinancialRepository(database)
    }

    // Flows from database
    val settings: StateFlow<BudgetSettings> = repository.settings
        .filterNotNull()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BudgetSettings()
        )

    val transactions: StateFlow<List<Transaction>> = repository.allItems
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Wait, let's map repository.allTransactions to VM transactions
    val allTransactions: StateFlow<List<Transaction>> = repository.allTransactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val creditCards: StateFlow<List<CreditCard>> = repository.allCards
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val alerts: StateFlow<List<AlertLog>> = repository.allAlerts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // UI State for edits/modifications
    private val _onboardingStep = MutableStateFlow(1)
    val onboardingStep = _onboardingStep.asStateFlow()

    // Temporary values held during onboarding
    val tempSalary = MutableStateFlow("10000")
    val tempBudget = MutableStateFlow("6000")
    val tempCardName = MutableStateFlow("")
    val tempCardDate = MutableStateFlow("")
    val tempCardDigits = MutableStateFlow("")

    // For editing a single transaction
    private val _editingTransaction = MutableStateFlow<Transaction?>(null)
    val editingTransaction = _editingTransaction.asStateFlow()

    // Interactive operations
    fun setOnboardingStep(step: Int) {
        _onboardingStep.value = step
    }

    fun updateMonthlySalary(salary: Double) {
        viewModelScope.launch {
            val current = settings.value
            val savings = salary - current.expensesBudget
            repository.updateSettings(
                current.copy(
                    monthlySalary = salary,
                    savingsGoal = if (savings > 0) savings else current.savingsGoal
                )
            )
        }
    }

    fun updateExpensesBudget(budget: Double) {
        viewModelScope.launch {
            val current = settings.value
            val savings = current.monthlySalary - budget
            repository.updateSettings(
                current.copy(
                    expensesBudget = budget,
                    savingsGoal = if (savings > 0) savings else current.savingsGoal
                )
            )
        }
    }

    fun updateSavingsGoal(goal: Double) {
        viewModelScope.launch {
            val current = settings.value
            repository.updateSettings(current.copy(savingsGoal = goal))
        }
    }

    fun updateSmsPermission(allowed: Boolean) {
        viewModelScope.launch {
            val current = settings.value
            repository.updateSettings(current.copy(smsPermissionAllowed = allowed))
        }
    }

    fun updateNotificationSettings(
        smsEnabled: Boolean,
        dailyLimitEnabled: Boolean,
        cardEnabled: Boolean
    ) {
        viewModelScope.launch {
            val current = settings.value
            repository.updateSettings(
                current.copy(
                    smsNotificationsEnabled = smsEnabled,
                    dailyLimitNotificationEnabled = dailyLimitEnabled,
                    cardNotificationsEnabled = cardEnabled
                )
            )
        }
    }

    fun triggerUpgradeSettings() {
        viewModelScope.launch {
            val current = settings.value
            repository.updateSettings(current.copy(isPremium = !current.isPremium))
        }
    }

    // Onboarding complete
    fun completeOnboarding() {
        viewModelScope.launch {
            val salary = tempSalary.value.replace(",", "").toDoubleOrNull() ?: 10000.0
            val budget = tempBudget.value.replace(",", "").toDoubleOrNull() ?: 6000.0
            val savings = if (salary - budget > 0) (salary - budget) else 4000.0

            repository.updateSettings(
                settings.value.copy(
                    monthlySalary = salary,
                    expensesBudget = budget,
                    savingsGoal = savings
                )
            )

            // Save credit card if values filled
            if (tempCardName.value.isNotEmpty()) {
                val day = tempCardDate.value.toIntOrNull() ?: 13
                val digits = tempCardDigits.value.ifEmpty { "0000" }
                repository.insertCard(
                    CreditCard(
                        name = tempCardName.value,
                        last4Digits = digits,
                        paymentDay = day,
                        amountDue = 1850.0,
                        isPrimary = true
                    )
                )
            }
        }
    }

    // Transaction management
    fun addTransaction(merchant: String, category: String, amount: Double, isSms: Boolean = false) {
        viewModelScope.launch {
            repository.insertTransaction(
                Transaction(
                    merchant = merchant,
                    category = category,
                    amount = amount,
                    timestamp = System.currentTimeMillis(),
                    isSmsSource = isSms
                )
            )
        }
    }

    fun setEditingTransaction(transaction: Transaction?) {
        _editingTransaction.value = transaction
    }

    fun saveEditingTransaction() {
        val t = _editingTransaction.value ?: return
        viewModelScope.launch {
            repository.updateTransaction(t)
            _editingTransaction.value = null
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            if (_editingTransaction.value?.id == transaction.id) {
                _editingTransaction.value = null
            }
        }
    }

    suspend fun getTransactionById(id: Int): Transaction? {
        return repository.getTransactionById(id)
    }

    fun updateEditingCategory(category: String) {
        val current = _editingTransaction.value ?: return
        _editingTransaction.value = current.copy(category = category)
    }

    fun updateEditingBudgetToggle(checked: Boolean) {
        val current = _editingTransaction.value ?: return
        _editingTransaction.value = current.copy(isCalculatedInBudget = checked)
    }

    fun updateEditingNote(note: String) {
        val current = _editingTransaction.value ?: return
        _editingTransaction.value = current.copy(note = note)
    }

    fun updateEditingReceipt(hasReceipt: Boolean) {
        val current = _editingTransaction.value ?: return
        _editingTransaction.value = current.copy(hasReceipt = hasReceipt)
    }

    fun resetDatabase() {
        viewModelScope.launch {
            val all = allTransactions.value
            for (t in all) {
                repository.deleteTransaction(t)
            }
            val cardsList = creditCards.value
            for (c in cardsList) {
                repository.deleteCard(c)
            }
            // Populate defaults again or keep raw empty
            // To provide clean reset, let's just restore base settings
            repository.updateSettings(
                BudgetSettings(
                    id = 1,
                    monthlySalary = 10000.0,
                    expensesBudget = 6000.0,
                    savingsGoal = 4000.0,
                    isPremium = false,
                    smsPermissionAllowed = false
                )
            )
        }
    }

    // Add general Alert Log
    fun addAlert(title: String, message: String, timeLabel: String, type: String) {
        viewModelScope.launch {
            repository.insertAlert(
                AlertLog(
                    title = title,
                    message = message,
                    timeLabel = timeLabel,
                    type = type
                )
            )
        }
    }

    // Add Credit Card
    fun addCreditCard(name: String, last4Digits: String, paymentDay: Int, amountDue: Double, isPrimary: Boolean) {
        viewModelScope.launch {
            repository.insertCard(
                CreditCard(
                    name = name,
                    last4Digits = last4Digits,
                    paymentDay = paymentDay,
                    amountDue = amountDue,
                    isPrimary = isPrimary
                )
            )
        }
    }
}

// Simple extension field since room-database-integration skill uses .allItems but we used standard naming
// We map allItems getter dynamically in AppDatabase callback, but to make compile perfectly safe, let's add `allItems`
// to the Database class as a custom flow or map in VM initializing from table query.
// To avoid conflicts, our repo has `allTransactions` and standard getters.
// Let's add a wrapper is VM to map:
val FinancialRepository.allItems: Flow<List<Transaction>> get() = this.allTransactions
