# ADR-0008: Local Notifications (Recurring Quiz Reminder)

> **Status:** Accepted  
> **Date:** 2026-02-17  
> **Deciders:** Architect  
> **Related:** ADR-0006 (settings/haptics), ADR-0004 (Firebase)

---

## Context

The app should remind users to play the quiz every other day at 18:00.
Notification permission should be requested **on the second app launch** (not the first) to avoid premature permission prompts.
Users must be able to disable notifications via a toggle on the HomeScreen (next to sound/haptics toggles).

### Platform constraints
- **Android 13+ (API 33):** Requires `POST_NOTIFICATIONS` runtime permission.
- **Android < 13:** Notifications work without runtime permission.
- **iOS:** Requires `UNUserNotificationCenter.requestAuthorization(.alert, .badge, .sound)`.
- Both platforms need a mechanism to schedule repeating local notifications that survives app restarts.

---

## Options Considered

### Option A: expect/actual `NotificationScheduler` (platform-native)
- **Android:** `WorkManager` periodic work (repeats every 48h, shows notification at ~18:00) OR `AlarmManager` exact alarm  
- **iOS:** `UNCalendarNotificationTrigger` with repeating date components  
- Shared interface in `composeApp/platform/`, implementations in `androidMain`/`iosMain`

### Option B: Third-party KMP notification library
- No mature, well-maintained KMP local-notification library exists as of 2026-02.
- Risk of abandoned dependency.

### Option C: Firebase Cloud Messaging (FCM)
- Requires server-side scheduling (Cloud Functions / cron).
- Overkill for simple recurring reminders with no user-specific targeting.
- Adds server-side maintenance.

---

## Decision

**Option A — expect/actual `NotificationScheduler`** with platform-native APIs.

### Android implementation
- **Permission:** `ActivityCompat.requestPermissions(POST_NOTIFICATIONS)` (API 33+).
- **Scheduling:** `AlarmManager.setRepeating()` with a `BroadcastReceiver` that creates the notification. `AlarmManager` is preferred over `WorkManager` here because we need a **specific time of day** (18:00), not just a periodic interval. `WorkManager` does not guarantee exact timing.
- **Boot persistence:** Register `BOOT_COMPLETED` BroadcastReceiver to reschedule alarm after reboot.
- **Notification channel:** Create `quiz_reminders` channel on app startup (Android O+).

### iOS implementation
- **Permission:** `UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound])`
- **Scheduling:** `UNCalendarNotificationTrigger` with `hour=18, minute=0` repeating every 2 days. iOS does not natively support "every N days" repeating — use a batch of scheduled notifications (up to 64 pending) covering the next ~128 days, refreshed on each app launch.
- **Alternative:** Schedule a daily notification but cancel and reschedule to skip odd days (simpler, still within 64 limit).

### Shared contract (`composeApp/src/commonMain/platform/`)
```kotlin
interface NotificationScheduler {
    /** Request notification permission. Returns true if granted. */
    suspend fun requestPermission(): Boolean
    
    /** Check if notification permission is granted. */
    fun isPermissionGranted(): Boolean
    
    /** Schedule recurring quiz reminder (every 2 days at 18:00). */
    fun scheduleQuizReminder()
    
    /** Cancel all scheduled quiz reminders. */
    fun cancelQuizReminder()
}
```

### Settings integration
- Add `notificationsEnabled: Boolean` to `SettingsState` (default: `true`)
- Add `appLaunchCount: Int` tracking to `SettingsRepository`
- Add `setNotificationsEnabled(Boolean)` + `getAppLaunchCount()`/`incrementAppLaunchCount()` to `SettingsRepository`
- On second launch (`launchCount == 2`): request permission, then schedule if granted
- Toggle on HomeScreen: cancel/reschedule based on setting

### Notification content
- **Title:** "🐝 Quiz Pszczelarski"
- **Body:** "Dowiedz się więcej o pszczołach! Rozegraj quiz."
- **Android:** Small icon = app icon, color = primary (#FFC933)
- **iOS:** Default sound, badge cleared on app open

---

## Consequences

### Positive
- No server dependency — fully local, works offline
- User controls via settings toggle
- Permission requested at optimal moment (second launch = user already engaged)
- Platform-native = reliable scheduling

### Negative
- Platform-specific code in `androidMain`/`iosMain` (~100-150 lines each)
- iOS 64-notification limit requires batch refresh strategy
- `AlarmManager` on Android may be affected by Doze mode (use `setExactAndAllowWhileIdle` for critical timing, or accept ~15min drift with `setInexactRepeating`)

### Risks
- Battery optimization on some Android OEMs may kill alarms. Mitigation: document for users, use `setExactAndAllowWhileIdle`.
- iOS may throttle notifications. Mitigation: using `UNCalendarNotificationTrigger` is well-supported.

---

## Follow-ups
- Add notification channel creation in `MainActivity.onCreate()`
- Add `RECEIVE_BOOT_COMPLETED` permission + `BootReceiver` to AndroidManifest
- Consider analytics event for notification tap-through (future)
