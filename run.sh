#!/bin/bash
# ─────────────────────────────────────────────────────────
#  Personal Finance & Expense Tracker — Build & Run Script
#  Requirements: Java JDK 11 or higher
# ─────────────────────────────────────────────────────────

echo "🔨 Compiling..."
mkdir -p out
find src -name "*.java" > sources.txt
javac -d out @sources.txt

if [ $? -ne 0 ]; then
  echo "❌ Compilation failed. Make sure JDK (not just JRE) is installed."
  exit 1
fi

echo "✅ Compilation successful!"
echo "🚀 Starting Personal Finance Tracker..."
echo ""

cd out
java financetracker.Main
