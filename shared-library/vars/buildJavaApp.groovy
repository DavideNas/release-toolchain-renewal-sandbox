def call() {
    // Definiamo direttamente qui la versione di default visto che non la passiamo dal Jenkinsfile
    def jdkVersion = '21'
    echo "🚀 [Shared Library] Avvio compilazione Java con JDK ${jdkVersion}..."

    // Diamo i permessi di esecuzione al wrapper (essenziale su sistemi Linux/Docker)
    sh "chmod +x ./mvnw"

    // Compiliamo il codice ed eseguiamo i test unitari generando i report di copertura
    sh "mvn clean verify -B"

    // Invio del codice a SonarQube per l'analisi SAST
    echo "🛡️ [Shared Library] Invio del codice a SonarQube per l'analisi SAST..."
    // Richiamiamo il server configurato su Jenkins con il nome 'SonarQube'
    withSonarQubeEnv('SonarQube') {
        // Usiamo env.JOB_NAME che è una variabile globale sempre disponibile in Jenkins
        sh "mvn sonar:sonar -Dsonar.projectKey=${env.JOB_NAME} -Dsonar.projectName=${env.JOB_NAME}"
    }

    // In attesa del verdetto del Quality Gate
    echo "⏳ [Shared Library] In attesa del verdetto del Quality Gate..."
    // Blocca la pipeline se SonarQube restituisce un errore
    timeout(time: 5, unit: 'MINUTES') {
        waitForQualityGate abortPipeline: true
    }
}