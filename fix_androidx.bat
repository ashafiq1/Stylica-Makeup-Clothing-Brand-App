@echo off
echo ========================================
echo AndroidX Configuration Fix
echo ========================================
echo.

REM Navigate to project directory (update this path if needed)
cd /d "%~dp0"

echo Adding AndroidX properties to gradle.properties...
echo.

REM Add AndroidX properties
echo android.useAndroidX=true >> gradle.properties
echo android.enableJetifier=true >> gradle.properties

echo Properties added successfully!
echo.
echo Contents of gradle.properties:
type gradle.properties
echo.
echo ========================================
echo Now cleaning and building project...
echo ========================================
echo.

REM Clean and build
call gradlew.bat clean build

echo.
echo ========================================
echo Done! Now sync project in Android Studio
echo ========================================
pause
