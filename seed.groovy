folder('CI-Pipeline') {
    displayName('CI Pipeline')
    description('CI Pipeline')
}
def component = ["frontend", "login", "users" , "todo"];

def count=(component.size()-1)
for (i in 0..count) {
    def j=component[i]
    pipelineJob("CI-Pipeline/${j}") {
        configure { flowdefinition ->
            flowdefinition / 'properties' << 'org.jenkinsci.plugins.workflow.job.properties.PipelineTriggersJobProperty' {
                'triggers' {
                    'hudson.triggers.SCMTrigger' {
                        'spec'('* * * * 1-5')
                        'ignorePostCommitHooks'(false)
                    }
                }
            }
            flowdefinition << delegate.'definition'(class:'org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition',plugin:'workflow-cps') {
                'scm'(class:'hudson.plugins.git.GitSCM',plugin:'git') {
                    'userRemoteConfigs' {
                        'hudson.plugins.git.UserRemoteConfig' {
                            'url'('https://github.com/naveenzelarsoft/'+j+'.git')
                            'refspec'('\'+refs/tags/*\':\'refs/remotes/origin/tags/*\'')
                        }
                    }
                    'branches' {
                        'hudson.plugins.git.BranchSpec' {
                            'name'('*/tags/*')
                        }
                    }
                }
                'scriptPath'('jenkinsfile')
                'lightweight'(true)
            }
        }
    }
}



pipelineJob("Deployment Pipeline") {
    configure { flowdefinition ->
        flowdefinition << delegate.'definition'(class:'org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition',plugin:'workflow-cps') {
            'scm'(class:'hudson.plugins.git.GitSCM',plugin:'git') {
                'userRemoteConfigs' {
                    'hudson.plugins.git.UserRemoteConfig' {
                        'url'('https://github.com/naveenzelarsoft/nexus.git')
                    }
                }
                'branches' {
                    'hudson.plugins.git.BranchSpec' {
                        'name'('master')
                    }
                }
            }
            'scriptPath'('jenkins-deployment')
            'lightweight'(true)
        }
    }
}