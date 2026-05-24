package com.example.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.ui.viewmodel.FinancialViewModel
import java.util.*
import java.util.regex.Pattern

object SmsParser {
    private const val TAG = "SmsParser"

    data class ParsedSms(
        val body: String,
        val sender: String,
        val timestamp: Long,
        val amount: Double,
        val isExpense: Boolean,
        val isSalary: Boolean,
        val merchant: String,
        val category: String
    )

    fun readAndSyncSms(context: Context, viewModel: FinancialViewModel): Map<String, Any?> {
        val results = mutableMapOf<String, Any?>()
        val parsedList = mutableListOf<ParsedSms>()
        var lastSalaryAmount = 0.0
        var lastSalaryDate = 0L

        try {
            val contentResolver = context.contentResolver
            val uri = Uri.parse("content://sms/inbox")
            
            // Get start of current month
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfMonth = calendar.timeInMillis

            val cursor = contentResolver.query(
                uri,
                arrayOf("_id", "address", "body", "date"),
                "date >= ?",
                arrayOf(startOfMonth.toString()),
                "date DESC"
            )

            val existingTimestamps = viewModel.allTransactions.value.map { it.timestamp }.toSet()

            cursor?.use { c ->
                val bodyIndex = c.getColumnIndex("body")
                val addressIndex = c.getColumnIndex("address")
                val dateIndex = c.getColumnIndex("date")

                if (bodyIndex != -1 && addressIndex != -1 && dateIndex != -1) {
                    while (c.moveToNext()) {
                        val body = c.getString(bodyIndex) ?: ""
                        val sender = c.getString(addressIndex) ?: "Bank"
                        val date = c.getLong(dateIndex)

                        val parsed = parseSMSMessage(body, sender, date)
                        if (parsed != null) {
                            parsedList.add(parsed)
                            if (parsed.isSalary) {
                                if (date > lastSalaryDate) {
                                    lastSalaryDate = date
                                    lastSalaryAmount = parsed.amount
                                }
                            }
                        }
                    }
                }
            }

            // Sync with DB if have any new transactions
            var newTransactionsCount = 0
            for (p in parsedList) {
                if (p.timestamp !in existingTimestamps) {
                    // Decide sign of amount: negative for expense, positive for salary/deposit
                    val finalAmount = if (p.isExpense) -p.amount else p.amount
                    viewModel.addTransaction(
                        merchant = p.merchant,
                        category = p.category,
                        amount = finalAmount,
                        isSms = true
                    )
                    // If it was a salary, also log it as an alert
                    if (p.isSalary) {
                        viewModel.addAlert(
                            title = "تم رصد إيداع الراتب",
                            message = "بناءً على الرسائل النصية، تم إيداع راتب بقيمة ${p.amount} ريال في حسابك.",
                            timeLabel = "اليوم",
                            type = "info"
                        )
                    }
                    newTransactionsCount++
                }
            }

            // If we found a salary, we update the monthly salary in Settings
            if (lastSalaryAmount > 0.0) {
                viewModel.updateMonthlySalary(lastSalaryAmount)
                results["lastSalaryAmount"] = lastSalaryAmount
                results["lastSalaryDate"] = lastSalaryDate
            }

            results["newTransactionsCount"] = newTransactionsCount
            results["totalSmsFound"] = parsedList.size

        } catch (e: Exception) {
            Log.e(TAG, "Error reading SMS", e)
            results["error"] = e.message
        }

        return results
    }

    // Advanced regex parser to extract amounts, merchants, categories and salary info
    fun parseSMSMessage(body: String, sender: String, timestamp: Long): ParsedSms? {
        val bodyLower = body.lowercase()
        
        // 1. Identify text indicators of banking SMS (Arabic & English)
        val isBanking = sender.contains("bank", ignoreCase = true) || 
                        sender.contains("ahli", ignoreCase = true) || 
                        sender.contains("rajhi", ignoreCase = true) || 
                        sender.contains("snb", ignoreCase = true) || 
                        sender.contains("riyad", ignoreCase = true) || 
                        sender.contains("alinma", ignoreCase = true) || 
                        sender.contains("sabb", ignoreCase = true) || 
                        sender.contains("bsf", ignoreCase = true) || 
                        sender.contains("mada", ignoreCase = true) || 
                        body.contains("شراء", ignoreCase = true) ||
                        body.contains("خصم", ignoreCase = true) ||
                        body.contains("سحب", ignoreCase = true) ||
                        body.contains("مدى", ignoreCase = true) ||
                        body.contains("راتب", ignoreCase = true) ||
                        body.contains("إيداع", ignoreCase = true) ||
                        body.contains("حساب", ignoreCase = true) ||
                        body.contains("بطاقة", ignoreCase = true) ||
                        body.contains("spent", ignoreCase = true) ||
                        body.contains("purchase", ignoreCase = true) ||
                        body.contains("deposit", ignoreCase = true) ||
                        body.contains("salary", ignoreCase = true)
                        
        if (!isBanking) return null

        // 2. Classify if it's a salary deposit or normal expense
        val isSalary = body.contains("راتب", ignoreCase = true) || 
                       body.contains("salary", ignoreCase = true) ||
                       (body.contains("إيداع", ignoreCase = true) && body.contains("وزارة", ignoreCase = true)) || // governmental salary
                       (body.contains("تحويل" , ignoreCase = true) && body.contains("راتب", ignoreCase = true))

        val isExpense = !isSalary && (
                        body.contains("شراء", ignoreCase = true) ||
                        body.contains("خصم", ignoreCase = true) ||
                        body.contains("سحب", ignoreCase = true) ||
                        body.contains("spent", ignoreCase = true) ||
                        body.contains("purchase", ignoreCase = true) ||
                        body.contains("payment", ignoreCase = true) ||
                        body.contains("pay", ignoreCase = true) ||
                        body.contains("دفع", ignoreCase = true) ||
                        body.contains("مدى", ignoreCase = true)
                    )

        // If it's neither an expense nor a salary (e.g., just info, login verification), ignore it
        if (!isSalary && !isExpense) return null

        // 3. Extract Amount
        // Regex to match typical money patterns: 120, 4,500.20, 10.5 etc.
        val amountRegex = """(?:\b|ر\.س\s*|sar\s*|ريال\s*)(\d{1,3}(?:[,\s]?\d{3})*(?:\.\d{1,2})?)(?:\s*ريال|\s*ر\.س|\s*sar|\s*sr)?"""
        val amountPattern = Pattern.compile(amountRegex, Pattern.CASE_INSENSITIVE)
        val matcher = amountPattern.matcher(body)
        var amount = 0.0
        if (matcher.find()) {
            val amtStr = matcher.group(1) ?: ""
            amount = amtStr.replace(",", "").replace(" ", "").toDoubleOrNull() ?: 0.0
        } else {
            // General decimal number finder as fallback
            val fallbackRegex = """\d+(?:\.\d{1,2})?"""
            val fallbackPattern = Pattern.compile(fallbackRegex)
            val fallbackMatcher = fallbackPattern.matcher(body)
            var foundMaxVal = 0.0
            while (fallbackMatcher.find()) {
                val valStr = fallbackMatcher.group()
                val dVal = valStr.toDoubleOrNull() ?: 0.0
                if (dVal > foundMaxVal) {
                    foundMaxVal = dVal
                }
            }
            amount = foundMaxVal
        }

        if (amount <= 0.0) return null

        // 4. Extract Merchant/Source and Category
        var merchant = sender.replace("AD-", "").replace("AD_", "").replace("F-", "").replace("F_", "")
        var category = "أخرى"

        // Search for Arabic or English merchant pattern in SMS bodies
        val merchantPatterns = listOf(
            Pattern.compile("""لدى\s+([^,.\n]+)""", Pattern.CASE_INSENSITIVE),
            Pattern.compile("""في\s+([^,.\n]+)""", Pattern.CASE_INSENSITIVE),
            Pattern.compile("""من\s+([^,.\n]+)""", Pattern.CASE_INSENSITIVE),
            Pattern.compile("""at\s+([^,.\n]+)""", Pattern.CASE_INSENSITIVE),
            Pattern.compile("""from\s+([^,.\n]+)""", Pattern.CASE_INSENSITIVE),
            Pattern.compile("""to\s+([^,.\n]+)""", Pattern.CASE_INSENSITIVE),
            Pattern.compile("""in\s+([^,.\n]+)""", Pattern.CASE_INSENSITIVE)
        )

        for (pattern in merchantPatterns) {
            val merchantMatcher = pattern.matcher(body)
            if (merchantMatcher.find()) {
                val candidate = merchantMatcher.group(1)?.trim() ?: ""
                if (candidate.isNotEmpty() && candidate.length < 25) {
                    merchant = candidate
                    break
                }
            }
        }

        // Clean merchant name if it gets trailing numbers or punctuation
        merchant = merchant.replace(Regex("""\d+.*"""), "").replace(Regex("""\*"""), "").trim()
        if (merchant.isEmpty()) {
            merchant = sender
        }

        // Categorization mapping
        val merchantLower = merchant.lowercase()

        if (isSalary) {
            merchant = "إيداع راتب"
            category = "أخرى" // category fallback
        } else if (merchantLower.contains("carrefour") || merchantLower.contains("كارفور") || 
            merchantLower.contains("بنده") || merchantLower.contains("panda") || 
            merchantLower.contains("العثيم") || merchantLower.contains("othaim") || 
            merchantLower.contains("سوبر") || merchantLower.contains("بقالة") || 
            bodyLower.contains("بقالة") || bodyLower.contains("سوبرماركت")) {
            category = "مقاضي"
        } else if (merchantLower.contains("starbucks") || merchantLower.contains("مطعم") || 
            merchantLower.contains("ستاربكس") || merchantLower.contains("قهوة") || 
            merchantLower.contains("كافيه") || merchantLower.contains("cafe") || 
            merchantLower.contains("mcdonald") || merchantLower.contains("بيك") || 
            merchantLower.contains("albaik") || bodyLower.contains("مطعم")) {
            category = "مطاعم"
        } else if (merchantLower.contains("uber") || merchantLower.contains("أوبر") || 
            merchantLower.contains("كريم") || merchantLower.contains("careem") || 
            merchantLower.contains("بنزين") || merchantLower.contains("fuel") || 
            merchantLower.contains("مواصلات") || merchantLower.contains("taxi")) {
            category = "مواصلات"
        } else if (merchantLower.contains("stc") || merchantLower.contains("موبايلي") || 
            merchantLower.contains("زين") || merchantLower.contains("فاتورة") || 
            merchantLower.contains("كهرباء") || merchantLower.contains("electricity") || 
            merchantLower.contains("bill") || merchantLower.contains("فواتير")) {
            category = "فواتير"
        } else if (merchantLower.contains("دواء") || merchantLower.contains("صيدلية") || 
            merchantLower.contains("مستشفى") || merchantLower.contains("nahdi") || 
            merchantLower.contains("النهدي") || merchantLower.contains("صحة") || 
            merchantLower.contains("health") || merchantLower.contains("clinic")) {
            category = "صحة"
        } else if (merchantLower.contains("zara") || merchantLower.contains("زارا") || 
            merchantLower.contains("تسوق") || merchantLower.contains("noon") || 
            merchantLower.contains("amazon") || merchantLower.contains("امازون") || 
            merchantLower.contains("نون") || merchantLower.contains("خرير") || 
            merchantLower.contains("jarir") || merchantLower.contains("جرير")) {
            category = "تسوق"
        }

        return ParsedSms(
            body = body,
            sender = sender,
            timestamp = timestamp,
            amount = amount,
            isExpense = isExpense,
            isSalary = isSalary,
            merchant = merchant,
            category = category
        )
    }
}
