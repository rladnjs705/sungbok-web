# Phase 4 Dependencies

## Required Maven Dependencies

Add the following dependencies to `pom.xml`:

```xml
<!-- Spring Boot Cache -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>

<!-- Caffeine Cache -->
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
</dependency>
```

## Installation

1. Open `backend/pom.xml`
2. Add the dependencies above to the `<dependencies>` section
3. Run `mvn clean install` to download dependencies

## Verification

After adding dependencies, verify by running:

```bash
cd backend
mvn dependency:tree | grep caffeine
```

Expected output:
```
[INFO] +- com.github.ben-manes.caffeine:caffeine:jar:3.x.x:compile
```

## Alternative: Spring Boot Already Includes Cache Starter

If you're using Spring Boot 3.x+, the cache starter might already be included.
Check your current dependencies:

```bash
mvn dependency:list | grep cache
```

If `spring-boot-starter-cache` is already present, you only need to add Caffeine.
