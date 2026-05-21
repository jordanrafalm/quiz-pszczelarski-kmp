# Phase 9 — Remove Exact Alarm Permissions (Google Play Policy Fix)

> **Status:** Ready for implementation  
> **Priority:** BLOCKER — Google Play Store odrzuca aplikacje używające `USE_EXACT_ALARM` poza kategoriami: budzik / kalendarz z alarmami  
> **Estimated effort:** ~5 minut (1 plik, 2 linijki)  
> **Related ADR:** [ADR-0008](../docs/adr/ADR-0008-local-notifications.md)

---

## Problem

`AndroidManifest.xml` deklaruje dwa uprawnienia do dokładnych alarmów:

```xml
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.USE_EXACT_ALARM" />
```

### Dlaczego to jest problem

| Uprawnienie | Ryzyko |
|---|---|
| `USE_EXACT_ALARM` | **HIGH RISK** — Google Play Policy pozwala go wyłącznie aplikacjom budzikowym i kalendarzowym. Skutkuje **odrzuceniem** z Google Play. |
| `SCHEDULE_EXACT_ALARM` | Wymaga od użytkownika aktywnego przyznania (Ustawienia → Alarmy i przypomnienia). Zbędne, bo kod nie używa dokładnych alarmów. |

### Dlaczego oba uprawnienia są zbędne

`AndroidNotificationScheduler.kt` używa `AlarmManager.setInexactRepeating()`:

```kotlin
alarmManager.setInexactRepeating(
    AlarmManager.RTC_WAKEUP,
    triggerAt,
    AlarmManager.INTERVAL_DAY * 2,
    pendingIntent,
)
```

`setInexactRepeating` **nie wymaga** ani `USE_EXACT_ALARM`, ani `SCHEDULE_EXACT_ALARM`.

---

## Zakres zmiany

**Jeden plik, dwie linijki do usunięcia:**

### `composeApp/src/androidMain/AndroidManifest.xml`

Usunąć:
```xml
<!-- AlarmManager permissions (Android 12+) -->
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.USE_EXACT_ALARM" />
```

Zostawić bez zmian:
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

**Brak zmian w kodzie Kotlina** — `AndroidNotificationScheduler.kt` jest poprawny.

---

## Weryfikacja po zmianie

```bash
# 1. Build release AAB
./gradlew :composeApp:bundleRelease

# 2. Sprawdź, że uprawnienia nie ma w zbudowanym manifeście
grep -i "exact_alarm" composeApp/build/intermediates/merged_manifest/release/processReleaseMainManifest/AndroidManifest.xml
# Oczekiwany wynik: brak dopasowań

# 3. Manualne testy powiadomień
# - Włącz powiadomienia w ustawieniach aplikacji
# - Odczekaj do 18:00 lub zmień czas testowy
# - Zweryfikuj, że powiadomienie pojawia się (z ~15 min tolerancją)
```

---

## Akceptowalne skutki uboczne

- Powiadomienia mogą pojawić się z opóźnieniem ~15 minut (to był świadomy trade-off w ADR-0008)
- Na mocno zoptymalizowanych bateriowo urządzeniach (np. Xiaomi MIUI, Samsung z agresywnym battery saver) alarm może być opóźniony lub pominięty — to zachowanie było akceptowalne od początku projektu
- Funkcjonalność powiadomień **nie ulega zmianie** z perspektywy użytkownika

---

## Nie rób

- ❌ Nie dodawaj `setExactAndAllowWhileIdle` — to wymagałoby `SCHEDULE_EXACT_ALARM` (runtime permission) i jest overkill dla codziennych przypomnień quizowych
- ❌ Nie zastępuj `AlarmManager` przez `WorkManager` — nie gwarantuje konkretnej godziny (18:00), co było wymaganiem z ADR-0008
- ❌ Nie dodawaj fallback logiki — `setInexactRepeating` jest wystarczające i działa na wszystkich wersjach Android >= 8.0 (minSdk=26)

---

## Commit

```
fix(android): remove USE_EXACT_ALARM and SCHEDULE_EXACT_ALARM from manifest

Neither permission is required — AndroidNotificationScheduler uses
setInexactRepeating which works without any exact alarm permission.

USE_EXACT_ALARM violates Google Play policy for non-alarm/calendar apps.
SCHEDULE_EXACT_ALARM requires runtime user approval and is also unused.

Fixes Play Store policy rejection.
```
