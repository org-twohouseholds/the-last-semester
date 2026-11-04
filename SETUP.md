# The Last Semester – Einrichtung für neue Teammitglieder

> 💡 **Tipp:** Für eine übersichtliche Darstellung öffne diese Datei in IntelliJ über den Projektbaum (Doppelklick auf `SETUP.md`) und wähle oben rechts **Preview**. So siehst du die fertig formatierte Anleitung statt des Markdown-Codes.

**Eine Anleitung für Windows, macOS und Linux.** Wir verwenden IntelliJ IDEA, Java 17 und den im Projekt enthaltenen Gradle Wrapper. **Gradle und libGDX werden nicht separat installiert.**

**Was muss ich einrichten?**
- **Alle:** Repository herunterladen, in IntelliJ öffnen, JDK 17 auswählen, Desktop-Version starten (Schritte 1–4).
- **Nur für lokale Android-Builds oder Android-Tests:** Zusätzlich Android SDK einrichten (Schritte 5–7). **Nicht jeder braucht Android Studio oder einen Emulator.**
- **Nur eine APK ausprobieren?** Im GitHub-Tab **Actions** gibt es bereits automatisch erstellte APK-Artefakte (Schritt 8).

## 1. IntelliJ IDEA und GitHub-Zugriff

1. **IntelliJ IDEA** herunterladen und installieren: https://www.jetbrains.com/idea/download/
   - Apple-Silicon-Macs (M1/M2/…): Version für **Apple Silicon / ARM**.
   - Intel-/AMD-Rechner: passende **x64**-Version.
2. **GitHub-Einladung** zum privaten Repository annehmen. Prüfen, dass diese Seite im Browser geöffnet werden kann:
   https://github.com/org-twohouseholds/the-last-semester
3. IntelliJ öffnen. **Noch kein eigenes Java-Projekt erstellen.**

> **Git nicht vorsorglich zusätzlich installieren:** Zuerst einfach den `git clone`-Befehl im nächsten Schritt ausprobieren. Bei uns funktionierte Git bereits im Terminal. Wenn `git` nicht erkannt wird, muss Git auf dem jeweiligen Rechner nachinstalliert werden: https://git-scm.com/downloads

## 2. Repository klonen und in IntelliJ öffnen

**Wichtig:** Bei uns schlug „Clone Repository“ direkt in IntelliJ mit `Repository not found` mehrmals fehl, obwohl wir Zugriff auf GitHub hatten. **Der Clone über das Terminal funktionierte** – deshalb verwenden wir diesen Weg.

1. Öffne ein Terminal:
   - **macOS:** App **Terminal** öffnen (über Spotlight).
   - **Windows:** **PowerShell** öffnen.
   - **Linux:** Terminal öffnen.
2. Wechsle in den Ordner, in dem du das Projekt speichern möchtest. Beispiel: deinen üblichen Studien-/Projektordner. **Nicht in einen bereits vorhandenen Ordner `the-last-semester` wechseln.**
3. Führe aus (unter allen drei Betriebssystemen gleich):

   ```bash
   git clone https://github.com/org-twohouseholds/the-last-semester.git
   ```

   **Ergebnis:** Ein neuer Ordner `the-last-semester` wird erstellt. `git clone` lädt den Code und die Git-Historie. Falls GitHub nach einer Anmeldung fragt, mit dem berechtigten Konto anmelden.
4. In **IntelliJ IDEA → File → Open…** den **neu erstellten Ordner `the-last-semester`** auswählen und öffnen. Falls angezeigt: **Trust Project** und **Load/Import Gradle Project** bestätigen.
5. Warte, bis IntelliJ das Projekt erkannt hat. Im obersten Projektordner siehst du unter anderem `gradlew`, `gradlew.bat`, `settings.gradle`, `core/`, `android/` und `lwjgl3/`.

**Merke:** Ab jetzt nutzt du das **Terminal unten in IntelliJ**. Es sollte sich bereits im Projektordner `the-last-semester` befinden. Nicht nochmal `cd the-last-semester` eingeben, wenn du schon darin bist.

## 3. Java 17 direkt in IntelliJ installieren und auswählen

Bei uns waren auf verschiedenen Rechnern Java 17 und Java 24 vorhanden. Für dieses Projekt verwenden wir **JDK 17**. Ein JDK ist das Java Development Kit (Java-Compiler und Laufzeitumgebung).

1. In IntelliJ: **File → Project Structure… → Project** öffnen.
2. Bei **SDK** → **Add SDK → Download JDK…** auswählen (falls bereits ein vollständiges JDK 17 eingetragen ist, dieses verwenden).
3. Im Download-Fenster:
   - **Version:** `17`
   - **Vendor:** `Eclipse Temurin (AdoptOpenJDK HotSpot)`
   - **Apple Silicon / M1:** Eintrag **`aarch64`** auswählen – das ist ARM64.
   - **Intel-/AMD-Rechner:** **`x86_64` / `x64`** wählen.
   - Vorgeschlagenen **Location**-Pfad beibehalten und **Download** klicken.
4. In **Project Structure → Project** prüfen:
   - **SDK:** `temurin-17`
   - **Language level:** `17`
   - **Compiler output:** nicht ändern.
   - **Apply → OK**.
5. In IntelliJ **Settings** öffnen (Mac: `⌘,`; Windows/Linux: **File → Settings**), dann:
   **Build, Execution, Deployment → Build Tools → Gradle**
6. Dort auswählen:
   - **Distribution:** `Wrapper`
   - **Gradle JVM:** `Project SDK temurin-17` (oder das bereits installierte JDK 17)
   - **Build and run using:** `Gradle (Default)`
   - **Run tests using:** `Gradle (Default)`
   - **Apply → OK**. Bei Bedarf das Gradle-Projekt neu laden.

**Temurin = Hersteller-Distribution von Java.** Ein vollständiges JDK 17 von einem anderen Hersteller funktioniert grundsätzlich ebenfalls. **`Project SDK temurin-17` und `temurin-17` im Gradle-JVM-Menü verweisen bei uns auf dieselbe Installation**, nicht auf zwei Downloads.

**Projektkonfiguration:** In `gradle/gradle-daemon-jvm.properties` soll die vereinbarte Daemon-Java-Version `toolchainVersion=17` stehen. Die Änderung muss im gemeinsamen Repository enthalten sein; Entwickler sollen diese Datei nicht jeweils individuell umschreiben.

## 4. Desktop-Version starten (alle Teammitglieder)

Öffne in IntelliJ das **Terminal** und prüfe, dass du im Ordner `the-last-semester` bist.

**macOS / Linux:**

```bash
./gradlew lwjgl3:run
```

**Windows (PowerShell):**

```powershell
.\gradlew.bat lwjgl3:run
```

- `gradlew` = mitgeliefertes Startskript für die festgelegte **Gradle-Version 9.8.0**. Gradle wird beim ersten Aufruf automatisch heruntergeladen.
- `lwjgl3` = das **Desktop-Modul** von libGDX für Windows, macOS und Linux.
- `run` = startet die Desktop-Anwendung.

**Erwartetes Ergebnis:** Das Fenster „The Last Semester“ öffnet sich und zeigt den bisherigen libGDX-Startbildschirm. Das Spiel läuft jetzt erfolgreich.

**Hinweis:** Die Gradle-Fortschrittsanzeige kann währenddessen bei etwa 90 % stehen bleiben. Das ist normal, da der `run`-Task aktiv bleibt, solange das Spiel läuft.

**Zum Beenden:** Einfach das Spielfenster schließen. Gradle beendet den `run`-Task anschließend automatisch.

Beim ersten Start können Downloads (Gradle/Bibliotheken) länger dauern. Sie werden für weitere Builds lokal zwischengespeichert.

**Wer nur Desktop, Shared oder Server entwickelt, muss für den ersten Start nichts Weiteres installieren.**

## 5. Android SDK einrichten (nur wenn du lokal APKs bauen möchtest)

**Android SDK ≠ JDK.** Das JDK kompiliert Java; das Android SDK liefert die Android-Plattform und Werkzeuge für die APK. **Ein Emulator ist zum Bauen nicht erforderlich.**

**Einfacher grafischer Weg (alle Betriebssysteme):**

1. **Android Studio** von https://developer.android.com/studio herunterladen. Auf dem M1-Mac die **Apple Silicon / ARM64**-Version verwenden. Du kannst **weiterhin IntelliJ zum Programmieren** nutzen.
2. Android Studio erstmals öffnen → im Setup-Assistenten **Custom** auswählen → **Next**.
3. Bei **SDK Components Setup**:
   - **Android SDK** auswählen bzw. ausgewählt lassen.
   - **Android Virtual Device** nur auswählen, wenn du einen Emulator nutzen willst (braucht mehr Speicher und RAM).
   - Vorhandenen SDK-Pfad beibehalten, falls Android Studio eine bestehende Installation erkennt.
   - **Next**, Lizenzbedingungen akzeptieren, **Finish**.
4. Im Android-Studio-Startfenster **More Actions → SDK Manager** öffnen.
5. Dort sicherstellen:
   - **SDK Platforms:** **Android API 36** installiert (im Projekt `android/build.gradle`: `compileSdk 36`).
   - **SDK Tools:** **Android SDK Build-Tools 35.0.0** und **Android SDK Platform-Tools** installiert. Gradle hat bei uns Build-Tools 35 beim ersten Build automatisch ergänzt.
6. Im SDK Manager den **Android SDK Location**-Pfad ablesen.
7. In IntelliJ im **Projekt-Hauptverzeichnis** (neben `settings.gradle`) die neue Datei **`local.properties`** erstellen. **Nur eine Zeile** mit deinem persönlichen SDK-Pfad einfügen, z. B.:

   **macOS (unser getesteter Pfad; Benutzername anpassen):**
   ```properties
   sdk.dir=/Users/DEIN_BENUTZERNAME/Library/Android/sdk
   ```

   **Windows (typischer Pfad; in Android Studio ablesen):**
   ```properties
   sdk.dir=C:/Users/DEIN_BENUTZERNAME/AppData/Local/Android/Sdk
   ```

   **Linux (typischer Pfad; in Android Studio ablesen):**
   ```properties
   sdk.dir=/home/DEIN_BENUTZERNAME/Android/Sdk
   ```

   **Nicht committen:** `local.properties` steht bereits in `.gitignore`, weil jeder einen anderen SDK-Pfad hat.

**Alternative ohne Android Studio (auf macOS ARM64 bei uns erfolgreich):**

1. Unter https://developer.android.com/studio#command-line-tools-only → **Command line tools only → Mac** herunterladen und ZIP entpacken.
2. Den entpackten Ordner `cmdline-tools` im Finder nach `~/Library/Android/sdk/cmdline-tools/` verschieben und dort in **`latest`** umbenennen. Ergebnis: `~/Library/Android/sdk/cmdline-tools/latest/bin/sdkmanager`.
3. Im Terminal (nur macOS):

   ```bash
   ~/Library/Android/sdk/cmdline-tools/latest/bin/sdkmanager --version
   ~/Library/Android/sdk/cmdline-tools/latest/bin/sdkmanager --sdk_root="$HOME/Library/Android/sdk" "platforms;android-36" "build-tools;35.0.0" "platform-tools"
   ```

4. Lizenzen bestätigen (`y`) und anschließend dieselbe `local.properties` wie oben erstellen. Eine Meldung, dass `sdkmanager` durch das neuere Android CLI ersetzt wird, ist aktuell eine **Warnung**, kein Installationsfehler.

## 6. Android-APK bauen (ohne Emulator)

Im IntelliJ-Terminal im Projektordner:

**macOS / Linux:**

```bash
./gradlew android:assembleDebug
```

**Windows (PowerShell):**

```powershell
.\gradlew.bat android:assembleDebug
```

- `android` = Android-Modul des Projekts.
- `assembleDebug` = Code kompilieren und eine **installierbare Test-APK** bauen. **Öffnet keinen Emulator.**

**Erwartete Datei:** `android/build/outputs/apk/debug/android-debug.apk` (bei uns etwa 4,8 MB). Dass Gradle beim ersten Android-Build weitere SDK-Build-Tools herunterlädt, ist normal.

## 7. Android-App im Emulator starten und aktualisieren (optional)

**Einmalig Emulator einrichten:**

1. Android Studio öffnen → Startfenster **More Actions → Device Manager** bzw. **Virtual Device Manager**.
2. Wenn noch kein Gerät vorhanden ist: **Create Virtual Device**, ein Smartphone wählen und ein passendes Android-Systemimage herunterladen. Auf dem M1 **ARM64 (`arm64-v8a`)** wählen. Bei uns wurde `Medium_Phone_API_37.0` mit API 37 eingerichtet; die App darf trotzdem gegen API 36 gebaut werden.
3. Beim virtuellen Gerät auf **▶** klicken, warten, bis der **Android-Startbildschirm** sichtbar ist. Gerade beim ersten Start kann das dauern.
4. Du kannst Android Studio danach schließen, **solange das Emulatorfenster geöffnet bleibt**.

**Jetzt aktuelle App bauen und installieren:**

**macOS / Linux:**

```bash
./gradlew android:installDebug
```

**Windows (PowerShell):**

```powershell
.\gradlew.bat android:installDebug
```

`installDebug` **baut die aktuelle APK und installiert sie** auf dem laufenden Emulator oder einem verbundenen Android-Gerät. Öffne danach auf Android die App „The Last Semester“.

**Nach Codeänderungen:** denselben `installDebug`-Befehl erneut ausführen – die APK aktualisiert sich nicht von selbst. **Kein erneutes manuelles `adb install` nötig.**

**Bei einem älteren oder schwächeren Laptop:** Der Emulator kann sehr viel RAM benötigen. Deshalb testen nur einzelne Teammitglieder oder ein echtes Android-Handy; alle anderen können die Desktop-Version verwenden. **Android Studio nicht unnötig parallel zu IntelliJ und Emulator geöffnet lassen.**

## 8. Fertige Android-APK aus GitHub Actions herunterladen (ohne lokalen Android-Build)

1. Repository im Browser öffnen: https://github.com/org-twohouseholds/the-last-semester
2. **Actions → Android Build**.
3. Einen **grünen** Workflow-Lauf auswählen.
4. Unten bei **Artifacts → `the-last-semester-debug`** klicken und die ZIP herunterladen.
5. ZIP entpacken: Darin liegt die von GitHub erstellte Debug-APK. Sie kann auf Android-Geräten zum Testen installiert werden. Für die tatsächliche Installation muss das Android-Gerät das Installieren von Test-APKs erlauben.

**Bei uns bereits geprüft:** CI-Lauf erfolgreich, Gradle `android:assembleDebug` erfolgreich, herunterladbares APK-Artefakt vorhanden. Das ist keine automatisch veröffentlichte Play-Store-Version.

## 9. Änderungen aus GitHub übernehmen

In IntelliJ den aktuellen Branch und deine lokalen Änderungen prüfen. Wenn du auf `develop` arbeitest und keine uncommitteten Änderungen hast, im Terminal:

```bash
git pull
```

Danach Desktop mit `lwjgl3:run` neu starten oder auf Android mit `android:installDebug` die aktuelle Version bauen/installieren. **Ein Pull allein installiert kein Update auf dem Emulator.**

## 10. Typische Probleme

- **IntelliJ „Clone Repository“ → `Repository not found`:** War bei uns ebenfalls so. GitHub-Einladung und Anmeldung prüfen, dann **Terminal `git clone`** verwenden und den Ordner über **File → Open** öffnen.
- **`git: command not found` / `git is not recognized`:** Auf diesem Rechner fehlt die ausführbare Git-Installation oder sie ist nicht im Suchpfad. Erst dann https://git-scm.com/downloads verwenden. Auf macOS kann das System stattdessen Apple Command Line Tools anbieten.
- **`cd: no such file or directory: the-last-semester`:** Vermutlich bist du bereits im richtigen Projektordner. Nicht ein zweites Mal `cd the-last-semester` ausführen.
- **Falsche Java-Version:** In IntelliJ **Project Structure → Project SDK** und **Settings → Gradle → Gradle JVM** auf JDK 17 stellen. Bei Versionsproblemen auch die gemeinsam eingecheckte `gradle/gradle-daemon-jvm.properties` prüfen.
- **`SDK location not found`:** `local.properties` im Projekt-Hauptverzeichnis fehlt oder `sdk.dir` verweist nicht auf das installierte Android SDK.
- **ADB zeigt `emulator-5554 offline`:** Emulator ist meist noch beim Starten. Auf den Android-Homescreen warten und erneut versuchen.
- **`adb: failed to stat ...apk: No such file or directory`:** APK-Pfad relativ zum aktuellen Terminalordner falsch. Für reguläre Updates lieber `./gradlew android:installDebug` verwenden.

---

**Stand der Team-Tests (04.11.2026):** Desktop-Start auf macOS und Linux, Android-APK-Build auf macOS, Installation und App-Start im Android-Emulator (M1), erfolgreiche GitHub-Actions-Läufe mit APK-Artefakt. Windows-Setup wurde noch nicht von uns praktisch bestätigt.
