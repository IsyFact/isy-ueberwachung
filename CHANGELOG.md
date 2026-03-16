# 5.0.0

### FEATURES
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
