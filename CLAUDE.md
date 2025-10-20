# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

SocialMeet is a social dating Android application with a Spring Boot backend. The project consists of two main modules:
- **backend**: Spring Boot 3.3.5 REST API server (Java 21)
- **app**: Android client using Kotlin/Java with Jetpack Compose UI

## Architecture

### Backend Architecture (Spring Boot)

**Technology Stack:**
- Spring Boot 3.3.5 with Spring Data JPA
- MySQL 8.0 database
- JWT authentication (io.jsonwebtoken 0.12.6)
- JPush SDK for push notifications (jiguang-sdk 5.1.17)
- Alipay SDK for payment integration

**Core Service Layers:**

1. **Authentication & User Management**
   - `AuthService`: Handles SMS verification code login/registration, user profile management
   - Test mode enabled by default (verification code fixed at "123456")
   - JWT token generation with 24-hour expiration
   - Auto-registration on first login via phone number

2. **Push Notification System** (`JPushService`)
   - Video/voice call notifications using JPush
   - Multi-device support (legacy single-device mode still supported)
   - Custom notification extras for call metadata (sessionId, callerId, callType)
   - Status update notifications (ACCEPTED/REJECTED/ENDED)

3. **Payment System** (`PaymentService`, `AlipayService`)
   - Alipay integration for virtual currency purchase
   - Order lifecycle management (PENDING → SUCCESS/CANCELLED)
   - Wallet balance updates with transaction tracking
   - Payment callback verification and idempotency

4. **Profile & Wallet System**
   - `ProfileService`: User profile, VIP status, settings management
   - `WalletService`: Virtual currency wallet with recharge/consume tracking
   - `WealthService`: Wealth level system with tiered benefits

5. **Props Mall** (`PropMallService`)
   - Lucky number (靓号) purchasing system
   - Three tiers: LIMITED, SUPER, TOP_TIER
   - Lucky number lifecycle: AVAILABLE → SOLD/RESERVED
   - Expiry management with validity days

6. **WebSocket Signaling** (`SignalingService`)
   - Real-time video call signaling using STOMP over WebSocket
   - Call session management with CallService
   - Message routing between call participants

**Database Schema:**
- Users table with profile, VIP, wealth level, and balance fields
- Wallets table (1:1 with users) for detailed transaction tracking
- User_devices table for multi-device push notification support
- User_settings table for call pricing and permissions
- Payment_orders table for Alipay transactions
- Lucky_numbers table for props mall items

**Key Configuration:**
- Database: `localhost:3306/socialmeet` (root/root)
- Server port: 8080
- JWT secret configured in `application.yml`
- Verification test mode enabled (code: "123456")

### Android App Architecture

**Technology Stack:**
- Kotlin + Java mixed codebase
- Jetpack Compose for modern UI screens
- Traditional XML layouts for legacy screens
- Retrofit + OkHttp for networking
- JPush SDK for receiving push notifications
- VolcEngine RTC SDK for video calling (火山引擎)
- Alipay SDK for payments
- Aliyun SDKs for identity verification (face, ID card, phone)

**Architecture Patterns:**
- MVVM with ViewModel and LiveData
- Repository pattern for data access
- Compose UI with state management
- Service layer for business logic

**Key Components:**

1. **Network Layer** (`NetworkService.kt`, `ApiService.java`)
   - Retrofit-based REST API client
   - JWT token authentication via AuthManager
   - Base URL: `http://10.0.2.2:8080/api` (emulator) or `http://localhost:8080/api`

2. **Push Notifications** (`MyApplication.java`, `JPushReceiver.java`)
   - JPush initialization on app startup
   - Automatic Registration ID upload to backend (multi-device support)
   - Custom receiver for incoming call notifications
   - Notification channel set to IMPORTANCE_NONE (notifications disabled in status bar)

3. **Video Calling**
   - VolcEngine RTC SDK integration (`libs/rtc/VolcEngineRTC.jar`)
   - Call initiation and receiving via CallService
   - Full-screen call activity (VideoChatActivity)

4. **Payment System**
   - `PaymentManager.kt`: Alipay integration
   - `RechargeViewModel.kt`: Virtual currency purchase flow
   - Wallet balance display and management

5. **Compose Screens:**
   - `ProfileScreen.kt`: User profile with Compose UI
   - `PropMallScreen.kt`: Lucky number purchase interface
   - `PropPurchaseScreen.kt`: Purchase confirmation
   - `SquareComposeHost.kt`: Social feed
   - `MessageComposeHost.kt`: Messaging interface

**Third-Party SDK Integration:**
- JPush: AppKey `ff90a2867fcf541a3f3e8ed4`
- VolcEngineRTC: Video calling engine
- Aliyun SDKs: Face verification, ID card OCR, phone auth
- Alipay SDK: Payment processing

## Common Development Tasks

### Backend Development

**Build and Run Backend:**
```bash
# Using Gradle wrapper (Windows)
cd backend
../gradlew clean build
../gradlew :backend:build

# Start the backend server
java -jar backend/build/libs/backend-0.0.1-SNAPSHOT.jar

# Or use the PowerShell script
.\start_backend_with_profile.ps1
```

**Database Setup:**
```bash
# Connect to MySQL
mysql -u root -proot

# Initialize database
mysql -u root -proot < backend/database/init_all_tables.sql

# Initialize wealth levels
mysql -u root -proot < backend/database/init_wealth_levels.sql
```

**Test API Endpoints:**
```bash
# Test profile API
.\test_profile_api.ps1

# Test payment API
.\test_payment_api.ps1

# Test multi-device push
.\test_multidevice_push.ps1
```

**Key Backend Files:**
- `SocialMeetApplication.java` - Main application entry point
- `application.yml` - Configuration (DB, JWT, verification settings)
- Controllers in `backend/src/main/java/com/socialmeet/backend/controller/`
- Services in `backend/src/main/java/com/socialmeet/backend/service/`
- Entities in `backend/src/main/java/com/socialmeet/backend/entity/`

### Android Development

**Build Android App:**
```bash
# Build debug APK
gradlew :app:assembleDebug

# Clean build
gradlew clean
gradlew :app:assembleDebug

# Refresh dependencies
gradlew --refresh-dependencies build
```

**Run on Emulator:**
- Ensure backend is running on `localhost:8080`
- Android emulator uses `10.0.2.2` to access host machine's localhost
- Install APK: `adb install app/build/outputs/apk/debug/app-debug.apk`

**Key Android Files:**
- `MyApplication.java` - App initialization, JPush setup
- `NetworkConfig.java` - Base URL and Retrofit configuration
- `ApiService.java` - API endpoint definitions
- `AuthManager.kt` - JWT token management
- ViewModels in `app/src/main/java/com/example/myapplication/viewmodel/`
- Compose screens in `app/src/main/java/com/example/myapplication/ui/screens/`

**Android Dependencies:**
- Local AAR/JAR files in `app/libs/` (VolcEngineRTC, Aliyun SDKs, JPush)
- Standard dependencies via Maven Central
- Compose BOM for UI components

## Important Implementation Details

### Authentication Flow
1. User enters phone number
2. Backend sends verification code (test mode: always "123456")
3. User verifies code
4. Backend creates user if first login, returns JWT token
5. Client stores token via AuthManager
6. Token included in Authorization header for authenticated requests

### Push Notification Flow
1. App initializes JPush on startup
2. Registration ID obtained and uploaded to backend (with retry logic)
3. Backend stores Registration ID in user_devices table (multi-device support)
4. When call initiated, backend sends push via JPushService
5. App receives push notification with custom extras (sessionId, callerId, etc.)
6. JPushReceiver triggers incoming call UI

### Payment Flow
1. User selects recharge package
2. App calls `/api/payment/create-order` with package details
3. Backend creates PaymentOrder and returns Alipay order string
4. App invokes Alipay SDK with order string
5. Alipay processes payment, sends callback to backend
6. Backend verifies callback signature, updates order status
7. Backend updates wallet balance via PaymentService.updateWalletBalance()

### Lucky Number Purchase Flow
1. User browses available lucky numbers via PropMallScreen
2. User selects number and validity period
3. App calls `/api/prop-mall/lucky-numbers/purchase`
4. Backend validates availability and user balance
5. Backend deducts from wallet, updates lucky_number status to SOLD
6. Creates transaction record
7. Returns updated lucky number with expiry date

## Database Relationships

**Users ↔ Wallets**: 1:1 relationship
- Both `users.balance` and `wallets.balance` maintained for compatibility
- Wallets table provides detailed transaction tracking

**Users ↔ UserDevices**: 1:N relationship (multi-device support)
- Each device has unique Registration ID
- Legacy: `users.jpush_registration_id` for backward compatibility

**Users ↔ UserSettings**: 1:1 relationship
- Pricing for calls and messages
- Call/message permissions

**Users ↔ PaymentOrders**: 1:N relationship
- Order history tracking
- Status transitions: PENDING → SUCCESS/CANCELLED/FAILED

**Users ↔ LuckyNumbers**: 1:N relationship (via owner_id)
- User can own multiple lucky numbers
- Each number has expiry date based on validity_days

## Testing

**Backend API Testing:**
Multiple PowerShell test scripts provided:
- `test_profile_api.ps1` - Profile, wallet, VIP endpoints
- `test_payment_api.ps1` - Payment order creation and queries
- `test_multidevice_push.ps1` - Multi-device push notification
- `test_backend_api_integration.ps1` - Full integration test

**Test Users:**
Database includes three test users (IDs 1-3) with different configurations.

## Configuration Notes

**Backend Configuration (`application.yml`):**
- Verification test mode: `verification.test-mode: true` (code always "123456")
- JWT expiration: 24 hours (86400000 ms)
- Database connection pool: HikariCP (max 20 connections)

**Android Configuration:**
- Package name: `com.example.myapplication`
- Min SDK: 24, Target SDK: 34
- Compose enabled with Material3
- ViewBinding enabled for XML layouts

## Documentation

- `PROFILE_API_DOCUMENTATION.md` - Profile, wallet, VIP API docs
- `PROP_MALL_API_DOCUMENTATION.md` - Lucky number system API docs
- `PAYMENT_API_OPTIMIZATION_SUMMARY.md` - Payment system details
- `MULTIDEVICE_PUSH_SYSTEM.md` - Push notification architecture
- `PUSH_NOTIFICATION_IMPROVEMENT.md` - Push notification improvements

## Known Issues & Gotchas

1. **MySQL Connection**: Ensure MySQL server is running on port 3306
2. **Port Conflicts**: Backend runs on 8080, check for conflicts with `netstat -ano | findstr :8080`
3. **Emulator Networking**: Use `10.0.2.2` instead of `localhost` in Android emulator
4. **JPush Registration**: May take a few seconds, retry logic implemented
5. **Alipay SDK**: Requires local AAR file, not available in Maven Central
6. **VolcEngine RTC**: JAR file extracted from AAR, placed in `app/libs/rtc/`
7. **Gradle Sync**: If dependencies fail, try `--refresh-dependencies` flag

## Code Style & Patterns

- Backend services use `@Transactional` for database operations
- DTOs used for API request/response (separate from entity classes)
- ApiResponse wrapper for consistent response format
- Lombok annotations (`@RequiredArgsConstructor`, `@Slf4j`) widely used in backend
- Android ViewModels use LiveData for state management
- Compose screens follow Material3 design guidelines
