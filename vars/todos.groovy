def call(Map params = [:]) {
    def args = [
            NEXUS_IP: '100.25.246.55',
    ]
    args << params
    pipeline {
        agent {
            label "${args.SLAVE_LABEL}"
        }
        triggers {
            pollSCM('* * * * 1-5')
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

            stage('Build Project') {
                steps {
                    script {
                        build = new nexus()
                        build.code_build("${APP_TYPE}", "${COMPONENT}")
                    }
                }
            }

            stage('Prepare Artifacts') {
                steps {
                    script {
                        prepare = new nexus()
                        prepare.make_artifacts("${APP_TYPE}", "${COMPONENT}")
                    }
                }
            }

            stage('Upload Artifact') {
                steps {
                    script {
                        prepare = new nexus()
                        prepare.nexus(COMPONENT)
                    }
                }
            }
        }
    }
}