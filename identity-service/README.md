# Identity & Employee Service

Owns authentication, JWT issuance, employee accounts, roles, authorities, and safe host discovery.

## Local configuration

Required environment variables outside local development:

```text
IDENTITY_DB_URL
IDENTITY_DB_USERNAME
IDENTITY_DB_PASSWORD
APP_JWT_SECRET
```

`APP_JWT_SECRET` must contain at least 32 bytes and must be identical in services that validate these JWTs.

## Public endpoints

- `POST /api/v1/auth/login`
- `GET /api/v1/public/hosts`
- OpenAPI/Swagger endpoints
- `GET /actuator/health`

All remaining endpoints require JWT authentication and method-level authorities.

## Local bootstrap accounts

The local defaults are `admin / Admin@123`, `asha.host / Host@123`, and
`reception / Reception@123`. Set `APP_BOOTSTRAP_ENABLED=false` outside development and override
all bootstrap passwords through environment variables.

## API documentation

When running locally: `http://localhost:8081/swagger-ui.html`
