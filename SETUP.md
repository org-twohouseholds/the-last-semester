# Einrichtung

## 1. Projekt öffnen

Du brauchst [IntelliJ IDEA](https://www.jetbrains.com/idea/download/)
und Zugriff auf das GitHub-Repository.

1. Im IntelliJ-Startfenster **Clone Repository** wählen.
2. Bei **URL** `https://github.com/org-twohouseholds/the-last-semester.git`
   eintragen, einen Zielordner wählen und **Clone** anklicken.
   Bei Bedarf bei GitHub anmelden und die angebotene Git-Installation bestätigen.
3. Das geklonte Projekt öffnen, **Trust Project** bestätigen und als
   Gradle-Projekt laden.

## 2. Java 17 einstellen

- **File → Project Structure → Project → SDK:** JDK 17 auswählen.
  Falls es fehlt: **Add SDK → Download JDK → Version 17**.
- **Settings → Build Tools → Gradle:** **Distribution** auf **Wrapper** und
  **Gradle JVM** auf JDK 17 setzen.

Gradle und libGDX werden automatisch heruntergeladen.

## 3. Desktop starten

In IntelliJ das **Gradle-Fenster** öffnen (**View → Tool Windows → Gradle**).
Unter **lwjgl3 → Tasks → application** auf **run** doppelklicken.

Beim ersten Start dauern die Downloads etwas. Der Task läuft, bis du das
Spielfenster schließt.

## Android (optional)

1. [Android Studio](https://developer.android.com/studio) installieren.
   Den Einrichtungsassistenten mit den Standardoptionen abschließen.
2. Über **Open** denselben Projektordner `the-last-semester` öffnen und den
   **Gradle-Sync** abwarten. Angebotene SDK-Downloads und Lizenzabfragen bestätigen.
3. **View → Tool Windows → Device Manager → + → Create Virtual Device** öffnen.
   Ein Smartphone wählen, ein empfohlenes Systemimage herunterladen und den
   Emulator starten. Alternativ ein Android-Gerät mit USB-Debugging anschließen.
4. Oben die Run-Konfiguration **android** und das Zielgerät auswählen, dann
   **▶ Run** anklicken. Android Studio baut, installiert und startet die App.

Nach Codeänderungen einfach erneut **▶ Run** anklicken.

**APK zum Weitergeben:** In **Build Variants** für `android` die Variante **debug**
wählen, dann **Build → Generate Bundle(s) / APK(s) → Generate APK(s)** anklicken.
Die Datei liegt unter `android/build/outputs/apk/debug/android-debug.apk`.

**Fertige APK ohne lokalen Build:** Im GitHub-Repository unter
**Actions → Android Build → erfolgreicher Lauf → Artifacts → `the-last-semester-debug`**
herunterladen und die ZIP entpacken.

<details>
<summary>Optional: Git- und Gradle-Befehle im Terminal</summary>

Repository klonen:

```bash
git clone https://github.com/org-twohouseholds/the-last-semester.git
```

Gradle-Befehle im Projektordner ausführen:

| Ziel | macOS / Linux | Windows (PowerShell) |
| --- | --- | --- |
| Desktop starten | `./gradlew lwjgl3:run` | `.\gradlew.bat lwjgl3:run` |
| APK bauen | `./gradlew android:assembleDebug` | `.\gradlew.bat android:assembleDebug` |
| App installieren oder aktualisieren | `./gradlew android:installDebug` | `.\gradlew.bat android:installDebug` |

Für `installDebug` muss ein Emulator laufen oder ein Android-Gerät verbunden sein.
Danach die App auf dem Gerät öffnen.

</details>
