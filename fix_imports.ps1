# PowerShell script to recursively fix import statements
# Find all .kt files in the project and replace old package references

$sourceDirectory = ".\app\src\main\java"
$oldPackage = "com.example.app"
$newPackage = "dev.aurakai.auraframefx"

# Get all Kotlin files recursively
$files = Get-ChildItem -Path $sourceDirectory -Filter "*.kt" -Recurse

$totalFiles = $files.Count
$processedFiles = 0
$modifiedFiles = 0

foreach ($file in $files)
{
    $processedFiles++
    $content = Get-Content -Path $file.FullName -Raw

    # Check if the file contains the old package
    if ($content -match $oldPackage)
    {
        Write-Host "Processing file $processedFiles of $totalFiles: $($file.FullName)"

        # Replace old package with new package
        $newContent = $content -replace $oldPackage, $newPackage

        # Write the file back only if content was changed
        if ($content -ne $newContent) {
        Set-Content -Path $file.FullName -Value $newContent -NoNewline
        $modifiedFiles++
        Write-Host "  - Modified" -ForegroundColor Green
        }
    }
    else
    {
        Write-Host "Processing file $processedFiles of $totalFiles: $($file.FullName) - No changes needed" -ForegroundColor Gray
    }
}

Write-Host "`nImport statements fixed in $modifiedFiles of $totalFiles files." -ForegroundColor Cyan
