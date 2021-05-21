def call(Map Params = [:]) {
    def args = [
            NEXUS_IP : '3.208.90.51',
    ]
    args << params
    pipeline {
        agent {
            label 'dev'
        }
        environment {
            COMPONENT    = "${args.COMPONENT}"
            NEXUS_IP     = "${args.NEXUS_IP}"
            PROJECT_NAME = "${args.PROJECT_NAME}"
            SLAVE_LABEL  = "${args.SLAVE_LABEL}"
        }
        stages {
            stage('Download Dependencies') {
                steps {
                    sh '''
                   echo ${COMPONENT}
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
          curl -f -v -u admin:admin --upload-file frontend.zip http://${NEXUS_IP}:8081/repository/frontend/frontend.zip

           '''
                }
            }
        }
    }
}

