def call(Map params = [:]) {
    def args = [
            NEXUS_IP: '100.25.246.55',
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

            stage('Build Project') {
                    steps {
                        script {
                            build = new nexus()
                            build.code_build ("$APP_TYPE)","${COMPONENT}")
                        }
                    }
                }

            stage('Prepare Artifacts') {
                steps {
                    script {
                        prepare = new nexus()
                        prepare.make_artifacts ("$APP_TYPE)","${COMPONENT}")
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