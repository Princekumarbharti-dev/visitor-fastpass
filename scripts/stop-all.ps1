$root = Split-Path -Parent $PSScriptRoot
$runDirectory = Join-Path $root '.run'

if (-not (Test-Path $runDirectory)) {
    Write-Host 'No runtime directory found.'
    exit 0
}

Get-ChildItem -Path $runDirectory -Filter '*.pid' | ForEach-Object {
    $service = $_.BaseName
    $processId = [int](Get-Content $_.FullName)
    $process = Get-Process -Id $processId -ErrorAction SilentlyContinue
    if ($process) {
        Stop-Process -Id $processId -Force
        Write-Host "[STOPPED] $service (PID $processId)"
    }
    Remove-Item -LiteralPath $_.FullName -Force
}
