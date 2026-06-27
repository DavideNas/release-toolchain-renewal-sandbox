def call() {
    def jdkVersion = '21'
    echo "🚀 [Shared Library] Avvio compilazione Java con JDK ${jdkVersion}..."

    // Carichiamo lo strumento Maven configurato su Jenkins (usa lo stesso nome inserito nei Tools)
    def mavenTool = tool name: 'M3', type: 'maven'

    // Eseguiamo i comandi inserendo il path corretto di Maven
    sh "${mavenTool}/bin/mvn clean verify -B"

    // Invio del codice a SonarQube per l'analisi SAST
    echo "🛡️ [Shared Library] Invio del codice a SonarQube per l'analisi SAST..."
    withSonarQubeEnv('SonarQube') {
        sh "${mavenTool}/bin/mvn sonar:sonar -Dsonar.projectKey=${env.JOB_NAME} -Dsonar.projectName=${env.JOB_NAME}"
    }

    // In attesa del verdetto del Quality Gate
    echo "⏳ [Shared Library] In attesa del verdetto del Quality Gate..."
    timeout(time: 5, unit: 'MINUTES') {
        waitForQualityGate abortPipeline: true
    }
}