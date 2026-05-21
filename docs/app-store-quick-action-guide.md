# App Store Submission — Quick Action Guide

> **Last updated**: 24 lutego 2026  
> **Purpose**: Resolve all App Store Connect blocking issues for Quiz Pszczelarski iOS

---

## Status Summary

| # | Issue | Status | Priority | Est. Time |
|---|-------|--------|----------|-----------|
| 1 | Privacy Policy URL | ❌ Missing | 🔴 Critical | 30 min |
| 2 | iPad 13" Screenshots | ❌ Missing | 🔴 Critical | 1-2 hours |
| 3 | Primary Category | ❌ Missing | 🟡 Medium | 5 min |
| 4 | Content Rights | ❌ Missing | 🟡 Medium | 5 min |
| 5 | Age Ratings | ❌ Missing | 🟡 Medium | 10 min |
| 6 | Export Compliance | ❌ Missing | 🟡 Medium | 5 min |
| 7 | Game Center Key | ⚠️ Warning | 🟢 Low | 2 min |
| 8 | App Privacy | ❌ Missing | 🔴 Critical | 20 min |
| 9 | Price Tier | ❌ Missing | 🟡 Medium | 5 min |

**Total estimated time**: ~3-4 hours (including screenshot generation)

---

## Critical Path (Must Complete Before Submission)

### 1. ✅ Privacy Policy URL (30 min)

**Action**: Host privacy-policy.md and provide public URL

**Quick Steps**:
```bash
# Option A: GitHub Pages (recommended)
# 1. Enable GitHub Pages in repo settings → Use /docs folder
# 2. Wait 2-5 minutes for deployment
# 3. Access URL: https://jordanrafalm.github.io/quiz-pszczelarski-kmp/privacy-policy

# Option B: Convert to HTML first
cd /Users/rafal/projects/quiz-pszczelarski-kmp
pandoc privacy-policy.md -o docs/privacy-policy.html --standalone
git add docs/privacy-policy.html
git commit -m "Add HTML privacy policy for App Store"
git push
# Then enable GitHub Pages
```

**Where to Paste URL**:
- **App Store Connect** → **App Information** → **Privacy Policy URL**

**📖 Full Guide**: [docs/hosting-privacy-policy.md](hosting-privacy-policy.md)

---

### 2. ✅ iPad 13" Screenshots (1-2 hours)

**Action**: Generate 3-5 screenshots at 2048x2732 or 2732x2048 resolution

**Quick Steps**:
```bash
# 1. Launch iPad Pro 13" simulator
open -a Simulator
# Select iPad Pro 12.9" (6th gen) or iPad Pro 13" from list

# 2. Build and run app
./gradlew :composeApp:embedAndSignAppleFrameworkForXcode
open iosApp/iosApp.xcodeproj
# In Xcode: Select iPad simulator → Run (Cmd+R)

# 3. Navigate to key screens:
# - Home (level selection)
# - Quiz (question with answers)
# - Result (score)
# - Leaderboard

# 4. Capture screenshots (Cmd+S in Simulator)

# 5. Verify resolution
sips -g pixelWidth -g pixelHeight ~/Desktop/Simulator*.png
# Should show: 2048 x 2732 or 2732 x 2048
```

**Where to Upload**:
- **App Store Connect** → **App Store** → **App Previews and Screenshots**
- Select **iPad Pro (6th Gen) 12.9"**
- Drag and drop screenshots

**📖 Full Guide**: [docs/generating-ipad-screenshots.md](generating-ipad-screenshots.md)

---

### 3. ✅ App Privacy Practices (20 min)

**Action**: Admin must complete privacy questionnaire in App Store Connect

**Reference Data** (from privacy-policy.md):

| Data Type | Collected? | Linked to User? | Used for Tracking? |
|-----------|------------|-----------------|-------------------|
| User ID (Anonymous UID) | Yes | No | No |
| Nickname | Yes (optional) | Yes | No |
| Game Score/Stats | Yes | Yes | No |
| Crash Data | Yes | No | No |
| Performance Data (Analytics) | Yes | No | No |
| Email, Phone, Name | ❌ No | N/A | N/A |
| Location | ❌ No | N/A | N/A |

**Key Points**:
- Anonymous authentication (no personal data)
- Nickname is user-provided and not verified
- Firebase Analytics = anonymous, technical data only
- No data sold to third parties

**Where to Complete**:
- **App Store Connect** → **App Privacy**
- Click **Get Started** and follow questionnaire

---

## Medium Priority (App Store Connect Portal)

### 4. ✅ Primary Category (5 min)

**Action**: Select app category

**Recommendation**:
- **Primary**: Education
- **Secondary** (optional): Reference

**Where**:
- **App Store Connect** → **App Information** → **Category**

---

### 5. ✅ Content Rights (5 min)

**Action**: Declare content ownership

**Answer**:
- "Does your app contain, show, or access third-party content?"
- **Answer**: **No** (all quiz questions are proprietary)

**Where**:
- **App Store Connect** → **App Information** → **Content Rights**

---

### 6. ✅ Age Ratings (10 min)

**Action**: Complete age rating questionnaire

**Answers** (all NO):
- Cartoon/Fantasy Violence: No
- Realistic Violence: No
- Sexual Content: No
- Profanity: No
- Alcohol/Drugs: No
- Horror: No
- Gambling: No
- Unrestricted Web Access: No

**Expected Result**: **4+** (All Ages)

**Where**:
- **App Store Connect** → **App Information** → **Age Rating**

---

### 7. ✅ Export Compliance (5 min)

**Action**: Declare encryption usage

**When prompted** (during build submission):
- "Does your app use encryption?" → **Yes**
- "Does it qualify for an exemption?" → **Yes**
- "Proprietary algorithms?" → **No**
- "Encryption for purposes outside the app?" → **No**

**Why**: App uses only HTTPS (TLS) → standard encryption → **exempt**

**Alternative** (future builds): Add to Info.plist
```xml
<key>ITSAppUsesNonExemptEncryption</key>
<false/>
```

**📖 Full Guide**: [docs/export-compliance-ios.md](export-compliance-ios.md)

---

### 8. ✅ Price Tier (5 min)

**Action**: Set pricing

**Recommendation**:
- **Tier 0** (Free)
- **Availability**: All Territories

**Rationale**: Educational app, no monetization

**Where**:
- **App Store Connect** → **Pricing and Availability**

---

### 9. ⚠️ Game Center Key (2 min)

**Action**: Verify Game Center is disabled

**Analysis**:
- ❌ App does **NOT** use Game Center
- ✅ Ranking is implemented via Firebase Firestore
- ❌ Do NOT add `com.apple.developer.game-center` entitlement

**Where to Check**:
- **App Store Connect** → **Features** → **Game Center**
- Ensure it's **disabled/not configured**

---

## Execution Checklist

### Phase 1: Hosting & Assets (Required)
- [x] GitHub Pages enabled and hosting privacy policy
- [ ] Generate 3-5 iPad Pro 13" screenshots (2048x2732)
- [x] Privacy policy URL verified and live
- [ ] Verify screenshots meet resolution requirements

### Phase 2: App Store Connect Portal (Required)
- [ ] Add Privacy Policy URL
- [ ] Upload iPad screenshots
- [ ] Select Primary Category (Education)
- [ ] Fill Content Rights (No third-party content)
- [ ] Complete Age Rating questionnaire (4+)
- [ ] Declare Export Compliance (Exempt)
- [ ] Set Price Tier (Free)
- [ ] Verify Game Center is disabled
- [ ] **Admin**: Complete App Privacy questionnaire

### Phase 3: Re-submit
- [ ] Click **Submit for Review**
- [ ] Monitor submission status
- [ ] Respond to any review feedback within 24 hours

---

## Timeline Estimate

| Task | Time | Status |
|------|------|--------|
| Host privacy policy | 30 min | ✅ DONE |
| Generate screenshots | 1-2 hours | ⏳ Next |
| Portal configuration | 1 hour | ⏳ Next |
| **Total** | **2.5-3.5 hours** | **1/3 complete** |

---

## After Submission

### Expected Review Time
- **Standard**: 1-3 days
- **Expedited** (if requested): 1 day

### Possible Rejection Reasons
1. **Privacy policy not accessible** → Verify HTTPS URL works
2. **Screenshots don't match app** → Re-capture from real app
3. **Age rating incorrect** → Re-evaluate if quiz content changes
4. **Missing functionality** → Ensure app works on iPad (test first)

### If Rejected
1. Read feedback carefully
2. Fix issues
3. Increment build number
4. Re-submit

---

## Quick Reference Links

| Document | Purpose |
|----------|---------|
| [app-store-submission-checklist.md](app-store-submission-checklist.md) | Detailed checklist with context |
| [hosting-privacy-policy.md](hosting-privacy-policy.md) | How to host privacy-policy.md |
| [generating-ipad-screenshots.md](generating-ipad-screenshots.md) | Screenshot capture guide |
| [export-compliance-ios.md](export-compliance-ios.md) | Export compliance details |

---

## Commands to Run Now

```bash
# 1. Privacy policy hosting ✅ DONE
# URL: https://jordanrafalm.github.io/quiz-pszczelarski-kmp/privacy-policy.html
# Source: main branch, /docs folder
# Status: Live (HTTPS, Water.css styling)

# 2. Generate screenshots
open -a Simulator
./gradlew :composeApp:embedAndSignAppleFrameworkForXcode
open iosApp/iosApp.xcodeproj
# Run on iPad Pro 13" simulator → Capture screenshots (Cmd+S)

# 3. Upload to App Store Connect (use web interface)
# https://appstoreconnect.apple.com/

# 4. Submit for review
```

---

## Next Steps

1. ✅ **Start with privacy policy hosting** (easiest, unblocks future steps)
2. ✅ **Generate screenshots** (most time-consuming)
3. ✅ **Complete portal forms** (15-20 min total)
4. ✅ **Submit for review**

---

## Need Help?

- **Technical issues**: Check [README.md](../README.md) for build instructions
- **App Store Connect**: [Apple Support](https://developer.apple.com/support/)
- **Privacy questions**: Review [privacy-policy.md](../privacy-policy.md)

---

**Good luck with the submission! 🐝**
