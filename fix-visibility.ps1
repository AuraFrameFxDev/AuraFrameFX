# Fixes Kotlin visibility issues for LSPosed compatibility
# Run this script from the project root directory

param (
    [string]$projectDir = "."
)

$ErrorActionPreference = "Stop"

Write-Host "=== AuraFrameFX Visibility Fixer ===" -ForegroundColor Cyan

# Find all Kotlin files
$kotlinFiles = Get-ChildItem -Path $projectDir -Recurse -Include "*.kt" -File
$processedCount = 0

foreach ($file in $kotlinFiles) {
    $content = Get-Content -Path $file.FullName -Raw
    $originalContent = $content
    
    # Remove internal modifier
    $content = $content -replace '\b(internal|private)\s+', ''
    
    # Remove private set
    $content = $content -replace 'private set', ''
    
    # Fix visibility for LSPosed
    $content = $content -replace 'internal class', 'public class'
    $content = $content -replace 'internal object', 'public object'
    $content = $content -replace 'internal fun', 'public fun'
    $content = $content -replace 'internal val', 'public val'
    $content = $content -replace 'internal var', 'public var'
    
    if ($content -ne $originalContent) {
        $relativePath = $file.FullName.Substring((Resolve-Path $projectDir).Path.Length + 1)
        Write-Host "Fixed visibility in: $relativePath" -ForegroundColor Green
        $content | Set-Content -Path $file.FullName -NoNewline
        $processedCount++
    }
}

Write-Host "=== Visibility fixing complete ===" -ForegroundColor Cyan
Write-Host "Processed $($kotlinFiles.Count) Kotlin files"
Write-Host "Modified $processedCount files"

# Also fix generated OpenAPI files if they exist
$openApiDir = "./app/build/generated/openapi/src/main/kotlin"
if (Test-Path $openApiDir) {
    Write-Host "Fixing visibility in generated OpenAPI files..." -ForegroundColor Cyan
    & $PSScriptRoot/fix-visibility.ps1 -projectDir $openApiDir
}

Write-Host "=== All done! ===" -ForegroundColor Green
