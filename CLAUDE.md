# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

SocialMeet is a complete social dating application consisting of a Spring Boot backend service and an Android client. The project has been optimized to reduce code redundancy and improve maintainability.

## Build and Run Commands

### Backend (Spring Boot)

```bash
# Start backend service
cd SocialMeet
gradlew bootRun

# Build JAR file
gradlew bootJar

# Clean build
gradlew clean build

# Skip tests (tests are disabled by default in build.gradle.kts)
```

The backend runs on `http://localhost:8080` by default.

### Android Client

```bash
# Build debug APK
gradlew assembleDebug

# Install to connected device
gradlew installDebug

# Clean build
gradlew clean

# Build specific architecture
gradlew assembleArm64-v8aDebug    # 64-bit ARM
gradlew assembleArmeabi-v7aDebug  # 32-bit ARM
```

### Unified Management Scripts

```bash
# Backend operations
scripts\unified_management.bat start-backend
scripts\unified_management.bat clean-build

# Android operations
scripts\unified_management.bat start-emulator
scripts\unified_management.bat build-app
scripts\unified_management.bat install-app

# Testing
scripts\unified_management.bat test-api
scripts\unified_management.bat test-basic
scripts\unified_management.bat test-auth

# Using Python test suite directly
python scripts\unified_test_suite.py --verbose
python scripts\unified_test_suite.py --test basic
```

## Architecture Overview

### Backend Architecture (SocialMeet/)

The backend follows a layered Spring Boot architecture:

- **Controller Layer** (`controller/`): REST API endpoints, handles HTTP requests
- **Service Layer** (`service/`): Business logic and transaction management
- **Repository Layer** (`repository/`): JPA repositories for data access
- **Entity Layer** (`entity/`): JPA entities mapping to database tables
- **DTO Layer** (`dto/`): Data Transfer Objects for API communication
- **WebSocket** (`websocket/`): Real-time messaging support
- **Config** (`config/`): Spring configuration classes including:
  - `SecurityConfig`: JWT authentication and authorization
  - `WebConfig`: CORS and web MVC configuration
  - `JPushConfig`: Push notification setup
  - `PaymentConfig`: Alipay and WeChat payment configuration
  - `DynamicDatabaseConfig`: Dynamic database configuration management

Key architectural patterns:
- **JWT Authentication**: Token-based auth implemented in `SecurityConfig` and `JwtAuthenticationEntryPoint`
- **WebSocket Communication**: Real-time messaging through Spring WebSocket
- **Multi-tenancy Support**: Dynamic configuration through `DynamicDatabaseConfig` and `DynamicCacheConfig`
- **Async Processing**: Configured in `AsyncConfig` for non-blocking operations

### Android Architecture (app/)

The Android app uses a modern architecture combining traditional Android Views with Jetpack Compose:

- **Network Layer** (`network/`):
  - `RetrofitClient`: Singleton Retrofit instance
  - `ApiService`: Main REST API interface
  - `NetworkService`: Kotlin coroutine-based service
  - `PostApiService`: Dedicated API for social posts

- **UI Layer** (`ui/`):
  - `screens/`: Jetpack Compose screens
  - `components/`: Reusable Compose components
  - `theme/`: Material3 theming

- **Compose Hosts** (`compose/`): Bridge between Activities and Compose:
  - `MessageComposeHost`: Messaging UI
  - `ProfileComposeHost`: Profile screens
  - `SettingsComposeHost`: Settings UI
  - `SquareComposeHost`: Social feed

- **ViewModel Layer** (`viewmodel/`): MVVM pattern for state management

- **Data Layer**:
  - `dto/`: Data Transfer Objects matching backend DTOs
  - `model/`: Local data models

- **Services** (`service/`): Background services for push notifications, etc.

- **Authentication** (`auth/`): Phone-based auth including:
  - One-click login via carrier authentication
  - Aliyun Phone Auth integration
  - Real-person face verification

Key architectural patterns:
- **MVVM with Compose**: ViewModels manage UI state, Compose observes state changes
- **Repository Pattern**: Network layer abstracts data sources
- **Coroutines**: All network operations use Kotlin coroutines
- **Hybrid UI**: Mix of XML-based Views and Jetpack Compose for flexibility

## Database Configuration

The backend uses MySQL 8.0. Configuration is in `SocialMeet/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/socialmeet?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: root
```

Database schema is auto-managed by JPA with `ddl-auto: update`. Major entity tables include:
- User management: `User`, `UserProfile`, `DeviceToken`
- Messaging: `Message`, `ConversationEntity`
- Social features: `Dynamic`, `FollowRelationship`, `Comment`, `DynamicLike`
- Calls: `CallSession`, `CallRecordEntity`, `CallSettings`
- Payment: `Wallet`, `CurrencyTransaction`, `RechargeOrder`
- Gamification: `Gift`, `Coupon`, `VipMembership`, `WealthLevel`

## Key Integration Points

### JWT Authentication Flow

1. User logs in via `AuthController.login()`
2. `AuthService` validates credentials and generates JWT token
3. Token contains user ID and expiration
4. Subsequent requests include token in `Authorization: Bearer <token>` header
5. `JwtAuthenticationFilter` validates token on each request
6. Token parsed by `JwtUtil` to extract user information

### WebSocket Real-time Messaging

1. Client connects to `/ws` endpoint
2. Connection handled by WebSocket configuration
3. Messages routed through message handlers
4. `MessageService` persists messages to database
5. Real-time delivery to online users via WebSocket
6. Offline messages stored for later delivery

### Push Notification System

Backend uses JPush (极光推送):
- Configuration in `application.yml` and `JPushConfig`
- `PushService` handles notification sending
- Device tokens stored in `DeviceToken` entity
- Notifications sent for: new messages, likes, comments, call requests

Android receives notifications through JPush SDK integrated in the app.

### Payment Integration

Two payment methods supported:

1. **Alipay**:
   - Service: `AlipayService`
   - Controller: `AlipayTestController`
   - SDK: alipay-sdk-java

2. **WeChat Pay**:
   - Service: `WeChatPayService`
   - Configuration in `PaymentConfig`

Payment flow:
1. User initiates recharge via `WalletController`
2. Order created in `RechargeOrder` table
3. Payment service generates payment parameters
4. Client completes payment
5. Callback updates order status and wallet balance

### Phone Authentication

Multiple authentication methods:
- **Aliyun Phone Auth**: One-click login via carrier authentication (`AliyunPhoneAuthController`, `AliyunPhoneAuthService`)
- **CMCC (China Mobile)**: Card auth and SIM auth services
- **Face Verification**: Real-person authentication with Aliyun Face SDK

## Configuration Management

### Backend Configuration

Primary config: `SocialMeet/src/main/resources/application.yml`

Active profile: `default` (can be changed via `spring.profiles.active`)

Key configurations:
- Database connection
- JWT secret keys (should be configured)
- JPush credentials
- Aliyun access keys
- Payment service credentials

Use `scripts/unified_config.py` to manage configurations:

```bash
# Save database config
python scripts\unified_config.py --config-type database --action save \
  --db-host localhost --db-port 3306 --db-name socialmeet

# Generate environment configs
python scripts\unified_config.py --generate-env --env development
python scripts\unified_config.py --generate-env --env production
```

### Android Configuration

Build config in `app/build.gradle.kts`:
- `compileSdk = 34`
- `minSdk = 24`
- `targetSdk = 34`

Base URL for API configured in `NetworkConfig.java` - ensure this points to your backend server.

## Testing

### API Testing

```bash
# Full API test suite
python scripts\unified_test_suite.py --verbose

# Specific test categories
python test_api_with_data.py       # API with test data
python test_message_api.py         # Messaging APIs
python test_dynamic_api.py         # Social posts APIs
python test_relationship_api.py    # Friend relationships
python test_wealth_level_api.py    # Wealth system
python test_profile_api.py         # User profiles
```

### Database Testing

```bash
python check_database.py              # Check DB structure
python check_relationship_tables.py   # Verify relationships
python check_user_data.py             # User data validation
```

## Important Notes

### Security Considerations

- JWT secret should be changed from default in production
- All API keys and credentials in `application.yml` should be externalized using environment variables
- Database password should not be hardcoded
- HTTPS should be enabled for production

### Known Limitations

- Redis configuration is commented out in `application.yml` to avoid JPA conflicts
- Payment configurations are partially disabled (`spring.profiles.include: payment` is commented)
- Tests are disabled in backend build by default to speed up builds
- Some carrier SDK features use mock implementations

### Development Workflow

1. Start MySQL database
2. Run backend via `gradlew bootRun` or management script
3. Verify backend at `http://localhost:8080/swagger-ui.html`
4. Update API base URL in Android NetworkConfig if needed
5. Build and install Android app
6. Test features using test scripts or manually

### API Documentation

Swagger UI available at: `http://localhost:8080/swagger-ui.html`
OpenAPI JSON: `http://localhost:8080/api-docs`

### Common Gotchas

- **CORS Issues**: CORS is configured in `WebConfig` - update allowed origins if deploying
- **Token Expiration**: JWT tokens expire after configured time, implement refresh logic
- **WebSocket Connection**: WebSocket requires HTTP upgrade, ensure proxy/firewall allows it
- **File Uploads**: File upload endpoint exists in `FileUploadController` - check size limits
- **Device Permissions**: Android app requires camera, location, audio permissions - handle runtime permissions
- **Gradle Version**: Project uses Gradle 8.9 - wrapper is included, use `gradlew` not global gradle
