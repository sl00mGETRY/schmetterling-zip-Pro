# Schmetterling-ZIP PRO
## WARNUNG!!! Ich erinnere mich nicht mehr an die genauen Namen der Ordner und JAR-Dateien, weil ich betrunken war, als ich das gepostet habe. Wenn ihr also einen Pfad/Dateinamen seht, überprüft ihn, um Fehler zu vermeiden!!!!

Schmetterling-ZIP PRO ist ein modernes, plattformübergreifendes Archivierungsprogramm auf Basis von Java und JavaFX. Es ermöglicht das Erstellen, Öffnen, Verwalten und Entpacken verschiedener Archivformate über eine übersichtliche grafische Benutzeroberfläche.

Ein besonderes Merkmal von Schmetterling-ZIP PRO ist die Unterstützung des proprietären Formats **`.myash`**, das zusätzlich **Polyglot-Archive** unterstützt. Dadurch können Archive erstellt werden, die sowohl öffentliche als auch versteckte Datenbereiche enthalten. Die Polyglot-Funktionalität ist ausschließlich für das Format **`.myash`** verfügbar.

Die Anwendung richtet sich sowohl an normale Anwender als auch an Entwickler und fortgeschrittene Benutzer, die mit unterschiedlichen Archivformaten arbeiten.


---

# Funktionen

## Archivverwaltung

- Erstellen neuer Archive
- Öffnen vorhandener Archive
- Entpacken kompletter Archive
- Extrahieren einzelner Dateien
- Anzeigen des Archivinhalts
- Aktualisieren geöffneter Archive
- Unterstützung großer Archive
- Beibehaltung der Ordnerstruktur beim Entpacken
- Schnelle Navigation innerhalb großer Archive

## Unterstützte Archivformate

- ZIP
- JAR
- WAR
- EAR
- APK
- MYASH

Je nach Format können Archive erstellt, geöffnet oder entpackt werden.

## Unterstützung des MYASH-Formats

Das Format **`.myash`** bietet zusätzliche Funktionen gegenüber klassischen ZIP-Archiven.

Dazu gehören unter anderem:

- Unterstützung von Polyglot-Archiven
- Öffentliche Datenbereiche
- Versteckte Datenbereiche
- Eigene Archivstruktur
- Zukunftssichere Erweiterbarkeit
- Optimierte Verarbeitung innerhalb von Schmetterling-ZIP PRO

Die Polyglot-Funktion steht ausschließlich für **`.myash`**-Archive zur Verfügung.

## Moderne Benutzeroberfläche

- Moderne JavaFX-Oberfläche
- Übersichtliche Navigation
- Intuitive Bedienung
- Kontextmenüs
- Statusanzeigen
- Fortschrittsanzeigen
- Mehrere Dialogfenster
- Plattformübergreifendes Erscheinungsbild

## Archivansicht

- Baumstruktur der Verzeichnisse
- Anzeige einzelner Dateien
- Anzeige von Ordnern
- Hierarchische Navigation
- Dateiinformationen
- Archivinformationen

## Dateiverwaltung

- Auswahl einzelner Dateien
- Auswahl kompletter Ordner
- Mehrfachauswahl
- Dateisuche
- Filtern nach Namen
- Drag-and-Drop-Unterstützung
- Komfortables Hinzufügen von Dateien
- Komfortables Hinzufügen ganzer Verzeichnisse

## Extraktion

- Gesamtes Archiv entpacken
- Nur ausgewählte Dateien entpacken
- Nur ausgewählte Ordner entpacken
- Originale Verzeichnisstruktur beibehalten
- Zielordner frei wählbar

## Weitere Funktionen

- Plattformübergreifend
- UTF-8-Unterstützung
- Große Dateinamen
- Lange Verzeichnisstrukturen
- Hohe Kompatibilität
- Einfache Erweiterbarkeit
- Open-Source-Projekt

---

# Unterstützte Formate

| Format | Öffnen | Erstellen | Entpacken |
|---------|:------:|:---------:|:---------:|
| ZIP | Ja | Ja | Ja |
| JAR | Ja | Ja | Ja |
| WAR | Ja | Ja | Ja |
| EAR | Ja | Ja | Ja |
| APK | Ja | Ja | Ja |
| MYASH | Ja | Ja | Ja |

Hinweis:

Die Unterstützung von **Polyglot-Archiven** ist ausschließlich im Format **`.myash`** verfügbar.

---

# Installation

Schmetterling-ZIP PRO benötigt eine aktuelle Java-Laufzeitumgebung.

Empfohlen wird:

- Java 21 oder neuer
- Gradle (nur zum Kompilieren aus dem Quellcode)

Offizielle Downloads:

- Java (OpenJDK): https://adoptium.net/
- Gradle: https://gradle.org/install/
- Git: https://git-scm.com/

---

# Installation unter Windows

## 1. Java installieren

Laden Sie Java von der offiziellen Seite herunter:

https://adoptium.net/

Installieren Sie anschließend die aktuelle LTS-Version.

Nach der Installation PowerShell öffnen und prüfen:

```powershell
java -version
```

Die Java-Version sollte angezeigt werden.

---

## 2. Git installieren

https://git-scm.com/

Nach der Installation prüfen:

```powershell
git --version
```

---

## 3. Gradle

Falls das Projekt den Gradle Wrapper (`gradlew`) enthält, muss Gradle **nicht** separat installiert werden.

Andernfalls:

https://gradle.org/install/

Prüfen:

```powershell
gradle --version
```

---

## 4. PowerShell öffnen

Startmenü → "PowerShell"

oder

Rechtsklick im Projektordner → "In Terminal öffnen"

---

## 5. Repository herunterladen

```powershell
git clone <Repository-URL>
cd Schmetterling-ZIP-PRO
```

Beiträge, Fehlerberichte und Verbesserungsvorschläge sind jederzeit willkommen.
---

## 6. Projekt kompilieren

Mit Gradle Wrapper:

```powershell
.\gradlew build
```

oder

```powershell
gradle build
```

---

## 7. Programm starten

```powershell
java -jar build\libs\Schmetterling-ZIP-PRO.jar
```

---

# Installation unter Arch Linux

Java installieren:

```bash
sudo pacman -S jdk21-openjdk
```

Git:

```bash
sudo pacman -S git
```

Gradle:

```bash
sudo pacman -S gradle
```

Falls ein Gradle Wrapper vorhanden ist, wird Gradle nicht benötigt.

Repository herunterladen:

```bash
git clone <Repository-URL>
cd Schmetterling-ZIP-PRO
```

Projekt bauen:

```bash
./gradlew build
```

oder

```bash
gradle build
```

Programm starten:

```bash
java -jar build/libs/Schmetterling-ZIP-PRO.jar
```

AUR:

Einige Java-Versionen oder Entwicklungswerkzeuge können alternativ über das AUR installiert werden. Für Schmetterling-ZIP PRO genügt jedoch das offizielle Paket `jdk21-openjdk`.

---

# Installation unter EndeavourOS

```bash
sudo pacman -S jdk21-openjdk git gradle
```

Danach:

```bash
git clone <Repository-URL>
cd Schmetterling-ZIP-PRO
./gradlew build
java -jar build/libs/Schmetterling-ZIP-PRO.jar
```

---

# Installation unter Manjaro

```bash
sudo pacman -S jdk21-openjdk git gradle
```

Danach:

```bash
git clone <Repository-URL>
cd Schmetterling-ZIP-PRO
./gradlew build
java -jar build/libs/Schmetterling-ZIP-PRO.jar
```

---

# Installation unter Ubuntu

```bash
sudo apt update
sudo apt install openjdk-21-jdk git gradle
```

Projekt herunterladen:

```bash
git clone <Repository-URL>
cd Schmetterling-ZIP-PRO
```

Kompilieren:

```bash
./gradlew build
```

Starten:

```bash
java -jar build/libs/Schmetterling-ZIP-PRO.jar
```

---

# Installation unter Debian

```bash
sudo apt update
sudo apt install openjdk-21-jdk git gradle
```

Projekt bauen:

```bash
git clone <Repository-URL>
cd Schmetterling-ZIP-PRO
./gradlew build
java -jar build/libs/Schmetterling-ZIP-PRO.jar
```

---

# Installation unter Linux Mint

```bash
sudo apt update
sudo apt install openjdk-21-jdk git gradle
```

Danach:

```bash
git clone <Repository-URL>
cd Schmetterling-ZIP-PRO
./gradlew build
java -jar build/libs/Schmetterling-ZIP-PRO.jar
```

---

# Installation unter Fedora

```bash
sudo dnf install java-21-openjdk-devel git gradle
```

Projekt:

```bash
git clone <Repository-URL>
cd Schmetterling-ZIP-PRO
./gradlew build
java -jar build/libs/Schmetterling-ZIP-PRO.jar
```

---

# Installation unter openSUSE

```bash
sudo zypper install java-21-openjdk-devel git gradle
```

Projekt:

```bash
git clone <Repository-URL>
cd Schmetterling-ZIP-PRO
./gradlew build
java -jar build/libs/Schmetterling-ZIP-PRO.jar
```

---

# Projekt kompilieren

Repository herunterladen:

```bash
git clone <Repository-URL>
```

Projektordner öffnen:

```bash
cd Schmetterling-ZIP-PRO
```

Projekt bauen:

```bash
./gradlew build
```

Alternativ:

```bash
gradle build
```

Nach erfolgreicher Kompilierung befindet sich die ausführbare JAR-Datei im Verzeichnis:

```
build/libs/
```

Programm starten:

```bash
java -jar build/libs/Schmetterling-ZIP-PRO.jar
```

---

## Beschreibung

**src/main/java**

Enthält den vollständigen Java-Quellcode der Anwendung.

**src/main/resources**

.css lol

**src/test**

Tests des Projekts.

**gradle**

Dateien des Gradle-Buildsystems.

**build**

Wird während der Kompilierung automatisch erzeugt und enthält unter anderem die fertige JAR-Datei.

**docs**

Zusätzliche Dokumentation.

---

# Verwendung

Schmetterling-ZIP PRO wurde so entwickelt, dass auch Benutzer ohne Vorkenntnisse Archive verwalten können.

## Ein Archiv öffnen

1. Programm starten.
2. Auf **Öffnen** klicken.
3. Das gewünschte Archiv auswählen.
4. Der Inhalt erscheint in der Baumansicht.

## Testarchiv ausprobieren

Im Repository befindet sich neben dieser README-Datei ein Testarchiv mit der Erweiterung **`.myash`**.

Öffnen Sie dieses Archiv direkt in Schmetterling-ZIP PRO, um die Funktionen des Programms sowie die Eigenschaften des MYASH-Formats kennenzulernen.

Dies ist der einfachste Weg, die Anwendung unmittelbar nach dem Kompilieren oder Herunterladen auszuprobieren.

## Dateien durchsuchen

- Suchfeld verwenden.
- Dateinamen eingeben.
- Treffer werden sofort angezeigt.

## Dateien entpacken

1. Datei oder Ordner auswählen.
2. Auf **Extrahieren** klicken.
3. Zielordner auswählen.
4. Der Inhalt wird mit unveränderter Ordnerstruktur gespeichert.

## Komplettes Archiv entpacken

1. Archiv öffnen.
2. "Alle extrahieren" auswählen.
3. Zielordner festlegen.
4. Warten, bis der Vorgang abgeschlossen ist.

## Neues Archiv erstellen

1. "Neues Archiv" auswählen.
2. Format wählen.
3. Dateien per Drag-and-Drop hinzufügen oder über den Dateidialog auswählen.
4. Speicherort festlegen.
5. Archiv erstellen.

---

# FAQ

## Welche Java-Version wird empfohlen?

Java 21 oder neuer.

---

## Benötige ich Gradle?

Nur wenn Sie das Projekt selbst aus dem Quellcode kompilieren möchten.

Beim Verwenden einer bereits kompilierten JAR-Datei wird lediglich Java benötigt.

---

## Unterstützt Schmetterling-ZIP PRO Polyglot-Archive?

Ja.

Diese Funktion steht ausschließlich für Archive im Format **`.myash`** zur Verfügung.

---

## Kann ich einzelne Dateien extrahieren?

Ja.

Es können einzelne Dateien oder komplette Ordner ausgewählt werden.

---

## Unterstützt das Programm Drag-and-Drop?

Ja.

Dateien und Verzeichnisse können direkt in das Programmfenster gezogen werden.

---

## Wo befindet sich die fertige JAR-Datei?

Nach erfolgreicher Kompilierung:

```
build/libs/
```

---

## Ist das Programm plattformübergreifend?

Ja.

Schmetterling-ZIP PRO läuft überall dort, wo eine kompatible Java-Laufzeitumgebung verfügbar ist.

---

# Lizenz

Dieses Projekt wird unter der im Repository enthaltenen Lizenz veröffentlicht.

Weitere Informationen finden Sie in der Datei **LICENSE**.

---

# Autor

Entwickelt von den Autoren des Projekts **Schmetterling-ZIP PRO**.

Beiträge, Fehlerberichte und Verbesserungsvorschläge sind jederzeit willkommen.
en Autoren des Projekts **Schmetterling-ZIP PRO**.

Beiträge, Fehlerberichte und Verbesserungsvorschläge sind jederzeit willkommen.
