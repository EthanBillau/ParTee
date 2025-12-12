# Script to run the Golf Login GUI
Set-Location $PSScriptRoot
$env:JAVA_HOME = "C:\Program Files\Java\java-21-openjdk-21.0.9.0.10-1.win.jdk.x86_64"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Compile first if needed
if (-not (Test-Path "target\classes")) {
    Write-Host "Compiling project..." -ForegroundColor Yellow
    & "C:\Users\Ethan\.maven\maven-3.9.11\bin\mvn.cmd" compile
}

Write-Host "Starting Golf Login GUI..." -ForegroundColor Green
& "$env:JAVA_HOME\bin\java.exe" -cp "target\classes" com.project.golf.gui.LoginGUI
