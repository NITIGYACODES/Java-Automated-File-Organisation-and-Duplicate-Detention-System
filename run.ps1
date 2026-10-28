$bluejJdk = "C:\Program Files\BlueJ\jdk\bin"
if (Test-Path "$bluejJdk\javac.exe") {
    $env:Path = "$bluejJdk;" + $env:Path
}

$binDir = Join-Path $PSScriptRoot "bin"
if (-not (Test-Path $binDir)) { New-Item -ItemType Directory -Path $binDir | Out-Null }

javac -d "$binDir" (Join-Path $PSScriptRoot "FileOrganizerSystem.java")
if ($LASTEXITCODE -eq 0) {
    java -cp "$binDir" FileOrganizerSystem
} else {
    Write-Error "Compilation failed."
}
