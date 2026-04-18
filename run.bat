@echo off
REM ─────────────────────────────────────────────────────────
REM  Personal Finance & Expense Tracker — Build & Run Script
REM  Requirements: Java JDK 11 or higher
REM ─────────────────────────────────────────────────────────

echo Compiling...
if not exist out mkdir out

dir /s /b src\*.java > sources.txt
javac -d out @sources.txt

if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed. Make sure JDK is installed and in PATH.
    pause
    exit /b 1
)

echo Compilation successful!
echo Starting Personal Finance Tracker...
echo.

cd out
java financetracker.Main
cd ..
pause
