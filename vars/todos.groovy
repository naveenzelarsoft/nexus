def call(Map Params = [:]) {
    def args = [
            NEXUS_IP : '3.208.90.51',
    ]
    args << params
    pipeline {
        agent {
            label 'DEV'
        }
        environment {
            NEXUS_IP     = "${args.NEXUS_IP}"
            PROJECT_NAME = "${args.PROJECT_NAME}"
        }
        stages {
            stage('Download Dependencies') {
                when {
                    environment name: 'COMPONENT', value: 'frontend'
                }
                steps {
                    sh '''     
                          sudo npm install && sudo npm run build
                       '''
                }
            }
            stage ('Prepare Artifacts') {
                when {
                    environment name: 'COMPONENT', value: 'frontend'
                }
                steps {
                    sh '''
              echo ${COMPONENT}
             zip -r ${COMPONENT}.zip node_modules dist  
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

