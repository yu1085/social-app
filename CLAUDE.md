# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## ⚠️ Project Structure Change (2025-10-13)

**IMPORTANT**: This project has been separated into two independent repositories:

- **Android Client** (this repository): Android app only
- **Backend Service**: Moved to `C:\Users\Administrator\IdeaProjects\social-meet-backend`

The backend (SocialMeet/) is NO LONGER in this repository. See the backend README for backend-specific instructions.

## Project Overview

This repository contains the **Android client** for SocialMeet social dating application. It's a modern Android app built with Kotlin and Jetpack Compose.

## Build and Run Commands

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

### Backend Service (Separate Project)

The backend is now located at: `C:\Users\Administrator\IdeaProjects\social-meet-backend`

```bash
# Navigate to backend project
cd ../social-meet-backend

# Start backend service
gradlew bootRun

# Or use start script
start_backend_simple.bat
```

The backend runs on `http://localhost:8080` by default.

## Architecture Overview

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

## API Configuration

### Network Configuration

The app communicates with the backend service. Configure the backend URL in:

`app/src/main/java/com/example/myapplication/network/NetworkConfig.java`

```java
public static final String BASE_URL = "http://localhost:8080/api/";
```

**Important**:
- For emulator: Use `http://10.0.2.2:8080/api/`
- For real device: Use your computer's IP address
- For production: Use `https://your-domain.com/api/`

## Backend Integration

### Backend Project Location

The backend service is in a separate project:
- **Path**: `C:\Users\Administrator\IdeaProjects\social-meet-backend`
- **Documentation**: See `../social-meet-backend/README.md`

### Starting the Backend

1. Navigate to backend directory
2. Ensure MySQL is running
3. Start backend: `gradlew bootRun` or `start_backend_simple.bat`
4. Verify at: http://localhost:8080/swagger-ui.html

### Backend API Endpoints

The Android app connects to these main endpoints:

- **Authentication**: `/api/auth/*`
- **User Profile**: `/api/user/*`
- **Messages**: `/api/messages/*`
- **Social Feed**: `/api/dynamics/*` or `/api/posts/*`
- **Wallet**: `/api/wallet/*`
- **Calls**: `/api/call/*`

Full API documentation: http://localhost:8080/swagger-ui.html

## Development Workflow

### 1. Start Backend Service

```bash
cd ../social-meet-backend
gradlew bootRun
```

### 2. Update Network Config

Update `NetworkConfig.java` with correct backend URL.

### 3. Build Android App

```bash
gradlew assembleDebug
```

### 4. Install and Test

```bash
gradlew installDebug
```

### 5. Test API Connection

Use Swagger UI or test with the app.

## Android Configuration

### Build Config

In `app/build.gradle.kts`:
- `compileSdk = 34`
- `minSdk = 24`
- `targetSdk = 34`
- Kotlin version: 1.9.10
- Compose version: 1.6.0

### Dependencies

Major dependencies:
- Retrofit 2.9.0 - HTTP client
- Kotlin Coroutines - Async operations
- Jetpack Compose - Modern UI
- Material3 - Design system
- Coil - Image loading
- Aliyun SDKs - Face recognition, auth

### APK Size Optimization

Current APK is ~74MB due to:
- Aliyun SDK (24MB): Face recognition, OCR, NFC
- Multiple ABI support

Optimization strategies:
1. Use APK splits for different ABIs
2. Make Aliyun SDK optional
3. Enable ProGuard/R8 shrinking

## Testing

### Android Testing

```bash
# Run unit tests
gradlew test

# Run instrumented tests
gradlew connectedAndroidTest
```

### API Testing

Backend API tests are in the backend project:

```bash
cd ../social-meet-backend
python check_database.py
python scripts/unified_test_suite.py --verbose
```

## Important Notes

### Security Considerations

- API keys should not be hardcoded in production
- Use ProGuard to obfuscate sensitive code
- Store tokens securely (EncryptedSharedPreferences)
- Use HTTPS in production

### Known Limitations

- Backend must be running for app to function
- Emulator requires special localhost address (10.0.2.2)
- Some Aliyun SDK features may not work on all devices
- Large APK size due to SDKs

### Development Tips

1. **Network Debugging**: Enable OkHttp logging in debug builds
2. **Compose Previews**: Use `@Preview` for rapid UI iteration
3. **Hot Reload**: Android Studio supports Compose hot reload
4. **ADB Commands**:
   - `adb logcat -s MyApplication` - View app logs
   - `adb shell am start -n com.example.myapplication/.MainActivity` - Launch app

### Common Issues

1. **Network Connection Failures**
   - Check backend is running
   - Verify NetworkConfig BASE_URL
   - Check device/emulator network connectivity

2. **Build Failures**
   - Clean project: `gradlew clean`
   - Invalidate caches in Android Studio
   - Check Gradle sync

3. **Large APK Size**
   - Use APK Analyzer to inspect size
   - Consider using App Bundles
   - Remove unused SDKs

## Project Links

- **Backend Project**: `C:\Users\Administrator\IdeaProjects\social-meet-backend`
- **Android Documentation**: [README.md](README.md)
- **Backend Documentation**: `../social-meet-backend/README.md`
- **API Documentation**: http://localhost:8080/swagger-ui.html

## Technology Stack

### Android
- **Language**: Kotlin 1.9.10
- **UI Framework**: Jetpack Compose 1.6.0 + XML Views
- **Architecture**: MVVM
- **Networking**: Retrofit 2.9.0 + OkHttp 4.12.0
- **Async**: Kotlin Coroutines
- **Image Loading**: Coil 2.5.0
- **Design**: Material3

### Backend (Separate Project)
- **Language**: Java 21
- **Framework**: Spring Boot 3.3.5
- **Database**: MySQL 8.0
- **Authentication**: JWT
- **Messaging**: WebSocket
- **Build Tool**: Gradle 8.9

## Getting Help

For Android-specific issues:
- Check this README and CLAUDE.md
- Review code comments and documentation
- Use Android Studio's built-in help

For backend-specific issues:
- See `../social-meet-backend/README.md`
- Check backend logs
- Use Swagger UI for API testing

---

**Last Updated**: 2025-10-13
**Android SDK**: 34
**Minimum SDK**: 24
**Kotlin Version**: 1.9.10
**Project Status**: Frontend and Backend Separated ✅
