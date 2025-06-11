# Script to verify Kotlin version consistency across the project
Write-Host "Checking Kotlin version consistency in AuraFrameFx project" -ForegroundColor Cyan

# Check version in libs.versions.toml
$libsVersionsPath = "gradle\libs.versions.toml"
if (Test-Path $libsVersionsPath)
{
    Write-Host "`nChecking $libsVersionsPath" -ForegroundColor Green
    $tomlContent = Get-Content $libsVersionsPath -Raw

    # Extract kotlin version from toml
    if ($tomlContent -match 'kotlin\s*=\s*"([^"]+)"')
    {
        $kotlinVersion = $matches[1]
        Write-Host "Found Kotlin version in libs.versions.toml: $kotlinVersion" -ForegroundColor Yellow
    }
    else
    {
        Write-Host "Kotlin version not found in libs.versions.toml" -ForegroundColor Red
    }

    # Extract KSP version from toml
    if ($tomlContent -match 'ksp\s*=\s*"([^"]+)"')
    {
        $kspVersion = $matches[1]
        Write-Host "Found KSP version in libs.versions.toml: $kspVersion" -ForegroundColor Yellow
    }
    else
    {
        Write-Host "KSP version not found in libs.versions.toml" -ForegroundColor Red
    }

    # Extract compose compiler version from toml
    if ($tomlContent -match 'compose-compiler\s*=\s*"([^"]+)"')
    {
        $composeVersion = $matches[1]
        Write-Host "Found Compose Compiler version in libs.versions.toml: $composeVersion" -ForegroundColor Yellow
    }
    else
    {
        Write-Host "Compose Compiler version not found in libs.versions.toml" -ForegroundColor Red
    }
}
else
{
    Write-Host "$libsVersionsPath not found" -ForegroundColor Red
}

# Check version in root build.gradle.kts
$rootBuildPath = "build.gradle.kts"
if (Test-Path $rootBuildPath)
{
    Write-Host "`nChecking $rootBuildPath" -ForegroundColor Green
    $buildContent = Get-Content $rootBuildPath -Raw

    # Check buildscript block
    if ($buildContent -match 'val\s+kotlinVersion\s*=\s*"([^"]+)"')
    {
        $rootKotlinVersion = $matches[1]
        Write-Host "Found Kotlin version in buildscript block: $rootKotlinVersion" -ForegroundColor Yellow

        if ($kotlinVersion -and $rootKotlinVersion -ne $kotlinVersion)
        {
            Write-Host "WARNING: Kotlin version in buildscript ($rootKotlinVersion) doesn't match version in libs.versions.toml ($kotlinVersion)" -ForegroundColor Red
        }
        else
        {
            Write-Host "Kotlin version in buildscript matches libs.versions.toml" -ForegroundColor Green
        }
    }

    # Check plugins block
    if ($buildContent -match 'id\("org\.jetbrains\.kotlin\.android"\)\.version\("([^"]+)"\)')
    {
        $pluginKotlinVersion = $matches[1]
        Write-Host "Found Kotlin version in plugins block: $pluginKotlinVersion" -ForegroundColor Yellow

        if ($kotlinVersion -and $pluginKotlinVersion -ne $kotlinVersion)
        {
            Write-Host "WARNING: Kotlin version in plugins block ($pluginKotlinVersion) doesn't match version in libs.versions.toml ($kotlinVersion)" -ForegroundColor Red
        }
        else
        {
            Write-Host "Kotlin version in plugins block matches libs.versions.toml" -ForegroundColor Green
        }
    }

    # Check KSP version
    if ($buildContent -match 'val\s+kspVersion\s*=\s*"([^"]+)"')
    {
        $rootKspVersion = $matches[1]
        Write-Host "Found KSP version in buildscript block: $rootKspVersion" -ForegroundColor Yellow

        if ($kspVersion -and $rootKspVersion -ne $kspVersion)
        {
            Write-Host "WARNING: KSP version in buildscript ($rootKspVersion) doesn't match version in libs.versions.toml ($kspVersion)" -ForegroundColor Red
        }
        else
        {
            Write-Host "KSP version in buildscript matches libs.versions.toml" -ForegroundColor Green
        }
    }
}
else
{
    Write-Host "$rootBuildPath not found" -ForegroundColor Red
}

# Check app module build.gradle.kts
$appBuildPath = "app\build.gradle.kts"
if (Test-Path $appBuildPath)
{
    Write-Host "`nChecking $appBuildPath" -ForegroundColor Green
    $appBuildContent = Get-Content $appBuildPath -Raw

    # Check compose compiler extension version
    if ($appBuildContent -match 'kotlinCompilerExtensionVersion\s*=\s*"([^"]+)"')
    {
        $appComposeVersion = $matches[1]
        Write-Host "Found Compose compiler extension version: $appComposeVersion" -ForegroundColor Yellow

        if ($composeVersion -and $appComposeVersion -ne $composeVersion)
        {
            Write-Host "WARNING: Compose compiler extension version in app module ($appComposeVersion) doesn't match version in libs.versions.toml ($composeVersion)" -ForegroundColor Red
        }
        else if ($kotlinVersion -eq "1.9.22" -and $appComposeVersion -ne "1.5.8")
        {
            Write-Host "WARNING: For Kotlin 1.9.22, the recommended Compose compiler extension version is 1.5.8, but found $appComposeVersion" -ForegroundColor Red
        }
        else
        {
            Write-Host "Compose compiler extension version is appropriate for Kotlin version" -ForegroundColor Green
        }
    }
    else
    {
        Write-Host "Compose compiler extension version not found in app module" -ForegroundColor Red
    }
}
else
{
    Write-Host "$appBuildPath not found" -ForegroundColor Red
}

Write-Host "`nVersion check complete!" -ForegroundColor Cyan
Write-Host "Remember: For Kotlin 1.9.22, you should use KSP 1.9.22-1.0.17 and Compose Compiler 1.5.8" -ForegroundColor Cyan
