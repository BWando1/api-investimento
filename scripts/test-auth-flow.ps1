$ErrorActionPreference = 'Stop'

function Get-Token {
    param(
        [string]$Username,
        [string]$Password
    )

    $body = @{
        client_id     = 'api-investimento'
        client_secret = 'api-secret'
        grant_type    = 'password'
        username      = $Username
        password      = $Password
    }

    for ($attempt = 1; $attempt -le 20; $attempt++) {
        try {
            $resp = Invoke-RestMethod -Method Post -Uri 'http://localhost:8180/realms/investimentos/protocol/openid-connect/token' -ContentType 'application/x-www-form-urlencoded' -Body $body
            return $resp.access_token
        } catch {
            if ($attempt -eq 20) {
                throw
            }
            Start-Sleep -Seconds 2
        }
    }
}

function Invoke-Endpoint {
    param(
        [string]$Name,
        [string]$Uri,
        [string]$Token,
        [int]$Expected
    )

    $headers = @{ Authorization = "Bearer $Token" }

    try {
        $resp = Invoke-WebRequest -Uri $Uri -Headers $headers -Method Get
        $status = [int]$resp.StatusCode
    } catch {
        if ($_.Exception.Response -ne $null) {
            $status = [int]$_.Exception.Response.StatusCode
        } else {
            throw
        }
    }

    $result = if ($status -eq $Expected) { 'PASS' } else { 'FAIL' }
    Write-Output ("$result | $Name | status=$status expected=$Expected")
}

function Invoke-PostEndpoint {
    param(
        [string]$Name,
        [string]$Uri,
        [string]$Token,
        [string]$JsonBody,
        [int]$Expected
    )

    $headers = @{ Authorization = "Bearer $Token" }

    try {
        $resp = Invoke-WebRequest -Uri $Uri -Headers $headers -Method Post -ContentType 'application/json' -Body $JsonBody
        $status = [int]$resp.StatusCode
    } catch {
        if ($_.Exception.Response -ne $null) {
            $status = [int]$_.Exception.Response.StatusCode
        } else {
            throw
        }
    }

    $result = if ($status -eq $Expected) { 'PASS' } else { 'FAIL' }
    Write-Output ("$result | $Name | status=$status expected=$Expected")
}

Write-Output '=== AUTH FLOW TEST START ==='

$tokenUsuario = Get-Token -Username 'usuario1' -Password '123456'
Write-Output ("PASS | token usuario1 | length=" + $tokenUsuario.Length)

$tokenAnalista = Get-Token -Username 'analista1' -Password '123456'
Write-Output ("PASS | token analista1 | length=" + $tokenAnalista.Length)

Invoke-Endpoint -Name 'perfil-risco com usuario' -Uri 'http://localhost:8080/perfil-risco/999' -Token $tokenUsuario -Expected 200
Invoke-Endpoint -Name 'telemetria com usuario' -Uri 'http://localhost:8080/telemetria' -Token $tokenUsuario -Expected 403
Invoke-Endpoint -Name 'telemetria com analista' -Uri 'http://localhost:8080/telemetria' -Token $tokenAnalista -Expected 200

$simulacaoValida = @'
{
    "clienteId": 999,
    "valor": 10000.00,
    "prazoMeses": 12,
    "tipoProduto": "CDB"
}
'@

$simulacaoInvalida = @'
{
    "clienteId": 999,
    "valor": 0,
    "prazoMeses": 0,
    "tipoProduto": ""
}
'@

Invoke-PostEndpoint -Name 'simular-investimento valido com usuario' -Uri 'http://localhost:8080/simular-investimento' -Token $tokenUsuario -JsonBody $simulacaoValida -Expected 200
Invoke-PostEndpoint -Name 'simular-investimento invalido com usuario' -Uri 'http://localhost:8080/simular-investimento' -Token $tokenUsuario -JsonBody $simulacaoInvalida -Expected 400

Write-Output '=== AUTH FLOW TEST END ==='
