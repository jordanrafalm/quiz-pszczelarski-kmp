# Phase 6: Notifications + Firebase Remote Config

> **Depends on:** Phase 4 (settings), Phase 2 (Firebase), Phase 3 (offline cache)  
> **ADRs:** ADR-0008 (notifications), ADR-0009 (remote config)  
> **Goal:** Add recurring quiz reminder notifications + Firebase Remote Config for force-update and "new questions" flags.

---

## Overview

Three features in this phase:
1. **Local recurring notifications** — ask permission on 2nd launch, send every 2 days at 18:00
2. **Notification toggle on HomeScreen** — icon next to sound/haptics toggles
3. **Firebase Remote Config** — fetch config during splash, support force-update screen + "new questions" badge

---

## Commit 6A: Settings Extension (notifications + launch count)

### What changes
- `SettingsState` — add `notificationsEnabled: Boolean = true`
- `SettingsRepository` — add methods:
  - `setNotificationsEnabled(Boolean)`
  - `getAppLaunchCount(): Int`
  - `incrementAppLaunchCount()`
- `SettingsRepositoryImpl` — implement with keys `notifications_enabled`, `app_launch_count`
- `LocalSettings.kt` — `LocalSettingsState` already provides `SettingsState`; no change needed, the new field flows automatically

### Files to create/edit
| File | Action |
|------|--------|
| `shared/.../domain/model/SettingsState.kt` | Add `notificationsEnabled` field |
| `shared/.../domain/repository/SettingsRepository.kt` | Add 3 new methods |
| `shared/.../data/settings/SettingsRepositoryImpl.kt` | Implement new methods + keys |

### Verification
- Existing `SettingsRepositoryImplTest` should still pass
- Add test for `incrementAppLaunchCount` and `notificationsEnabled` toggle

---

## Commit 6B: NotificationScheduler Interface + Android Implementation

### What changes
- Create `NotificationScheduler` interface in `composeApp/src/commonMain/.../platform/`
- Create `AndroidNotificationScheduler` in `composeApp/src/androidMain/.../platform/`
- Create `QuizReminderReceiver` (BroadcastReceiver) in androidMain
- Create `BootReceiver` (BroadcastReceiver) in androidMain for alarm persistence after reboot
- Update `AndroidManifest.xml`:
  - Add `POST_NOTIFICATIONS` permission
  - Add `RECEIVE_BOOT_COMPLETED` permission
  - Add `SCHEDULE_EXACT_ALARM` permission (Android 12+)
  - Register `QuizReminderReceiver`
  - Register `BootReceiver`
- Create notification channel `quiz_reminders` in `MainActivity.onCreate()`

### NotificationScheduler interface (commonMain)
```kotlin
// composeApp/src/commonMain/.../platform/NotificationScheduler.kt
interface NotificationScheduler {
    suspend fun requestPermission(): Boolean
    fun isPermissionGranted(): Boolean
    fun scheduleQuizReminder()
    fun cancelQuizReminder()
}
```

### Android implementation key points
- `AndroidNotificationScheduler(context: Context, activity: Activity?)`
- Uses `AlarmManager.setInexactRepeating()` with `INTERVAL_DAY * 2`
  - Initial trigger: next 18:00 that is ≥24h from now (ensures every-other-day pattern)
- `QuizReminderReceiver.onReceive()`:
  - Creates notification with `NotificationCompat.Builder`
  - Channel: `quiz_reminders`, title: "🐝 Quiz Pszczelarski", body: "Dowiedz się więcej o pszczołach! Rozegraj quiz."
  - PendingIntent opens `MainActivity`
- `BootReceiver.onReceive()`:
  - Reads `notifications_enabled` from SharedPreferences
  - If enabled, reschedules alarm via `AndroidNotificationScheduler`

### Files to create
| File | Action |
|------|--------|
| `composeApp/src/commonMain/.../platform/NotificationScheduler.kt` | Create interface |
| `composeApp/src/androidMain/.../platform/AndroidNotificationScheduler.kt` | Create impl |
| `composeApp/src/androidMain/.../notification/QuizReminderReceiver.kt` | Create receiver |
| `composeApp/src/androidMain/.../notification/BootReceiver.kt` | Create receiver |
| `composeApp/src/androidMain/AndroidManifest.xml` | Add permissions + receivers |
| `composeApp/src/androidMain/.../MainActivity.kt` | Create notification channel |

### Verification
- Install on Android 13+ device → verify permission dialog appears
- Schedule alarm → verify notification appears at 18:00
- Reboot device → verify alarm persists

---

## Commit 6C: iOS Notification Implementation

### What changes
- Create `IosNotificationScheduler` in `composeApp/src/iosMain/.../platform/`
- Uses `UNUserNotificationCenter` for permission + scheduling
- Batch-schedules notifications for next 64 slots (128 days, every other day at 18:00)
- Refreshes batch on each app launch if notifications enabled

### iOS implementation key points
- `IosNotificationScheduler` — class implementing `NotificationScheduler`
- `requestPermission()`: calls `UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound])`
  - Wrap in `suspendCancellableCoroutine` for Kotlin suspend compatibility
- `scheduleQuizReminder()`:
  - Remove all pending notification requests first
  - Schedule 64 `UNNotificationRequest` with `UNCalendarNotificationTrigger`:
    - Each trigger: `DateComponents(hour: 18, minute: 0)` + calculated day offset
  - Content: title = "🐝 Quiz Pszczelarski", body = "Dowiedz się więcej o pszczołach! Rozegraj quiz."
- `cancelQuizReminder()`: `UNUserNotificationCenter.current().removeAllPendingNotificationRequests()`
- Refresh batch on each `MainViewController` creation (or via `App` composition)

### Files to create
| File | Action |
|------|--------|
| `composeApp/src/iosMain/.../platform/IosNotificationScheduler.kt` | Create impl |

### Verification
- Run on iOS Simulator → verify permission prompt
- Check `UNUserNotificationCenter.getPendingNotificationRequests` returns 64 items
- Advance simulator clock to 18:00 → verify notification

---

## Commit 6D: Notification Permission Flow + HomeScreen Toggle

### What changes
- Wire `NotificationScheduler` through `App.kt` → `AppNavigation` (same pattern as `Haptics`)
- Add `LocalNotificationScheduler` CompositionLocal
- Add notification permission request logic in `AppNavigation` Splash `LaunchedEffect`:
  - Call `settingsRepo.incrementAppLaunchCount()`
  - If `launchCount == 2` → `notificationScheduler.requestPermission()` → if granted, `scheduleQuizReminder()`
- Add `ToggleNotifications` intent/effect in Home MVI
- Add notification bell icon toggle on HomeScreen (next to sound icon)

### HomeScreen UI change
In the bottom-left `Row` of settings toggles, add a third `IconButton`:
```kotlin
// Notifications toggle
IconButton(
    onClick = { onIntent(HomeIntent.ToggleNotifications) },
    modifier = Modifier.size(40.dp),
    colors = IconButtonDefaults.iconButtonColors(
        containerColor = if (settingsState.notificationsEnabled) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
    ),
) {
    Text(
        text = "🔔",  // or "🔕" when disabled
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.alpha(if (settingsState.notificationsEnabled) 1f else 0.4f),
    )
}
```

### MVI additions
- `HomeIntent.ToggleNotifications`
- `HomeEffect.ToggleNotifications`
- Handler in `AppNavigation`: toggle setting + call `scheduleQuizReminder()` or `cancelQuizReminder()`

### Files to edit
| File | Action |
|------|--------|
| `composeApp/src/commonMain/.../platform/LocalNotificationScheduler.kt` | Create CompositionLocal |
| `composeApp/src/commonMain/.../App.kt` | Add `notificationScheduler` parameter |
| `composeApp/src/commonMain/.../navigation/AppNavigation.kt` | Wire scheduler, permission flow, effect handler |
| `composeApp/src/commonMain/.../presentation/home/HomeIntent.kt` | Add `ToggleNotifications` |
| `composeApp/src/commonMain/.../presentation/home/HomeEffect.kt` | Add `ToggleNotifications` |
| `composeApp/src/commonMain/.../presentation/home/HomeViewModel.kt` | Handle new intent |
| `composeApp/src/commonMain/.../ui/screens/HomeScreen.kt` | Add bell toggle icon |
| `composeApp/src/androidMain/.../MainActivity.kt` | Pass `AndroidNotificationScheduler` |
| `composeApp/src/iosMain/.../MainViewController.kt` | Pass `IosNotificationScheduler` |

### Verification
- First launch: no permission prompt
- Second launch: permission prompt appears
- After granting: notifications scheduled (verify via alarm/pending requests)
- Toggle off on HomeScreen: notifications canceled
- Toggle on: notifications rescheduled

---

## Commit 6E: Firebase Remote Config — Dependency + Repository

### What changes
- Add `firebase-config` dependency to version catalog + `:shared` build.gradle.kts
- Create `AppConfig` domain model in `shared/domain/model/`
- Create `AppConfigRepository` interface in `shared/domain/repository/`
- Create `FirebaseAppConfigRepository` in `shared/data/config/`
- Add `FirebaseRemoteConfig` to iOS SPM dependencies

### Firebase Console setup instructions
See ADR-0009 for full details. Summary:
1. Firebase Console → Remote Config → Add 4 parameters:
   - `force_update_required` (Boolean, default: `false`)
   - `force_update_min_version` (String, default: `"0.0.0"`)
   - `new_questions_available` (Boolean, default: `false`)
   - `maintenance_mode` (Boolean, default: `false`)
2. Publish changes.

### Gradle changes
```toml
# libs.versions.toml
firebase-config = { module = "dev.gitlive:firebase-config", version.ref = "firebase-gitlive" }

# shared/build.gradle.kts commonMain dependencies
implementation(libs.firebase.config)
```

### iOS SPM
Add `FirebaseRemoteConfig` product from `firebase-ios-sdk` package in Xcode:
1. Open `iosApp.xcodeproj`
2. Project → Package Dependencies → `firebase-ios-sdk` → add `FirebaseRemoteConfig` product

### Files to create/edit
| File | Action |
|------|--------|
| `gradle/libs.versions.toml` | Add `firebase-config` library |
| `shared/build.gradle.kts` | Add `firebase-config` dependency |
| `shared/.../domain/model/AppConfig.kt` | Create data class |
| `shared/.../domain/repository/AppConfigRepository.kt` | Create interface |
| `shared/.../data/config/FirebaseAppConfigRepository.kt` | Create implementation |

### Verification
- Project compiles on both platforms
- `FirebaseAppConfigRepository.fetchConfig()` returns defaults when no params set in console
- Set `new_questions_available = true` in console → fetch returns `true`

---

## Commit 6F: Splash Integration + ForceUpdate Screen + HomeScreen Badge

### What changes
- Add `Route.ForceUpdate` to sealed interface
- Create `ForceUpdateScreen` composable — blocking, no back navigation, store link button
- Wire `AppConfigRepository` in `AppNavigation`:
  - Create in `remember {}` block alongside other repos
  - Fetch config in Splash `LaunchedEffect` (parallel with auth + sync)
  - After splash delay: check `forceUpdateRequired` → route to `ForceUpdate` or `Home`
- Pass `newQuestionsAvailable` to `HomeState` via `HomeViewModel` constructor parameter or initial state set from `AppNavigation`
- Show badge on HomeScreen "Zagraj" card when new questions available

### Version comparison logic
```kotlin
fun isVersionOutdated(currentVersion: String, minVersion: String): Boolean {
    val current = currentVersion.split(".").map { it.toIntOrNull() ?: 0 }
    val min = minVersion.split(".").map { it.toIntOrNull() ?: 0 }
    for (i in 0 until maxOf(current.size, min.size)) {
        val c = current.getOrElse(i) { 0 }
        val m = min.getOrElse(i) { 0 }
        if (c < m) return true
        if (c > m) return false
    }
    return false
}
```
- Current version obtained from `BuildConfig.VERSION_NAME` (Android) or `Bundle.main.infoDictionary["CFBundleShortVersionString"]` (iOS)
- Provide via expect/actual `fun getAppVersion(): String`

### ForceUpdateScreen
```
┌──────────────────────────────┐
│                              │
│         🐝 (bee icon)        │
│                              │
│   Aktualizacja wymagana      │
│                              │
│   Dostępna jest nowa wersja  │
│   aplikacji. Zaktualizuj     │
│   aby kontynuować.           │
│                              │
│   [  Aktualizuj  ]           │
│                              │
└──────────────────────────────┘
```
- Button opens Play Store / App Store link (platform-specific URL via expect/actual or runtime check)

### HomeScreen badge
- If `newQuestionsAvailable == true`, show a small chip/badge on "Zagraj" ActionCard:
  - Positioned top-right of the card or below the description
  - Text: "🆕 Nowe pytania!"
  - Color: `correctAnswer` green or `primary`

### Files to create/edit
| File | Action |
|------|--------|
| `composeApp/src/commonMain/.../navigation/Route.kt` | Add `ForceUpdate` |
| `composeApp/src/commonMain/.../ui/screens/ForceUpdateScreen.kt` | Create screen |
| `composeApp/src/commonMain/.../navigation/AppNavigation.kt` | Wire config fetch, ForceUpdate route, pass badge to Home |
| `composeApp/src/commonMain/.../presentation/home/HomeState.kt` | Add `newQuestionsAvailable` field |
| `composeApp/src/commonMain/.../presentation/home/HomeViewModel.kt` | Accept config flag in constructor |
| `composeApp/src/commonMain/.../ui/screens/HomeScreen.kt` | Show badge on Zagraj card |
| `composeApp/src/commonMain/.../platform/AppVersion.kt` | Create expect fun |
| `composeApp/src/androidMain/.../platform/AppVersion.kt` | Android actual |
| `composeApp/src/iosMain/.../platform/AppVersion.kt` | iOS actual |

### Verification
- Set `force_update_required = true`, `force_update_min_version = "99.0.0"` → ForceUpdate screen blocks app
- Set `new_questions_available = true` → badge visible on HomeScreen
- Offline: cached config or defaults used → app not blocked
- Normal flow (defaults): splash → home as before

---

## Dependency Graph

```
Commit 6A (settings) ← 6B (android notif) ← 6D (wiring + UI)
                      ← 6C (ios notif)     ←
Commit 6E (remote config repo) ← 6F (splash + force update + badge)
```

6A and 6E are independent and can be developed in parallel.
6B and 6C are independent (platform-specific) and depend only on 6A.
6D depends on 6A + 6B + 6C.
6F depends on 6E.

---

## Risk Assessment

| Risk | Impact | Mitigation |
|------|--------|------------|
| AlarmManager killed by OEM battery optimization | Medium | Use `setExactAndAllowWhileIdle`; document for users |
| iOS 64 notification batch runs out after 128 days | Low | Refresh batch on each app launch |
| Remote Config fetch timeout delays splash | Medium | Parallel fetch + 2s timeout, fallback to cache |
| GitLive `firebase-config` API changes | Low | Pin to 2.1.0, test on upgrade |
| `POST_NOTIFICATIONS` denied by user | Low | Respect denial, don't re-prompt; toggle shows disabled state |
