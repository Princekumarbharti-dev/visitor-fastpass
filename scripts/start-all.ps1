param(
    [switch]$SkipBuild
)

$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$runDirectory = Join-Path $root '.run'
New-Item -ItemType Directory -Path $runDirectory -Force | Out-Null

if (-not $SkipBuild) {
    & (Join-Path $root 'mvnw.cmd') -q package -DskipTests
    if ($LASTEXITCODE -ne 0) { throw 'Maven build failed' }
}

function Wait-ForPort([int]$Port, [string]$Name, [int]$Seconds = 60) {
    for ($attempt = 0; $attempt -lt $Seconds; $attempt++) {
        try {
            $response = Invoke-WebRequest -Uri "http://localhost:$Port/actuator/health" -UseBasicParsing -TimeoutSec 1
            if ($response.StatusCode -eq 200) {
                Write-Host "[UP] $Name on port $Port" -ForegroundColor Green
                return
            }
        } catch { }
        Start-Sleep -Seconds 1
    }
    throw "$Name did not become healthy on port $Port. Check $runDirectory\$Name.err.log"
}

function Start-App([string]$Name, [string]$Jar, [int]$Port) {
    $pidFile = Join-Path $runDirectory "$Name.pid"
    if (Test-Path $pidFile) {
        $existingPid = [int](Get-Content $pidFile)
        if (Get-Process -Id $existingPid -ErrorAction SilentlyContinue) {
            Write-Host "[SKIP] $Name is already running as PID $existingPid" -ForegroundColor Yellow
            return
        }
    }
    $process = Start-Process -FilePath java -ArgumentList '-jar', (Join-Path $root $Jar) `
        -WorkingDirectory $root `
        -RedirectStandardOutput (Join-Path $runDirectory "$Name.out.log") `
        -RedirectStandardError (Join-Path $runDirectory "$Name.err.log") `
        -WindowStyle Hidden -PassThru
    Set-Content -Path $pidFile -Value $process.Id
    Write-Host "[START] $Name as PID $($process.Id)"
}

$mysql = Test-NetConnection -ComputerName localhost -Port 3306 -WarningAction SilentlyContinue
if (-not $mysql.TcpTestSucceeded) { throw 'MySQL is not reachable on localhost:3306' }
$kafka = Test-NetConnection -ComputerName localhost -Port 9092 -WarningAction SilentlyContinue
if (-not $kafka.TcpTestSucceeded) { throw 'Kafka is not reachable on localhost:9092' }

Start-App 'discovery-server' 'discovery-server\target\discovery-server-0.1.0-SNAPSHOT.jar' 8761
Wait-ForPort 8761 'discovery-server'

Start-App 'config-server' 'config-server\target\config-server-0.1.0-SNAPSHOT.jar' 8888
Wait-ForPort 8888 'config-server'

Start-App 'identity-service' 'identity-service\target\identity-service-0.1.0-SNAPSHOT.jar' 8081
Start-App 'pass-service' 'pass-service\target\pass-service-0.1.0-SNAPSHOT.jar' 8083
Start-App 'visitor-service' 'visitor-service\target\visitor-service-0.1.0-SNAPSHOT.jar' 8082
Start-App 'notification-service' 'notification-service\target\notification-service-0.1.0-SNAPSHOT.jar' 8084

Wait-ForPort 8081 'identity-service' 90
Wait-ForPort 8083 'pass-service' 90
Wait-ForPort 8082 'visitor-service' 90
Wait-ForPort 8084 'notification-service' 90

Start-App 'api-gateway' 'api-gateway\target\api-gateway-0.1.0-SNAPSHOT.jar' 8080
Wait-ForPort 8080 'api-gateway' 90

Write-Host 'Visitor FastPass backend is ready at http://localhost:8080' -ForegroundColor Cyan
