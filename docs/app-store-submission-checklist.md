# App Store Submission Checklist

> Last updated: 24 lutego 2026  
> App: Quiz Pszczelarski (iOS)

---

## Status Overview

| Item | Type | Status | Action Required |
|------|------|--------|----------------|
| Privacy Policy URL | Portal + Hosting | ❌ Missing | Host privacy-policy.md and provide URL |
| iPad 13" Screenshots | Assets | ❌ Missing | Generate iPad Pro 13" screenshots |
| Primary Category | Portal | ❌ Missing | Select category in App Store Connect |
| Content Rights | Portal | ❌ Missing | Fill out in App Information section |
| Age Ratings | Portal | ❌ Missing | Complete questionnaire |
| Export Compliance | Portal/Code | ❌ Missing | Add declaration to Info.plist or App Store Connect |
| Game Center Key | Code | ⚠️ Warning | **Not applicable** — app doesn't use Game Center |
| App Privacy Practices | Portal | ❌ Missing | Admin must complete privacy questionnaire |
| Price Tier | Portal | ❌ Missing | Select pricing in App Store Connect |

---

## 1. Privacy Policy URL ✅ (Code + Hosting)

### Current State
- Privacy policy exists: `/privacy-policy.md` (Polish, comprehensive, GDPR-compliant)
- ✅ **No code changes needed**

### Status: ✅ COMPLETED

**Privacy Policy is live on GitHub Pages:**
```
https://jordanrafalm.github.io/quiz-pszczelarski-kmp/privacy-policy.html
```

**Setup details:**
- Repository: GitHub Pages enabled
- Source: main branch → /docs folder
- Format: HTML with Water.css styling
- HTTPS: Enforced
- Deployment status: ✅ Live

#### Option B: Convert to HTML and host elsewhere
1. Convert `privacy-policy.md` to HTML
2. Host on Netlify, Vercel, or custom domain
3. Ensure HTTPS

#### Portal Configuration
- Go to **App Store Connect** → **App Information** → **Privacy Policy URL**
- Enter the hosted URL
- Save changes

---

## 2. iPad 13-inch Screenshots ❌ (Assets)

### Requirements
- **Display**: iPad Pro 13-inch (2732 x 2048 pixels or 2048 x 2732 pixels)
- **Count**: Minimum 1, recommended 3-5
- **Format**: PNG or JPEG

### Action Required

#### Option A: Use iOS Simulator
```bash
# Launch iPad Pro 13" simulator
xcrun simctl list devices | grep "iPad Pro 13"
open -a Simulator --args -CurrentDeviceUDID <UDID>

# Run app and capture screenshots
# Press Cmd+S to save screenshot to Desktop
```

#### Option B: Use Real Device
- Connect iPad Pro 13"
- Run app from Xcode
- Use Xcode's screenshot capture: **Window** → **Take Screenshot**

#### Option C: Design in Figma
- Create 2732x2048 frames with app screens
- Export as PNG
- Upload to App Store Connect

### Portal Configuration
- **App Store Connect** → **App Store** → **Screenshots**
- Select **iPad Pro (6th Gen) 12.9"** or **iPad Pro 13"**
- Upload screenshots
- Add localized captions (optional)

---

## 3. Primary Category ❌ (Portal — Business Decision)

### Recommended Category
- **Primary**: **Education**
- **Secondary** (optional): **Reference** or **Games**

### Rationale
- App is a quiz application for beekeeping education
- Primary goal: learning and knowledge assessment
- Not purely entertainment

### Action Required
- **App Store Connect** → **App Information** → **Category**
- Select **Education**
- Save

---

## 4. Content Rights Information ❌ (Portal)

### Action Required
- **App Store Connect** → **App Information** → **Content Rights**
- Answer:
  - "Does your app contain, show, or access third-party content?"
    - **No** (all quiz questions are proprietary or created by developer)
  - If "Yes": provide attribution details

---

## 5. Age Ratings ❌ (Portal)

### Recommended Rating: **4+ or 9+**

### Action Required
- **App Store Connect** → **App Information** → **Age Rating**
- Answer questionnaire:
  - Cartoon or Fantasy Violence: **No**
  - Realistic Violence: **No**
  - Sexual Content or Nudity: **No**
  - Profanity or Crude Humor: **No**
  - Alcohol, Tobacco, or Drug Use: **No**
  - Mature/Suggestive Themes: **No**
  - Horror/Fear Themes: **No**
  - Medical/Treatment Information: **No**
  - Gambling: **No**
  - Unrestricted Web Access: **No**
  - Gambling and Contests: **No**

### Expected Result
- **4+** (All Ages) — Quiz content is educational and family-friendly

---

## 6. Export Compliance ⚠️ (Portal or Code)

### Background
iOS apps using encryption must declare export compliance per US regulations.

### Assessment

#### Does Quiz Pszczelarski Use Encryption?
- ✅ **HTTPS** (for Firebase API calls) — **EXEMPT** (standard TLS)
- ✅ **Firebase SDK** uses encryption — **EXEMPT** (not custom crypto)
- ❌ NO custom cryptographic algorithms

### Action Required

#### Option A: Declare in App Store Connect (Recommended)
1. Submit build for review
2. When prompted about **Export Compliance**, select:
   - "No, this app does not use encryption"
   - OR
   - "Yes, but it qualifies for an exemption" → "Uses only standard encryption in HTTPS"

#### Option B: Add to Info.plist (Prevents Future Prompts)
Add to Xcode build settings or generate Info.plist:

```xml
<key>ITSAppUsesNonExemptEncryption</key>
<false/>
```

### Recommendation
- Use **Option A** for this submission (no code change required)
- If recurring builds are planned, add **Option B** later

---

## 7. Game Center Key ⚠️ (Not Applicable)

### Current State
- ❌ App **does not use Game Center**
- No leaderboards, achievements, or multiplayer via Game Center
- Ranking is implemented via **Firebase Firestore** (custom solution)

### Analysis
This warning appears because:
1. App category or metadata may have incorrectly indicated Game Center support
2. Or it's a generic warning for all game-like apps

### Action Required

#### Verify Metadata
- **App Store Connect** → **App Information** → **Game Center**
- Ensure **Game Center is NOT enabled**
- If enabled, disable it

#### No Code Changes Needed
- ❌ Do NOT add `com.apple.developer.game-center` entitlement
- App functions correctly without it

---

## 8. App Privacy Practices ❌ (Portal — Admin Only)

### Background
Apple requires app privacy disclosures in App Store Connect (separate from Privacy Policy).

### What Needs to Be Disclosed

Based on `privacy-policy.md` and Firebase integration:

#### Data Collected
| Data Type | Purpose | Linked to User | Used for Tracking |
|-----------|---------|----------------|-------------------|
| User ID (Anonymous UID) | App Functionality | No | No |
| Game Score / Stats | App Functionality | Yes (pseudonymous) | No |
| Nickname (user-provided) | App Functionality | Yes (public) | No |
| Crash Data | Analytics | No | No |
| Performance Data (Analytics) | Analytics | No | No |

#### Data NOT Collected
- ❌ Email, Phone, Name, Address
- ❌ Purchase History
- ❌ Location
- ❌ Photos, Camera, Microphone
- ❌ Health Data

### Action Required
- **Admin** must complete in **App Store Connect** → **App Privacy**
- Follow questionnaire based on table above
- Key points:
  - Anonymous authentication (no personal data)
  - Nickname is optional and not verified
  - Firebase Analytics and Crashlytics collect only technical/performance data
  - No data sold to third parties

---

## 9. Price Tier ❌ (Portal — Business Decision)

### Recommendation
- **Price Tier**: **Free (Tier 0)**

### Rationale
- App is designed as a free educational tool for beekeepers
- No in-app purchases or subscriptions implemented
- Business model: community value, not monetization

### Action Required
- **App Store Connect** → **Pricing and Availability**
- Select **Tier 0 (Free)**
- Select **All Territories** (or specific countries)
- Set **Availability Date** (immediate or scheduled)
- Save

---

## Summary: Immediate Actions

### Can Be Done Now (No Code Changes)

1. ✅ **Host privacy policy**:
   - GitHub Pages: Enable in repo settings, push `docs/` folder
   - URL: `https://jordanrafalm.github.io/quiz-pszczelarski-kmp/privacy-policy.html`

2. ❌ **Generate iPad 13" screenshots** (requires design work or simulator run)

3. ✅ **Portal configurations** (App Store Connect):
   - Select **Education** category
   - Fill Content Rights (No third-party content)
   - Complete Age Rating questionnaire (4+)
   - Declare Export Compliance (No encryption / HTTPS exemption)
   - **Disable** Game Center in metadata
   - Complete App Privacy questionnaire (Admin)
   - Select **Free** pricing

### Blocking Issues

- **Privacy Policy URL**: Must host MD file as HTML first
- **13" iPad Screenshots**: Must generate assets
- **App Privacy**: Requires Admin access in App Store Connect

---

## Next Steps

1. **Developer** (You):
   - Host privacy policy → provide URL
   - Generate iPad screenshots
   - Fill out portal forms (Category, Age Rating, Pricing)
   - Declare Export Compliance

2. **Admin** (if different person):
   - Complete App Privacy questionnaire in App Store Connect

3. **Re-submit** build once all checklist items are ✅

---

## References

- Privacy Policy: `/privacy-policy.md`
- Firebase Privacy: https://firebase.google.com/support/privacy
- Apple Privacy Guidelines: https://developer.apple.com/app-store/app-privacy-details/
- Export Compliance: https://developer.apple.com/documentation/security/complying_with_encryption_export_regulations
