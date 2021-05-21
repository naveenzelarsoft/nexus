def call(Map params = [:]) {
    def args = [
            NEXUS_IP : '52.3.229.32',
    ]
    args << params
    pipeline {
        agent {
            label "${args.SLAVE_LABEL}"
        }
        environment {
            COMPONENT    = "${args.COMPONENT}"
            NEXUS_IP     = "${args.NEXUS_IP}"
            PROJECT_NAME = "${args.PROJECT_NAME}"
            SLAVE_LABEL  = "${args.SLAVE_LABEL}"
            APP_TYPE     = "${args.APP_TYPE}"
        }
        stages {
            stage('Download Dependencies') {
                when {
                    environment name: 'APP_TYPE', value: 'NPM'
                }
                steps {
                    sh '''     
                          sudo npm install && sudo npm run build
                       '''
                }
            }
            stage ('Prepare Artifacts-frontend') {
                when {
                    environment name: 'APP_TYPE', value: 'NPM'
                }
                steps {
                    sh '''
              echo ${COMPONENT}
             zip -r ${COMPONENT}.zip node_modules dist  
          '''
                }
            }

            stage ('Get Dependencies'){
                when {
                    environment name: 'APP_TYPE', value: 'GOLANG'
                }

                steps{
                    sh '''
          go get github.com/dgrijalva/jwt-go
          go get github.com/labstack/echo
          go get github.com/labstack/echo/middleware
          go get github.com/labstack/gommon/log
          go get github.com/openzipkin/zipkin-go
          go get github.com/openzipkin/zipkin-go/middleware/http
          go get github.com/openzipkin/zipkin-go/reporter/http
          '''
                }
            }

            stage ('Build Packages'){
                when {
                    environment name: 'APP_TYPE', value: 'GOLANG'
                }

                steps {
                    sh '''
           go build
          '''
                }
            }
            stage ('Prepare Artifacts-login') {
                when {
                    environment name: 'APP_TYPE', value: 'GOLANG'
                }
                steps {
                    sh '''
             zip -r ${COMPONENT}.zip Login
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

