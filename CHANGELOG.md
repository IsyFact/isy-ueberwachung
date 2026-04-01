# 5.0.0

### FEATURES
- `IFS-5234`: Hinzugefügt: Konzeptionelles Readiness- und Liveness-Modell für IF.5
- `IFS-4818`: Autokonfiguration von Load Balancer separiert
- `IFS-4748`: Dokumentation von aktualisierten Properties

### BREAKING CHANGES
- `IFS-4791`: Entfernt Metriken mit Zeiteinschränkungen
- `IFS-4817`: Verwendung von `securityMatcher` in actuatorSecurityFilterChain und loadbalancerSecurityFilterChain für korrektes Filtern von Anfragen.
- `IFS-4911`: Absicherung Actuator mit OAuth2
- `IFS-5215`: Entkopplung des Bausteins isy-ueberwachung aus IsyFact Standards

## MIGRATION GUIDE

Die folgenden zeitbeschränkten Metriken wurden entfernt:

* `AnzahlAufrufeLetzteMinute`: Anzahl der Anrufe in der letzten Minute
* `AnzahlTechnicalExceptionsLetzteMinute`: Anzahl der technischen Fehler in der letzten Minute
* `AnzahlBusinessExceptionsLetzteMinute`: Anzahl der geschäftlichen Fehler in der letzten Minute

Diese Metriken waren zuvor über die `ServiceStatistik`-Schnittstelle verfügbar und wurden automatisch bei Micrometer in der `IsyMetricsAutoConfiguration` registriert.
Anwendungen, die auf die zeitbeschränkten Metriken (`...LetzteMinute`) zur Überwachung oder Alarmierung angewiesen sind, müssen ihre Überwachungskonfigurationen anpassen.
Die übrigen Metriken ohne Zeitbeschränkung funktionieren weiterhin wie gehabt.

> **Hinweis:** Mithilfe der z.B. in Prometheus verfügbaren Funktionen lässt sich die gewohnte Funktionalität nahezu vollständig nachbilden.

### Dependency Änderungen

```xml
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-webmvc-test</artifactId>
<scope>test</scope>

<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-webtestclient</artifactId>
<scope>test</scope>
```
## RELEASE NOTES

##### Modernisierung der Anwendungsüberwachung (Readiness & Liveness)
Mit der Aktualisierung des IsyFact 5 (IF.5) Konzepts führen wir einen Cloud-nativen Monitoring-Standard ein, der die bisherige Vermischung von Betriebszuständen auflöst.
Die Neuerungen im Überblick:
* Präzise Traffic-Steuerung (Readiness): Loadbalancer erkennen über /actuator/health/readiness exakt, wann eine Instanz fachlich bereit ist (z. B. nach Cache-Initialisierung).
* Erhöhte Robustheit (Liveness): Der neue /actuator/health/liveness Endpunkt liefert Infrastrukturen (wie Kubernetes) ein sauberes Signal für Prozess-Neustarts, ohne durch temporäre Nachbarsystem-Störungen Fehlalarme auszulösen.
* Klare Abhängigkeiten: Neue Architekturvorgaben definieren präzise, welche Nachbarsysteme (Datenbanken, APIs) als essenziell für die Betriebsbereitschaft gelten.
* Risikofreie Migration: Durch Parallelbetrieb bleibt der bestehende /actuator/health Endpunkt funktional identisch. Bestehende Monitorings laufen ohne Anpassungszwang weiter (kein Breaking Change).
