def call(String imageName, Object imageTag) {

    // Convertiamo esplicitamente il tag in Stringa pura, così se arriva un GString da Jenkins viene ripulito
    def tag = imageTag.toString()

    if(!imageName) {
        error " ❌ [Shared Library] Il parametro 'imageName' è obbligatorio"
    }

    // Recuperiamo il percorso del client Docker installato tramite i Tools di Jenkins
    def dockerTool = tool name: 'docker-tool', type: 'org.jenkinsci.plugins.docker.commons.tools.DockerTool'

    // Costruiamo il percorso completo del binario
    def dockerBinary = "${dockerTool}/bin/docker"

    echo "🐳 [Shared Library] Costruzione immagine Docker: ${imageName}:${imageTag}..."

    // Utilizziamo il percorso assoluto del tool per eseguire il comando build
    // Aggiungiamo il percorso (il punto alla fine indica il contesto, -f indica dove sta il Dockerfile)
    sh "docker build --no-cache -t ${imageName}:${tag} -f services/order-service/Dockerfile ."
}