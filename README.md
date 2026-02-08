# QuickScan – QR & Barcode Scanner

A professional, production-ready Android application for scanning QR codes and barcodes with premium features.

## Features

### Free Tier
- ✅ Scan all QR code and barcode formats (QR_CODE, EAN, UPC, CODE_128, etc.)
- ✅ View scan results with smart type detection (URL, Email, Phone, WiFi, etc.)
- ✅ Copy, share, and open scanned content
- ✅ Scan history (limited to 20 scans)
- ✅ Camera flash toggle
- ✅ Material 3 Design

### PRO Tier (₹299/year)
- 🌟 Unlimited scan history
- 🌟 QR Code Generator
- 🌟 Batch scanning mode
- 🌟 Export history to CSV
- 🌟 App Lock (PIN + Biometric authentication)
- 🌟 Dark mode customization
- 🌟 No upgrade banners
- 🌟 Priority support

## Tech Stack

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Design:** Material 3
- **Architecture:** MVVM
- **Database:** Room
- **Camera:** CameraX
- **Barcode Scanning:** ML Kit
- **Billing:** Google Play Billing v6.1.0
- **Security:** Biometric API
- **QR Generation:** ZXing

## Requirements

- Android SDK 24+ (Android 7.0+)
- Target SDK: 34 (Android 14)
- Gradle: 8.2
- Kotlin: 1.9.22
- AGP: 8.2.2

## Project Structure

```
QuickScan/
├── app/
│   ├── src/main/
│   │   ├── java/com/zaheer/quickscan/
│   │   │   ├── QuickScanApplication.kt
│   │   │   ├── MainActivity.kt
│   │   │   ├── ui/
│   │   │   │   ├── theme/          # Material 3 theming
│   │   │   │   ├── screens/        # 9 Compose screens
│   │   │   │   ├── components/     # Reusable UI components
│   │   │   │   └── navigation/     # Navigation graph
│   │   │   ├── viewmodel/          # 5 ViewModels (MVVM)
│   │   │   ├── data/
│   │   │   │   ├── database/       # Room database
│   │   │   │   ├── repository/     # Data repositories
│   │   │   │   └── model/          # Data models
│   │   │   ├── util/               # Utility classes
│   │   │   └── billing/            # Billing manager
│   │   ├── res/                    # Android resources
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## Setup Instructions

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/QuickScan-QR-Barcode-Scanner.git
cd QuickScan-QR-Barcode-Scanner
```

### 2. Open in Android Studio
- Open Android Studio (Hedgehog 2023.1.1 or later recommended)
- Select "Open an Existing Project"
- Navigate to the cloned directory and select it

### 3. Sync Gradle
Android Studio will automatically sync Gradle. If not, click:
```
File > Sync Project with Gradle Files
```

### 4. Configure Google Play Billing (Optional)
To test in-app purchases:
1. Create a Google Play Console account
2. Create an app in the console
3. Set up a subscription product:
   - Product ID: `quickscan_pro_yearly`
   - Base Plan ID: `yearly_base`
   - Price: ₹299/year
4. Upload a signed APK/AAB to internal testing
5. Add test users in Google Play Console

### 5. Run the App
- Connect an Android device or start an emulator (API 24+)
- Click the "Run" button in Android Studio
- Or use command line:
```bash
./gradlew assembleDebug
./gradlew installDebug
```

## Building for Production

### Generate Signed APK/AAB
1. Create a keystore (if you don't have one):
```bash
keytool -genkey -v -keystore quickscan.keystore -alias quickscan -keyalg RSA -keysize 2048 -validity 10000
```

2. Add to `local.properties`:
```properties
RELEASE_STORE_FILE=../quickscan.keystore
RELEASE_STORE_PASSWORD=your_store_password
RELEASE_KEY_ALIAS=quickscan
RELEASE_KEY_PASSWORD=your_key_password
```

3. Build release:
```bash
./gradlew assembleRelease
# or for AAB (Google Play)
./gradlew bundleRelease
```

Output location:
- APK: `app/build/outputs/apk/release/`
- AAB: `app/build/outputs/bundle/release/`

## Permissions

The app requires the following permissions:
- `CAMERA` - For scanning QR codes and barcodes
- `VIBRATE` - For haptic feedback on scan
- `INTERNET` - For Google Play Billing
- `POST_NOTIFICATIONS` - For scan notifications (optional)

## Key Features Implementation

### 1. Camera Scanning (CameraX + ML Kit)
- Real-time barcode detection
- Support for all major formats
- Auto-focus and flash control

### 2. Smart Result Parsing
- Detects content type (URL, Email, Phone, WiFi, etc.)
- Provides contextual actions (Open URL, Call, Send Email)
- WiFi QR code parsing

### 3. History Management
- Room database for offline storage
- Search and filter functionality
- Favorite marking
- Free tier: 20 scans limit
- Pro tier: Unlimited scans

### 4. QR Code Generator (PRO)
- Generate QR codes from text/URLs
- Save to device storage
- Customizable size

### 5. CSV Export (PRO)
- Export all scan history
- SAF (Storage Access Framework) integration
- Formatted with timestamps

### 6. App Lock (PRO)
- 4-digit PIN protection
- Biometric authentication (fingerprint/face)
- DataStore for secure storage

### 7. Google Play Billing v6
- Subscription management
- Purchase verification
- Restore purchases functionality
- Proper acknowledgment flow

## Testing

### Unit Testing
```bash
./gradlew test
```

### Instrumentation Testing
```bash
./gradlew connectedAndroidTest
```

### Manual Testing Checklist
- [ ] Camera permission flow
- [ ] Scan QR code and barcodes
- [ ] View and interact with results
- [ ] Add to history
- [ ] Search history
- [ ] Mark as favorite
- [ ] Delete scan
- [ ] Generate QR code (PRO)
- [ ] Export to CSV (PRO)
- [ ] Set up app lock (PRO)
- [ ] Purchase subscription
- [ ] Restore purchases
- [ ] Dark mode (PRO)

## Known Limitations

1. **Billing Testing**: In-app purchases require Google Play Console setup and internal testing
2. **Camera**: Requires physical device for real camera testing (emulator has limited support)
3. **Biometric**: Not available in emulators without special setup

## Troubleshooting

### Gradle Sync Failed
- Ensure you have Java 17 installed
- Clear Gradle cache: `./gradlew clean`
- Invalidate caches: `File > Invalidate Caches / Restart`

### Camera Not Working
- Check camera permissions in device settings
- Ensure CameraX dependencies are synced
- Test on physical device (emulator camera has limitations)

### Billing Not Working
- Ensure app is uploaded to Google Play Console
- Add test account in Google Play Console
- Use real device (not emulator) for billing testing
- Wait 2-4 hours after uploading for billing to activate

### Build Failed
- Check minimum SDK (24) is met
- Ensure all dependencies are compatible
- Run: `./gradlew clean build`

## Contributing

Contributions are welcome! Please follow these steps:
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Privacy & Data

- All scan data is stored locally on the device
- No data is sent to external servers (except Google Play Billing)
- Users can delete their data anytime
- No analytics or tracking

## Support

For support, please contact:
- Email: support@quickscan.app
- GitHub Issues: [Create an issue](https://github.com/yourusername/QuickScan-QR-Barcode-Scanner/issues)

## Acknowledgments

- Google ML Kit for barcode scanning
- ZXing for QR code generation
- Android CameraX for camera functionality
- Jetpack Compose for modern UI

## Version History

### 1.0.0 (Current)
- Initial release
- QR code and barcode scanning
- History management
- QR code generator (PRO)
- CSV export (PRO)
- App lock (PRO)
- Google Play Billing integration
- Dark mode (PRO)

---

**Built with ❤️ using Kotlin & Jetpack Compose**
