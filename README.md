# Playback Log Import Service

## Setup & Start

### Voraussetzungen

- Java 25
- Maven 3.9.16
- Docker

### Starten

- Startet die App und die zugehörige PostgreSQL-Datenbank

````bash
docker compose up --build
````

- Alternativ kann die App direkt über den folgenden Befehl gestartet werden. Voraussetzung hierfür ist, dass die PostgreSQL-Datenbank bereits läuft und die richtigen Umgebungsvariablen bzw. Startparameter gesetzt sind

````bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.datasource.url=jdbc:postgresql://localhost:5432/playbacklog,--spring.datasource.username=test,--spring.datasource.password=test,--playbacklog.storage.baseDir=/tmp/playbacklog"
````

### Beispiel REST Api

Die OpenAPI-Dokumentation ist über GET /swagger-ui/index.html erreichbar

````curl
curl --request POST \
  --url http://localhost:8080/playbacklog/startupload \
  --header 'Content-Type: multipart/form-data' \
  --form 'file=@C:\Tmp\test-logs.csv'
````

````curl
curl --request GET \
  --url http://localhost:8080/playbacklog/jobstatus/1
````


## Architektur

Die Applikation ist eine Spring Boot App, die PostgreSQL als Datenbank und das Spring Batch Framework nutzt, 
um große Datenmengen effizient verarbeiten und persistieren zu können. <br>

### Ablauf

Der Grobe Ablauf ist wie folgt:

1. Der Client kann über eine REST-API CSV-Dateien hochladen. Die Dateien werden in einem Verzeichnis abgelegt.
2. Ein Spring Batch Job für die Verarbeitung und den Import wird daraufhin asynchron gestartet.
3. Der Client bekommt sofort eine Rückmeldung über den Start und kann über eine REST-API den Status des Jobs abfragen.
4. Der Batch-Job liest die CSV-Dateien ein.
5. Die Daten werden validiert und angereichert.
6. Die Daten werden in die PostgreSQL-Datenbank importiert.

### Architektur

- Import
  - Upload per MultipartFile, Job-Anlage und Statusabfrage über REST.
- Verarbeitung mit Spring Batch
  - Dateien einlesen über den FlatFileItemReader, Format- und fachliche Validierung, Anreicherung der Daten.
- Persistenz
  - Persistierung der Daten in die PostgreSQL-Datenbank über JPA/Hibernate.
- Fehler Behandlung
  -  Format- oder fachliche Fehler werden über einen SkipListener gesammelt und ausgegeben.

### Effiziente Verarbeitung

Um den Speicherbedarf in Grenzen zu halten, wird der FlatFileItemReader benutzt, der die Daten zeilenweise und in Chunks einliest.
Auch sehr große Dateien werden so nicht komplett in den Arbeitsspeicher geladen. <br>

Damit der Client nicht auf die Verarbeitung warten muss, wird diese asynchron gestartet. 
Dies verhindert HTTP-Timeouts, die auftreten könnten, wenn der Client die gesamte Verarbeitungszeit blockiert wird. <br>

Der Datenbankzugriff wurde über JPA/Hibernate implementiert. 
Da als Strategie zur ID-Generierung GenerationType.UUID genutzt wird (die UUID wird im Speicher generiert), tritt das bekannte Performance-Problem nicht auf, bei dem Hibernate durch datenbankseitig generierte IDs (wie GenerationType.IDENTITY) das JDBC-Batching deaktiviert. 
Trotzdem wäre ein Umstieg auf natives JDBC (z. B. JdbcTemplate) eine Möglichkeit, um noch mehr Performance beim Insert herauszuholen. <br>

Bei der Anreicherung der Daten könnte es, abhängig von der aufgerufenen API, zu einem Flaschenhals (Bottleneck) kommen. 
Ein Cache könnte hier helfen, die Performance deutlich zu verbessern. <br>

Bei sehr großen Dateien könnte eine Parallelisierung des Batch-Jobs helfen. Wenn mehrere Threads gleichzeitig aus derselben Datei lesen sollen, muss der FlatFileItemReader thread-safe gemacht werden, 
beispielsweise durch das Wrappen in einen SynchronizedItemStreamReader. <br>

### Fehlerbehandlung

Im Verarbeitungsschritt werden die Daten sowohl auf Formatfehler als auch auf fachliche Fehler geprüft. 
Einträge, die fehlerhaft sind, werfen eine entsprechende Exception und werden zunächst übersprungen. <br>

Ein SkipListener gibt den fehlerhaften Eintrag im Log aus. 
In einer zukünftigen Iteration könnten die Fehler stattdessen in einer Dead Letter Queue (DLQ) für eine nachträgliche Verarbeitung gesammelt werden. 
Sollten die Fehler eine bestimmte, konfigurierbare Menge (Skip-Limit) überschreiten, scheitert der Job, um zu verhindern, dass fehlerhafte Dateien unbemerkt verarbeitet werden.
<br>

Bei einer Skalierung auf mehrere Instanzen müssen neue Fehlerfälle beachtet werden. 
Spring Batch sorgt durch Datenbank-Locks bereits dafür, dass eine Job-Instanz nicht mehrfach parallel gestartet wird. 
Wenn eine App-Instanz jedoch mitten im Job abstürzt, bleibt der Job in der Datenbank z.b im Status "STARTED" hängen. Da ihn in diesem Zustand niemand übernehmen kann, müsste ein Aufräum- oder Lease-Konzept implementiert werden 
(z. B. Instanzen erneuern regelmäßig die Reservierung ihrer Jobs; Jobs ohne aktuelles Lease werden aufgeräumt).
<br>

### Duplikate

Aktuell gibt es noch keinen Duplikatschutz.
Wenn möglich, sollte ein Unique Key (oder mehrere Attribute, die zusammen eindeutig sind) festgelegt werden. 
Technisch lässt sich dies über einen Unique Constraint in der Datenbanktabelle abbilden. Das verhindert Duplikate auf Datenbankebene, und die entsprechenden Datensätze könnten bei einer Constraint Violation beim Import übersprungen werden.
<br>

Zusätzlich sollten Duplikate bereits beim File-Upload abgelehnt werden, beispielsweise durch den Abgleich von Dateinamen oder Dateiprüfsummen (Hashes).
<br>

### TODOs und Verbesserungen

- Aktuell ist die fachliche Validierung nur ein Dummy. Die fachlichen Regeln müssen inkl. Tests definiert und implementiert werden.
- Das Anreichern mit zusätzlichen Daten ist ebenfalls nur ein Dummy. Echte REST-API-Requests und idealerweise ein Caching müssen implementiert werden.
- Aufsetzen eines zentralen Fileservers oder Umstieg auf Blob-Storage (z. B. AWS S3) anstelle des lokalen Dateisystems für hochgeladene Dateien.
- Erweiterung der Job-Steuerungs-APIs (Restart, Stop, etc.).
- DLQ (Dead Letter Queue) oder eine spezifische Fehlerbehandlung für fehlerhafte Einträge anstelle von reinem Logging.
- Möglicher Umstieg auf reines JDBC für noch bessere Insert-Performance beim Batch.














 

