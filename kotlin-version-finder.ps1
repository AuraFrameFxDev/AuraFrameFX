Get-ChildItem -Path . -Recurse -Include *.gradle*, *.kt, *.toml, *.properties |
        Select-String -Pattern "kotlin.*2\.1|kotlin\s*=|kotlinVersion" |
        Format-Table -Property Path, LineNumber, Line -AutoSize
