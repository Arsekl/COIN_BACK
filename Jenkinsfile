#!/usr/bin/groovy

pipeline{
    agent any
    post {
      failure {
        updateGitlabCommitStatus name: 'getcode', state: 'failed'
        updateGitlabCommitStatus name: 'package', state: 'failed'
        updateGitlabCommitStatus name: 'deploy', state: 'failed'
      }
      success {
        updateGitlabCommitStatus name: 'getcode', state: 'success'
        updateGitlabCommitStatus name: 'package', state: 'success'
        updateGitlabCommitStatus name: 'deploy', state: 'success'
      }
    }
    options {
      gitLabConnection('test')
      gitlabBuilds(builds: ['getcode', 'package', 'deploy'])
    }
    stages{
        stage("getcode"){
           steps{
               echo "get code from scm"
           }
        }
        stage("package"){
            steps{
                echo "packge code"
            }
        }
        stage("deploy"){
            steps{
                echo "deploy packge to node1"
            }
        }
    }
}