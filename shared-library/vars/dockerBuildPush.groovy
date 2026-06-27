def call(String imageName, Object imageTag) {

    // Convertiamo esplicitamente il tag in Stringa pura, così se arriva un GString da Jenkins viene ripulito
    def tag = imageTag.toString()


    if(!imageName) {
        error " ❌ [Shared Library] Il parametro 'imageName' è obbligatorio"
    }

    echo "🐳 [Shared Library] Costruzione immagine Docker: ${imageName}:${imageTag}..."
    sh "docker build --no-cache -t ${imageName}:${imageTag} ."
}