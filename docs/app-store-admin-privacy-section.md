# App Privacy — Admin Section (REQUIRED BEFORE SUBMIT)

> **Location**: App Store Connect → App Privacy  
> **Who**: App Store Connect ADMIN (account owner or person with Admin role)  
> **Status**: BLOCKING — Must complete before "Submit for Review"

---

## What is "Admin"?

In App Store Connect, an **Admin** is:
- The person who created the app
- The account owner
- Someone with "Admin" role in Team settings

**For Quiz Pszczelarski**: Likely **Rafał Jordan** (account owner)

---

## Blocking Error Message

```
⚠️ Before you can submit this app for review, an Admin must 
   provide information about the app's privacy practices in 
   the App Privacy section.
```

This means:
1. ✅ Privacy Policy URL → Done
2. ✅ Privacy Questionnaire → NOT DONE (needs Admin)
3. ❌ App Privacy section → EMPTY (needs filling)

---

## What Admin Needs to Do

### Step 1: Go to App Privacy Section
```
App Store Connect 
  → Your App 
  → App Privacy
```

### Step 2: Click "Get Started" or "Edit Privacy Practices"

### Step 3: Complete Questionnaire (use guide below)

---

## Quick Answers for Admin

Use the file: **`app-store-privacy-questionnaire-answers.md`**

### Summary:

| Question | Answer |
|----------|--------|
| Collect data? | ✅ YES |
| What data? | User ID, Score, Settings, Analytics, Crashes, Remote Config |
| Linked to user? | Some YES, some NO (see guide) |
| Used for tracking? | ❌ NO (all) |
| Third parties? | ✅ YES → Firebase |
| Privacy policy? | ✅ YES → GitHub Pages URL |
| User can delete? | ✅ YES → Email |

---

## Step-by-Step for Admin

### Section 1: Data Collection Purpose

**Question**: What data do you collect?

**Checkboxes to enable:**
- [x] User ID (Firebase)
- [x] Game Score & Statistics (custom)
- [x] Game Settings (custom)
- [x] Crash Reports (custom)
- [x] Analytics Events (custom)
- [x] Remote Configuration (custom)

**Uncheck everything else** (Location, Photos, Health, Contacts, etc.)

---

### Section 2: Data Collection Details

**For EACH data type**, answer:

```
User ID
├─ Purpose: App Functionality
├─ Linked to User: YES
└─ Used for Tracking: NO

Game Score & Statistics
├─ Purpose: App Functionality
├─ Linked to User: YES
└─ Used for Tracking: NO

Game Settings
├─ Purpose: App Functionality
├─ Linked to User: YES
└─ Used for Tracking: NO

Crash Reports
├─ Purpose: App Functionality
├─ Linked to User: NO
└─ Used for Tracking: NO

Analytics Events
├─ Purpose: Analytics
├─ Linked to User: NO
└─ Used for Tracking: NO

Remote Configuration
├─ Purpose: App Functionality
├─ Linked to User: NO
└─ Used for Tracking: NO
```

---

### Section 3: Third-Party Partners

**Question**: Do you use third-party SDKs?

**Answer**: ✅ **YES**

**Add Partner:**
```
Company: Google Firebase
Privacy Policy: https://firebase.google.com/support/privacy
Data Collected:
  - User ID (Firebase Auth)
  - Game Scores (Firestore)
  - Analytics Events
  - Crash Reports
  - App Configuration
```

---

### Section 4: Additional Questions

**Q: Do users have options to delete/opt-out?**
- ✅ YES → jordanrafalm@gmail.com

**Q: Is data encrypted in transit?**
- ✅ YES → HTTPS/TLS (Firebase default)

**Q: Do you share data with third parties for advertising?**
- ❌ NO

---

## Detailed Guide Location

Send Admin this file for complete step-by-step:
```
docs/app-store-privacy-questionnaire-answers.md
```

It contains:
- Every question
- Exact answers
- Why each answer
- Common mistakes to avoid

---

## After Admin Completes

Once Admin fills out App Privacy:

1. ✅ Blocking message disappears
2. ✅ "Submit for Review" button becomes active
3. ✅ You can proceed to Export Compliance
4. ✅ You can finally submit the build

---

## Timeline

**Admin needs to:**
1. Navigate to App Privacy
2. Answer questionnaire (10-15 min)
3. Click Save
4. Notify you when done

**You can then:**
1. Prepare screenshots (1-2h, can do in parallel)
2. Click "Submit for Review"
3. Answer Export Compliance questions
4. Submit build

---

## Important Notes

⚠️ **Only Admin can fill this**
- Non-admin will see "You don't have permission"
- Need to have Admin role in App Store Connect

⚠️ **Must match Privacy Policy**
- Answers should match what's in GitHub Pages privacy policy
- If inconsistency, Apple may reject app

⚠️ **Be honest**
- Incomplete/misleading answers = app rejection
- Apple takes privacy very seriously

---

## What to Tell Admin

"Hi Admin,

Please complete the App Privacy questionnaire in App Store Connect before we can submit the app for review.

📍 Go to: App Store Connect → App Privacy → Get Started

📄 Use this guide for answers:
   docs/app-store-privacy-questionnaire-answers.md

⏱ Should take ~15 minutes

🚀 Once done, we can proceed to submit the app."

---

## Current Status

```
✅ 1. Privacy Policy URL
✅ 2. Category (Education)
✅ 3. Age Ratings (4+)
✅ 4. Content Rights (NO third-party)
✅ 5. Pricing (Free)
❌ 6. App Privacy (BLOCKING - NEEDS ADMIN)
⏳ 7. Screenshots (1-2h work)
⏳ 8. Export Compliance (during Submit)
```

**BLOCKED until Admin completes App Privacy** ⛔
