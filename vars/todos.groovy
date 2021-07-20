def call(Map params = [:]) {
    def args = [
            NEXUS_IP: '172.31.8.2',
    ]
    args << params
    pipeline {
        agent {
            label "${args.SLAVE_LABEL}"
        }
//      triggers {
//       pollSCM('* * * * 1-5')
//        }

        tools {
            jdk 'jdk1.8'
            maven 'mvn3.6.3'
            go 'go1.16.5'
            nodejs 'npm16.4.0'
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
           stage('Deploy to DEV Env'){
               steps{
                   script{
                       get_branch = "env | grep GIT_BRANCH | awk -F / '{print \$NF}' | xargs echo -n"
                       env. get_branch_exec=sh(returnStdout: true, script: get_branch)
                       print "${get_branch_exec}"
                   }
                   build job: 'Deployment Pipeline' , parameters: [string (name: 'ENV' , value: 'dev'), string(name:'COMPONENT',value: "${COMPONENT}"), string(name: 'VERSION', value: "${get_branch_exec}")]
               }
           }


        }
    }
}