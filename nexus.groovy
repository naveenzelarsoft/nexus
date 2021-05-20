def nexus() {
    command = "curl -f -v -u admin:admin --upload-file users.zip http://3.208.90.51:8081/repository/users/users.zip"
    dif execute_state=sh(returnStdout: true, script: command)
}

