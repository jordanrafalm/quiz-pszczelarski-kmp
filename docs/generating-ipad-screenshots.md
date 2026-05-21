# Generating iPad Screenshots for App Store

> Guide for capturing 13-inch iPad Pro screenshots for App Store Connect submission

---

## Requirements

### Display Sizes Required by App Store

| Device | Resolution (Portrait) | Resolution (Landscape) |
|--------|----------------------|------------------------|
| iPad Pro 13" (6th Gen) | 2048 x 2732 | 2732 x 2048 |
| iPad Pro 12.9" (6th Gen) | 2048 x 2732 | 2732 x 2048 |

**Minimum**: 1 screenshot  
**Recommended**: 3-5 screenshots

---

## Option 1: iOS Simulator (Recommended)

### Prerequisites
- Xcode 15+ installed
- KMP project builds successfully

### Steps

#### 1. List Available Simulators
```bash
xcrun simctl list devices available | grep -i "iPad Pro"
```

Look for:
- `iPad Pro (12.9-inch) (6th generation)`
- `iPad Pro 13-inch (M4)`

#### 2. Launch Simulator
```bash
# Replace <DEVICE_NAME> with exact name from step 1
open -a Simulator --args -CurrentDeviceUDID $(xcrun simctl list devices | grep "iPad Pro 13" | head -1 | grep -oE '\([A-F0-9-]+\)' | tr -d '()')
```

Or manually:
- Open **Xcode** → **Window** → **Devices and Simulators** → **Simulators**
- Click **+** to add iPad Pro 13" if not present
- Right-click device → **Open with Simulator**

#### 3. Build and Run App
```bash
cd /Users/rafal/projects/quiz-pszczelarski-kmp

# Build framework
./gradlew :composeApp:embedAndSignAppleFrameworkForXcode

# Open Xcode project
open iosApp/iosApp.xcodeproj
```

In Xcode:
- Select iPad Pro 13" simulator as target
- Click **Run** (Cmd+R)

#### 4. Navigate to Key Screens
Capture screenshots of:
1. **Home Screen** (level selection)
2. **Quiz Screen** (question with answers)
3. **Result Screen** (score display)
4. **Leaderboard Screen** (ranking)
5. **Settings Screen** (optional)

#### 5. Capture Screenshots
**Method A: Simulator Menu**
- **Device** → **Take Screenshot** (Cmd+S)
- Screenshots save to **Desktop**

**Method B: macOS Screenshot**
- Press **Cmd+Shift+4**
- Press **Spacebar** → Click simulator window
- Saves to Desktop as `.png`

#### 6. Verify Resolution
```bash
cd ~/Desktop
sips -g pixelWidth -g pixelHeight Screenshot*.png
```

Should show: `2048 x 2732` or `2732 x 2048`

---

## Option 2: Real iPad Device

### Prerequisites
- iPad Pro 12.9" or 13" physically available
- Provisioning profile configured

### Steps

#### 1. Connect iPad to Mac
- Use USB-C cable
- Trust device if prompted

#### 2. Open Xcode Project
```bash
cd /Users/rafal/projects/quiz-pszczelarski-kmp
open iosApp/iosApp.xcodeproj
```

#### 3. Build and Run on Device
- Select your iPad as target device
- Click **Run** (Cmd+R)
- Wait for app to launch on iPad

#### 4. Capture Screenshots
**Method A: Xcode Devices Window**
- **Window** → **Devices and Simulators** → **Devices**
- Select your iPad
- Click **Take Screenshot** button
- Screenshot appears in Xcode → Right-click → **Export...**

**Method B: iPad Native Screenshot**
- Press **Top Button + Volume Up** simultaneously
- Screenshot saves to Photos app
- AirDrop or sync to Mac

---

## Option 3: Figma Design Mockups

### When to Use
- App not fully functional yet
- Need polished, marketing-quality screenshots
- Want to add text overlays or highlights

### Steps

#### 1. Create Frame in Figma
- **New Frame** → **Custom Size**
- Width: `2048px`, Height: `2732px` (portrait)
- Or Width: `2732px`, Height: `2048px` (landscape)

#### 2. Design Screens
- Place app screenshots or design mockups
- Add text overlays (e.g., "Learn beekeeping interactively")
- Add highlights or annotations

#### 3. Export
- Select frame
- **Export** → **PNG**
- **Scale**: 1x (keep original resolution)
- Export

---

## Option 4: Automated Screenshot Generation (Optional)

### Using XCUITest (Advanced)
If you plan frequent updates and need automation:

#### 1. Add UI Test Target
In Xcode:
- **File** → **New** → **Target** → **UI Testing Bundle**

#### 2. Write Screenshot Test
```swift
import XCTest

class ScreenshotTests: XCTestCase {
    override func setUpWithError() throws {
        continueAfterFailure = false
        let app = XCUIApplication()
        setupSnapshot(app)
        app.launch()
    }
    
    func testGenerateScreenshots() throws {
        let app = XCUIApplication()
        
        // Home screen
        snapshot("01-Home")
        
        // Navigate to quiz
        app.buttons["Start Quiz"].tap()
        snapshot("02-Quiz")
        
        // Complete quiz and show result
        // ... interact with UI ...
        snapshot("03-Result")
        
        // Leaderboard
        app.buttons["Leaderboard"].tap()
        snapshot("04-Leaderboard")
    }
}
```

#### 3. Run Test
```bash
xcodebuild test \
  -scheme iosApp \
  -destination 'platform=iOS Simulator,name=iPad Pro 13-inch (M4)' \
  -resultBundlePath ./screenshots
```

---

## Best Practices for App Store Screenshots

### Content Guidelines
1. **Show Real App UI** — no mockups, photoshopped elements
2. **Localization** — if app supports Polish, show Polish UI
3. **Privacy** — no real user data (use test accounts)
4. **No Offensive Content** — Apple rejects inappropriate screenshots

### Composition Tips
1. **Key Features** — highlight unique selling points:
   - Multiple quiz levels
   - Leaderboard
   - Offline support
   - Beautiful design
2. **Text Overlays** (optional) — short descriptions in Polish:
   - "Ucz się pszczelarstwa w zabawny sposób"
   - "Rywalizuj w globalnym rankingu"
3. **Order** — first screenshot is most important (shows in search)

### Technical Requirements
- **Format**: PNG or JPEG
- **Color Space**: sRGB or Display P3
- **No Alpha Channel** (no transparency)
- **File Size**: < 10 MB per file

---

## Quick Command Summary

```bash
# 1. List simulators
xcrun simctl list devices | grep "iPad Pro"

# 2. Launch simulator (adjust UDID)
open -a Simulator

# 3. Build framework
./gradlew :composeApp:embedAndSignAppleFrameworkForXcode

# 4. Open Xcode
open iosApp/iosApp.xcodeproj

# 5. Run app on simulator (Cmd+R in Xcode)

# 6. Take screenshot (Cmd+S in Simulator)

# 7. Verify screenshot resolution
sips -g pixelWidth -g pixelHeight ~/Desktop/Screenshot*.png
```

---

## Upload to App Store Connect

### Steps

1. Go to **App Store Connect** → **Your App** → **App Store**
2. Click **App Store** tab (if not selected)
3. Scroll to **App Previews and Screenshots**
4. Select **iPad Pro (6th Gen) 12.9"** or **iPad Pro 13"**
5. Click **+** icon
6. Select screenshots from Finder
7. Drag to reorder (first screenshot shows in search results)
8. Add **Promotional Text** (optional) for each screenshot:
   - "Wybierz poziom trudności"
   - "Sprawdź swoją wiedzę"
   - "Rywalizuj z innymi pszczelarzami"
9. Click **Save**

---

## Troubleshooting

### "Simulator doesn't match required resolution"
- Use iPad Pro 12.9" (6th gen) or newer
- **NOT** iPad Air or smaller models

### "Screenshot file too large"
```bash
# Compress PNG without losing quality
pngquant --quality=85-95 screenshot.png
```

### "App crashes on iPad"
- Check layout constraints in Compose
- Test with larger screen in Android Studio (tablet preview)
- Check logs: `xcrun simctl spawn booted log stream --level=debug`

---

## Next Steps

1. ✅ Capture 3-5 key screens on iPad Pro 13" simulator
2. ✅ Verify resolution: 2048x2732 or 2732x2048
3. ✅ Upload to App Store Connect
4. ✅ Add promotional text (optional)
5. ✅ Save and re-submit for review
