# 📑 Workshop Journal: Toolchain Renewal Sandbox

Questo documento traccia, giorno per giorno, tutti i passaggi operativi, le decisioni architetturali e i traguardi raggiunti durante lo sviluppo della sandbox per il rinnovo della toolchain di rilascio.

---

## 🗓️ Giorno 1: Inizializzazione e Analisi dei Requisiti

L'obiettivo della prima giornata è stato definire il perimetro tecnologico e preparare l'ambiente locale per la simulazione della pipeline.

### Attività Eseguite:

1. **Definizione dello Stack Obiettivo:** Scelta di **Java 21** come baseline per i nuovi microservizi aziendali, abbandonando le vecchie versioni (Java 8/11) in ottica di modernizzazione.
2. **Generazione del Microservizio Target:** Utilizzo di *Spring Initializr* per creare lo scheletro di `order-service` con i seguenti metadati:
* **Artifact ID:** `order-service`
* **Package Name Legale:** Sostituito il trattino con l'underscore (`com.capgemini.order_service`) per rispettare i vincoli di sintassi Java.


3. **Integrazione dei Componenti di Osservabilità:** Inserimento delle dipendenze nel file `pom.xml` per predisporre il servizio al futuro monitoraggio in produzione:
* **Spring Boot Actuator:** Per l'esposizione degli endpoint di salute del sistema.
* **Micrometer Prometheus Registry:** Per la raccolta e la formattazione delle metriche compatibili con i server Prometheus.



---

## 🗓️ Giorno 2: Sviluppo del Codice, Configurazione e Containerizzazione

L'obiettivo della seconda giornata è stato rendere il microservizio autonomo, configurabile e pacchettizzarlo in un container Docker seguendo gli standard di sicurezza *enterprise*.

### Attività Eseguite:

1. **Scrittura del Controller REST:** Implementazione della classe principale `OrderServiceApplication.java` con un endpoint di test esposto su `/api/orders` per simulare la risposta del servizio durante i test di pipeline:
```json
{"status":"ALIVE","message":"Order Service temporaneo per simulazione pipeline","wave":"1"}

```


2. **Configurazione Centralizzata (YAML):** Creazione del file `application.yml` dentro `src/main/resources/` per istruire il server web integrato (Tomcat) ad ascoltare sulla porta corretta (`8081`) ed esporre pubblicamente i canali di monitoraggio.
3. **Ingegnerizzazione del Dockerfile Multi-Stage:** Scrittura di un flusso di build ottimizzato diviso in due fasi distinte:
* **Stage 1 (Build):** Utilizzo di un'immagine contenente Maven 3.9.6 e Java 21 per compilare il codice sorgente e generare l'artefatto `.jar`, sfruttando la cache di Docker per il download delle dipendenze.
* **Stage 2 (Runtime):** Copia del solo file `.jar` risultante (usando il carattere jolly `*.jar` per superare le discrepanze di denominazione) in un'immagine pulita e leggera dotata del solo JRE Java 21 su base Alpine Linux.


4. **Applicazione delle Best Practice di Sicurezza:** All'interno del container è stato inibito l'utente `root`. È stato creato un gruppo e un utente dedicato a bassi privilegi (`devopsuser`) per l'esecuzione del processo Java.
5. **Validazione Locale:** Esecuzione dei comandi di build e avvio del container, culminati nel test positivo tramite `curl http://localhost:8081/api/orders`, che ha certificato il perfetto funzionamento del binomio Spring Boot + Docker.

---

## 🗓️ Giorno 3: Jenkins Shared Library & Configurazione Infrastrutturale

L'obiettivo della terza giornata è stato l'avvio dell'automazione tramite la centralizzazione delle logiche di CI/CD in una libreria riutilizzabile, affrontando l'allineamento dei plugin del server Jenkins.

### Attività Eseguite:

1. **Scaffolding della Shared Library:** Creazione della struttura standard di cartelle richiesta da Jenkins nella radice della sandbox:
* `vars/`: Destinata a ospitare le funzioni globali (pipeline steps).
* `src/`: Predisposta per eventuali classi Groovy avanzate.


2. **Sviluppo dei Componenti Core (Groovy):** Scrittura dei primi due step riutilizzabili per astrarre la logica dai singoli microservizi:
* `buildJavaApp.groovy`: Automatizza il comando di compilazione Maven (`mvn clean package -DskipTests -B`).
* `dockerBuildPush.groovy`: Gestisce il build isolato dell'immagine Docker locale accettando parametri dinamici (`imageName`, `imageTag`).


3. **Troubleshooting & Allineamento Core Jenkins:**
* Identificata l'assenza del plugin `Pipeline: Groovy Libraries` (precedentemente noto come *Pipeline: Shared Groovy Libraries*) nell'installazione core minimale di Jenkins, provvedendo all'installazione tramite GUI.
* Gestito il blocco del menu a tendina *Retrieval method -> Modern SCM* installando il plugin `Git` core per abilitare il recupero dei sorgenti dal filesystem locale.
* Sbloccato lo stato di stallo (*Pending*) dei plugin di boot accodati nella GUI eseguendo un riavvio forzato del container Docker (`docker restart`).


4. **Introduzione a Jenkins Configuration as Code (JCasC):** Analisi dello scheletro del file `jenkins.yaml` per mappare in modalità *As Code* la registrazione della libreria sotto la voce `globalLibraries` (nella sezione `Global Trusted Pipeline Libraries`), ponendo le basi per azzerare i clic manuali sull'interfaccia grafica in produzione.

---

## 🗓️ Giorno 4: Primo Jenkinsfile & Orchestrazione Pipeline (`order-service`)

L'obiettivo della quarta giornata è l'implementazione del primo ciclo di CI/CD automatizzato per il microservizio `order-service`, sfruttando le funzioni astratte all'interno della nostra Shared Library aziendale.

### Attività Eseguite:

1. **Creazione della Pipeline Declarative:** Scrittura e inserimento del file `Jenkinsfile` nella radice del progetto `order-service`.
2. **Integrazione della Shared Library:** Importazione della libreria globale in cima al file tramite la direttiva `@Library('capgemini-devops-library') _` per abilitare l'uso dei custom steps sviluppati nei giorni precedenti.
3. **Strutturazione degli Stage di Build:**
* **Stage 'Checkout':** Configurato per prelevare il codice sorgente aggiornato del microservizio.
* **Stage 'Compile & Package':** Invocazione della funzione centralizzata `buildJavaApp()` per avviare la compilazione Maven in ambiente isolato.
* **Stage 'Docker Image Creation':** Invocazione del custom step `dockerBuildPush()` passando come parametri il nome immagine (`order-service`) e il tag dinamico per buildare l'artefatto finale localmente.


4. **Validazione su Jenkins Core Aggiornato:** Esecuzione del primo run di test sulla nuova istanza Jenkins v2.555.3 per verificare la corretta risoluzione delle dipendenze Groovy, il caricamento della libreria dal volume locale e la persistenza dei dati post-aggiornamento del container.

---

