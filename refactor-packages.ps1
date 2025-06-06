# PowerShell Script to refactor package declarations in Kotlin files
# from com.example.app to dev.aurakai.auraframefx

# Base directories
$sourceDir = ".\app\src"
$oldPackage = "com.example.app"
$newPackage = "dev.aurakai.auraframefx"

# Function to create directory if it doesn't exist
function EnsureDirectoryExists {
    param (
        [string]$path
    )
    
    if (!(Test-Path -Path $path)) {
        New-Item -ItemType Directory -Path $path -Force | Out-Null
        Write-Host "Created directory: $path"
    }
}

# Find all Kotlin files in the source directory
Write-Host "Finding all Kotlin files in $sourceDir..."
$kotlinFiles = Get-ChildItem -Path $sourceDir -Filter "*.kt" -Recurse

foreach ($file in $kotlinFiles) {
    $content = Get-Content -Path $file.FullName -Raw
    
    # Skip files that are already in the new package
    if ($content -match "package $newPackage") {
        Write-Host "Skipping already migrated file: $($file.FullName)"
        continue
    }
    
    # Replace package declaration
    $updatedContent = $content -replace "package\s+$oldPackage", "package $newPackage"
    
    # Replace imports
    $updatedContent = $updatedContent -replace "import\s+$oldPackage", "import $newPackage"
    
    # Calculate the new file path
    $relativePath = $file.FullName -replace [regex]::Escape($sourceDir), ""
    $relativePath = $relativePath -replace "\\$oldPackage\\", "\$newPackage\"
    $newFilePath = Join-Path -Path $sourceDir -ChildPath $relativePath
    
    # Create directory structure if needed
    $newFileDir = Split-Path -Path $newFilePath -Parent
    EnsureDirectoryExists -path $newFileDir
    
    # Write the updated content to the new location
    $updatedContent | Set-Content -Path $newFilePath -NoNewline
    
    Write-Host "Processed: $($file.FullName) -> $newFilePath"
    
    # If the file was moved to a new location, delete the original
    if ($file.FullName -ne $newFilePath) {
        Remove-Item -Path $file.FullName
        Write-Host "Removed original file: $($file.FullName)"
    }
}

Write-Host "Package refactoring completed successfully!"
