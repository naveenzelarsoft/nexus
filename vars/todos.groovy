def () {
    pipeline {
        agent {
            label 'dev'
        }
        stages {
            stage('Download Dependencies') {
                steps {
                    sh '''
         sudo npm install && sudo npm run build
       '''
                }
            }
            stage ('Prepare Artifacts') {
                steps {
                    sh '''
             zip -r frontend.zip node_modules dist
          '''
                }
            }
            stage ('Upload Artifact') {
                steps {
                    sh '''
          curl -f -v -u admin:admin --upload-file frontend.zip http://3.208.90.51:8081/repository/frontend/frontend.zip

           '''
                }
            }
        }
    }
}

