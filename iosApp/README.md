# Metan Mobile iOS

Откройте `MetanMobile.xcodeproj` в Xcode. Kotlin framework собирается автоматически shell phase:

```text
./gradlew :composeApp:embedAndSignAppleFrameworkForXcode
```

Общий Compose UI находится в `composeApp/src/commonMain`, а SwiftUI-хост — в `MetanMobileApp`.
