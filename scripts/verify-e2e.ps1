<#
Prueba manual automatizada: requiere Docker Compose arriba y las tres apps ejecutándose.
Verifica REST -> Kafka listener y Wikimedia local -> Kafka -> MySQL.
#>
$ErrorActionPreference = 'Stop'
Invoke-RestMethod http://localhost:8080/api/messages -Method Post -ContentType 'text/plain' -Body 'mensaje-e2e' | Out-Null
Invoke-RestMethod http://localhost:8080/api/users -Method Post -ContentType 'application/json' -Body '{"id":"e2e-user","name":"E2E","email":"e2e@example.com"}' | Out-Null
for ($i = 0; $i -lt 15; $i++) {
  $events = Invoke-RestMethod http://localhost:8081/api/wikimedia-events
  if ($events.Count -gt 0) { Write-Host "E2E OK: se encontraron $($events.Count) eventos persistidos."; exit 0 }
  Start-Sleep -Seconds 2
}
throw 'No llegó un evento a MySQL. Revisá docker compose logs y los tres servicios.'
