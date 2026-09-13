# NSFW — XRPL Companion (Android)

Native Android-App für das [XRDOGE-XRPL](https://github.com/XRDOGE-XRPL)-Ökosystem. Read-only Ledger-Companion: Netzwerkstatus, Konto-Lookup, Trustlines und Token-Filter.

Kein Wallet, kein Signing, keine Seeds. Es werden nur öffentliche XRPL-RPC-Daten gelesen.

## Features

- Home: XRPL-Node-Status, Ledger-Index, XRP-Preis (USD/EUR)
- Explorer: Classic-Address (`r…`) Lookup, XRP-Balance, Sequence, letzte Transaktionen
- Tokens: Trustlines inkl. NSFW-Währungsfilter (Issuer in den Einstellungen)
- Settings: öffentlicher Node, gespeichertes Konto, Token-Code/Issuer

## Stack

- Kotlin, Jetpack Compose, Material 3
- minSdk 26 / targetSdk 35
- OkHttp → `xrplcluster.com` (oder Ripple `s1`/`s2`)
- DataStore für Einstellungen

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

## Konfiguration

In der App unter **Settings**:

1. RPC-Node wählen
2. Optional Standard-Konto (Classic Address) speichern
3. Token-Währung (z. B. `NSFW`) und Issuer setzen

## Sicherheit

- Keine Private Keys, Mnemonics oder Seeds
- Nur HTTPS zu öffentlichen APIs
- Lookups validieren Classic Addresses (`r` + Base58 ohne `0OIl`)
