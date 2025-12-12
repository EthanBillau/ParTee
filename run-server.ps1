# Script to run the Golf Server
$env:JAVA_HOME = "C:\Program Files\Java\java-21-openjdk-21.0.9.0.10-1.win.jdk.x86_64"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

Write-Host "Starting Golf Server..." -ForegroundColor Green
& "$env:JAVA_HOME\bin\java.exe" -cp "target\classes" com.project.golf.server.ServerMain
