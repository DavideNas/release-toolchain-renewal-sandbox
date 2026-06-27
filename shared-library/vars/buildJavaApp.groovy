def call(Map config = [:]) {
    pipeline {
        agent any

        options {
            timeout(time: 1, unit: 'HOURS')
            ansiColor('xterm')
        }

        stages {
            stage('Checkout') {
                steps {
                    checkout scm
                }
            }

            stage('Maven Build & Test') {
                steps {
                    script {
                        def jdkVersion = config.get('jdkVersion', '21')
                        echo "🚀 [Shared Library] Avvio compilazione Java  con JDK ${jdkVersion}..."
                    }
                    // Compiliamo il codice ed eseguiamo i test unitari generando i report di copertura
                    sh "mv clean verify -B"
                }
            }

            stage('SonarQube Analysis') {
                steps {
                    echo "🛡️ [Shared Library] Invio del codice a SonarQube per l'analisi SAST..."
                    // Richiamiamoo il server configurato su Jenkins con il nome 'SonarQube'
                    withSonarQubeEnv('SonarQube') {
                        // Lanciamo lo scanner di Sonar integrato in Maven
                        sh "mvn sonar:sonar -Dsonar.projectKey=${env.JOB_NAME} -Dsonar.projectName=${env.JOB_NAME}"
                    }
                }
            }

            stage('Quality Gate') {
                steps {
                    echo "⏳ [Shared Library] In attesa del verdetto del Quality Gate..."
                    // Blocca la pipeline se SonarQube restituisce un errore (es. troppi bug o security vulnerability)
                    timeout(time: 5, unit: 'MINUTES') {
                        waitForQualityGate abortPipeline: true
                    }
                }
            }

            stage('Docker Build') {
                steps {
                    echo "📦 [Shared Library] Creazione immagine Docker..."
                    sh "docker build -t ${config.appName}:latest ."
                }
            }
        }
    }
}