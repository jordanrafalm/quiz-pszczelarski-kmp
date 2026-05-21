# User ID Purpose — App Privacy

> **For Admin**: Question about User ID usage in App Store Connect  
> **Location**: App Privacy → Data Collection → User ID → "How is this data used?"

---

## Correct Answer

```
☑ App Functionality

☐ Third-Party Advertising
☐ Developer's Advertising or Marketing
☐ Analytics
☐ Product Personalization
☐ Other Purposes
```

**SELECT ONLY: "App Functionality"** ✅

---

## Why "App Functionality" Only?

### ✅ We use User ID for:
- **Authentication**: Firebase Anonymous Auth to verify user
- **Account linking**: user's scores, settings, profile
- **Server operations**: Firestore queries, database access
- **Prevent fraud**: Rate limiting, duplicate detection
- **Security**: Access control, data isolation
- **Customer support**: Help users delete their data (email request)
- **Minimize crashes**: Error tracking per user
- **Performance**: Load balancing, database optimization

### ❌ We do NOT use User ID for:

#### Not "Third-Party Advertising"
- We don't display third-party ads
- We don't partner with ad networks
- We don't share User ID with advertisers

#### Not "Developer's Advertising or Marketing"
- We don't send marketing emails
- We don't display our own ads
- We don't promote other products

#### Not "Analytics"
- User ID is NOT sent to Firebase Analytics
- Analytics data is completely anonymized
- We never tie User ID to analytics events

#### Not "Product Personalization"
- We don't personalize quiz content per user
- All users see same quiz questions
- Ranking order is same for everyone
- Nickname is user-chosen, not personalized

#### Not "Other Purposes"
- No other undisclosed purposes

---

## Visual Guide

```
┌──────────────────────────────────────────────┐
│    How is User ID being used?                 │
│    (Select all that apply)                    │
├──────────────────────────────────────────────┤
│                                               │
│ ☑ App Functionality                           │
│    "authenticate user, enable features,       │
│     prevent fraud, security, support"         │
│                                               │
│ ☐ Third-Party Advertising                     │
│    (NOT applicable)                           │
│                                               │
│ ☐ Developer's Advertising or Marketing        │
│    (NOT applicable)                           │
│                                               │
│ ☐ Analytics                                   │
│    (User ID not sent to Analytics)            │
│                                               │
│ ☐ Product Personalization                     │
│    (No per-user customization)                │
│                                               │
│ ☐ Other Purposes                              │
│    (NOT applicable)                           │
│                                               │
│                    [ Cancel ]  [ Confirm ]    │
└──────────────────────────────────────────────┘
```

---

## What Admin Will See After This

After selecting "App Functionality", App Store Connect will ask:

**"Is this data linked to the user's identity?"**
- ✅ YES (User ID is the identity)

**"Is this data used to track the user?"**
- ❌ NO (We track in-app, not cross-app)

**"Is this data shared with third parties?"**
- ✅ YES → Firebase (Google LLC)

---

## For Each Other Data Type

When Admin fills out other data types (Game Score, Crashes, Analytics, etc.):

| Data Type | Purpose | Linked? | Tracking? |
|-----------|---------|---------|-----------|
| Game Score | App Functionality | YES | NO |
| Game Settings | App Functionality | YES | NO |
| Crash Data | App Functionality | NO | NO |
| Performance Data | Analytics | NO | NO |
| Remote Config | App Functionality | NO | NO |

---

## Reference

If Admin is unsure, they can reference:
1. **Privacy Policy**: `/privacy-policy.md` section 3 "W jakim celu używamy danych"
2. **Firebase Privacy**: https://firebase.google.com/support/privacy
3. **This guide**: Share this file

---

## After Confirming

Admin will continue answering similar questions for:
- Product Interaction (Usage Data → Analytics)
- Crash Data (Diagnostics → App Functionality)
- Performance Data (Diagnostics → Analytics)
- Custom types

All should follow same pattern:
- **Don't check boxes you don't need**
- **Be honest about actual usage**
- **Apple reviews these carefully**

---

## Important Reminder

⚠️ **Be accurate**
- Wrong declarations = app rejection
- Apple's privacy team verifies claims
- If you say "App Functionality" but actually use for "Analytics", app gets rejected

✅ **For Quiz Pszczelarski**
- User ID truly is used only for App Functionality
- No hidden analytics tracking
- No advertising or marketing
- Honest declaration matches implementation

---

## Summary

**SELECT ONLY CHECKBOX:**
```
☑ App Functionality
```

**Why:**
- Firebase Auth + Firestore access
- User data isolation
- Support requests
- Security & fraud prevention
- NOT for ads, marketing, or personalization

**Then click: Confirm / Next**
