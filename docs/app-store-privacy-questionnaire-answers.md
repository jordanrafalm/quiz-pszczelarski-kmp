# App Store Privacy Questionnaire — Quiz Pszczelarski

> **Location**: App Store Connect → App Privacy  
> **Status**: Use these exact answers for submission

---

## SEKCJA 1: Data Collection

### Q1: Do you or your third-party partners collect data from this app?

**SELECT: YES** ✅
- "Yes, we collect data from this app"

**Why**: We transmit data to Firebase (User IDs, scores, analytics, crashes) that is stored longer than needed for real-time requests.

---

## SEKCJA 2: What Data Do You Collect?

After selecting YES, you'll see data type checklist. Select ALL that apply:

### ✅ SELECT:
- [x] **User ID** (Firebase Anonymous UID)
- [x] **Product Purchase** — NO (uncheck if checked)
- [x] **Financial Info** — NO (uncheck)
- [x] **Precise Location** — NO (uncheck)
- [x] **Coarse Location** — NO (uncheck)
- [x] **Health/Fitness** — NO (uncheck)
- [x] **Sensitive Info** — NO (uncheck)
- [x] **Contacts** — NO (uncheck)
- [x] **Photos/Videos** — NO (uncheck)
- [x] **Audio** — NO (uncheck)
- [x] **Search History** — NO (uncheck)
- [x] **Browsing History** — NO (uncheck)

### ⚠️ CUSTOM DATA TYPES (Add by clicking "+"):

**ADD CUSTOM #1:**
```
Name: Game Score & Statistics
Category: App Functionality
Description: User's quiz score, games played, and ranking position
```

**ADD CUSTOM #2:**
```
Name: Game Settings
Category: App Functionality
Description: User preferences (nickname, notifications, sound, vibration)
```

**ADD CUSTOM #3:**
```
Name: Crash Reports
Category: Person
Description: Technical crash data and device information (via Crashlytics)
```

**ADD CUSTOM #4:**
```
Name: Analytics Events
Category: App Functionality
Description: Quiz interaction events and session data (via Analytics)
```

**ADD CUSTOM #5:**
```
Name: Remote Configuration
Category: App Functionality
Description: App configuration parameters from Firebase Remote Config
```

---

## SEKCJA 3: Is This Data Linked to the User's Identity?

For **EACH data type** (including custom), answer:

| Data Type | Linked to User? | Reason |
|-----------|-----------------|--------|
| **User ID** | ✅ YES | Anonymous UID is consistent identifier |
| **Game Score & Statistics** | ✅ YES | Associated with user's profile in ranking |
| **Game Settings** | ✅ YES | Stored per-user in Firestore |
| **Crash Reports** | ❌ NO | Technical data, anonymized |
| **Analytics Events** | ❌ NO | No personal identifiers |
| **Remote Configuration** | ❌ NO | Global config, not user-specific |

---

## SEKCJA 4: Is This Data Used for Tracking?

For **EACH data type**, answer: **NO** ❌

All responses should be:
- ❌ "Used to track you across other apps/websites?"
- ❌ "Used for targeting ads?"
- ❌ "Shared with data brokers?"

**Why**: We don't do cross-app tracking or targeted advertising. We only use data for app functionality.

---

## SEKCJA 5: Third-Party Partners

### Q: Do you have third-party partners collecting data?

**SELECT: YES** ✅
- "Yes, we use third-party SDKs"

### Add Partners:

#### Partner #1: **Google Firebase**
```
Name: Google Firebase
Privacy URL: https://firebase.google.com/support/privacy
Data Collected:
  ✅ User ID (Authentication)
  ✅ Game Scores (Firestore)
  ✅ Analytics Events
  ✅ Crash Reports
  ✅ App Configuration
```

---

## SEKCJA 6: Prominent Disclosures

### Q: Do you or your partners require explicit user consent before collecting data?

**SELECT: NO** ❌

**Why**: 
- Analytics collection is implicit (no explicit opt-in)
- App works with Firebase automatically
- User can opt-out via app settings (notifications)

---

## SEKCJA 7: Data Retention

### Q: How long do you retain user data?

**ANSWER**: 
- User IDs: Indefinitely (until account deletion)
- Game scores: Indefinitely (until account deletion)
- Crash reports: 90 days (Firebase default)
- Analytics: Aggregated (90 days), no individual tracking
- App config: Until app update

---

## SEKCJA 8: User Rights

### Q: Do users have the ability to delete their data?

**SELECT: YES** ✅

**How**: 
- Users can request deletion via: jordanrafalm@gmail.com
- Reference privacy policy section "6. Prawa użytkownika (RODO)"

---

## SEKCJA 9: Privacy Policy

### Q: Do you have a privacy policy?

**SELECT: YES** ✅

**URL**: `https://jordanrafalm.github.io/quiz-pszczelarski-kmp/privacy-policy.html`

---

## Summary Table (Quick Reference)

| Question | Answer | Evidence |
|----------|--------|----------|
| Collect data? | YES | Firebase (Auth, Firestore, Analytics, Crashlytics) |
| User ID? | YES | Firebase Anonymous UID |
| Game Score? | YES (Linked) | Firestore scores |
| Crash Data? | YES (Not Linked) | Crashlytics technical |
| Analytics? | YES (Not Linked) | Firebase Analytics (anonymized) |
| Used for tracking? | NO | No cross-app or targeted ads |
| Third parties? | YES | Google Firebase only |
| Privacy policy? | YES | GitHub Pages URL |
| User can delete? | YES | Email request to jordanrafalm@gmail.com |

---

## Tips

✅ **Do save after each section** — App Store Connect may timeout  
✅ **Be consistent** — All data types should have same "not used for tracking" answer  
✅ **Link to privacy policy** — When asked, provide GitHub Pages URL  
⚠️ **Don't exaggerate** — Only select data types you actually collect  
⚠️ **No "I don't know"** — Select NO if you don't collect it

---

## Common Mistakes to Avoid

❌ Saying "NO" to User ID (we DO collect it via Firebase)  
❌ Saying "YES" to Location (we don't collect it)  
❌ Saying "Used for tracking" (we don't do this)  
❌ Forgetting Firebase as third-party partner  
❌ Not providing privacy policy URL  

---

## After Completing Questionnaire

1. ✅ Review all answers
2. ✅ Click **Save**
3. ✅ Screenshot for documentation (optional)
4. ✅ Move to next section in App Store Connect

---

**Reference**: `/privacy-policy.md` sections 2, 3, 4 for detailed data practices
