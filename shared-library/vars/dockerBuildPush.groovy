def call(Map config = [:]) {
    def imageName = config.imageName
    def imageTag = config.get('imageTag', 'latest')

    if(!imageName) {
        error " ❌ [Shared Library] Il parametro 'imageName' è obbligatorio"
    }

    script {
        echo "🐳 [Shared Library] Costruzione immagine Docker: ${imageName}:${imageTag}..."

        stage('Docker Build') {
            sh "docker build --no-cache -t ${imageName}:${imageTag} ."
        }
    }
}