# Export Compliance Configuration (iOS)

> Guide for handling US export compliance requirements for encryption in iOS apps

---

## Background

### What is Export Compliance?

US law regulates the export of encryption technology. Apps submitted to the App Store must declare:
1. Whether they use encryption
2. If yes, whether they qualify for an exemption

### Does Quiz Pszczelarski Use Encryption?

| Technology | Uses Encryption? | Exempt? |
|------------|------------------|---------|
| HTTPS (TLS/SSL) | Yes | ✅ Yes — Standard encryption |
| Firebase SDK | Yes (internal) | ✅ Yes — Standard encryption |
| Custom Crypto | No | N/A |
| VPN | No | N/A |

**Conclusion**: App uses **only standard encryption** → Qualifies for exemption

---

## Option 1: Declare in App Store Connect (Current Approach)

### When Submitting Build
1. Upload build via Xcode or Transporter
2. Go to **App Store Connect** → **TestFlight** → Select build
3. You'll see: **"Missing Export Compliance"**
4. Click **Provide Export Compliance Information**
5. Answer questions:

**Question 1**: "Is your app designed to use cryptography or does it contain or incorporate cryptography?"
- **Answer**: **Yes**

**Question 2**: "Does your app qualify for any of the exemptions provided in Category 5, Part 2 of the U.S. Export Administration Regulations?"
- **Answer**: **Yes**

**Question 3**: "Does your app implement any encryption algorithms that are proprietary or not accepted as standard by international standards bodies?"
- **Answer**: **No**

**Question 4**: "Does your app use encryption for purposes other than within the app?"
- **Answer**: **No**

6. Click **Start Internal Testing** or **Submit for Review**

### Advantages
- ✅ No code changes required
- ✅ Flexible (can change answers later)

### Disadvantages
- ❌ Must complete questionnaire for **every build**
- ❌ Cannot automate

---

## Option 2: Add to Info.plist (Recommended for Production)

### Configuration

Add to iOS app's `Info.plist` or Xcode build settings.

#### Method A: Generate Info.plist File

Currently, the project uses `GENERATE_INFOPLIST_FILE = YES`, so no `Info.plist` exists. To add custom keys:

**Create file**: `iosApp/iosApp/Info.plist`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>ITSAppUsesNonExemptEncryption</key>
    <false/>
</dict>
</plist>
```

**Update Xcode project**: `iosApp/iosApp.xcodeproj/project.pbxproj`

In build settings, change:
```
GENERATE_INFOPLIST_FILE = YES;
```
to:
```
GENERATE_INFOPLIST_FILE = NO;
INFOPLIST_FILE = iosApp/Info.plist;
```

#### Method B: Add to Xcode Build Settings (Easier)

1. Open `iosApp/iosApp.xcodeproj` in Xcode
2. Select **iosApp** target
3. **Build Settings** tab
4. Search for: **Info.plist Values**
5. Click **+** → **Add User-Defined Setting**
6. Key: `ITSAppUsesNonExemptEncryption`
7. Value: `NO`

This automatically adds the key to the generated `Info.plist` at build time.

---

## Key Explanation

### `ITSAppUsesNonExemptEncryption`

| Value | Meaning | When to Use |
|-------|---------|-------------|
| `false` or `NO` | App uses **only exempt encryption** (HTTPS, standard algos) | ✅ Quiz Pszczelarski |
| `true` or `YES` | App uses **non-exempt encryption** (custom crypto, VPN, etc.) | ❌ Not applicable |
| (key missing) | Must complete questionnaire in App Store Connect | Default state |

---

## Recommended Approach for This Project

### Phase 1: Current Submission ✅
- Use **Option 1** (App Store Connect questionnaire)
- No code changes required
- Fastest path to submission

### Phase 2: Future Builds (Optional)
- Add **Option 2** (Info.plist key)
- Prevents recurring questionnaire prompts
- Simplifies CI/CD automation

---

## Implementation: Add to Build Settings

### File to Modify
`iosApp/iosApp.xcodeproj/project.pbxproj`

### Change Required

Locate the section:
```
/* Begin XCBuildConfiguration section */
```

Find both `Debug` and `Release` configurations, and add:

```
INFOPLIST_KEY_ITSAppUsesNonExemptEncryption = NO;
```

Example:
```
2152ACE52B03E5B400DE6E51 /* Debug */ = {
    isa = XCBuildConfiguration;
    buildSettings = {
        ASSETCATALOG_COMPILER_APPICON_NAME = AppIcon;
        CODE_SIGN_STYLE = Automatic;
        DEVELOPMENT_TEAM = XXXXXXXXXX;
        ENABLE_PREVIEWS = YES;
        GENERATE_INFOPLIST_FILE = YES;
        INFOPLIST_KEY_ITSAppUsesNonExemptEncryption = NO;  // <-- ADD THIS
        INFOPLIST_KEY_UIApplicationSceneManifest_Generation = YES;
        // ... other settings ...
    };
};
```

Repeat for `Release` configuration.

---

## Verification

### Test Locally
```bash
# Build app
./gradlew :composeApp:embedAndSignAppleFrameworkForXcode
cd iosApp
xcodebuild -scheme iosApp -configuration Release archive

# Verify Info.plist in built app
unzip -p iosApp.app/Info.plist | grep -A1 ITSAppUsesNonExemptEncryption
```

Expected output:
```xml
<key>ITSAppUsesNonExemptEncryption</key>
<false/>
```

### Test in TestFlight
1. Upload build with new configuration
2. Go to **App Store Connect** → **TestFlight** → Build
3. **Export Compliance** should show: ✅ **Not Required** or auto-approved

---

## When to Use `true` (Non-Exempt)

Use `ITSAppUsesNonExemptEncryption = YES` if app:
- Implements **custom encryption** algorithms (not AES, RSA, etc.)
- Uses **VPN** or **tunneling** protocols
- Encrypts data **outside the app** (e.g., sends encrypted messages to other users)
- Uses **proprietary cryptography**

**Quiz Pszczelarski does NOT do any of this** → Use `NO`

---

## Additional Resources

- [Apple — Complying with Encryption Export Regulations](https://developer.apple.com/documentation/security/complying_with_encryption_export_regulations)
- [Category 5 Part 2 Exemptions (PDF)](https://www.bis.doc.gov/index.php/documents/regulation-docs/412-part-740-license-exceptions/file)
- [App Store Connect Help — Export Compliance](https://help.apple.com/app-store-connect/#/dev63c95e436)

---

## Summary

### Current State
- ❌ Export compliance key **not** in `Info.plist`
- ✅ Can declare in App Store Connect (manual)

### Recommendation for This Submission
- Use **Option 1** (App Store Connect) — no code changes
- Add `ITSAppUsesNonExemptEncryption = NO` in **Phase 2** (future optimization)

### If Adding to Code Later
- Modify: `iosApp/iosApp.xcodeproj/project.pbxproj`
- Add: `INFOPLIST_KEY_ITSAppUsesNonExemptEncryption = NO;` to Debug and Release configs
- Commit and rebuild
- Future builds will auto-declare exemption
