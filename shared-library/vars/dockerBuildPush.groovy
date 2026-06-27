def call(String imageName, Object imageTag) {

    // Convertiamo esplicitamente il tag in Stringa pura
    def tag = imageTag.toString()

    if(!imageName) {
        error " ❌ [Shared Library] Il parametro 'imageName' è obbligatorio"
    }

    echo "🐳 [Shared Library] Costruzione immagine Docker: ${imageName}:${tag}..."

    // Utilizziamo il comando 'docker' nativo (ora installato nel container)
    // Usiamo 'dir' per spostarci nella cartella corretta prima di eseguire il build.
    // In questo modo il contesto di build è corretto e Docker trova pom.xml e src.
    dir('services/order-service') {
        sh "docker build --no-cache -t ${imageName}:${tag} ."
    }
}