@echo off
echo Password Strength Checker
echo ========================

REM Try to find JavaFX automatically
set JAVAFX_PATH=""

REM Check common JavaFX installation paths
if exist "C:\Program Files\Java\javafx-21\lib" set JAVAFX_PATH="C:\Program Files\Java\javafx-21\lib"
if exist "C:\Program Files\Java\javafx-17\lib" set JAVAFX_PATH="C:\Program Files\Java\javafx-17\lib"
if exist "C:\Program Files\Java\javafx-11\lib" set JAVAFX_PATH="C:\Program Files\Java\javafx-11\lib"
if exist "C:\Program Files\Eclipse Adoptium\jdk-11.0.16.101-hotspot\lib\javafx" set JAVAFX_PATH="C:\Program Files\Eclipse Adoptium\jdk-11.0.16.101-hotspot\lib\javafx"
if exist "C:\Program Files\Eclipse Adoptium\jdk-17.0.4.101-hotspot\lib\javafx" set JAVAFX_PATH="C:\Program Files\Eclipse Adoptium\jdk-17.0.4.101-hotspot\lib\javafx"

REM Check if user has set JAVAFX_HOME environment variable
if defined JAVAFX_HOME (
    if exist "%JAVAFX_HOME%\lib" set JAVAFX_PATH="%JAVAFX_HOME%\lib"
)

REM If still not found, prompt user
if "%JAVAFX_PATH%"=="" (
    echo JavaFX not found automatically. Please set JAVAFX_HOME environment variable
    echo or download JavaFX from: https://openjfx.io/
    echo.
    echo Example: set JAVAFX_HOME=C:\javafx-sdk-21
    echo.
    pause
    exit /b 1
)

echo Found JavaFX at: %JAVAFX_PATH%
echo.

echo Compiling Password Strength Checker...
javac --module-path %JAVAFX_PATH% --add-modules javafx.controls,javafx.fxml PasswordChecker.java

if %errorlevel% neq 0 (
    echo Compilation failed! Please check your JavaFX installation.
    echo Make sure JavaFX is properly installed and JAVAFX_HOME is set correctly.
    pause
    exit /b 1
)

echo Compilation successful!
echo Running Password Strength Checker...
echo.

java --module-path %JAVAFX_PATH% --add-modules javafx.controls,javafx.fxml PasswordChecker

pause
