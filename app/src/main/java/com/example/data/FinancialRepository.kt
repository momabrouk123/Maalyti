package com.example.data

import kotlinx.coroutines.flow.Flow

class FinancialRepository(private val db: AppDatabase) {

    val settings: Flow<BudgetSettings?> = db.budgetSettingsDao.getSettingsFlow()
    val allTransactions: Flow<List<Transaction>> = db.transactionDao.getAllTransactionsFlow()
    val allCards: Flow<List<CreditCard>> = db.creditCardDao.getAllCardsFlow()
    val allAlerts: Flow<List<AlertLog>> = db.alertLogDao.getAllAlertsFlow()

    suspend fun getSettings(): BudgetSettings? = db.budgetSettingsDao.getSettings()

    suspend fun updateSettings(settings: BudgetSettings) {
        db.budgetSettingsDao.updateSettings(settings)
    }

    suspend fun insertTransaction(transaction: Transaction): Long {
        return db.transactionDao.insertTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        db.transactionDao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        db.transactionDao.deleteTransaction(transaction)
    }

    suspend fun getTransactionById(id: Int): Transaction? {
        return db.transactionDao.getTransactionById(id)
    }

    suspend fun insertCard(card: CreditCard): Long {
        return db.creditCardDao.insertCard(card)
    }

    suspend fun updateCard(card: CreditCard) {
        db.creditCardDao.updateCard(card)
    }

    suspend fun deleteCard(card: CreditCard) {
        db.creditCardDao.deleteCard(card)
    }

    suspend fun insertAlert(alert: AlertLog): Long {
        return db.alertLogDao.insertAlert(alert)
    }
}
