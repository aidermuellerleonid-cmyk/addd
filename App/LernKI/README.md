# LernKI – Lern- und KI-App für Schüler

Vollständiges Android-Studio-/Gradle-Projekt (Kotlin, Jetpack Compose) plus
Node.js-Backend, das die KI-Anfragen verarbeitet.

## Wichtiger Hinweis

Dieses Paket enthält den **vollständigen Quellcode**. Es wurde bewusst
**keine fertige `.apk`-Datei** mitgeliefert, weil das Bauen einer APK ein
Android SDK, die Gradle-Toolchain und einen Signier-Vorgang benötigt – das
ist in der Umgebung, in der dieser Code erzeugt wurde, nicht verfügbar
(kein Internetzugriff, kein installiertes Android SDK).

Um eine echte APK zu erhalten:

1. **Android Studio** installieren (kostenlos, aktuelle Version).
2. Den Ordner `LernKI/` als Projekt öffnen ("Open" → Ordner auswählen).
3. Android Studio lädt beim ersten Öffnen automatisch den Gradle-Wrapper
   und alle Abhängigkeiten herunter (Internetverbindung nötig).
4. Rechts oben auf **Run ▶** klicken (Emulator oder eigenes Handy per USB),
   oder über **Build → Build Bundle(s) / APK(s) → Build APK(s)** eine
   installierbare `.apk` erzeugen (liegt danach unter
   `app/build/outputs/apk/debug/app-debug.apk`).

## Projektstruktur

```
LernKI/
├── app/                        Android-App (Kotlin, Jetpack Compose)
│   └── src/main/java/com/lernki/app/
│       ├── ui/screens/         Alle Bildschirme (Home, Zusammenfassen,
│       │                       Mathe, Lernmodus, Chat, Verlauf, Bildanalyse)
│       ├── ui/components/      Wiederverwendbare UI-Bausteine
│       ├── viewmodel/          Ein ViewModel pro Funktion (MVVM)
│       ├── data/remote/        Retrofit-Schnittstelle zum Backend
│       ├── data/repository/    AiRepository (austauschbare KI-Anbindung)
│       ├── data/local/         Room-Datenbank für den Verlauf
│       ├── ocr/                On-Device-OCR (ML Kit) zur Qualitätsprüfung
│       └── di/                 Einfacher, manueller Dependency-Container
└── server/                     Node.js/Express-Backend (KI-Verarbeitung)
    ├── index.js                REST-Endpunkte, ruft das KI-Modell auf
    └── prompts.js              Alle System-Prompts an einer Stelle
```

Die Trennung **Frontend (App) → Backend (Node-Server) → KI-Modell**
(Abschnitt 12 der Anforderungen) ist bewusst so umgesetzt:

* Die App kennt **keinen** KI-Anbieter-API-Key. Sie spricht ausschließlich
  mit dem eigenen Backend (`AiApiService` via Retrofit).
* Das Backend kapselt die eigentliche KI-Anbindung in `prompts.js` /
  `index.js`. Um das Modell zu wechseln, muss nur dort etwas geändert
  werden – die App bleibt unverändert.
* Innerhalb der App abstrahiert `AiRepository` die KI-Aufrufe zusätzlich,
  falls später z.B. direkt ein anderes Backend oder ein On-Device-Modell
  angebunden werden soll.

## Backend starten

```bash
cd server
npm install
cp .env.example .env     # ANTHROPIC_API_KEY eintragen
npm start
```

Der Server läuft danach auf `http://localhost:3000`.

Für Tests im **Android-Emulator** ist die App bereits auf
`http://10.0.2.2:3000/` vorkonfiguriert (Standard-Adresse, über die der
Emulator den Host-Rechner erreicht). Für ein echtes Smartphone im selben
WLAN musst du in `app/build.gradle.kts` die `BACKEND_BASE_URL` auf die
lokale IP deines Rechners ändern (z.B. `http://192.168.1.23:3000/`), oder
das Backend auf einem echten Server/Hosting-Dienst deployen.

## Umgesetzte Funktionen

* **Zusammenfassen** – Text/Bilder/mehrere Bilder, 5 Stile (Kurz, Normal,
  Ausführlich, Stichpunkte, Lernzettel), OCR, Markdown-Formatierung.
* **Mathe lösen** – Foto oder Texteingabe, vollständiger Rechenweg,
  mehrere Aufgaben pro Bild werden einzeln erkannt und dargestellt.
* **Lernmodus** – Zusammenfassung, Lernzettel, Karteikarten, Quiz,
  wichtige Begriffe, Fragen zum Text, Prüfungssimulation.
* **KI-Chat** – mit optionalem Bild-/Dokumentkontext.
* **Bildanalyse** – erkennt automatisch Text, Matheaufgabe, Diagramm,
  Tabelle, Arbeitsblatt, Zeichnung oder Formel; zeigt danach passende
  Aktionen an.
* **Ergebnisse** – kopieren, bearbeiten, speichern, löschen, teilen.
* **Verlauf** – lokal in einer Room-Datenbank, durchsuchbar.
* **Fehlerbehandlung** – die KI wird angewiesen, bei unklaren/unscharfen
  Bildern dies explizit zu kennzeichnen statt Inhalte zu erfinden; bei
  Matheaufgaben wird die KI angewiesen, ihr Ergebnis vor der Ausgabe zu
  überprüfen.

## Bekannte Einschränkungen / nächste Schritte

* Es ist noch kein Nutzer-Login vorgesehen; der Verlauf ist rein lokal
  auf dem Gerät gespeichert.
* Für den Produktivbetrieb sollte das Backend mit HTTPS, Rate-Limiting
  und Authentifizierung abgesichert werden, bevor es öffentlich erreichbar ist.
* App-Icon und Farbschema sind bewusst einfach gehalten und können in
  `ui/theme/` bzw. `res/drawable/` leicht angepasst werden.
