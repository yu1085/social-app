# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Quick Start (First Time Setup)

1. **Setup Database** (one-time):
   ```bash
   # Windows - Run from project root
   cd backend\database
   init-simple.bat
   ```

2. **Start Backend** (required before running Android app):
   ```bash
   # Windows
   gradlew.bat :backend:bootRun

   # Verify: http://localhost:8080/api/auth/health
   ```

3. **Build Android App**:
   ```bash
   # Windows
   gradlew.bat :app:assembleDebug

   # Install to device
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

4. **Configure Network** (if needed):
   - Edit `app/src/main/java/com/example/myapplication/network/NetworkConfig.java`
   - Emulator: Use `http://10.0.2.2:8080/api/`
   - Physical device: Use `http://YOUR_IP:8080/api/`

## Project Overview

SocialMeet is a full-stack social dating application consisting of an Android client and Spring Boot backend. The project uses a Gradle multi-module structure with two main modules:

- **app/** - Android client (Kotlin + Jetpack Compose hybrid)
- **backend/** - Spring Boot REST API (Java 21 + MySQL)

The Android app features user authentication, social feeds, messaging, video/voice calling, VIP subscriptions, and payment systems. The backend provides REST APIs for all services with JWT authentication.

**Key Technologies:**

*Android Client:*
- Kotlin 1.9.10 with Java 17
- Jetpack Compose 1.6.0 + XML Views (hybrid UI)
- Min SDK 24, Target SDK 34
- Retrofit 2.9.0 for networking
- Material3 design system
- VolcEngine RTC SDK for video/audio calls
- Aliyun SDKs for authentication and face recognition
- JPush SDK 5.9.0 for push notifications

*Backend:*
- Spring Boot 3.3.5
- Java 21
- MySQL 8.0 with Spring Data JPA
- JWT authentication (io.jsonwebtoken)
- JPush Server SDK for notifications
- Lombok for boilerplate reduction

## Build Commands

### Backend (Spring Boot)

The backend module must be built and running before using the Android app.

```bash
# Windows (from project root)
gradlew.bat :backend:build
gradlew.bat :backend:bootRun

# Linux/Mac (from project root)
./gradlew :backend:build
./gradlew :backend:bootRun

# Clean backend build
gradlew.bat :backend:clean      # Windows
./gradlew :backend:clean         # Linux/Mac
```

**Backend Database Setup:**
1. Install MySQL 8.0 and start the service
2. Run initialization script from project root:
   ```bash
   # Windows - Automated script
   cd backend\database
   init-simple.bat

   # Or manually with MySQL client (Windows)
   mysql -u root -proot < backend\database\init.sql

   # Linux/Mac
   mysql -u root -proot < backend/database/init.sql
   ```
3. Verify database connection in `backend/src/main/resources/application.yml`
   - Database: `socialmeet`
   - Username: `root`
   - Password: `root`
   - Port: `3306`

**Test Backend Health:**
```bash
curl http://localhost:8080/api/auth/health
```

**Backend Detailed Documentation:**
- HOW-TO-START.md: `backend/HOW-TO-START.md` - Complete startup guide
- API_DOCUMENTATION.md: `backend/API_DOCUMENTATION.md` - Full API reference

### Android App

```bash
# Windows
gradlew.bat :app:assembleDebug        # Debug build
gradlew.bat :app:assembleRelease      # Release build

# Linux/Mac
./gradlew :app:assembleDebug          # Debug build
./gradlew :app:assembleRelease        # Release build

# Architecture-specific builds (Windows)
gradlew.bat :app:assembleArm64-v8aDebug      # 64-bit ARM
gradlew.bat :app:assembleArmeabi-v7aDebug    # 32-bit ARM
```

### Install to Device

```bash
# Install debug APK to connected device
gradlew :app:installDebug

# Manual installation
adb install app/build/outputs/apk/debug/app-debug.apk

# Uninstall existing app
adb uninstall com.example.myapplication
```

### Clean Build

```bash
# Windows
gradlew.bat clean                      # Clean entire project
gradlew.bat :app:clean                 # Clean Android app module
gradlew.bat :backend:clean             # Clean backend module
gradlew.bat clean :app:assembleDebug   # Clean and rebuild Android app

# Linux/Mac
./gradlew clean                        # Clean entire project
./gradlew :app:clean                   # Clean Android app module
./gradlew :backend:clean               # Clean backend module
./gradlew clean :app:assembleDebug     # Clean and rebuild Android app
```

### Testing

```bash
# Windows
gradlew.bat :app:test                           # Run Android unit tests
gradlew.bat :app:testDebugUnitTest              # Run debug unit tests
gradlew.bat :app:connectedAndroidTest           # Run instrumented tests (requires device)
gradlew.bat :app:test --tests com.example.myapplication.ExampleUnitTest  # Run specific test
gradlew.bat :backend:test                       # Run backend tests

# Linux/Mac
./gradlew :app:test                             # Run Android unit tests
./gradlew :app:testDebugUnitTest                # Run debug unit tests
./gradlew :app:connectedAndroidTest             # Run instrumented tests (requires device)
./gradlew :app:test --tests com.example.myapplication.ExampleUnitTest    # Run specific test
./gradlew :backend:test                         # Run backend tests
```

### Debugging

```bash
# View Android app logs
adb logcat -s MyApplication

# View network requests (logging interceptor enabled in NetworkConfig)
adb logcat | grep OkHttp

# View backend logs
# Check backend/logs/socialmeet.log or console output
```

## Architecture Overview

### Multi-Module Structure

The project uses Gradle multi-module architecture defined in `settings.gradle.kts`:

```
social-app-android-backend/
├── app/                           # Android client module
├── backend/                       # Spring Boot backend module
│   ├── database/                  # Database initialization scripts (**CORRECTED LOCATION**)
│   │   ├── init.sql               # Main schema + seed data
│   │   ├── init-simple.bat        # Auto-detecting Windows script
│   │   └── init-with-charset.bat  # Charset-aware initialization
│   ├── src/main/java/             # Backend source code
│   └── src/main/resources/        # Application configuration
├── RTCVideoCall-Android/          # RTC SDK reference implementation
├── jpush-android-5.9.0-release/  # JPush Android SDK
├── settings.gradle.kts            # Multi-module configuration
└── build.gradle.kts               # Root build configuration
```

**Important Path Note:** Database scripts are in `backend/database/`, NOT `backend-setup/database/`

### Backend Architecture (Spring Boot)

**Backend Location:** `backend/`

The backend follows standard Spring Boot layered architecture:

```
backend/src/main/java/com/socialmeet/backend/
├── SocialMeetApplication.java    # Main Spring Boot application
├── entity/                        # JPA entities (User, VerificationCode, etc.)
├── dto/                          # Data Transfer Objects (UserDTO, LoginRequest, etc.)
├── repository/                    # Spring Data JPA repositories
├── service/                       # Business logic layer
│   ├── AuthService.java          # Authentication service
│   ├── UserService.java          # User management
│   ├── CallService.java          # Video/voice call management
│   └── JPushService.java         # Push notification service
├── controller/                    # REST API controllers
│   ├── AuthController.java       # /api/auth/* endpoints
│   ├── UserController.java       # /api/users/* endpoints
│   └── CallController.java       # /api/call/* endpoints
├── security/                      # Security configuration
│   └── JwtUtil.java              # JWT token utilities
└── config/                        # Application configuration
```

**Backend Database:** MySQL 8.0 with Spring Data JPA
- Database name: `socialmeet`
- Default credentials: root/root
- Connection: `jdbc:mysql://localhost:3306/socialmeet`

**Backend Test Users:**
- video_caller (ID: 23820512, Phone: 19812342076)
- video_receiver (ID: 22491729, Phone: 19887654321)
- test_user (Phone: 13800138000)
- All test verification codes: 123456

### Android App Hybrid UI Architecture

The app uses a unique hybrid approach combining XML and Compose:

1. **MainActivity (XML)**: Container activity with bottom navigation
2. **ComposeHost Pattern**: Bridge between Activities and Compose screens
3. **Compose Screens**: Complex interactive UIs (Square, Messages, Profile)
4. **Traditional Activities**: Settings, detail pages, authentication flows

**ComposeHost Files:**
- `compose/SquareComposeHost.kt`: Social feed integration
- `compose/MessageComposeHost.kt`: Messaging UI integration
- `compose/ProfileComposeHost.kt`: Profile screen integration
- `compose/SettingsComposeHost.kt`: Settings UI integration

**Integration Pattern:**
```kotlin
// In MainActivity.java
ComposeView composeSquare = findViewById(R.id.compose_square);
SquareComposeHost.attach(composeSquare);
```

### Network Layer Architecture

**Three-tier network implementation:**

1. **NetworkConfig.java**: Retrofit singleton with OkHttp configuration
   - Base URL configuration (defaults to `http://10.0.2.2:8080/api/` for emulator)
   - Logging interceptor for debug builds
   - 30-second timeouts for all operations

2. **ApiService.java**: Main REST API interface
   - Authentication endpoints (`/auth/*`)
   - User management (`/users/*`)
   - Social features (`/posts/*`, `/messages/*`)
   - Wallet/Payment (`/wallet/*`, `/payment/*`)
   - VIP subscriptions (`/vip/*`)
   - Wealth levels (`/wealth-level/*`)
   - ID verification (`/auth/id-card/*`)
   - Video/Voice calls (`/call/*`)

3. **NetworkService.kt**: Kotlin coroutine-based wrapper
   - Suspending functions for async operations
   - Error handling and response mapping

**Specialized API Services:**
- `PostApiService.kt`: Dedicated social post operations
- `CallService.kt`: Video/voice call session management

### Data Layer

**DTOs (Data Transfer Objects):** Located in `dto/`
- Mirror backend API responses exactly
- Used for network communication
- Examples: `UserDTO`, `PostDTO`, `MessageDTO`, `WalletDTO`

**Models:** Located in `model/`
- Local app data representations
- Business logic entities
- Examples: `VipLevel`, `VipSubscription`, `LuckyNumber`

### ViewModel Layer

**MVVM Pattern with Compose:**
- `viewmodel/SquareViewModel.kt`: Social feed state management
- `viewmodel/MessageViewModel.kt`: Messaging state
- `viewmodel/ProfileViewModel.kt`: User profile state
- `viewmodel/WalletViewModel.kt`: Wallet/transaction state
- `viewmodel/VipViewModel.kt`: VIP subscription state
- `viewmodel/EnhancedSquareViewModel.kt`: Enhanced social features

**ViewModel Pattern:**
```kotlin
// ViewModels use StateFlow for Compose integration
class SquareViewModel : ViewModel() {
    private val _posts = MutableStateFlow<List<PostDTO>>(emptyList())
    val posts: StateFlow<List<PostDTO>> = _posts
}
```

### Authentication System

**Multi-method authentication in `auth/AuthManager.java`:**
- Phone number + SMS verification code
- One-click carrier authentication (Aliyun Fusion SDK)
- Face recognition verification (Aliyun Face SDK)
- ID card two-factor verification

**Auth Flow:**
1. `LoginActivity.java`: Initial login screen
2. `PhoneIdentityAuthActivity.kt`: Carrier-based auth
3. `RealPersonAuthActivity.kt`: Face verification
4. `IdCardVerifyActivity.kt`: ID card verification

### Video/Voice Call System

**RTC Integration (`rtc/` package):**
- `RTCManager.kt`: Singleton managing VolcEngine RTC SDK
- `RTCConfig.kt`: RTC configuration (AppID, tokens, room settings)
- `VideoChatActivity.kt`: Video call UI
- Uses VolcEngine RTC SDK (same technology as TikTok/Douyin)

**Call Service (`service/` package):**
- `CallService.kt`: Backend API integration for call sessions
- Manages call pricing, initiation, acceptance, rejection, and termination
- Tracks call sessions and billing

**Call Notification System (JPush Integration):**
- **JPush SDK**: Located in `jpush-android-5.9.0-release/`
- **AppKey**: `ff90a2867fcf541a3f3e8ed4`
- **Master Secret**: `112ee5a04324ae703d2d6b3d`
- **Backend Status**: JPush Server SDK integrated (`io.github.jpush:jiguang-sdk:5.1.17`)
- **Android Status**: JPush client dependencies added to `app/build.gradle.kts`
- **IncomingCallActivity**: ⚠️ Activity class exists but JPush integration is NOT YET COMPLETE
  - Still needs: JPush initialization in Application class
  - Still needs: Alias registration after login
  - Still needs: JPush broadcast receiver configuration
  - See "JPush 'Cannot Find User' Error" section below for implementation steps
- **Notification Flow** (when fully implemented):
  1. Caller initiates → Backend creates session → Sends push to receiver
  2. Receiver gets full-screen incoming call UI via `IncomingCallActivity.java`
  3. Receiver can accept/reject
  4. Both parties enter RTC room upon acceptance

**Call Flow:**
1. `UserDetailActivity` → Click "视频通话" button
2. Get call pricing via `/api/call/rate-info`
3. Initiate call via `/api/call/initiate` → Creates CallSession
4. **Backend sends JPush notification to receiver**
5. **Receiver shows `IncomingCallActivity.java`** (full-screen incoming call UI)
6. Launch `VideoChatActivity` with session ID (after acceptance)
7. Join RTC room using VolcEngine SDK
8. End call → Update session duration and cost

**Call Features:**
- 1v1 video/voice calling
- **Incoming call notifications (WeChat-style full screen via `IncomingCallActivity.java`)**
- **Call ringing and vibration**
- **Accept/Reject call interface in `IncomingCallActivity.java`**
- Picture-in-picture local preview
- Camera switching
- Audio/video mute controls
- Call duration tracking
- Real-time pricing (video: 300元/分钟, voice: 150元/分钟)
- Call session management with backend

**Test Users for Video Calls:**
- `video_caller` (ID: 23820512, Phone: 19812342076)
- `video_receiver` (ID: 22491729, Phone: 19887654321)

### Payment Integration

**Payment Module (`payment/` package):**
- Alipay SDK integration (v15.8.11)
- Virtual currency (coins) system
- Wallet management
- Transaction history
- Recharge flow via `RechargeActivity.kt`

## Configuration

### Network Configuration

**File:** `app/src/main/java/com/example/myapplication/network/NetworkConfig.java`

```java
// For Android Emulator (default)
private static final String BASE_URL = "http://10.0.2.2:8080/api/";

// For physical device on same network
private static final String BASE_URL = "http://YOUR_IP:8080/api/";

// Production
private static final String BASE_URL = "https://api.socialmeet.com/api/";
```

### Backend Service

**IMPORTANT: Backend Location**
The backend is located within THIS project at `backend/` subdirectory, NOT in a separate project.

Path: `social-app-android-backend/backend/`

The app requires the backend service (Spring Boot) to be running:
```bash
# Windows (from project root)
gradlew.bat :backend:bootRun

# Linux/Mac (from project root)
./gradlew :backend:bootRun
```

Backend runs on `http://localhost:8080` by default.

**Important API Endpoints:**
- Authentication: `/api/auth/*`
- User profiles: `/api/users/*`
- Social posts: `/api/posts/*`
- Messages: `/api/messages/*`
- Wallet: `/api/wallet/*` (**Note**: Currently returns 404, needs implementation)
- Video calls: `/api/call/*` ✅ (Implemented: rate-info, initiate, accept, reject, end)
  - `/api/call/rate-info` - Get call pricing information
  - `/api/call/initiate` - Initiate a call (sends JPush notification to receiver)
  - `/api/call/accept/{sessionId}` - Accept incoming call
  - `/api/call/reject/{sessionId}` - Reject incoming call
  - `/api/call/end/{sessionId}` - End active call
  - `/api/call/status/{sessionId}` - Get call session status

## Common Issues and Solutions

### JPush "Cannot Find User" Error (CRITICAL)

**Error:** `cn.jiguang.sdk.exception.ApiErrorException: cannot find user by this audience or has been inactive for more than 255 days`

**Location:** Backend logs when calling `/api/call/initiate`

**Root Cause:** The Android app hasn't registered with JPush or hasn't set the user alias correctly. JPush uses the alias (user ID) to target specific devices for push notifications.

**Solution - Android Client Setup:**

1. **Initialize JPush in Application class:**
```kotlin
// In MyApplication.kt or Application class
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize JPush
        JPushInterface.setDebugMode(true) // Enable debug logging
        JPushInterface.init(this)

        // Set AppKey
        JPushInterface.setAppKey(this, "ff90a2867fcf541a3f3e8ed4")
    }
}
```

2. **Register JPush alias after login:**
```kotlin
// After successful login, set the user ID as JPush alias
val userId = loginResponse.userId
JPushInterface.setAlias(
    applicationContext,
    userId.hashCode(), // sequence number
    userId.toString() // alias = user ID
)

// Listen for alias set result
class JPushReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val bundle = intent?.extras
        val sequence = bundle?.getInt(JPushInterface.EXTRA_SEQUENCE)
        val errorCode = bundle?.getInt(JPushInterface.EXTRA_ERROR_CODE)

        if (errorCode == 0) {
            Log.d("JPush", "Alias set successfully")
        } else {
            Log.e("JPush", "Alias set failed: $errorCode")
        }
    }
}
```

3. **Add JPush receiver to AndroidManifest.xml:**
```xml
<receiver
    android:name=".receiver.JPushReceiver"
    android:enabled="true"
    android:exported="false">
    <intent-filter>
        <action android:name="cn.jpush.android.intent.REGISTRATION" />
        <action android:name="cn.jpush.android.intent.MESSAGE_RECEIVED" />
        <action android:name="cn.jpush.android.intent.NOTIFICATION_RECEIVED" />
        <action android:name="cn.jpush.android.intent.NOTIFICATION_OPENED" />
        <action android:name="cn.jpush.android.intent.ACTION_RICHPUSH_CALLBACK" />
        <category android:name="${applicationId}" />
    </intent-filter>
</receiver>

<!-- JPush required permissions -->
<permission
    android:name="${applicationId}.permission.JPUSH_MESSAGE"
    android:protectionLevel="signature" />
<uses-permission android:name="${applicationId}.permission.JPUSH_MESSAGE" />
```

4. **Verify registration:**
```bash
# Check Android logs for JPush registration
adb logcat | grep JPush

# Should see:
# JPush: [JPushInterface] Action:init
# JPush: Registration ID: xxxxx
# JPush: Alias set successfully
```

**Testing the fix:**
1. Launch the Android app and log in as user 22491729
2. Wait for JPush registration to complete (check logs)
3. Try initiating a call from the backend or another device
4. You should now receive the push notification

**Backend alternative (temporary):** If you want to skip push notifications during testing, modify `CallService.java`:
```java
// Comment out the notification line temporarily
// jPushService.sendCallNotification(receiverId, callerId, callType, sessionId);
System.out.println("Skipping push notification (development mode)");
```

### Backend JPush Configuration (RESOLVED)

**Status:** JPush Server SDK has been added to `backend/build.gradle` with dependency:
```gradle
implementation 'io.github.jpush:jiguang-sdk:5.1.17'
```

**Current Issue:** "接收方未注册推送服务" (Receiver not registered with push service)

This error occurs because the Android app needs to:
1. Initialize JPush SDK in Application class
2. Set user ID as JPush alias after login
3. Register broadcast receiver for push notifications

See the "JPush 'Cannot Find User' Error" section above for detailed solution.

### Network Connection Failures
1. Verify backend is running at `http://localhost:8080`
2. For emulator, ensure using `10.0.2.2` instead of `localhost`
3. Check `NetworkConfig.java` BASE_URL configuration
4. Enable logging interceptor to debug requests

### APK Size (Currently ~74MB)
- Aliyun SDKs contribute ~24MB
- Multiple ABI support adds overhead
- Consider: APK splits, dynamic feature modules, ProGuard

### Compose Preview Issues
1. Ensure `@Preview` annotation is present
2. Use `MyApplicationTheme` wrapper in preview
3. Invalidate Caches / Restart in Android Studio

### RTC Connection Issues
1. Verify `RTCConfig.kt` has valid AppID and tokens
2. Check VolcEngine console for service status
3. Ensure proper permissions (CAMERA, RECORD_AUDIO)

### Backend Database Connection Failures
**Error:** `Communications link failure`

**Solutions:**
- Check MySQL service is running
- Confirm port 3306 is available
- Verify credentials in `backend/src/main/resources/application.yml`
- Test connection: `mysql -u root -proot -e "USE socialmeet;"`

## Third-Party SDK Configuration

**VolcEngine RTC (Video Calling):**
- AppID and tokens configured in `app/src/main/java/com/example/myapplication/rtc/RTCConfig.kt`
- Requires valid VolcEngine account and RTC service enabled
- Same technology as TikTok/Douyin

**Aliyun SDKs:**
- Fusion Auth SDK: One-click login (`app/libs/fusionAuthSDK_*`)
- Face Recognition SDK: Real-person verification (`app/libs/aliyun-face-*`)
- OCR SDK: ID card scanning (`app/libs/aliyun-ocr-*`)

**Alipay:**
- SDK version 15.8.11
- Payment configuration in payment module

**JPush (Extreme Push Notification):**

*Android Client:*
- SDK version 5.9.0
- Complete SDK in `jpush-android-5.9.0-release/`
- AppKey: `ff90a2867fcf541a3f3e8ed4`
- Used for incoming call notifications and real-time messaging
- Add to `app/build.gradle.kts`:
  ```kotlin
  implementation(files("../jpush-android-5.9.0-release/jcore-3.x.x.jar"))
  implementation(files("../jpush-android-5.9.0-release/jpush-android-5.x.x.jar"))
  ```
- Configure manufacturer push channels (Xiaomi, Huawei, OPPO, Vivo) for better delivery
- See guides in `jpush-android-5.9.0-release/third-push/`

*Backend (Spring Boot):*
- Master Secret: `112ee5a04324ae703d2d6b3d`
- AppKey: `ff90a2867fcf541a3f3e8ed4`
- Add to `backend/build.gradle`:
  ```gradle
  implementation 'cn.jpush.api:jpush-client:3.7.9'
  implementation 'cn.jpush.api:jiguang-common:1.2.1'
  ```
- Service location: `backend/src/main/java/com/socialmeet/backend/service/JPushService.java`

## Project Structure

```
app/src/main/java/com/example/myapplication/
├── MainActivity.java              # Main container with bottom navigation
├── LoginActivity.java             # Login/registration entry point
│
├── compose/                       # Compose-Activity bridge
│   ├── SquareComposeHost.kt
│   ├── MessageComposeHost.kt
│   ├── ProfileComposeHost.kt
│   └── SettingsComposeHost.kt
│
├── ui/                           # Jetpack Compose UI
│   ├── screens/                  # Full-screen Compose screens
│   ├── components/               # Reusable Compose components
│   └── theme/                    # Material3 theme configuration
│
├── viewmodel/                    # MVVM ViewModels
│   ├── SquareViewModel.kt
│   ├── MessageViewModel.kt
│   ├── ProfileViewModel.kt
│   └── [other ViewModels]
│
├── network/                      # Network layer
│   ├── NetworkConfig.java        # Retrofit configuration
│   ├── ApiService.java          # Main REST API
│   ├── NetworkService.kt        # Kotlin coroutine wrapper
│   └── PostApiService.kt        # Specialized post API
│
├── dto/                         # Data Transfer Objects
│   ├── UserDTO.java
│   ├── PostDTO.java
│   ├── MessageDTO.java
│   └── [other DTOs]
│
├── model/                       # Local data models
├── auth/                        # Authentication module
│   └── AuthManager.java
│
├── rtc/                        # Video/voice calling
│   ├── RTCManager.kt
│   └── RTCConfig.kt
│
├── payment/                    # Payment integration
├── service/                    # Background services
└── util/                       # Utility classes
```

## Key Development Patterns

### Compose-Activity Integration

The hybrid architecture uses ComposeHost pattern to bridge XML Activities and Compose UIs:

```kotlin
// ComposeHost pattern (SquareComposeHost.kt)
object SquareComposeHost {
    @JvmStatic
    fun attach(target: ComposeView) {
        target.setContent {
            MyApplicationTheme {
                SquareScreen(viewModel = viewModel())
            }
        }
    }
}
```

### API Call Pattern

**Retrofit callbacks (Java Activities):**
```java
NetworkConfig.getApiService().getProfile(authHeader)
    .enqueue(new Callback<ApiResponse<UserDTO>>() {
        @Override
        public void onResponse(Call call, Response response) {
            if (response.isSuccessful()) {
                // Handle success
            }
        }

        @Override
        public void onFailure(Call call, Throwable t) {
            // Handle error
        }
    });
```

**Kotlin coroutines (Compose/ViewModels):**
```kotlin
viewModelScope.launch {
    try {
        val response = networkService.getPosts()
        _posts.value = response
    } catch (e: Exception) {
        // Handle error
    }
}
```

### Navigation Pattern

**Bottom Navigation in MainActivity:**
- Home: User discovery cards
- Square: Social feed (Compose)
- Message: Chat list (Compose)
- Profile: User profile (Compose)

**Activity Transitions:**
```java
Intent intent = new Intent(this, TargetActivity.class);
intent.putExtra("key", value);
startActivity(intent);
```

## Dependencies and Libraries

### Core Android
- AndroidX Core KTX 1.12.0
- AppCompat (for compatibility)
- Material Components 1.10.0
- ConstraintLayout

### Compose
- Compose BOM 2023.10.01
- Material3
- Navigation Compose 2.7.7
- ViewModel Compose 2.7.0
- Coil for image loading 2.5.0

### Networking
- Retrofit 2.9.0
- Gson converter 2.9.0
- OkHttp 4.12.0
- Logging interceptor 4.12.0

### RTC & Authentication
- VolcEngine RTC SDK (JAR in `libs/rtc/`)
- Aliyun Fusion Auth SDK (AAR in `libs/fusionAuthSDK_*`)
- Aliyun Face/OCR SDKs (AAR in `libs/`)

### Push Notifications
- JPush SDK 5.9.0 (in `jpush-android-5.9.0-release/`)
- Add to `build.gradle.kts`:
  ```kotlin
  implementation(files("jpush-android-5.9.0-release/jcore-3.x.x.jar"))
  implementation(files("jpush-android-5.9.0-release/jpush-android-5.x.x.jar"))
  ```
- **IMPORTANT**: Configure manufacturer push channels (Xiaomi, Huawei, OPPO, Vivo, etc.) for better delivery
  - See guides in `jpush-android-5.9.0-release/third-push/`

### Payment
- Alipay SDK 15.8.11

### Camera
- CameraX 1.3.1 (core, camera2, lifecycle, view)

## Testing Structure

**Unit Tests:** `app/src/test/java/`
- JUnit 4.13.2
- Run on JVM without Android framework

**Instrumented Tests:** `app/src/androidTest/java/`
- AndroidX Test (JUnit 1.1.5, Espresso 3.5.1)
- Requires connected device or emulator

## Important Notes

### Implementing Call Notifications (WeChat-style)

**Frontend (Android) Requirements:**
1. **JPush SDK Integration**:
   - Add JPush dependencies from `jpush-android-5.9.0-release/`
   - Initialize JPush in Application class with AppKey
   - Register JPush receiver for incoming call notifications
   - Request notification permissions for Android 13+

2. **Incoming Call Activity**:
   - Create full-screen activity for incoming calls (similar to WeChat)
   - Display caller information (avatar, nickname, call type)
   - Implement ringtone and vibration
   - Provide Accept (green) and Reject (red) buttons
   - Handle timeout scenarios (auto-reject after 30-60 seconds)

3. **Outgoing Call Activity**:
   - Create waiting screen showing "等待对方接听..."
   - Display receiver information
   - Provide Cancel button
   - Listen for call status updates via polling or WebSocket

4. **Call State Management**:
   - Track call states: INITIATING, RINGING, ACCEPTED, REJECTED, ENDED
   - Handle network disconnections and reconnections
   - Prevent duplicate calls (check for active sessions)

**Backend Requirements:**
1. **JPush Server SDK Integration**:
   - Add JPush Java SDK to backend dependencies
   - Configure Master Secret: `112ee5a04324ae703d2d6b3d`
   - Implement push notification service

2. **Call Notification Flow**:
   - When `/api/call/initiate` is called:
     a. Create CallSession in database
     b. Send JPush notification to receiver with call details
     c. Return session ID to caller
   - When receiver accepts/rejects:
     a. Update CallSession status
     b. Notify caller via callback or polling

3. **WebSocket (Optional but Recommended)**:
   - Implement WebSocket for real-time call status updates
   - Send events: CALL_INCOMING, CALL_ACCEPTED, CALL_REJECTED, CALL_ENDED
   - More responsive than polling

**Implementation Example (Backend - JPush):**
```java
// In CallService.java
public void sendCallNotification(Long receiverId, Long callerId, String sessionId) {
    JPushClient client = new JPushClient(MASTER_SECRET, APP_KEY);

    PushPayload payload = PushPayload.newBuilder()
        .setPlatform(Platform.android())
        .setAudience(Audience.alias(receiverId.toString()))
        .setNotification(Notification.android(
            "视频通话",
            "video_caller 向您发起视频通话",
            Map.of(
                "type", "INCOMING_CALL",
                "sessionId", sessionId,
                "callerId", callerId.toString()
            )
        ))
        .build();

    client.sendPush(payload);
}
```

**Reference Implementation:**
- Complete examples available in `jpush-android-5.9.0-release/`
- Check manufacturer-specific push guides in `third-push/` subdirectories

### Backend Dependency
The app is **tightly coupled** with the backend service. Most features require the backend running at the configured BASE_URL.

### SDK Credentials
- RTC: Requires VolcEngine AppID and access tokens
- Aliyun: Requires valid Aliyun account for auth/face SDKs
- Alipay: Requires merchant configuration

### Privacy & Permissions
The app requests sensitive permissions:
- CAMERA: Video calling, face verification
- RECORD_AUDIO: Voice/video calling
- INTERNET: All network operations
- ACCESS_FINE_LOCATION: Location-based matching
- BLUETOOTH_CONNECT: Bluetooth audio devices (Android 12+)

First launch shows privacy policy dialog (see `MainActivity.checkFirstLaunchAndShowPrivacyPolicy()`)

### Build Configuration
- `compileSdk = 34`
- `minSdk = 24` (Android 7.0)
- `targetSdk = 34`
- Java 17 compatibility
- ViewBinding enabled
- Compose enabled with Kotlin compiler 1.5.3

## Development Workflow

### Making Changes to Backend
1. Edit backend code in `backend/src/main/java/`
2. Restart backend: `gradlew.bat :backend:bootRun`
3. Test changes with Android app or curl

### Making Changes to Android App
1. Edit code in `app/src/main/java/`
2. For Compose changes: Preview updates automatically in Android Studio
3. Rebuild: `gradlew.bat :app:assembleDebug`
4. Reinstall: `adb install -r app/build/outputs/apk/debug/app-debug.apk`
5. Check logs: `adb logcat -s MyApplication`

### Debugging Network Issues
1. Enable OkHttp logging in `NetworkConfig.java` (already enabled in debug builds)
2. View requests: `adb logcat | grep OkHttp`
3. Test backend directly: `curl http://localhost:8080/api/auth/health`
4. Verify emulator can reach backend: Use `10.0.2.2` instead of `localhost`

### Working with Database
- **Schema location**: `backend/database/init.sql`
- **Reset database**: Re-run `backend\database\init-simple.bat`
- **View data**: Use MySQL Workbench or command line: `mysql -u root -proot socialmeet`
- **Test users**: See "Backend Test Users" section above
