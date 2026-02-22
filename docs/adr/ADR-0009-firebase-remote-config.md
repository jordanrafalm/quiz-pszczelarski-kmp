# ADR-0009: Firebase Remote Config (App Configuration Flags)

> **Status:** Accepted  
> **Date:** 2026-02-17  
> **Deciders:** Architect  
> **Related:** ADR-0004 (Firebase integration), ADR-0005 (offline cache)

---

## Context

The app needs a mechanism to deliver dynamic configuration flags from Firebase without releasing a new app version. Initial use cases:

1. **Force Update flag** — show a blocking screen telling users to update the app.
2. **"Nowe pytania w bazie"** flag — display a badge/banner on HomeScreen indicating new questions have been added.
3. Future flags (feature toggles, maintenance mode, etc.)

Firebase Remote Config is the standard Firebase product for this. The GitLive KMP SDK provides `dev.gitlive:firebase-config`.

### Constraints
- Config must be fetched during splash screen (alongside auth + question sync).
- If fetch fails (offline/timeout), use last-fetched cached values or sensible defaults.
- Must not block splash for more than ~2s beyond the existing 3s delay.

---

## Options Considered

### Option A: Firebase Remote Config (GitLive `firebase-config`)
- True multiplatform via GitLive SDK.
- Built-in caching (12h default, configurable).
- Console UI for changing values without deploy.
- Free tier covers all reasonable usage.

### Option B: Custom Firestore document
- Already have Firestore. Could use a `config/app` document.
- Pro: no new dependency.
- Con: no built-in defaults, no conditional targeting, no A/B testing. Manual caching.

### Option C: Custom REST endpoint
- Full control but requires server maintenance.
- Overkill for simple flags.

---

## Decision

**Option A — Firebase Remote Config** via `dev.gitlive:firebase-config`.

### Dependency
- Add to `libs.versions.toml`: `firebase-config = { module = "dev.gitlive:firebase-config", version.ref = "firebase-gitlive" }`
- Add to `:shared` commonMain dependencies: `implementation(libs.firebase.config)`

### Domain model (`shared/domain/model/`)
```kotlin
data class AppConfig(
    val forceUpdateRequired: Boolean = false,
    val forceUpdateMinVersion: String = "0.0.0",
    val newQuestionsAvailable: Boolean = false,
    val maintenanceMode: Boolean = false,
)
```

### Repository contract (`shared/domain/repository/`)
```kotlin
interface AppConfigRepository {
    /** Fetch latest config from Firebase. Returns cached values on failure. */
    suspend fun fetchConfig(): AppConfig
    
    /** Get last-known config without network call. */
    fun getCachedConfig(): AppConfig
}
```

### Data layer (`shared/data/config/`)
```kotlin
class FirebaseAppConfigRepository : AppConfigRepository {
    private val remoteConfig = Firebase.remoteConfig
    
    init {
        // Set defaults
        remoteConfig.setDefaults(mapOf(
            "force_update_required" to false,
            "force_update_min_version" to "0.0.0",
            "new_questions_available" to false,
            "maintenance_mode" to false,
        ))
    }
    
    override suspend fun fetchConfig(): AppConfig {
        try {
            remoteConfig.fetchAndActivate()
        } catch (_: Exception) { /* use cached/defaults */ }
        return readConfig()
    }
    
    override fun getCachedConfig(): AppConfig = readConfig()
    
    private fun readConfig(): AppConfig = AppConfig(
        forceUpdateRequired = remoteConfig["force_update_required"].asBoolean(),
        forceUpdateMinVersion = remoteConfig["force_update_min_version"].asString(),
        newQuestionsAvailable = remoteConfig["new_questions_available"].asBoolean(),
        maintenanceMode = remoteConfig["maintenance_mode"].asBoolean(),
    )
}
```

### Firebase Console Configuration

To set up Remote Config in Firebase Console:

1. Go to [Firebase Console](https://console.firebase.google.com/) → your project → **Remote Config** (left sidebar under "Engage" or "Run")
2. Click **"Add parameter"** for each flag:

| Parameter key            | Type    | Default value | Description |
|--------------------------|---------|---------------|-------------|
| `force_update_required`  | Boolean | `false`       | When `true`, show blocking update screen |
| `force_update_min_version` | String | `"0.0.0"`   | Minimum required version string (e.g. `"1.2.0"`) |
| `new_questions_available` | Boolean | `false`      | When `true`, show "Nowe pytania!" badge on HomeScreen |
| `maintenance_mode`       | Boolean | `false`       | When `true`, show maintenance screen instead of app |

3. Click **"Publish changes"** to make them live.
4. To change a value later: edit the parameter → **Publish changes**. Clients will pick it up on next `fetchAndActivate()`.

**Fetch interval configuration:**
- Development: set `minimumFetchIntervalInSeconds = 0` for instant updates
- Production: use default (12 hours) or set to 3600 (1 hour) — keeps costs at zero

### Splash integration
```
// In AppNavigation Splash LaunchedEffect (parallel with auth + sync):
val configJob = async {
    try { appConfigRepo.fetchConfig() }
    catch (_: Exception) { appConfigRepo.getCachedConfig() }
}
// After splash delay:
val config = configJob.await()
if (config.forceUpdateRequired && currentVersion < config.forceUpdateMinVersion) {
    currentRoute = Route.ForceUpdate
} else {
    currentRoute = Route.Home
}
```

### New Route: `ForceUpdate`
- Add `Route.ForceUpdate` to sealed interface
- Simple screen: app icon + message "Dostępna jest nowa wersja aplikacji. Zaktualizuj, aby kontynuować." + button linking to Play Store / App Store
- No back navigation — blocks the app

### HomeScreen "new questions" badge
- Pass `newQuestionsAvailable: Boolean` to `HomeState`
- Show a small badge/chip above or on the "Zagraj" ActionCard: "🆕 Nowe pytania!"

---

## Consequences

### Positive
- Zero-deploy flag changes via Firebase Console
- Built-in caching — works offline with last-known values
- Extensible — easy to add more flags later
- Tiny dependency footprint (`firebase-config` is lightweight)

### Negative
- New dependency: `dev.gitlive:firebase-config`
- iOS: must add `FirebaseRemoteConfig` to SPM dependencies
- Slight splash delay risk (mitigated by parallel fetch + timeout)

### Risks
- GitLive `firebase-config` API may differ slightly from native API. Mitigation: API is stable and well-documented.
- Remote Config has a 12h default cache. Urgent force-update may take up to 12h to propagate. Mitigation: set fetch interval to 1h in production, or use conditional fetch on Firestore `configVersion` doc (similar to question sync).

---

## Follow-ups
- Add `FirebaseRemoteConfig` to iOS SPM package dependencies
- Consider `configVersion` check via Firestore for faster propagation of critical flags
- Add analytics for force-update impressions (future)
