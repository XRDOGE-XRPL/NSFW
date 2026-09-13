# NSFW — Creatorin/Adult/Plattform (Android)

Native Android-App für eine NSFW Creatorinnen-Plattform.

Keine XRPL-Funktionen: Die App fokussiert auf Creator-Discovery, Content-Feed und Subscription-Tiers.

## Features

- Home: Plattform-Dashboard mit Creator-, Post- und Profilstatus
- Creator Hub: Suche/Kuratierung von Creatorinnen nach Name, Nische oder Experience-Tags
- Favoriten & DM-Drafts: Creatorinnen merken und Story-/Voice-Intro-Anfragen vorbereiten
- Content: Feed + auswählbare Subscription-Tiers mit Feature-Vergleich
- Settings: Creator-Alias, Plattformmodus, DM- und Preview-Policy

## Stack

- Kotlin, Jetpack Compose, Material 3
- minSdk 26 / targetSdk 35
- DataStore für lokale Plattform-Einstellungen

## Bauen

Voraussetzungen: JDK 17+, Android SDK (`ANDROID_HOME`).

```bash
./gradlew assembleDebug
```

Windows:

```bat
gradlew.bat assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

Release:

```bash
./gradlew assembleRelease
```

## Sicherheit

- Keine Private Keys, Mnemonics oder Seeds
- Keine Blockchain-/Wallet-Funktionen in der App aktiv
