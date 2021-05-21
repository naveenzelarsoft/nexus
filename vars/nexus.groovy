def nexus() {
    command = "curl -f -v -u admin:admin --upload-file users.zip http://3.208.90.51:8081/repository/users/users.zip"
    dif execute_state=sh(returnStdout: true, script: command)
}

def make_artifacts (APP_TYPE , COMPONENT) {
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

