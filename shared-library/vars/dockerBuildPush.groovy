def call(Map config = [:]) {
    def imageName = config.imageName
    def imageTag = config.get('imageTag', 'latest')

    if(!imageName) {
        error " ❌ [Shared Library] Il parametro 'imageName' è obbligatorio"
    }

    echo "🐳 [Shared Library] Costruzione immagine Docker: ${imageName}:${imageTag}..."
    sh "docker build --no-cache -t ${imageName}:${imageTag} ."
}