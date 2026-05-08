# 5.0.0

### FEATURES
- `IFS-5235`: Aktualisierung der Nutzungsvorgaben für Readiness/Liveness-Probes
- `IFS-5234`: Hinzugefügt: Konzeptionelles Readiness- und Liveness-Modell für IF.5
- `IFS-4818`: Autokonfiguration von Load Balancer separiert
- `IFS-4748`: Dokumentation von aktualisierten Properties
- `IFS-5434`: Absicherung der Aktuatoren durch OAuth2.

### BREAKING CHANGES
- `IFS-4791`: Entfernt Metriken mit Zeiteinschränkungen
- `IFS-4817`: Verwendung von `securityMatcher` in actuatorSecurityFilterChain und loadbalancerSecurityFilterChain für korrektes Filtern von Anfragen.
- `IFS-4911`: Absicherung Actuator mit OAuth2
- `IFS-5215`: Entkopplung des Bausteins isy-ueberwachung aus IsyFact Standards
- `IFS-5434`: Die Aktuatoren /actuator/** werden mit OAuth2 abgesichert.
  Die jwk-set-uri kann dafür in den application.properties als `isy.ueberwachung.security.jwk-set-uri` laut Dokumentation angegeben werden.
  Wird dieses Property nicht gesetzt, ist ein Zugriff auf die Aktuatoren mit der vorhanden Security-Konfiguration der API-Endpunkte möglich.
  Ist die Security-Konfiguration nicht vorhanden, ist ein Zugriff auf die Endpunkte nicht möglich.

## MIGRATION GUIDE

Die folgenden zeitbeschränkten Metriken wurden entfernt:

* `AnzahlAufrufeLetzteMinute`: Anzahl der Anrufe in der letzten Minute
* `AnzahlTechnicalExceptionsLetzteMinute`: Anzahl der technischen Fehler in der letzten Minute
* `AnzahlBusinessExceptionsLetzteMinute`: Anzahl der geschäftlichen Fehler in der letzten Minute

Diese Metriken waren zuvor über die `ServiceStatistik`-Schnittstelle verfügbar und wurden automatisch bei Micrometer in der `IsyMetricsAutoConfiguration` registriert.
Anwendungen, die auf die zeitbeschränkten Metriken (`...LetzteMinute`) zur Überwachung oder Alarmierung angewiesen sind, müssen ihre Überwachungskonfigurationen anpassen.
Die übrigen Metriken ohne Zeitbeschränkung funktionieren weiterhin wie gehabt.

> **Hinweis:** Mithilfe der z.B. in Prometheus verfügbaren Funktionen lässt sich die gewohnte Funktionalität nahezu vollständig nachbilden.

- `IFS-5434`: Die jwk-set-uri für die Absicherung der Überwachungsendpunkte kann in den application.properties als `isy.ueberwachung.security.jwk-set-uri` laut Dokumentation angegeben werden.
  Die alten Properties `isy.ueberwachung.security.username` und `isy.ueberwachung.security.password` können entfernt werden.

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

##### Modernisierung der Anwendungsüberwachung: Readiness & Liveness

Mit der Aktualisierung des IsyFact 5 (IF.5) Konzepts führen wir einen Cloud-nativen Monitoring-Standard ein, der die bisherige Vermischung von Betriebszuständen auflöst.
Die Neuerungen im Überblick:

* **Readiness für Traffic-Steuerung:** Der Endpunkt `/actuator/health/readiness` zeigt an, ob eine Instanz Traffic annehmen soll. Die Readiness kombiniert den nativen Spring-Boot-Zustand `readinessState` mit dem IsyFact-Nachbarsystem-Indicator `isyNachbarsystem`.
* **Liveness für Neustartentscheidungen:** Der Endpunkt `/actuator/health/liveness` liefert ein Signal, ob der Prozess grundsätzlich funktionsfähig ist. Temporäre Störungen externer Systeme sollen die Liveness nicht beeinflussen.
* **Essenzielle Nachbarsysteme in Readiness:** Als essenziell konfigurierte Nachbarsysteme beeinflussen standardmäßig die Readiness. Nicht essenzielle Nachbarsysteme beeinflussen die Readiness nicht.
* **Abwärtskompatibler Parallelbetrieb:** Der bestehende Endpunkt `/actuator/health` bleibt unverändert verfügbar. Bestehende Monitorings laufen ohne Anpassungszwang weiter.
* **Hinweis für eigene Health-Groups:** Anwendungen, die `management.endpoint.health.group.readiness.include` selbst setzen, müssen die Defaults `readinessState,isyNachbarsystem` beibehalten und eigene Checks nur ergänzen.
