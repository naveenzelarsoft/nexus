def nexus(COMPONENT) {
    get_branch = "env | grep GIT_BRANCH | awk -F / '{print \$NF}' | xargs echo -n"
    def get_branch_exec=sh(returnStdout: true, script: get_branch)
    def FILENAME=COMPONENT+'-'+get_branch_exec+'.zip'

    command = "curl -f -v -u admin:admin --upload-file ${FILENAME} http://3.208.90.51:8081/repository/${COMPONENT}/${FILENAME}"
    def execute_state=sh(returnStdout: true, script: command)
}

def make_artifacts (APP_TYPE , COMPONENT) {
    get_branch = "env | grep GIT_BRANCH | awk -F / '{print \$NF}' | xargs echo -n"
    def get_branch_exec=sh(returnStdout: true, script: get_branch)
    def FILENAME=COMPONENT+'-'+get_branch_exec+'.zip'
    if (APP_TYPE == "NGINX") {
        command = "zip -r ${COMPONENT}.zip node_modules dist"
        def execute_com = sh(returnnStdout: true, script: command)
        print execute_com
    }
    else if (APP_TYPE == "GOLANG") {
        command = "zip -r ${COMPONENT}.zip Login"
        def execute_com = sh(returnnStdout: true, script: command)
        print execute_com
    }
    else if (APP_TYPE == "NODEJS") {
        command = "zip -r todo.zip node_modules server.js"
        def execute_com = sh(returnnStdout: true, script: command)
        print execute_com
    }
    else if (APP_TYPE == "MAVEN") {
        command = "cp target/users-api-0.0.1.jar users.jar && zip -r users.zip users.jar"
        def execute_com = sh(returnnStdout: true, script: command)
        print execute_com
    }
}

def code_build(APP_TYPE , COMPONENT) {
    if (APP_TYPE == "NGINX") {
        command = "sudo npm install && sudo npm run build"
        def execute_com = sh(returnnStdout: true, script: command)
        print execute_com
    }
    else if (APP_TYPE == "GOLANG") {
        command = "  go get github.com/dgrijalva/jwt-go && go get github.com/labstack/echo && go get github.com/labstack/echo/middleware && go get github.com/labstack/gommon/log && go get github.com/openzipkin/zipkin-go && go get github.com/openzipkin/zipkin-go/middleware/http && go get github.com/openzipkin/zipkin-go/reporter/http &&  go build"
        def execute_com = sh(returnnStdout: true, script: command)
        print execute_com
    }
    else if (APP_TYPE == "NODEJS") {
        command = "npm install"
        def execute_com = sh(returnnStdout: true, script: command)
        print execute_com
    }
    else if (APP_TYPE == "MAVEN") {
        command = "mvn clean package"
        def execute_com = sh(returnnStdout: true, script: command)
        print execute_com
    }
}