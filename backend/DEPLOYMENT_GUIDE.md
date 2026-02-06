# Church Backend API - Production Deployment Guide

> **Purpose**: Step-by-step guide for deploying the Church Backend API to production
>
> **Status**: Production Ready
> **Date**: 2026-02-04
> **Quality Score**: 95/100
> **Design Match Rate**: 99%

---

## Table of Contents

1. [Pre-Deployment Checklist](#pre-deployment-checklist)
2. [Environment Configuration](#environment-configuration)
3. [Deployment Steps](#deployment-steps)
4. [Post-Deployment Verification](#post-deployment-verification)
5. [Monitoring & Alerts](#monitoring--alerts)
6. [Rollback Procedures](#rollback-procedures)
7. [Troubleshooting](#troubleshooting)

---

## Pre-Deployment Checklist

### Code & Quality Verification

- [x] All unit tests passing (67/67)
- [x] Code quality score: 95/100
- [x] Design match rate: 99%
- [x] Zero critical security issues
- [x] Production profile configured
- [x] Health checks operational
- [x] Error handling standardized
- [x] Environment variables documented
- [x] Security documentation complete
- [x] PDCA cycle completed

### Infrastructure Preparation

- [ ] PostgreSQL database created and tested
- [ ] Database user with appropriate permissions created
- [ ] Redis/Valkey cache configured (optional)
- [ ] SSL/TLS certificates obtained and installed
- [ ] Reverse proxy (nginx/Apache) configured
- [ ] Log directory created with proper permissions
- [ ] Backup strategy defined and tested
- [ ] Monitoring system configured (Prometheus/Grafana)
- [ ] Alerting rules configured
- [ ] Disaster recovery procedure documented

### Security Requirements

- [ ] JWT_SECRET generated and secured
- [ ] Database credentials secured
- [ ] API keys (YouTube, etc.) secured
- [ ] CORS origins configured for production domain
- [ ] Firewall rules configured
- [ ] DDoS protection enabled
- [ ] SSL/TLS enabled (HTTPS only)
- [ ] Security headers configured
- [ ] API rate limiting configured
- [ ] Audit logging enabled

### Stakeholder Communication

- [ ] Deployment window scheduled
- [ ] Team notified of deployment time
- [ ] Rollback procedure reviewed with team
- [ ] Monitoring dashboard prepared
- [ ] On-call person assigned
- [ ] Client notification prepared

---

## Environment Configuration

### 1. Generate Secure JWT Secret

```bash
# Generate 48-character random string (288+ bits)
openssl rand -base64 48

# Output example:
# abc1234567890defghijklmnopqrstuvwxyzABCDEFGHIJ+/==

# Store in secure location (e.g., AWS Secrets Manager, HashiCorp Vault)
```

### 2. Create Environment Variables

Create `.env` file (never commit to version control):

```bash
# JWT Configuration
JWT_SECRET=<your-generated-secret-from-above>
JWT_EXPIRATION=3600000

# Database Configuration
DB_URL=jdbc:postgresql://db.production.local:5432/sungbok_church
DB_USERNAME=sungbok_user
DB_PASSWORD=<secure-database-password>
DB_POOL_SIZE=20
DB_POOL_MIN_IDLE=5

# YouTube API Configuration (if livestreaming enabled)
YOUTUBE_API_KEY=<your-youtube-api-key>
YOUTUBE_CHANNEL_ID=<your-channel-id>

# CORS Configuration
CORS_ALLOWED_ORIGINS=https://www.sungbok-church.com,https://admin.sungbok-church.com

# Spring Profile
SPRING_PROFILES_ACTIVE=prod

# Server Configuration
SERVER_PORT=8080
SERVER_SERVLET_CONTEXT_PATH=/api

# Logging Configuration
LOGGING_FILE_NAME=/var/log/sungbok-church/application.log
LOGGING_FILE_MAX_SIZE=10MB
LOGGING_FILE_MAX_HISTORY=30

# Application Information
APP_NAME=sungbok-church-api
APP_VERSION=1.0.0
```

### 3. Database Connection Verification

```bash
# Test PostgreSQL connection
psql -h db.production.local \
     -U sungbok_user \
     -d sungbok_church \
     -c "SELECT version();"

# Expected output: PostgreSQL version information
```

### 4. Log Directory Setup

```bash
# Create log directory
sudo mkdir -p /var/log/sungbok-church

# Set permissions (run as application user)
sudo chown application_user:application_group /var/log/sungbok-church
sudo chmod 755 /var/log/sungbok-church

# Verify permissions
ls -la /var/log/sungbok-church
```

---

## Deployment Steps

### Step 1: Build Application

```bash
# Clone repository
git clone https://github.com/sungbok-church/backend.git
cd backend

# Build with Gradle
./gradlew clean build

# Output: build/libs/application-1.0.0.jar
```

### Step 2: Transfer to Server

```bash
# Using SCP
scp build/libs/application-1.0.0.jar \
    deployment@production.local:/opt/sungbok-church/

# Or using SSH deploy key:
rsync -avz --delete \
      -e "ssh -i ~/.ssh/deploy_key" \
      build/libs/application-1.0.0.jar \
      deployment@production.local:/opt/sungbok-church/
```

### Step 3: Stop Current Service

```bash
# SSH into production server
ssh deployment@production.local

# Stop running service
sudo systemctl stop sungbok-church-api

# Verify stopped
sudo systemctl status sungbok-church-api

# Expected: "inactive (dead)"
```

### Step 4: Backup Current Version

```bash
# Create timestamped backup
BACKUP_DIR="/opt/sungbok-church/backups/$(date +%Y%m%d_%H%M%S)"
sudo mkdir -p $BACKUP_DIR
sudo cp /opt/sungbok-church/current.jar $BACKUP_DIR/

# Verify backup
ls -la $BACKUP_DIR/
```

### Step 5: Deploy New Version

```bash
# Link new JAR as current
sudo ln -sf /opt/sungbok-church/application-1.0.0.jar \
           /opt/sungbok-church/current.jar

# Set permissions
sudo chown application_user:application_group /opt/sungbok-church/current.jar
sudo chmod 755 /opt/sungbok-church/current.jar
```

### Step 6: Start Service

```bash
# Load environment variables
source /opt/sungbok-church/.env

# Start service
sudo systemctl start sungbok-church-api

# Wait for startup (typically 10-30 seconds)
sleep 15

# Verify service started
sudo systemctl status sungbok-church-api
sudo journalctl -u sungbok-church-api -n 50 --no-pager

# Expected logs:
# - "Tomcat started on port(s): 8080"
# - "Started SungbokChurchApplication"
```

### Step 7: Systemd Service Configuration

If not already configured, create `/etc/systemd/system/sungbok-church-api.service`:

```ini
[Unit]
Description=Sungbok Church API
After=network.target postgresql.service

[Service]
User=application_user
Group=application_group
Type=simple
ExecStart=/usr/bin/java -jar /opt/sungbok-church/current.jar
EnvironmentFile=/opt/sungbok-church/.env
Restart=on-failure
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=sungbok-church

[Install]
WantedBy=multi-user.target
```

Enable and reload:
```bash
sudo systemctl daemon-reload
sudo systemctl enable sungbok-church-api
```

---

## Post-Deployment Verification

### 1. Health Check Endpoints

```bash
# Overall health check
curl -s http://localhost:8080/actuator/health | jq

# Expected response:
# {
#   "status": "UP",
#   "components": {
#     "db": {"status": "UP"},
#     "livenessState": {"status": "UP"},
#     "readinessState": {"status": "UP"}
#   }
# }

# Liveness probe (for Kubernetes)
curl -s http://localhost:8080/actuator/health/liveness | jq

# Readiness probe (for Kubernetes)
curl -s http://localhost:8080/actuator/health/readiness | jq
```

### 2. Service Verification Tests

```bash
# Test NoticeService endpoint
curl -s http://localhost:8080/api/notices | jq

# Test health endpoint with authentication (if required)
curl -s -H "Authorization: Bearer <JWT_TOKEN>" \
     http://localhost:8080/api/notices | jq

# Check response time
time curl -s http://localhost:8080/actuator/health > /dev/null
# Expected: < 100ms
```

### 3. Database Connectivity

```bash
# Query health endpoint which includes DB check
curl -s http://localhost:8080/actuator/health/db | jq

# Expected: Database should show "UP" status
```

### 4. Log Verification

```bash
# Check application logs
sudo tail -f /var/log/sungbok-church/application.log

# Look for:
# - "Started SungbokChurchApplication"
# - "Tomcat initialized"
# - No ERROR or WARN messages

# Check system logs
sudo journalctl -u sungbok-church-api -f
```

### 5. Security Verification

```bash
# Verify JWT secret is not using default
curl -s http://localhost:8080/actuator/env | jq '.propertySources[] | select(.name | contains("jwt"))'

# Should show: JWT secret is configured (not shown in output for security)
# If not configured, service will show startup error

# Verify error details are hidden
curl -s http://localhost:8080/api/invalid-endpoint | jq

# Expected: No stack trace, generic error message
```

### 6. Metrics Collection

```bash
# Verify Prometheus metrics available
curl -s http://localhost:8080/actuator/metrics | jq

# Verify specific metrics
curl -s http://localhost:8080/actuator/metrics/http.server.requests | jq

# Check memory usage
curl -s http://localhost:8080/actuator/metrics/jvm.memory.used | jq
```

---

## Monitoring & Alerts

### 1. Configure Prometheus Scraping

Create `/etc/prometheus/targets.d/sungbok-church.yml`:

```yaml
- job_name: 'sungbok-church-api'
  metrics_path: '/actuator/metrics'
  static_configs:
    - targets: ['localhost:8080']
  scrape_interval: 30s
  scrape_timeout: 10s
```

Reload Prometheus:
```bash
sudo systemctl reload prometheus
```

### 2. Key Metrics to Monitor

```
jvm.memory.used              # JVM memory usage
jvm.memory.max               # Maximum JVM memory
jvm.gc.memory.allocated      # Garbage collection
http.server.requests         # HTTP request metrics
jdbc.connections.max         # Database connection pool
jdbc.connections.active      # Active connections
```

### 3. Create Grafana Dashboards

Import dashboard JSON or create from metrics:
- Memory usage trend
- Request latency (p50, p95, p99)
- Error rate (4xx, 5xx)
- Database connection pool
- JVM garbage collection

### 4. Alert Rules

Create `/etc/prometheus/rules/sungbok-church.yml`:

```yaml
groups:
  - name: sungbok-church-alerts
    interval: 30s
    rules:
      - alert: SungbokChurchDown
        expr: up{job="sungbok-church-api"} == 0
        for: 1m
        annotations:
          summary: "Sungbok Church API is down"

      - alert: HighMemoryUsage
        expr: jvm_memory_used_bytes{job="sungbok-church-api"} > 0.9 * jvm_memory_max_bytes
        for: 5m
        annotations:
          summary: "High JVM memory usage"

      - alert: HighErrorRate
        expr: rate(http_server_requests_seconds_count{status=~"5..", job="sungbok-church-api"}[5m]) > 0.05
        for: 2m
        annotations:
          summary: "High error rate detected"

      - alert: SlowRequests
        expr: histogram_quantile(0.95, http_server_requests_seconds_bucket{job="sungbok-church-api"}) > 1
        for: 5m
        annotations:
          summary: "p95 request latency > 1s"
```

### 5. Set Up Alerting Channels

Configure alert delivery:
- Email notifications
- Slack channel messages
- PagerDuty integration
- SMS for critical alerts (optional)

---

## Rollback Procedures

### Automatic Rollback (Systemd)

Service automatically restarts on failure:
```
RestartSec=10    # Wait 10 seconds between restarts
Restart=on-failure
```

### Manual Rollback

```bash
# SSH into production server
ssh deployment@production.local

# Stop current version
sudo systemctl stop sungbok-church-api

# List available backups
ls -la /opt/sungbok-church/backups/

# Restore previous version
BACKUP_JAR=/opt/sungbok-church/backups/20260204_120000/current.jar
sudo cp $BACKUP_JAR /opt/sungbok-church/current.jar

# Start previous version
sudo systemctl start sungbok-church-api

# Verify
sudo systemctl status sungbok-church-api
curl -s http://localhost:8080/actuator/health | jq
```

### Database Rollback

If schema migration needed (not applicable for validate mode):

```bash
# With validate mode, schema changes are prevented
# Restore from backup if data was corrupted

# Backup database before any deployment
pg_dump -h db.production.local \
        -U sungbok_user \
        -d sungbok_church > /backups/sungbok_church_$(date +%Y%m%d_%H%M%S).sql

# Restore from backup if needed
psql -h db.production.local \
     -U sungbok_user \
     -d sungbok_church < /backups/sungbok_church_backup.sql
```

---

## Troubleshooting

### Application Won't Start

**Symptom**: Service status shows "inactive (dead)"

**Debugging Steps**:
```bash
# Check system logs
sudo journalctl -u sungbok-church-api -n 100 --no-pager

# Check application logs
sudo tail -200 /var/log/sungbok-church/application.log

# Common issues:
# 1. JWT_SECRET not set
#    → Set JWT_SECRET environment variable
#
# 2. Database connection failed
#    → Verify DB_URL, DB_USERNAME, DB_PASSWORD
#    → Check PostgreSQL is running
#    → Test connection: psql -h db.host -U user -d db_name
#
# 3. Port already in use
#    → Change SERVER_PORT or stop conflicting process
#    → Check: lsof -i :8080
#
# 4. Insufficient permissions
#    → Verify log directory permissions
#    → Verify JAR file permissions
#    → Run: sudo chown -R application_user:application_group /opt/sungbok-church
```

### High Memory Usage

**Symptom**: JVM memory approaching max

**Investigation**:
```bash
# Monitor memory usage
curl -s http://localhost:8080/actuator/metrics/jvm.memory.used | jq

# Check garbage collection activity
curl -s http://localhost:8080/actuator/metrics/jvm.gc.memory.allocated | jq

# View heap dump (if necessary)
jmap -dump:live,format=b,file=heap.bin <PID>

# Solution:
# 1. Increase max heap size:
#    export JAVA_OPTS="-Xmx1024m"
#
# 2. Monitor for memory leaks
#    Check application logs for repeated objects
#
# 3. Optimize query results
#    Ensure pagination is used for large result sets
```

### Slow Requests

**Symptom**: Response time > 1 second

**Investigation**:
```bash
# Check request latency percentiles
curl -s http://localhost:8080/actuator/metrics/http.server.requests | jq

# Enable query logging (development profile only)
export SPRING_JPA_SHOW_SQL=true

# Check database query performance
EXPLAIN ANALYZE SELECT * FROM notice ORDER BY created_at DESC LIMIT 10;

# Solution:
# 1. Add database indexes (check SECURITY.md)
# 2. Enable Redis caching
# 3. Optimize query using @EntityGraph
# 4. Implement pagination for large result sets
```

### Database Connection Pool Exhausted

**Symptom**: "Cannot get a connection from the pool"

**Investigation**:
```bash
# Check active connections
curl -s http://localhost:8080/actuator/metrics/jdbc.connections.active | jq

# Check connection pool size
curl -s http://localhost:8080/actuator/metrics/jdbc.connections.max | jq

# Check service logs for long-running queries
sudo grep "Query took" /var/log/sungbok-church/application.log

# Solution:
# 1. Increase pool size (DB_POOL_SIZE)
#    Check application-prod.yml: maximum-pool-size
#
# 2. Reduce connection timeout (idle-timeout)
#    Check application-prod.yml: idle-timeout
#
# 3. Close idle connections
#    Application logs should show connection cleanup
#
# 4. Optimize query performance
#    Look for N+1 problems or missing indexes
```

### JWT Authentication Fails

**Symptom**: 401 Unauthorized on API calls

**Investigation**:
```bash
# Verify JWT is being sent
curl -v -H "Authorization: Bearer <token>" http://localhost:8080/api/notices

# Check token expiration
# JWT tokens expire after 3600000ms (1 hour) in production

# Verify JWT_SECRET configuration
curl -s http://localhost:8080/actuator/env | grep -i jwt

# Solution:
# 1. Ensure JWT_SECRET is properly set
#    Re-deploy with correct environment variables
#
# 2. Check token expiration
#    Generate new token if older than 1 hour
#
# 3. Verify client sends Authorization header
#    Format: "Authorization: Bearer <token>"
```

---

## Verification Checklist

After deployment, verify all items:

- [ ] Service status: `systemctl status sungbok-church-api` shows "active"
- [ ] Health check: `curl http://localhost:8080/actuator/health` returns UP
- [ ] Database: Health check shows DB connection UP
- [ ] Logs: No ERROR messages in `/var/log/sungbok-church/application.log`
- [ ] API: All endpoints return proper responses
- [ ] Security: JWT enforcement working
- [ ] Monitoring: Prometheus scraping metrics
- [ ] Alerts: Alert system configured and tested
- [ ] Performance: Request latency within acceptable range
- [ ] Error handling: Invalid requests return structured errors

---

## Support

### Emergency Contact

- **On-call Engineer**: [Name/Phone]
- **DevOps Team**: [Contact Info]
- **Database Team**: [Contact Info]

### Documentation References

- [Security Best Practices](SECURITY.md)
- [PDCA Completion Report](docs/04-report/features/church.report.md)
- [Phase 4 Report](PHASE4_COMPLETION_REPORT.md)
- [API Documentation](docs/api/) (Swagger/OpenAPI)

### Issue Reporting

1. Describe the issue
2. Include error logs and timestamps
3. Note when issue started
4. Include steps to reproduce
5. Reference relevant service/endpoint

---

## Version Control

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | 2026-02-04 | Initial deployment guide |

**Last Updated**: 2026-02-04
**Status**: APPROVED
**Next Review**: After first production deployment
