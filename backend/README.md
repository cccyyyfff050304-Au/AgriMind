# AgriMind Backend

Spring Boot backend skeleton for the AgriMind smart agriculture platform.

## Requirements

- JDK 17
- Maven 3.9+

The current machine has JDK 17 at `D:\jdk-17.0.15`. If your default `java -version` is not 17, set it in the current PowerShell session:

```powershell
$env:JAVA_HOME='D:\jdk-17.0.15'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
```

## Run

```powershell
cd D:\AgriMind\backend
mvn spring-boot:run
```

By default the project activates the `dev` profile. Create a local `src/main/resources/application-dev.yml` from the example file, or provide the same values through environment variables.

## Health Check

```powershell
Invoke-RestMethod http://localhost:8080/api/health
```

## Database Ping

After MySQL is running and `agri_mind` has been created, test the backend database connection:

```powershell
Invoke-RestMethod http://localhost:8080/api/db/ping
```

Expected response:

```json
{
  "code": 200,
  "message": "success",
  "data": "Database connection is OK"
}
```

Expected response:

```json
{
  "code": 200,
  "message": "success",
  "data": "AgriMind backend is running"
}
```

## Authentication

Configure a local JWT secret before testing auth endpoints. The secret must be at least 32 characters.

```powershell
$env:AGRIMIND_JWT_SECRET='replace-with-at-least-32-characters'
$env:AGRIMIND_JWT_EXPIRATION_SECONDS='86400'
```

Register:

```powershell
$body = @{
  username = 'demo_user'
  password = 'demo123456'
  realName = 'Demo User'
} | ConvertTo-Json

Invoke-RestMethod http://localhost:8080/api/auth/register `
  -Method Post `
  -ContentType 'application/json' `
  -Body $body
```

Login:

```powershell
$body = @{
  username = 'demo_user'
  password = 'demo123456'
} | ConvertTo-Json

$login = Invoke-RestMethod http://localhost:8080/api/auth/login `
  -Method Post `
  -ContentType 'application/json' `
  -Body $body
```

Current user:

```powershell
Invoke-RestMethod http://localhost:8080/api/user/profile `
  -Headers @{ Authorization = "Bearer $($login.data.token)" }
```

## API Docs

After the service starts, open:

- `http://localhost:8080/swagger-ui.html`
- `http://localhost:8080/v3/api-docs`

## Local Development Config

`src/main/resources/application-dev.example.yml` is only a sample. Do not commit real local configuration.

When database and Redis integration starts, copy it locally:

```powershell
Copy-Item src/main/resources/application-dev.example.yml src/main/resources/application-dev.yml
```

Then replace values through environment variables such as `AGRIMIND_DB_PASSWORD` and API keys. Never hard-code secrets in committed files.
