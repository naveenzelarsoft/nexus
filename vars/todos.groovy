def call(Map params = [:]) {
    def args = [
            NEXUS_IP: '52.3.229.32',
    ]
    args << params
    pipeline {
        agent {
            label "${args.SLAVE_LABEL}"
        }
        tools {
            maven 'mvn3.6.3'

        }
        environment {
            COMPONENT = "${args.COMPONENT}"
            NEXUS_IP = "${args.NEXUS_IP}"
            PROJECT_NAME = "${args.PROJECT_NAME}"
            SLAVE_LABEL = "${args.SLAVE_LABEL}"
            APP_TYPE = "${args.APP_TYPE}"
        }
        stages {
            stage('Download Dependencies-frontend') {
                when {
                    environment name: 'APP_TYPE', value: 'NGINX'
                }
                steps {
                    sh '''     
                          sudo npm install && sudo npm run build
                       '''
                }
            }


            stage('Get Dependencies') {
                when {
                    environment name: 'APP_TYPE', value: 'GOLANG'
                }

                steps {
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

            stage('Build Packages') {
                when {
                    environment name: 'APP_TYPE', value: 'GOLANG'
                }

                steps {
                    sh '''
           go build
          '''
                }
            }
            stage('Prepare Artifacts-login') {
                when {
                    environment name: 'APP_TYPE', value: 'GOLANG'
                }
                steps {
                    sh '''
             zip -r ${COMPONENT}.zip Login
          '''
                }
            }

                stage('Download Dependencies-todo') {
                    when {
                        environment name: 'APP_TYPE', value: 'NODEJS'
                    }

                    steps {
                        sh '''
           npm install
         '''
                    }
                }
                stage('Prepare Artifacts-todo') {

                    when {
                        environment name: 'APP_TYPE', value: 'NODEJS'
                    }
                    steps {
                        sh '''
              zip -r todo.zip node_modules server.js
          '''
                    }
                }

                stage('Build Project') {
                    when {
                        environment name: 'APP_TYPE', value: 'MAVEN'
                    }
                    steps {
                        sh '''
          mvn clean package
          '''
                    }
                }
                stage('Prepare Artifacts-users') {
                    when {
                        environment name: 'APP_TYPE', value: 'MAVEN'
                    }
                    steps {
                        sh '''
            cp target/users-api-0.0.1.jar users.jar
            zip -r users.zip users.jar
          '''
                    }
                }

            stage('Prepare Artifacts') {
                when {
                    environment name: 'APP_TYPE', value: 'NGINX'
                }
                steps {
                    script {
                        prepare = new nexus()
                        prepare.make_artifacts "${COMPONENT}"
                    }
                    sh '''
                      ls 
                    '''
                }
            }

                stage('Upload Artifact') {
                    steps {
                        sh '''
          curl -f -v -u admin:admin --upload-file ${COMPONENT}.zip http://${NEXUS_IP}:8081/repository/${COMPONENT}/${COMPONENT}.zip

           '''
                    }
                }
            }
        }
    }