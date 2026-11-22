# The Last Semester – Spielablauf

**Stand:** 16.11.2026  
**Zweck:** Gemeinsame Übersicht für UI-, Server- und Game-Logic-Team.  
**Hinweis:** Noch nicht entschiedene Regeln sind mit **OFFEN** gekennzeichnet.

## 1. Spielablauf (Kurzüberblick)

1. **Charakter auswählen:** Der Spieler wählt im Menü einen von **4 Charakteren**. Jeder Charakter hat eine passive Fähigkeit und verfügt über die gleiche Basic Attack.
2. **Initial Shop:** Vor dem ersten Kampf öffnet sich der Shop. Es werden **2 zufällige Perks** aus insgesamt **8 Perks** angeboten. Der erste Perk ist kostenlos. Das UI zeigt dem Spieler die beiden Optionen an.
3. **Kostenlosen Perk auswählen:** Der Spieler wählt einen der beiden angebotenen Perks kostenlos aus. Dieser wird seiner Charakterinstanz hinzugefügt.

   Zu Beginn des ersten Matches stehen dem Spieler somit zwei aktive Fähigkeiten zur Verfügung: die **Basic Attack**, die für alle Charaktere identisch ist, und der kostenlos erworbene **Shop-Perk**. Die charaktergebundene passive Fähigkeit wird automatisch berücksichtigt und muss nicht ausgewählt werden.

4. **Erstes Match:** Zwei Teilnehmer treten gegeneinander an. Das UI zeigt HP, Energy, Match-Timer und Attack-Timer.
5. **Aktion auswählen:** Beide Teilnehmer wählen verdeckt eine verfügbare aktive Fähigkeit (Basic Attack oder erworbener Perk).
6. **Beide Aktionen vorhanden?** Das System überprüft, ob beide Teilnehmer eine Fähigkeit ausgewählt haben.

   - **Ja:** Beide Aktionen liegen vor und die Kampfauflösung wird mit Schritt 7 fortgesetzt.
   - **Nein:** Das System bleibt bei Schritt 6 und wartet auf die fehlende Aktion, solange der Attack-Timer noch läuft.

   **OFFEN:** Läuft der Attack-Timer ab, ohne dass beide Aktionen vorliegen, muss das System entsprechend einer noch festzulegenden Timeout-Regel reagieren.

7. **Aktionen prüfen:** Der Server prüft, ob die Fähigkeit verfügbar ist und genügend Energy vorhanden ist.
8. **Speed vergleichen:** Die Fähigkeit mit dem höheren Speed-Wert wird zuerst ausgeführt. **OFFEN:** Gleiche Speed-Werte.
9. **Effekte vor dem Angriff:** Passende `BEFORE_ATTACK`-Effekte werden berücksichtigt.
10. **Angriff berechnen:** Schaden und Schadensreduktion werden berechnet, HP angepasst und Energy-Kosten der ausgeführten Fähigkeit abgezogen.
11. **Effekte nach dem Angriff:** Passende `AFTER_ATTACK`- und `AFTER_DMG_TAKEN`-Effekte werden verarbeitet.
12. **Zweiten Teilnehmer prüfen:** Ist der zweite Charakter bereits besiegt, entfällt seine Aktion ohne Energy-Verbrauch.
13. **Zweite Aktion:** Ist der zweite Charakter noch kampffähig, führt er seine Fähigkeit nach denselben Regeln aus (Schritte 9–11).
14. **Zustand aktualisieren:** Der Server stellt aktualisierte HP-/Energy-Werte und Kampfevents für das UI bereit.
15. **Match-Ende prüfen:** Sind beide Charaktere noch kampffähig, beginnt wieder Schritt 5. Andernfalls endet das Match.
16. **Ergebnis:** Der Gewinner kommt weiter, der Verlierer **scheidet aus dem Turnier aus**.
17. **Turnier-Ende prüfen:** Steht der Turniersieger fest, wird das Ergebnis angezeigt. Ansonsten geht es nach Abschluss der betreffenden Turnierrunde weiter.
18. **Nächster Shop:** Die verbleibenden Spieler erhalten CP nach den vereinbarten Regeln. Es werden wieder **2 zufällige Perks** angeboten, diesmal gegen CP und mit einem **Shop-Timer (geplant: ca. 10–15 Sekunden)**. Danach beginnt das nächste Match bei Schritt 4.

Der Ablauf wiederholt sich, bis ein Turniersieger feststeht.

## 2. Combat Rules (Details zu Schritt 5–15)

### 5–7. Auswahl und Validierung

Beide Teilnehmer wählen unabhängig voneinander eine Fähigkeit. Die Auswahl bleibt bis zur Kampfauflösung vor dem Gegner verborgen. Der Server prüft, ob die gewählte Ability die allgemeine Basic Attack oder ein vom Teilnehmer erworbener Perk ist, ob ausreichend Energy vorhanden ist und ob der Teilnehmer die Aktion ausführen darf. Ungültige Eingaben verändern HP und Energy nicht.

### 8. Reihenfolge durch Speed

Die ausgewählten Fähigkeiten werden anhand ihres Speed-Werts sortiert: **Höherer Wert = frühere Ausführung**. Die Regel bei Gleichstand wird noch festgelegt.

### 9–11. Ersten Angriff ausführen

Zuerst werden relevante `BEFORE_ATTACK`-Effekte angewendet. Danach wird der Schaden aus dem Grundschaden der Ability und möglichen Verstärkungs- oder Reduktionseffekten ermittelt. Der Schaden wird vom Ziel abgezogen und die Energy-Kosten der tatsächlich ausgeführten Fähigkeit werden berücksichtigt. Anschließend folgen passende `AFTER_ATTACK`- und `AFTER_DMG_TAKEN`-Effekte. Die genaue Reihenfolge mehrerer gleichzeitiger Effekte ist noch offen.

### 12–13. Zweiten Angriff ausführen oder überspringen

Nach dem ersten Angriff wird geprüft, ob der zweite Teilnehmer noch HP besitzt. Ist er besiegt, **wird seine ausgewählte Fähigkeit nicht mehr ausgeführt und kostet keine Energy**. Andernfalls wird sie mit denselben Effekt- und Schadensregeln aufgelöst.

### 14–15. Kampfrunde abschließen

Nach der Auswertung werden die aktualisierten Werte und Ereignisse an das UI übermittelt. Solange das Match nicht beendet ist, wählen beide Teilnehmer für die nächste Kampfrunde erneut eine Fähigkeit. Bei einem besiegten Teilnehmer endet das Match; der Verlierer scheidet aus dem Turnier aus.

## 3. Noch abzustimmen

- Reihenfolge bei gleicher Speed. (Random vlt. coinflip)
- Energy-Regeneration während eines Matches. (1-2 Energy pro runde regenerieren, Energyleiste 5 bis 10 insgesamt groß)
- HP-/Energy-Reset vor dem nächsten Match. (Full Reset)
- Verhalten bei Attack-, Match- und Shop-Timer-Ablauf. (Besprechen mit Frontend team wegen Match-timer)
- Reihenfolge mehrerer Effekte mit gleichem Auslösezeitpunkt. (was zuerst hinzugefügt wird, wird als erstes ausgelöst)    
