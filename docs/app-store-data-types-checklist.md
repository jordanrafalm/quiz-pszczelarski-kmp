# App Privacy Data Types — Quiz Pszczelarski

> **For Admin**: Which data categories to select in App Store Connect

---

## Quick Summary

### ✅ SELECT (Check these):

```
IDENTIFIERS
  [x] User ID

USAGE DATA
  [x] Product Interaction

DIAGNOSTICS
  [x] Crash Data
  [x] Performance Data
```

### ❌ DO NOT SELECT (Leave unchecked):

```
CONTACT INFO
  [ ] Name
  [ ] Email Address
  [ ] Phone Number
  [ ] Physical Address
  [ ] Other User Contact Info

HEALTH & FITNESS
  [ ] Health
  [ ] Fitness

FINANCIAL INFO
  [ ] Payment Info
  [ ] Credit Info
  [ ] Other Financial Info

LOCATION
  [ ] Precise Location
  [ ] Coarse Location

SENSITIVE INFO
  [ ] (any)

CONTACTS
  [ ] (unchecked)

USER CONTENT
  [ ] Emails or Text Messages
  [ ] Photos or Videos
  [ ] Audio Data
  [ ] Gameplay Content
  [ ] Customer Support
  [ ] Other User Content

BROWSING HISTORY
  [ ] (unchecked)

SEARCH HISTORY
  [ ] (unchecked)

IDENTIFIERS
  [ ] Device ID
  [ ] Purchases

USAGE DATA
  [ ] Advertising Data
  [ ] Other Usage Data

DIAGNOSTICS
  [ ] Other Diagnostic Data

SURROUNDINGS
  [ ] Environment Scanning

BODY
  [ ] Hands
  [ ] Head

OTHER DATA
  [ ] (any)
```

---

## ✅ SELECTED CATEGORIES EXPLAINED

### 1. **User ID** (Identifiers)
```
Data Collected: Firebase Anonymous UID
Linked to User: YES
Used for Tracking: NO
Purpose: App Functionality

Why: Every user gets unique ID to track their scores and settings
```

### 2. **Product Interaction** (Usage Data)
```
Data Collected: quiz_started, quiz_completed, quiz_abandoned events
Linked to User: NO (aggregated)
Used for Tracking: NO
Purpose: Analytics

Why: We measure how users interact with quiz features
```

### 3. **Crash Data** (Diagnostics)
```
Data Collected: Crash logs, stack traces, device info
Linked to User: NO
Used for Tracking: NO
Purpose: App Functionality (bug fixing)

Why: Firebase Crashlytics automatically collects crash reports
```

### 4. **Performance Data** (Diagnostics)
```
Data Collected: Launch time, app performance metrics
Linked to User: NO
Used for Tracking: NO
Purpose: Analytics

Why: We measure app performance via Firebase Analytics
```

---

## CUSTOM DATA TYPES (Add via "+")

Since not all our data fits standard categories, add:

### Custom #1: Game Score & Statistics
```
Category Mapping: Identifiers (because it's linked to User ID)
Data: Quiz score, games played, streak
Linked to User: YES
Used for Tracking: NO
Purpose: App Functionality
```

### Custom #2: Game Settings
```
Category Mapping: Usage Data
Data: Nickname, notification preferences, sound/vibration settings
Linked to User: YES
Used for Tracking: NO
Purpose: App Functionality
```

### Custom #3: Remote Configuration
```
Category Mapping: Usage Data
Data: Firebase Remote Config (force update, feature flags)
Linked to User: NO
Used for Tracking: NO
Purpose: App Functionality
```

---

## Data Purposes Mapping

When asked "What is this data used for?":

| Data Type | Purpose |
|-----------|---------|
| User ID | App Functionality |
| Game Score | App Functionality |
| Game Settings | App Functionality |
| Product Interaction | Analytics |
| Crash Data | App Functionality |
| Performance Data | Analytics |
| Remote Config | App Functionality |

---

## Tracking Questions

For ALL data types, answer:

**"Is this data used to track the user?"**
- ❌ NO (for all)

**"Shared with advertisers?"**
- ❌ NO

**"Sold to data brokers?"**
- ❌ NO

---

## What Admin Will See in App Store Connect

```
┌──────────────────────────────────────────────┐
│          DATA COLLECTION CATEGORIES            │
├──────────────────────────────────────────────┤
│                                               │
│ CONTACT INFO                                  │
│  ☐ Name                                       │
│  ☐ Email Address                              │
│  ☐ Phone Number                               │
│  ☐ Physical Address                           │
│  ☐ Other User Contact Info                    │
│                                               │
│ HEALTH & FITNESS                              │
│  ☐ Health                                     │
│  ☐ Fitness                                    │
│                                               │
│ FINANCIAL INFO                                │
│  ☐ Payment Info                               │
│  ☐ Credit Info                                │
│  ☐ Other Financial Info                       │
│                                               │
│ LOCATION                                      │
│  ☐ Precise Location                           │
│  ☐ Coarse Location                            │
│                                               │
│ SENSITIVE INFO                                │
│  ☐ [none listed, just checkbox]               │
│                                               │
│ CONTACTS                                      │
│  ☐ [unchecked]                                │
│                                               │
│ USER CONTENT                                  │
│  ☐ Emails or Text Messages                    │
│  ☐ Photos or Videos                           │
│  ☐ Audio Data                                 │
│  ☐ Gameplay Content                           │
│  ☐ Customer Support                           │
│  ☐ Other User Content                         │
│                                               │
│ BROWSING HISTORY                              │
│  ☐ [unchecked]                                │
│                                               │
│ SEARCH HISTORY                                │
│  ☐ [unchecked]                                │
│                                               │
│ IDENTIFIERS                                   │
│  ☑ User ID                    ← CHECK THIS    │
│  ☐ Device ID                                  │
│  ☐ Purchases                                  │
│                                               │
│ USAGE DATA                                    │
│  ☑ Product Interaction         ← CHECK THIS   │
│  ☐ Advertising Data                           │
│  ☐ Other Usage Data                           │
│                                               │
│ DIAGNOSTICS                                   │
│  ☑ Crash Data                  ← CHECK THIS   │
│  ☑ Performance Data            ← CHECK THIS   │
│  ☐ Other Diagnostic Data                      │
│                                               │
│ SURROUNDINGS                                  │
│  ☐ Environment Scanning                       │
│                                               │
│ BODY                                          │
│  ☐ Hands                                      │
│  ☐ Head                                       │
│                                               │
│ OTHER DATA                                    │
│  ☐ Any other data types                       │
│                                               │
│                          [ Add Custom... ]    │
│                                               │
│                            [ Save ]           │
└──────────────────────────────────────────────┘
```

---

## Step-by-Step for Admin

1. **Go to**: App Store Connect → App Privacy
2. **Scroll through** all categories
3. **Check ONLY**:
   - ☑ User ID (under Identifiers)
   - ☑ Product Interaction (under Usage Data)
   - ☑ Crash Data (under Diagnostics)
   - ☑ Performance Data (under Diagnostics)
4. **Add Custom Types** (click "+ Add"):
   - Game Score & Statistics
   - Game Settings
   - Remote Configuration
5. **For each data type**, fill:
   - Purpose dropdown
   - "Linked to user?" YES/NO
   - "Used for tracking?" NO (all)
6. **Add Third-party**: Google Firebase
7. **Click Save**

---

## Common Mistakes

❌ Checking "Device ID" (we don't use advertising ID)  
❌ Checking "Precise Location" (we don't collect location)  
❌ Checking "Health & Fitness" (not relevant)  
❌ Checking "Payment Info" (app is free, no payments collected)  
❌ Not adding custom types (our data won't fit standard categories)  
❌ Saying "Used for tracking: YES" (we don't do cross-app tracking)  

---

## After Completing

✅ Admin clicks **Save**  
✅ Blocking message disappears  
✅ "Submit for Review" becomes active  
✅ You can proceed to screenshots + export compliance

---

## References

- Full Privacy Details: `/privacy-policy.md`
- Complete Questionnaire Answers: `app-store-privacy-questionnaire-answers.md`
- Admin Guide: `app-store-admin-privacy-section.md`
