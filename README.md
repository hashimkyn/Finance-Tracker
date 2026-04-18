# 💰 Personal Finance & Expense Tracker
### Console-Based Java OOP Application

---

## 📌 Overview

A fully-featured, console-based Personal Finance and Expense Tracker built in Java using core Object-Oriented Programming principles and file-based persistence. Supports multiple users, income/expense tracking, budget management, and financial insights — all without a database.

---

## 🏗️ Project Structure

```
FinanceTracker/
├── src/
│   └── financetracker/
│       ├── Main.java                    ← Entry point & full console UI
│       ├── model/
│       │   ├── Transaction.java         ← Abstract base class (Abstraction)
│       │   ├── Income.java              ← Extends Transaction (Inheritance)
│       │   ├── Expense.java             ← Extends Transaction (Inheritance)
│       │   ├── User.java                ← User credentials & auth (Encapsulation)
│       │   ├── Wallet.java              ← Balance + transaction history (Composition)
│       │   └── Budget.java              ← Monthly category budget model
│       ├── service/
│       │   ├── AuthService.java         ← Register / Login logic
│       │   ├── WalletService.java       ← Core financial operations
│       │   └── InsightsService.java     ← Analytics & spending guide
│       └── util/
│           ├── FileManager.java         ← All file I/O (persistence)
│           └── ConsoleHelper.java       ← Coloured console UI helpers
├── data/                                ← Auto-created; stores all user data
├── run.sh                               ← Build & run (Linux / macOS)
├── run.bat                              ← Build & run (Windows)
└── README.md
```

---

## 🎯 OOP Concepts Demonstrated

| Concept         | Where Applied |
|----------------|---------------|
| **Abstraction** | `Transaction` is abstract; `getType()`, `getCategory()`, `toFileString()` are abstract methods implemented by subclasses |
| **Inheritance** | `Income` and `Expense` both extend `Transaction`, inheriting `id`, `amount`, `date`, `description` |
| **Encapsulation** | All fields are `private`; exposed via getters. `User` hashes passwords internally |
| **Composition** | `Wallet` owns a `List<Transaction>`; `WalletService` owns a `Wallet` and a `Map<String,Budget>` |

---

## ✨ Features

### 👥 Multi-User System
- Register with username + password (min 4 chars)
- Passwords stored as hashed values
- Each user has completely separate data files

### 💵 Income Recording
- Record income with amount, source, and description
- Sources are free-form (Salary, Freelance, Business, etc.)
- Balance updates instantly

### 💸 Expense Recording
- 9 predefined categories: Food, Transport, Housing, Health, Education, Entertainment, Shopping, Utilities, Other
- Pre-expense budget warning if limit would be exceeded
- Prevents recording if wallet balance is insufficient

### 📋 Transaction History
- View **all** transactions in a unified list
- Filter transactions by **year and month**
- Color-coded: Green = Income, Red = Expense
- Each transaction has a unique 8-character ID and auto-assigned date

### 📊 Budget Manager
- Set monthly limits per expense category
- Visual progress bar showing spending vs. limit
- Automatic monthly reset when a new month begins

### 🔍 Financial Insights
- Total income, total expenses, net balance
- Current wallet balance
- Category-wise spending breakdown for current month
- Budget violation alerts
- **Daily Spending Guide**: Wallet ÷ Days remaining in month

---

## 💾 File Storage Format

All data is stored in `data/` as plain text files:

| File | Format |
|------|--------|
| `users.txt` | `username\|passwordHash` |
| `{user}_transactions.txt` | `TYPE\|id\|amount\|category\|description\|date` |
| `{user}_wallet.txt` | `balance` (single number) |
| `{user}_budgets.txt` | `category\|limit\|YYYY-MM` |

---

## 🚀 How to Run

### Requirements
- **Java JDK 11 or higher** — [Download here](https://adoptium.net/)

### Linux / macOS
```bash
chmod +x run.sh
./run.sh
```

### Windows
```
Double-click run.bat
OR run in Command Prompt: run.bat
```

### Manual (any OS)
```bash
mkdir out
javac -d out $(find src -name "*.java")   # Linux/Mac
# OR on Windows:
dir /s /b src\*.java > sources.txt && javac -d out @sources.txt

java -cp out financetracker.Main
```

---

## 🧪 Sample Walkthrough

1. **Register** a new account (e.g. username: `ali`, password: `1234`)
2. **Login** with your credentials
3. **Add Income** → Amount: `50000`, Source: `Salary`
4. **Add Expense** → Amount: `3000`, Category: `Food`, Description: `Groceries`
5. **Set Budget** → Category: `Food`, Limit: `8000`
6. **View Insights** → See balance, spending, and daily guide
7. **Logout** — all data saved automatically

---

## 📁 Data Persistence

- Data is saved **immediately** after every transaction
- No database required — everything is in human-readable `.txt` files
- Re-launching the app fully restores all previous data
- Budget months auto-reset when a new calendar month begins

---

*Built with Java 11+ · No external libraries required*
