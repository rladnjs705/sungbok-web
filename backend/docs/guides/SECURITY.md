# Security Best Practices

## Phase 4 Security Improvements

This document outlines the security measures implemented in Phase 4 of the development.

## 1. JWT Secret Enforcement

### ❌ Before (Insecure)
```java
@Value("${jwt.secret:default-secret-key}") String secret
```
- Had a default fallback value
- Could run in production without proper configuration
- Security vulnerability

### ✅ After (Secure)
```java
@Value("${jwt.secret}") String secret
```
- **No default value** - Application will fail to start if not configured
- Validates secret is at least 256 bits (32 characters)
- Throws clear error message if secret is missing or too short

### Configuration
Set the `JWT_SECRET` environment variable:

```bash
# Generate a secure secret (recommended)
openssl rand -base64 48

# Set environment variable
export JWT_SECRET="your-generated-secret-key-at-least-32-characters-long"
```

## 2. Production Profile

### Application Profiles

- **Development** (`application.yml`)
  - SQL logging enabled
  - Debug level logging
  - Longer JWT expiration (24 hours)

- **Production** (`application-prod.yml`)
  - SQL logging disabled
  - INFO/WARN level logging
  - Shorter JWT expiration (1 hour)
  - Graceful shutdown enabled
  - Error messages hidden from API responses
  - Connection pooling optimized

### Activate Production Profile

```bash
export SPRING_PROFILES_ACTIVE=prod
```

Or via JVM argument:
```bash
java -jar -Dspring.profiles.active=prod sungbok-church-backend.jar
```

## 3. Phase 2/3 Security Improvements

### Race Condition Prevention
- `@Modifying` queries with `@Query` for atomic updates
- `EntityManager.refresh()` after counter increments
- Prevents concurrent update issues

Example:
```java
// Atomic view count increment
@Modifying
@Query("UPDATE Notice n SET n.viewCount = n.viewCount + 1 WHERE n.id = :id")
void incrementViewCount(@Param("id") Long id);

// Refresh entity to get updated value
entityManager.refresh(notice);
```

### Delete Safety
- **Always check existence** before deletion
- Prevents silent failures
- Clear error messages

Example:
```java
if (!repository.existsById(id)) {
    throw new IllegalArgumentException("Resource not found: " + id);
}
repository.deleteById(id);
```

### N+1 Query Prevention
- Use `@EntityGraph` for eager fetching related entities
- Keep default `FetchType.LAZY` on relationships
- Apply selective fetching only where needed

Example:
```java
@EntityGraph(attributePaths = {"worship"})
Optional<Sermon> findById(Long id);
```

## 4. Environment Variables

All sensitive configuration is externalized to environment variables:

| Variable | Purpose | Required | Example |
|----------|---------|----------|---------|
| `JWT_SECRET` | JWT signing key | ✅ Yes | `openssl rand -base64 48` |
| `DB_USERNAME` | Database username | ✅ Yes | `dbuser` |
| `DB_PASSWORD` | Database password | ✅ Yes | `securepassword123` |
| `YOUTUBE_API_KEY` | YouTube API key | ✅ Yes | `AIzaSy...` |
| `CORS_ALLOWED_ORIGINS` | Allowed CORS origins | ✅ Yes (Prod) | `https://example.com` |

See `.env.example` for complete list.

## 5. Health Checks (Actuator)

### Endpoints

- `/actuator/health` - Application health status
- `/actuator/health/liveness` - Kubernetes liveness probe
- `/actuator/health/readiness` - Kubernetes readiness probe
- `/actuator/info` - Application information
- `/actuator/metrics` - Prometheus metrics

### Production Configuration

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: when-authorized  # Hide sensitive info from public
```

### Kubernetes Integration

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  initialDelaySeconds: 30
  periodSeconds: 10

readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 10
  periodSeconds: 5
```

## 6. Logging Best Practices

### Production Logging

- Log level: `INFO` for application, `WARN` for framework
- Log rotation: 30 days retention, 10MB max size
- Structured logging with timestamps
- No sensitive data in logs (passwords, tokens, PII)

### Security Logging

```java
@Slf4j
public class SecurityService {
    
    // ✅ Good - No sensitive data
    log.info("User login attempt: username={}", username);
    
    // ❌ Bad - Exposes sensitive data
    log.info("User login: username={}, password={}", username, password);
}
```

## 7. CORS Configuration

### Development
```yaml
cors:
  allowed-origins: "*"
```

### Production (Restricted)
```yaml
cors:
  allowed-origins: ${CORS_ALLOWED_ORIGINS:https://www.sungbok-church.com}
```

## 8. Database Security

### Connection Pooling
- Maximum pool size: 20 (production)
- Minimum idle: 5
- Connection timeout: 30 seconds
- Max lifetime: 30 minutes

### Schema Management
- Production: `ddl-auto: validate` (never auto-update schema)
- Use Flyway or Liquibase for production migrations

## Deployment Checklist

Before deploying to production:

- [ ] Set all required environment variables
- [ ] Generate strong JWT secret (minimum 32 characters)
- [ ] Configure database credentials
- [ ] Set `SPRING_PROFILES_ACTIVE=prod`
- [ ] Configure CORS allowed origins
- [ ] Set up log directory with proper permissions
- [ ] Configure reverse proxy (Nginx) with HTTPS
- [ ] Enable firewall rules
- [ ] Set up monitoring and alerting
- [ ] Configure backup strategy
- [ ] Document incident response procedures
- [ ] Review and test health check endpoints
- [ ] Verify graceful shutdown behavior

## Security Contact

For security issues, please contact: security@sungbok-church.com

Do not file public issues for security vulnerabilities.
