# AndroidX Configuration Fix for PowerShell
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "AndroidX Configuration Fix" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Get current directory
$projectPath = Get-Location

Write-Host "Adding AndroidX properties to gradle.properties..." -ForegroundColor Yellow
Write-Host ""

# Add AndroidX properties
Add-Content -Path "gradle.properties" -Value "android.useAndroidX=true"
Add-Content -Path "gradle.properties" -Value "android.enableJetifier=true"

Write-Host "Properties added successfully!" -ForegroundColor Green
Write-Host ""
Write-Host "Contents of gradle.properties:" -ForegroundColor Yellow
Get-Content "gradle.properties"
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Now cleaning and building project..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Clean and build
& .\gradlew.bat clean build

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Done! Now sync project in Android Studio" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Read-Host "Press Enter to exit"
