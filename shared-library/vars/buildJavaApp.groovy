def call(Map config = [:]) {
    def jdkVersion = config.get('jdkVersion', '21')

    echo "🚀 [Shared Library] Avvio compilazione Java  con JDK ${jdkVersion}..."

    // Per ora simuliamo i passaggi della pipeline all'interno di  uno stage
    stage('Maven Build') {
        // Il comando assume che ci sia Maven installato sul build mode o nel container
        sh "mvn clean package -DskipTest -B"
    }
}