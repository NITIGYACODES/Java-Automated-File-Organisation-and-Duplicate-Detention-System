@echo off
set "JAVA_BIN=C:\Program Files\BlueJ\jdk\bin"
if exist "%JAVA_BIN%\javac.exe" (
    "%JAVA_BIN%\javac.exe" -d "%~dp0bin" "%~dp0FileOrganizerSystem.java"
    "%JAVA_BIN%\java.exe" -cp "%~dp0bin" FileOrganizerSystem
) else (
    javac -d "%~dp0bin" "%~dp0FileOrganizerSystem.java"
    java -cp "%~dp0bin" FileOrganizerSystem
)
pause
